package duplicatesearcher;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;

import duplicatesearcher.analysis.frequency.TermFrequencyCounter;
import duplicatesearcher.processing.Stemmer;
import duplicatesearcher.processing.stoplists.StopList;

/**
 * IssueProcessor controls and interacts with all other major components used for
 * parsing and manipulating issues.
 *
 */
public class IssueProcessor
{
	private final EnumSet<ProcessingFlags> flags;
	
	private final StopList stopListCommon;
	private final StopList stopListGitHub;
	private final Stemmer stemmer = new Stemmer();

	public IssueProcessor(EnumSet<ProcessingFlags> flags) throws IOException
	{
		this.flags = flags;
		this.stopListCommon = new StopList(new File("stoplists/long/ReqSimile.txt"));
		this.stopListGitHub = new StopList(new File("stoplists/github/github.txt"));
	}

	public IssueProcessor(final ProcessingFlags... flags) throws IOException
	{
		this(EnumSet.copyOf(Arrays.asList(flags)));
	}

	public StrippedIssue process(final Issue issue, final List<Comment> comments) throws IOException
	{
		final String pullRequestError = "Pull requests should no longer exist in any data set. Remove this data set and download a new one.";
		assert issue.getPullRequest().getHtmlUrl() == null: pullRequestError;
		return process(new StrippedIssue(issue, comments));
	}

	public StrippedIssue process(final StrippedIssue issue) throws IOException
	{
		if(!run(ProcessingFlags.PARSE_COMMENTS))
			issue.removeComments();
		if(run(ProcessingFlags.SPELL_CORRECTION))
			System.out.println(ProcessingFlags.SPELL_CORRECTION);
		if(run(ProcessingFlags.STEMMING))
			stem(issue);
		if(run(ProcessingFlags.STOP_LIST_COMMON))
			removeStopWords(stopListCommon, issue);
		if(run(ProcessingFlags.STOP_LIST_GITHUB))
			removeStopWords(stopListGitHub, issue);
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

	private void stem(StrippedIssue issue)
	{
		stem(issue.getTitle());
		stem(issue.getBody());
		if(run(ProcessingFlags.PARSE_COMMENTS))
			stem(issue.getComments());
		stem(issue.getAll());
	}

	private void stem(TermFrequencyCounter counter)
	{
		for(Token token : counter.getTokens())
		{
			stemmer.setCurrentToken(token);
			stemmer.stem();
			final Token stemmedToken = stemmer.getCurrentToken();
			if(!token.equals(stemmedToken))
				counter.change(token, stemmedToken);
		}
	}

	private void removeStopWords(StopList stopList, StrippedIssue issue) throws IOException
	{
		stopList.removeStopWords(issue.getTitle().getTokens());
		stopList.removeStopWords(issue.getBody().getTokens());
		if(run(ProcessingFlags.PARSE_COMMENTS))
			stopList.removeStopWords(issue.getComments().getTokens());
		stopList.removeStopWords(issue.getAll().getTokens());
	}

	private boolean run(ProcessingFlags flag)
	{
		return flags.contains(flag);
	}
}
