package dsv2.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Analyzer<T>
{
	private final InverseDocumentFrequency<T> idfc;
	
	public Analyzer()
	{
		this.idfc = null;
	}
	
	public Analyzer(InverseDocumentFrequency<T> idfc)
	{
		this.idfc = idfc;
	}
	
	public double cosineSimilarity(VectorUnit<T> e1, VectorUnit<T> e2)
	{
		final Map<T, Double> weights1 = weightAndNormalize(e1);
		final Map<T, Double> weights2 = weightAndNormalize(e2);
		
		double similarity = 0;
		
		final Set<T> intersection = new HashSet<T>(weights1.keySet());
		intersection.retainAll(weights2.keySet());
		
		for(T key : intersection)
		{
			final double weight1 = weights1.get(key);
			final double weight2 = weights2.get(key);
			similarity += weight1 * weight2;
		}
		
		return similarity;
	}

	private Map<T, Double> weightAndNormalize(VectorUnit<T> e)
	{
		final TermFrequency<T> tf = new TermFrequency<T>(e.vectors());
		Map<T, Double> weights = tf.getWeights();
		weights = idfcWeighting(weights);
		return Normalizer.normalizeVector(weights);
	}

	public Map<T, Double> idfcWeighting(Map<T, Double> weights) 
	{
		if(idfc == null)
			return weights;
		
		Map<T, Double> idfcWeighted = new HashMap<T, Double>(weights.size());
		Iterator<Entry<T, Double>> iter = weights.entrySet().iterator();
		while(iter.hasNext())
		{
			final Entry<T, Double> entry = iter.next();
			final T obj = entry.getKey();
			final double tfIdfWeight = entry.getValue()*idfc.getWeight(obj);
			idfcWeighted.put(obj, tfIdfWeight);
		}
		
		return idfcWeighted;
	}
}
