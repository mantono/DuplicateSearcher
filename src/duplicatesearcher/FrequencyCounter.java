package duplicatesearcher;

import java.util.HashMap;
import java.util.Map;

import duplicatesearcher.processing.Tokenizer;

public class FrequencyCounter
{
	private final String data;

	public FrequencyCounter(String input)
	{
		this.data = input;
	}

	public Map<String, Integer> getTokenFrequency()
	{
		final Tokenizer tokenizer = new Tokenizer(data);
		final String[] tokens = tokenizer.getTokens();
		Map<String, Integer> tokenFreq = new HashMap<String, Integer>(tokens.length);
		for(String str : tokens)
			increment(tokenFreq, str);
		
		return tokenFreq;
	}

	private void increment(Map<String, Integer> tokenFreq, String token)
	{
		if(!tokenFreq.containsKey(token))
		{
			tokenFreq.put(token, 1);
		}
		else
		{
			final int frequency = tokenFreq.get(token);
			tokenFreq.put(token, frequency+1);
		}
	}

}
