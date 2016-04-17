package duplicatesearcher;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;

import duplicatesearcher.analysis.frequency.TermFrequencyCounter;
import duplicatesearcher.processing.Stemmer;
import duplicatesearcher.processing.spellcorrecting.SpellCorrector;
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
	private final SpellCorrector spell;

	public IssueProcessor(EnumSet<ProcessingFlags> flags) throws IOException
	{
		this.flags = flags;
		this.stopListCommon = new StopList(new File("stoplists/long/ReqSimile.txt"));
		this.stopListGitHub = new StopList(new File("stoplists/github/github.txt"));
		this.spell = new SpellCorrector(new File("dictionary/dict.txt"));
	}

	public IssueProcessor(final ProcessingFlags... flags) throws IOException
	{
		this(EnumSet.copyOf(Arrays.asList(flags)));
	}

	public StrippedIssue process(final Issue issue, final List<Comment> comments) throws IOException
	{
		final String pullRequestError = "Pull requests should no longer exist in any data set. Remove this data set and download a new one.";
		assert issue.getPullRequest().getHtmlUrl() == null: pullRequestError;
		StrippedIssue strippedIssue = new StrippedIssue(issue, comments); 
		strippedIssue = process(strippedIssue);
		strippedIssue.createFrequencyCounterForAll();
		return strippedIssue;
	}

	public StrippedIssue process(final StrippedIssue issue) throws IOException
	{
		if(!run(ProcessingFlags.PARSE_COMMENTS))
			issue.removeComments();
		if(run(ProcessingFlags.SPELL_CORRECTION))
			spellCorrection(issue);
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
		if(run(ProcessingFlags.STEMMING))
			stem(issue);
		if(run(ProcessingFlags.FILTER_BAD))
			System.out.println(ProcessingFlags.FILTER_BAD);

		return issue;
	}

	private void spellCorrection(StrippedIssue issue)
	{
		spell.process(issue.getTitle());
		spell.process(issue.getBody());
		spell.process(issue.getComments());
	}

	private void stem(StrippedIssue issue)
	{
		stem(issue.getTitle());
		stem(issue.getBody());
		stem(issue.getComments());
	}

	private void stem(TermFrequencyCounter counter)
	{
		Set<Token> issueTokensCopy = new HashSet<Token>(counter.getTokens());
		Iterator<Token> tokens = issueTokensCopy.iterator();
		while(tokens.hasNext())
		{
			final Token token = tokens.next();
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
		stopList.removeStopWords(issue.getComments().getTokens());
	}

	private boolean run(ProcessingFlags flag)
	{
		return flags.contains(flag);
	}
}
