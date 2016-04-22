package duplicatesearcher.analysis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import duplicatesearcher.Token;

public class Normalizer
{	
	private static double getRootOfSum(Map<Token, Double> weights)
	{
		double sum = 0;
		for(Entry<Token, Double> pair : weights.entrySet())
		{
			double squared = Math.pow(pair.getValue(), 2);
			sum += squared;
		}

		return Math.sqrt(sum);
	}

	public static Map<Token, Double> normalizeVector(Map<Token, Double> weights)
	{
		final double divider = getRootOfSum(weights);
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
