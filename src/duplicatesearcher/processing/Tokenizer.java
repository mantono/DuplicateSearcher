package duplicatesearcher.processing;

import java.util.regex.Pattern;

import duplicatesearcher.Token;

public class Tokenizer
{
	private final static String UNWANTED = "([\\W_A-Z])";
	private final static String REPLACE = "[\\W_]";
	private final static String EMOJI = "\\:\\S+\\:";
	private final static String RE_SUFFIX = "'re";
	private final static String S_SUFFIX = "'s";
	private final static String LL_SUFFIX = "'ll";
	private final static String APOSTROPHE = "'";
	private final static String URL = "http[s]?://[\\w+\\.]+\\w{2,}[/\\w]*";
	private final static String DOMAIN = "\\b[\\w+.]*\\w+\\.\\w{2,}[/\\w]*";
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
		input = input.toLowerCase();
		input = removeURLs(input);
		input = removeApostrophes(input);
		input = removeEmojis(input);
		input = purge(input);
		return split(input);
	}

	public String removeURLs(String input)
	{
		input = input.replaceAll(URL, "");
		input = input.replaceAll(DOMAIN, "");
		return input;
	}

	public String removeApostrophes(String input)
	{
		input = input.replaceAll(RE_SUFFIX, " are");
		input = input.replaceAll(S_SUFFIX, "");
		input = input.replaceAll(LL_SUFFIX, " will");
		input = input.replaceAll(APOSTROPHE, "");
		return input;
	}

	public String removeEmojis(String input)
	{
		return input.replaceAll(EMOJI, " ");
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
	
	public static boolean isToken(final CharSequence input)
	{
		if(input == null)
			return false;
		Pattern pattern = Pattern.compile(UNWANTED);
		return !pattern.matcher(input).find();
	}
}
