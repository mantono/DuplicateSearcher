package duplicatesearcher.analysis.frequency;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import duplicatesearcher.processing.Tokenizer;

/**
 * The {@link InverseDocumentFrequencyCounter} measures in how many documents a
 * word occurs. Unlike the {@link TermFrequencyCounter}, this class does not
 * take any consideration to how many times a word occurs within each specific
 * document, only in how many documents.
 *
 */
public class InverseDocumentFrequencyCounter implements FrequencyCounter
{
	private final Map<String, Integer> frequency = new HashMap<String, Integer>();
	private final Map<Integer, Set<String>> addedTokens = new HashMap<Integer, Set<String>>(100);

	@Override
	public int getTokenFrequency(final String token)
	{
		if(!frequency.containsKey(token))
			return 0;

		return frequency.get(token);
	}

	private boolean increment(String token)
	{
		if(token == null || token.length() == 0)
			return false;
		if(!frequency.containsKey(token))
		{
			frequency.put(token, 1);
		}
		else
		{
			final int tokenFrequency = frequency.get(token) + 1;
			frequency.put(token, tokenFrequency);
		}
		return true;
	}

	public int add(final int id, final Set<String> input)
	{
		Set<String> tokens = tokenizeInput(input);
		return addTokens(id, tokens);
	}

	private Set<String> tokenizeInput(Set<String> input)
	{
		final Set<String> tokens = new HashSet<String>();

		for(String str : input)
		{
			if(Tokenizer.isToken(str))
			{
				tokens.add(str);
			}
			else
			{
				Tokenizer tokenizer = new Tokenizer(str);
				tokens.addAll(Arrays.asList(tokenizer.getTokens()));
			}

		}
		return tokens;
	}

	private int addTokens(int id, Set<String> tokens)
	{
		Set<String> savedTokens;
		int added = 0;
		
		if(addedTokens.containsKey(id))
		{
			savedTokens = addedTokens.get(id);
			tokens.removeAll(savedTokens);
		}
		else
		{
			savedTokens = new HashSet<String>(tokens.size());
			addedTokens.put(id, savedTokens);
		}
		
		for(String token : tokens)
		{
			if(increment(token))
			{
				savedTokens.add(token);
				added++;
			}
		}
		
		return added;
	}

	@Override
	public double getWeight(final String token)
	{
		final double inverseFrequency = addedTokens.size() / (double) getTokenFrequency(token);
		return 1 + Math.log10(inverseFrequency);
	}
}
