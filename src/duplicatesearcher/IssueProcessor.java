package duplicatesearcher;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.CommunicationException;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;

import duplicatesearcher.analysis.frequency.TermFrequencyCounter;
import duplicatesearcher.processing.Stemmer;
import duplicatesearcher.processing.SynonymFinder;
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
	private final SynonymFinder synonyms;
	private final Map<Token, Token> processedTokens = new HashMap<Token, Token>(120_000);

	public IssueProcessor(EnumSet<ProcessingFlags> flags) throws IOException, InterruptedException, ClassNotFoundException
	{
		this.flags = flags;
		this.stopListCommon = new StopList(new File("stoplists/long/ReqSimile.txt"));
		this.stopListGitHub = new StopList(new File("stoplists/github/github.txt"));
		this.spell = new SpellCorrector(new File("dictionary/noun.txt"));
		spell.addDictionary(new File("dictionary/sense.txt"));
		spell.addDictionary(new File("dictionary/verb.txt"));
		spell.addDictionary(new File("dictionary/adj.txt"));
		spell.addDictionary(new File("dictionary/adv.txt"));
		spell.addDictionary(new File("stoplists/github/github.txt"));
		spell.addDictionary(new File("stoplists/long/ReqSimile.txt"));
		this.synonyms = new SynonymFinder();
	}

	public IssueProcessor(final ProcessingFlags... flags) throws IOException, InterruptedException, ClassNotFoundException
	{
		this(EnumSet.copyOf(Arrays.asList(flags)));
	}
	
	public boolean hasFlag(ProcessingFlags flag)
	{
		return flags.contains(flag);
	}

	public StrippedIssue process(final Issue issue, final List<Comment> comments)
	{
		final String pullRequestError = "Pull requests should no longer exist in any data set. Remove this data set and download a new one.";
		assert issue.getPullRequest().getHtmlUrl() == null: pullRequestError;
		StrippedIssue strippedIssue = new StrippedIssue(issue, comments); 
		strippedIssue = processIssue(strippedIssue);
		strippedIssue.createFrequencyCounterForAll();
		return strippedIssue;
	}

	private StrippedIssue processIssue(StrippedIssue strippedIssue)
	{
		processFrequencyCounter(strippedIssue.getTitle());
		processFrequencyCounter(strippedIssue.getBody());
		processFrequencyCounter(strippedIssue.getComments());
		processFrequencyCounter(strippedIssue.getLabels());
		return strippedIssue;
	}

	private void processFrequencyCounter(TermFrequencyCounter counter)
	{
		Set<Token> iterationSet = new HashSet<Token>(counter.getTokens());
		for(Token input : iterationSet)
		{
			if(input == null)
				continue;
			Token output;
			
			if(processedTokens.containsKey(input))
			{
				output = processedTokens.get(input);
			}
			else
			{
				output = process(input);
				processedTokens.put(input, output);
			}
			
			if(output == null)
				counter.remove(input);
			else if(!input.equals(output))
				counter.change(input, output);
		}
	}

	public Token process(Token token)
	{
		for(ProcessingFlags flag : flags)
		{
			token = applyProcess(token, flag);
			if(token == null)
				return null;
		}

		return token;
	}


	private Token applyProcess(Token token, ProcessingFlags flag)
	{
		switch(flag)
		{
			//case PARSE_COMMENTS: issue.removeComments(); break;
			case SPELL_CORRECTION: return spell.process(token);
			case STOP_LIST_COMMON: return stopListCommon.process(token);
			case STOP_LIST_GITHUB: return stopListGitHub.process(token);
			case STOP_LIST_TEMPLATE_DYNAMIC: System.out.println("Not implemented"); break;
			case STOP_LIST_TEMPLATE_STATIC: System.out.println("Not implemented"); break;
			case SYNONYMS: return synonyms.process(token);
			case STEMMING: return stemmer.process(token);
			//case FILTER_BAD: issue.checkQuality(); break;
		}
		return token;
	}

}
