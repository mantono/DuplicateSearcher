package duplicatesearcher;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;

import duplicatesearcher.analysis.Duplicate;

/**
 * Issue is controls and interacts with all other major components used for
 * parsing and manipulating issues.
 *
 */
public class IssueProcessor
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

	public IssueProcessor(final int flags)
	{
		this.flags = flags;
	}

	public IssueProcessor(final int... flags)
	{
		this.flags = andFlags(flags);
	}

	private static int andFlags(int[] flagArray)
	{
		int flagMasked = 0;
		for(int flag : flagArray)
			flagMasked |= flag;
		return flagMasked;
	}
	
	public StrippedIssue process(final Issue issue, final List<Comment> comments)
	{
		return process(new StrippedIssue(issue, comments));
	}

	public StrippedIssue process(final StrippedIssue issue)
	{
		if(run(PARSE_COMMENTS))
			System.out.println(PARSE_COMMENTS);
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

		return issue;
	}

	private boolean run(int flag)
	{
		return (flags & flag) > 0;
	}
}
