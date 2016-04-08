package duplicatesearcher.processing;

import java.util.regex.Pattern;

import duplicatesearcher.Token;

public class Tokenizer
{
	private final static String UNWANTED = "([\\W_A-Z])";
	private final static String REPLACE = "[\\W_]";
	private final String data;
	
	public Tokenizer(final String input)
	{
		this.data = input;
	}

	public Token[] getTokens()
	{
		final String[] strings = tokenize(data);
		final Token[] tokens = createTokens(strings);
		return tokens;
	}

	public String[] tokenize(String input)
	{
		input = purge(input);
		input = input.toLowerCase();
		return split(input);
	}

	private Token[] createTokens(String[] strings)
	{
		final Token[] tokens = new Token[strings.length];
		for(int i = 0; i < strings.length; i++)
			tokens[i] = new Token(strings[i]);
		
		return tokens;
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
