package duplicatesearcher.analysis.frequency;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import duplicatesearcher.Token;
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
	private final Map<Token, Integer> frequency = new HashMap<Token, Integer>();
	private final Map<Integer, Set<Token>> addedTokens = new HashMap<Integer, Set<Token>>(100);

	@Override
	public int getTokenFrequency(final Token token)
	{
		if(!frequency.containsKey(token))
			return 0;

		return frequency.get(token);
	}
	
	@Override
	public double getWeight(final Token token)
	{
		final double inverseFrequency = addedTokens.size() / (double) getTokenFrequency(token);
		return 1 + Math.log10(inverseFrequency);
	}
	
	@Override
	public Set<Token> getTokens()
	{
		return frequency.keySet();
	}
	
	/**
	 * Add a {@link Token} to the counter.
	 * @param id is the identifying number of the document from which the token belongs to.
	 * @param token is the token that should be added to the counter.
	 * @return true the token had not previously been added for the particular document id, else false.
	 */
	public boolean add(final int id, final Token token)
	{
		Set<Token> savedTokens;
		if(addedTokens.containsKey(id))
		{
			savedTokens = addedTokens.get(id);
			if(savedTokens.contains(token))
				return false;
		}
		else
		{
			savedTokens = new HashSet<Token>(4);
			addedTokens.put(id, savedTokens);
		}
		
		savedTokens.add(token);
		return increment(token);
	}
	
	/**
	 * Add a {@link Set} of {@link Token} objects to the counter.
	 * @param id of the document the tokens belongs to.
	 * @param input tokens that should be added.
	 * @return the number of tokens that was added to the counter.
	 */
	public int add(final int id, final Set<Token> input)
	{
		Set<Token> copyOfInput = new HashSet<Token>(input);
		return addTokens(id, copyOfInput);
	}

	private int addTokens(final int id, final Set<Token> tokens)
	{
		Set<Token> savedTokens;
		int added = 0;

		if(addedTokens.containsKey(id))
		{
			savedTokens = addedTokens.get(id);
			tokens.removeAll(savedTokens);
		}
		else
		{
			savedTokens = new HashSet<Token>(tokens.size());
			addedTokens.put(id, savedTokens);
		}

		for(Token token : tokens)
		{
			if(increment(token))
			{
				savedTokens.add(token);
				added++;
			}
		}

		return added;
	}

	private boolean increment(Token token)
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
}
