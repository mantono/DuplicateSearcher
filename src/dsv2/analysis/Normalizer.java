package dsv2.analysis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Normalizer
{	
	public static <T> double getRootOfSum(Map<T, Double> vector)
	{
		double sum = 0;
		for(Entry<T, Double> pair : vector.entrySet())
		{
			double squared = Math.pow(pair.getValue(), 2);
			sum += squared;
		}

		return Math.sqrt(sum);
	}

	public static <T> Map<T, Double> normalizeVector(Map<T, Double> vector)
	{
		final double divider = getRootOfSum(vector);
		Map<T, Double> normalizedVector = new HashMap<T, Double>(vector);

		
		for(Entry<T, Double> pair : normalizedVector.entrySet())
		{
			double normalized = pair.getValue() / divider;
			if(normalized == 0 || Double.isNaN(normalized))
				normalizedVector.put(pair.getKey(), 0.0);
			else
				normalizedVector.put(pair.getKey(), normalized);
		}
		
		return normalizedVector;
	}

}