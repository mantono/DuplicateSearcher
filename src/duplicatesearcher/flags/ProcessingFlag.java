package duplicatesearcher.flags;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

public enum ProcessingFlag implements BooleanFlag
{
	SPELL_CORRECTION('S', "spell", "Enable spell correction of tokens", 1),
	STOP_LIST('T', "stop", "Apply a stop word list for common English words or words common on GitHub and its ISSUE_TEMPLATE file", 1<<1),
	SYNONYMS('Y', "synonyms", "Change uncommon words to more common ones that are synonyms", 1<<2),
	STEMMING('E', "stemming", "Apply stemming on words", 1<<3),
	FILTER_BAD('F', "filter", "Filter issues which does not contain enough data to be confidently analyzed", 1<<4);
	
	private final char shortFlag;
	private final String longFlag, description;
	private final int bitFlag;
	
	private ProcessingFlag(final char shortFlag, final String longFlag, final String description, final int bitFlag)
	{
		this.shortFlag = shortFlag;
		this.longFlag = longFlag;
		this.description = description;
		this.bitFlag = bitFlag;
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
	
	public static EnumSet<ProcessingFlag> setOf(final int bitFlags)
	{
		List<ProcessingFlag> flags = new LinkedList<ProcessingFlag>();
		for(ProcessingFlag flag : values())
		{
			if((flag.bitFlag & bitFlags) != 0)
				flags.add(flag);
		}
		return EnumSet.copyOf(flags);
	}
}
