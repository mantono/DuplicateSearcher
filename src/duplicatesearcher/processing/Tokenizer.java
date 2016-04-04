package duplicatesearcher.processing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Tokenizer
{ 
	private final String data;
	
	public Tokenizer(final String input)
	{
		this.data = input;
	}

	public String[] getTokens()
	{
		return tokenize(data);
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
}
