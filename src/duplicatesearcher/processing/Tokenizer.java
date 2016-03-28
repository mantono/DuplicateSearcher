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
		input = purge(input);
		input = input.toLowerCase();
		return split(input);
	}

	public String purge(String input)
	{
		return input.replaceAll("[\\W_]", " ");
	}

	public String[] split(String input)
	{
		return input.trim().split("[\\s]+");
	}

	private Set<String> convertToSet(String[] tokens)
	{
		return new HashSet<String>(Arrays.<String>asList(tokens));
	}

}
