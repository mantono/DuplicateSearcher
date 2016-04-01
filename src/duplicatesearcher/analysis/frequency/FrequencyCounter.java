package duplicatesearcher.analysis.frequency;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This abstract class counts the occurences of a String token in an issue.
 *
 */
public abstract class FrequencyCounter
{
	private final Map<String, Integer> frequency = new HashMap<String, Integer>();
	
	public abstract double getWeight(final String token);
	
	public int getTokenFrequency(final String token)
	{
		if(!frequency.containsKey(token))
			return 0;
		
		return frequency.get(token);
	}
	
	public Set<String> getTokens()
	{
		return frequency.keySet();
	}
	
	public int size()
	{
		return frequency.size();
	}
	
	public void clear()
	{
		frequency.clear();
	}
	
	public int remove(final String key)
	{
		return frequency.remove(key);
	}
	
	public boolean change(final String currentKey, final String newKey)
	{
		if(!frequency.containsKey(currentKey))
			return false;
		
		final int value = frequency.get(currentKey);
		frequency.remove(currentKey);
		frequency.put(newKey, value);
		return true;
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
