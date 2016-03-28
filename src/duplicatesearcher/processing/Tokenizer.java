package duplicatesearcher.processing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Tokenizer implements TokenProcessor
{
	final String data;
	
	public Tokenizer(final String input)
	{
		this.data = input;
	}

	@Override
	public Set<String> getProcessedData()
	{
		final String[] tokens = tokenize(data);
		return convertToSet(tokens);
	}

	private String[] tokenize(String input)
	{
		String[] tokens = split(input);
		lowerCase(tokens);
		purge(tokens);
		return tokens;
	}
	
	public void lowerCase(String[] tokens)
	{
		for(int i = 0; i < tokens.length; i++)
			tokens[i] = tokens[i].toLowerCase();
	}

	public void purge(String[] tokens)
	{
		for(int i = 0; i < tokens.length; i++)
			tokens[i] = tokens[i].replaceAll("\\W", "");
	}

	public String[] split(String input)
	{
		return input.trim().split("[\\s/\\-_]+");
	}

	private Set<String> convertToSet(String[] tokens)
	{
		return new HashSet<String>(Arrays.<String>asList(tokens));
	}

}
