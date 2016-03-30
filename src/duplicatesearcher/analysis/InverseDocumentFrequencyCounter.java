package duplicatesearcher.analysis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import duplicatesearcher.processing.Tokenizer;

public class InverseDocumentFrequencyCounter extends FrequencyCounter
{
	private int documents = 0;
	
	@Override
	public int add(final String input)
	{
		documents++;
		final Tokenizer tokenizer = new Tokenizer(input);
		final String[] tokens = tokenizer.getTokens();
		final Set<String> uniqueTokens = new HashSet<String>(Arrays.asList(tokens));
		for(String str : uniqueTokens)
			increment(str);
		
		return tokens.length;
	}
	
	@Override
	public double getWeight(final String token)
	{
		return 1+ Math.log(documents/getTokenFrequency(token));
	}
}
