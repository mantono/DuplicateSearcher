package duplicatesearcher.analysis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import duplicatesearcher.Token;

public class Normalizer
{
	private final Map<Token, Double> weights;
	
	public Normalizer(Map<Token, Double> weightedMap)
	{
		this.weights = weightedMap;
	}
	
	private double getRootOfSum()
	{
		double sum = 0;
		for(Entry<Token, Double> pair : weights.entrySet())
		{
			double squared = Math.pow(pair.getValue(), 2);
			sum += squared;
		}

		return Math.sqrt(sum);
	}

	public Map<Token, Double> normalizeVector()
	{
		final double divider = getRootOfSum();
		Map<Token, Double> normalizedWeights = new HashMap<Token, Double>(weights);

		
		for(Entry<Token, Double> pair : normalizedWeights.entrySet())
		{
			double normalized = pair.getValue() / divider;
			if(normalized == 0 || Double.isNaN(normalized))
				normalizedWeights.put(pair.getKey(), 0.0);
			else
				normalizedWeights.put(pair.getKey(), normalized);
		}
		
		return normalizedWeights;
	}

}
