package duplicatesearcher.analysis;

import java.util.HashMap;
import java.util.Map;

abstract class FrequencyCounter
{
	private final Map<String, Integer> frequency = new HashMap<String, Integer>();
	
	public abstract int add(final String input);
	public abstract double getWeight(final String token);
	
	public int getTokenFrequency(final String token)
	{
		if(!frequency.containsKey(token))
			return 0;
		
		return frequency.get(token);
	}
	
	public int size()
	{
		return frequency.size();
	}

	protected int increment(String token)
	{
		if(!frequency.containsKey(token))
		{
			frequency.put(token, 1);
			return 1;
		}
		else
		{
			final int tokenFrequency = frequency.get(token) + 1;
			frequency.put(token, tokenFrequency);
			return tokenFrequency;
		}
	}
}
