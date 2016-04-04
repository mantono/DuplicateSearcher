package duplicatesearcher.processing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Tokenizer
{
	private final static String UNWANTED = "([\\W_A-Z])";
	private final static String REPLACE = "[\\W_]";
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
		return input.replaceAll(REPLACE, " ");
	}

	public String[] split(String input)
	{
		return input.trim().split("[\\s]+");
	}
	
	public static boolean isToken(final String input)
	{
		Pattern pattern = Pattern.compile(UNWANTED);
		return !pattern.matcher(input).find();
	}
}
