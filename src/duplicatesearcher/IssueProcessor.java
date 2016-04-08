package duplicatesearcher;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;

/**
 * Issue is controls and interacts with all other major components used for
 * parsing and manipulating issues.
 *
 */
public class IssueProcessor
{
	private final EnumSet<ProcessingFlags> flags;

	public IssueProcessor(EnumSet<ProcessingFlags> flags)
	{
		this.flags = flags;
	}

	public IssueProcessor(final ProcessingFlags... flags)
	{
		this.flags = EnumSet.copyOf(Arrays.asList(flags));
	}
	
	public StrippedIssue process(final Issue issue, final List<Comment> comments)
	{
		return process(new StrippedIssue(issue, comments));
	}

	public StrippedIssue process(final StrippedIssue issue)
	{
		if(!run(ProcessingFlags.PARSE_COMMENTS))
			issue.removeComments();
		if(run(ProcessingFlags.SPELL_CORRECTION))
			System.out.println(ProcessingFlags.SPELL_CORRECTION);
		if(run(ProcessingFlags.STEMMING))
			System.out.println(ProcessingFlags.STEMMING);
		if(run(ProcessingFlags.STOP_LIST_COMMON))
			System.out.println(ProcessingFlags.STOP_LIST_COMMON);
		if(run(ProcessingFlags.STOP_LIST_GITHUB))
			System.out.println(ProcessingFlags.STOP_LIST_GITHUB);
		if(run(ProcessingFlags.STOP_LIST_TEMPLATE_DYNAMIC))
			System.out.println(ProcessingFlags.STOP_LIST_TEMPLATE_DYNAMIC);
		if(run(ProcessingFlags.STOP_LIST_TEMPLATE_STATIC))
			System.out.println(ProcessingFlags.STOP_LIST_TEMPLATE_STATIC);
		if(run(ProcessingFlags.SYNONYMS))
			System.out.println(ProcessingFlags.SYNONYMS);
		if(run(ProcessingFlags.FILTER_BAD))
			System.out.println(ProcessingFlags.FILTER_BAD);

		return issue;
	}

	private boolean run(ProcessingFlags flag)
	{
		return flags.contains(flag);
	}
}
