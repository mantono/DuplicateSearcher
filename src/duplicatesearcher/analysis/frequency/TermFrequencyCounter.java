package duplicatesearcher.analysis.frequency;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.egit.github.core.Label;

import duplicatesearcher.Token;
import duplicatesearcher.processing.Tokenizer;

public class TermFrequencyCounter implements FrequencyCounter
{
	private final Map<Token, Integer> frequency = new HashMap<Token, Integer>();

	@Override
	public int getTokenFrequency(final Token token)
	{
		if(!frequency.containsKey(token))
			return 0;

		return frequency.get(token);
	}

	@Override
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

	public int remove(final Token token)
	{
		if(!frequency.containsKey(token))
			return -1;
		return frequency.remove(token);
	}
	
	public int remove(final String token)
	{
		if(!Tokenizer.isToken(token))
			return 0;
		return remove(new Token(token));
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

	public int add(final CharSequence input)
	{
		return addString(input.toString());
	}
	
	public boolean add(final Token token)
	{
		return increment(token) > 0;
	}

	public int add(final TermFrequencyCounter counter)
	{
		int increase = 0;
		for (Map.Entry<Token, Integer> e : counter.frequency.entrySet())
		{
			if (!frequency.containsKey(e.getKey()))
			{
				frequency.put(e.getKey(), e.getValue());
			}
			else
			{
				Token token = e.getKey();
				int freq = e.getValue();
				int freqOld = frequency.get(token);
				frequency.put(token, freq+freqOld);
			}
			increase += e.getValue();
		}
		
		return increase;
	}
	
	public void addAll(Collection<String> input)
	{
		for(String chars : input)
			add(chars);
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

	public void addLabels(Collection<Label> labelCollection) {
		for(Label label : labelCollection)
			add(new Token(label.toString().toLowerCase().replaceAll("\\s", "")));
			
	}
	
	@Override
	public String toString(){
		StringBuilder stringBuilder = new StringBuilder();
    	for(Map.Entry<Token, Integer> entry : frequency.entrySet())
    		stringBuilder.append(entry.getKey()+"("+entry.getValue()+")");
    	
    	return stringBuilder.toString();
	}
}
