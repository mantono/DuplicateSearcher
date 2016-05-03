package duplicatesearcher.flags;

public enum IssueComponent implements Flag<Double>
{
	TITLE('t', "title", "Sets the weight for the title of an issue"),
	BODY('b', "body", "Sets the weight for the issue's body"),
	LABELS('l', "labels", "Sets the weight for an issues's labels"),
	CODE('d', "code", "Sets the weight for any code in the issue body or comments"),
	COMMENTS('c', "comments", "Sets the weights for the comments in the issue"),
	ALL('a', "all", "Set the weight for all found terms in the issue");

	private final char shortFlag;
	private final String longFlag, description;
	
	private IssueComponent(final char shortFlag, final String longFlag, final String description)
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

	@Override
	public Double defaultValue()
	{
		return Double.valueOf(1);
	}

	@Override
	public Double getMinimumValue()
	{
		return Double.valueOf(0); 
	}

	@Override
	public Double getMaximumValue()
	{
		return Double.MAX_VALUE;
	}

	@Override
	public Double parse(String arg)
	{
		return Double.parseDouble(arg);
	}
}
