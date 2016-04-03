package duplicatesearcher.analysis.frequency;

import duplicatesearcher.processing.Tokenizer;

public class TermFrequencyCounter extends FrequencyCounter
{
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
		int tokenFreq = getTokenFrequency(token);
		double tokenLog = Math.log(tokenFreq);
		if(tokenLog < 0)
			return 0;
		return 1 + tokenLog; 
	}
}
