package duplicatesearcher;

import java.util.Set;

import duplicatesearcher.analysis.Duplicate;

public class DuplicateSearcher
{
	public final static int SPELL_CORRECTION = 1;
	public final static int STEMMING = 1 << 1;
	public final static int STOP_LIST_COMMON = 1 << 2;
	public final static int STOP_LIST_GITHUB = 1 << 3;
	public final static int STOP_LIST_TEMPLATE_STATIC = 1 << 4;
	public final static int STOP_LIST_TEMPLATE_DYNAMIC = 1 << 5;
	public final static int PARSE_COMMENTS = 1 << 6;
	public final static int SYNONYMS = 1 << 7;
	public final static int FILTER_BAD = 1 << 8;
	
	private final int flags;
	private final double threshold;
	
	public DuplicateSearcher(final double threshold, final int flags)
	{
		this.threshold = threshold;
		this.flags = flags;
	}
	
	public Set<Duplicate> search(Set<StrippedIssue> issues)
	{
		if(run(SPELL_CORRECTION))
			System.out.println(SPELL_CORRECTION);
		if(run(STEMMING))
			System.out.println(STEMMING);
		if(run(STOP_LIST_COMMON))
			System.out.println(STOP_LIST_COMMON);
		if(run(STOP_LIST_GITHUB))
			System.out.println(STOP_LIST_GITHUB);
		if(run(STOP_LIST_TEMPLATE_DYNAMIC))
			System.out.println(STOP_LIST_TEMPLATE_DYNAMIC);
		if(run(STOP_LIST_TEMPLATE_STATIC))
			System.out.println(STOP_LIST_TEMPLATE_STATIC);
		if(run(SYNONYMS))
			System.out.println(SYNONYMS);
		if(run(FILTER_BAD))
			System.out.println(FILTER_BAD);
		
		return null;
	}

	private boolean run(int flag)
	{
		return (flags & flag) > 0;
	}
}
