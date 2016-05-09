package duplicatesearcher.flags;

public enum ProcessingFlag implements BooleanFlag
{
	SPELL_CORRECTION('S', "spell", "Enable spell correction of tokens"),
	STOP_LIST_COMMON('C', "common", "Apply a stop word list for common English words"),
	STOP_LIST_GITHUB('G', "github", "Apply a stop word list for common words on GitHub"),
	STOP_LIST_TEMPLATE('T', "template", "Apply a dynamic stop word list based on the ISSUE_TEMPLATE when the issue was created"),
	SYNONYMS('Y', "synonyms", "Change uncommon words to more common ones that are synonyms"),
	STEMMING('E', "stemming", "Apply stemming on words"),
	FILTER_BAD('F', "filter", "Filter issues which does not contain enough data to be confidently analyzed");
	
	private final char shortFlag;
	private final String longFlag, description;
	
	private ProcessingFlag(final char shortFlag, final String longFlag, final String description)
	{
		this.shortFlag = shortFlag;
		this.longFlag = longFlag;
		this.description = description;
	}

	@Override
	public char getShortFlag()
	{
		return shortFlag;
	}

	@Override
	public String getLongFlag()
	{
		return longFlag;
	}

	@Override
	public String getDescription()
	{
		return description;
	}
}
