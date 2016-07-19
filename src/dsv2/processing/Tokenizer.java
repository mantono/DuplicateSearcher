package dsv2.processing;

import java.util.EnumSet;
import java.util.regex.Pattern;

import duplicatesearcher.Token;

public class Tokenizer
{
	private final static String UNWANTED = "([\\W_A-Z])";
	private final static String REPLACE = "[\\W_]";
	
	private final EnumSet<? extends RegexFilter> filters;

	public Tokenizer()
	{
		this.filters = null;
	}
	
	public Tokenizer(final EnumSet<? extends RegexFilter> filters)
	{
		this.filters = filters;
	}

	public Token[] tokenize(String input)
	{
		input = input.toLowerCase();
		input = filter(input);
		input = purge(input);
		final String[] splitted = split(input);
		return createTokens(splitted);
	}

	public String filter(String input)
	{
		if(filters == null)
			return input;
		
		for(RegexFilter filter : filters)
			input = input.replaceAll(filter.regex(), filter.substitute());
		
		return input;
	}

	private static Token[] createTokens(CharSequence[] charSequences)
	{
		final Token[] tokens = new Token[charSequences.length];
		for(int i = 0; i < charSequences.length; i++)
			tokens[i] = new Token(charSequences[i]);
		
		return tokens;
	}

	public static String purge(String input)
	{
		return input.replaceAll(REPLACE, " ");
	}

	public static String[] split(String input)
	{
		return input.trim().split("[\\s]+");
	}
	
	public static boolean isToken(final CharSequence input)
	{
		if(input == null)
			return false;
		Pattern pattern = Pattern.compile(UNWANTED);
		return !pattern.matcher(input).find();
	}
}