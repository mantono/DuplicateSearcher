package duplicatesearcher.analysis.frequency;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import duplicatesearcher.StrippedIssue;
import duplicatesearcher.processing.Tokenizer;

public class InverseDocumentFrequencyCounter extends FrequencyCounter
{
	private final Set<Integer> documents = new HashSet<Integer>();
	
	public int add(final StrippedIssue input)
	{
		if(!documents.add(input.getNumber()))
			return 0;
		
		final Set<String> tokens = input.wordSet();
		for(String str : tokens)
			increment(str);
		
		return tokens.size();
	}
	
	@Override
	public double getWeight(final String token)
	{
		final double inverseFrequency = documents.size()/(double)getTokenFrequency(token);
		return 1+ Math.log10(inverseFrequency);
	}
}
