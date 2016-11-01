package dsv2.processing;

public enum GithubFilter implements RegexFilter
{
	EMOJI("\\:\\S+\\:"),
	DOMAIN("\\b(?<!\\.)[\\w\\.]*\\w+\\.\\w{2,}[/\\w]*"),
	URL(protocol()+urlCharacters()+"+\\w{2,}[/\\w-\\.]*"),
	NUMBER("\\b(\\d+[.|,]?)\\b"),
	USERNAME("@\\w[\\w-]*\\b(?![-.])");
	//CODE("(`[^`]+`)|([\\ ]{4,}.+)");
	
	private final static String URL_LEGAL_CHARACTERS = "[\\w+\\-\\.]";
	private final static String PROTOCOL = "http[s]?://";
	private final String regex;
	
	private GithubFilter(String filter)
	{
		this.regex = filter;
	}

	@Override
	public String regex()
	{
		return regex;
	}

	private static String protocol()
	{
		return PROTOCOL;
	}

	private static String urlCharacters()
	{
		return URL_LEGAL_CHARACTERS;
	}
}
