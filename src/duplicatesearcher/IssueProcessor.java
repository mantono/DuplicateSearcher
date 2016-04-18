package duplicatesearcher;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;

import duplicatesearcher.processing.Stemmer;
import duplicatesearcher.processing.TokenProcessor;
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
		spell.addDictionary(new File("dictionary/dict_extra1.txt"));
		spell.addDictionary(new File("dictionary/dict_extra2.txt"));
		spell.addDictionary(new File("stoplists/github/github.txt"));
		spell.addDictionary(new File("stoplists/long/ReqSimile.txt"));
	}

	public IssueProcessor(final ProcessingFlags... flags) throws IOException
	{
		this(EnumSet.copyOf(Arrays.asList(flags)));
	}

	public StrippedIssue process(final Issue issue, final List<Comment> comments)
	{
		final String pullRequestError = "Pull requests should no longer exist in any data set. Remove this data set and download a new one.";
		assert issue.getPullRequest().getHtmlUrl() == null: pullRequestError;
		StrippedIssue strippedIssue = new StrippedIssue(issue, comments); 
		strippedIssue = process(strippedIssue);
		strippedIssue.createFrequencyCounterForAll();
		return strippedIssue;
	}

	public StrippedIssue process(final StrippedIssue issue)
	{
		for(ProcessingFlags flag : flags)
			applyProcess(issue, flag);

		return issue;
	}

	private void applyProcess(StrippedIssue issue, ProcessingFlags flag)
	{
		switch(flag)
		{
			case PARSE_COMMENTS: issue.removeComments(); break;
			case SPELL_CORRECTION: apply(issue, spell); break;
			case STOP_LIST_COMMON: apply(issue, stopListCommon); break;
			case STOP_LIST_GITHUB: apply(issue, stopListGitHub); break;
			case STOP_LIST_TEMPLATE_DYNAMIC: System.out.println("Not implemented"); break;
			case STOP_LIST_TEMPLATE_STATIC: System.out.println("Not implemented"); break;
			case SYNONYMS: System.out.println("Not implemented"); break;
			case STEMMING: apply(issue, stemmer); break;
			case FILTER_BAD: System.out.println("Not implemented"); break;
		}
	}
	
	private void apply(StrippedIssue issue, TokenProcessor processor)
	{
		processor.process(issue.getTitle());
		processor.process(issue.getBody());
		processor.process(issue.getComments());
	}

}
