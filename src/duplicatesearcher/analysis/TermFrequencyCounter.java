package duplicatesearcher.analysis;

import duplicatesearcher.processing.Tokenizer;

public class TermFrequencyCounter extends FrequencyCounter
{
	@Override
	public int add(final String input)
	{
		final Tokenizer tokenizer = new Tokenizer(input);
		final String[] tokens = tokenizer.getTokens();
		for(String str : tokens)
			increment(str);
		
		return tokens.length;
	}

	@Override
	public double getWeight(String token)
	{
		return 1 + Math.log(getTokenFrequency(token));
	}
}
