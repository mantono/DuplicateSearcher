package duplicatesearcher.analysis.frequency;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import duplicatesearcher.Token;
import duplicatesearcher.processing.Tokenizer;

public class TermFrequencyCounter implements FrequencyCounter
{
	private final Map<Token, Integer> frequency = new HashMap<Token, Integer>();

	public int getTokenFrequency(final Token token)
	{
		if(!frequency.containsKey(token))
			return 0;

		return frequency.get(token);
	}

	public Set<Token> getTokens()
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

	public int remove(final Token three)
	{
		return frequency.remove(three);
	}

	public boolean change(final Token currentKey, final Token newKey)
	{
		if(!frequency.containsKey(currentKey))
			return false;

		final int value = frequency.get(currentKey);
		frequency.remove(currentKey);
		frequency.put(newKey, value);
		return true;
	}

	protected int addString(final String input)
	{
		final Tokenizer tokenizer = new Tokenizer(input);
		final Token[] tokens = tokenizer.getTokens();
		for(Token str : tokens)
			increment(str);

		return tokens.length;
	}

	protected int increment(Token token)
	{
		if(token == null || token.length() == 0)
			return 0;
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

	public int add(final String input)
	{
		return addString(input);
	}

	@Override
	public double getWeight(Token token)
	{
		int tokenFreq = getTokenFrequency(token);
		double tokenLog = Math.log(tokenFreq);
		if(tokenLog < 0)
			return 0;
		return 1 + tokenLog;
	}
}
