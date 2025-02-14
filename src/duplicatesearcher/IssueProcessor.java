package duplicatesearcher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;

import duplicatesearcher.analysis.frequency.TermFrequencyCounter;
import duplicatesearcher.flags.IssueComponent;
import duplicatesearcher.flags.ProcessingFlag;
import duplicatesearcher.processing.Stemmer;
import duplicatesearcher.processing.SynonymFinder;
import duplicatesearcher.processing.spellcorrecting.SpellCorrector;
import duplicatesearcher.processing.stoplists.StopList;
import duplicatesearcher.processing.stoplists.TemplateLoader;

/**
 * IssueProcessor controls and interacts with all other major components used
 * for parsing and manipulating issues.
 * 
 */
public class IssueProcessor
{
	private final EnumSet<ProcessingFlag> flags;

	private final RepositoryId repo;
	private final StopList stopListCommon, stopListGitHub;
	private StopList stopIssueTemplate;
	private final Stemmer stemmer = new Stemmer();
	private final SpellCorrector spell;
	private final SynonymFinder synonyms;
	private final Map<Token, Token> processedTokens = new HashMap<Token, Token>(120_000);
	private final SortedMap<LocalDateTime, StopList> issueTemplates;

	public IssueProcessor(RepositoryId repo, EnumSet<ProcessingFlag> flags)
			throws IOException, InterruptedException, ClassNotFoundException, URISyntaxException
	{
		this.flags = flags;
		this.repo = repo;

		this.stopListCommon = new StopList(new File("stoplists/long/ReqSimile.txt"));
		this.stopListGitHub = new StopList(new File("stoplists/github/github.txt"));
		TemplateLoader loader = new TemplateLoader(repo);
		this.issueTemplates = loader.retrieveStopList();

		if(hasFlag(ProcessingFlag.SPELL_CORRECTION))
		{
			this.spell = new SpellCorrector(new File("dictionary/words.txt"));
			spell.addDictionary(new File("dictionary/words2.txt"));
			spell.addDictionary(new File("stoplists/github/github.txt"));
			spell.addDictionary(new File("stoplists/long/ReqSimile.txt"));
		}
		else
			this.spell = null;

		this.synonyms = new SynonymFinder();
	}

	public IssueProcessor(final RepositoryId repo, final ProcessingFlag... flags)
			throws IOException, InterruptedException, ClassNotFoundException, URISyntaxException
	{
		this(repo, EnumSet.copyOf(Arrays.asList(flags)));
	}

	public boolean hasFlag(ProcessingFlag flag)
	{
		return flags.contains(flag);
	}

	public StrippedIssue process(final Issue issue, final List<Comment> comments)
	{
		final String pullRequestError = "Pull requests should no longer exist in any data set. Remove this data set and download a new one.";
		assert issue.getPullRequest().getHtmlUrl() == null : pullRequestError;
		StrippedIssue strippedIssue = new StrippedIssue(issue, comments);
		strippedIssue = processIssue(strippedIssue);
		return strippedIssue;
	}

	private StrippedIssue processIssue(StrippedIssue strippedIssue)
	{
		stopIssueTemplate = getStopListForDate(strippedIssue.getDateCreated());
		for(IssueComponent component : IssueComponent.values())
		{
			TermFrequencyCounter componentCounter = strippedIssue.getComponent(component);
			processFrequencyCounter(componentCounter);
			componentCounter.add(new Token("token123456789"));
		}

		if(hasFlag(ProcessingFlag.FILTER_BAD))
			strippedIssue.checkQuality();

		return strippedIssue;
	}

	private StopList getStopListForDate(Date dateCreated)
	{
		final long seconds = dateCreated.getTime() / 1000;
		final int nanoSeconds = (int) (dateCreated.getTime() % 1000) * 1000000;
		LocalDateTime date = LocalDateTime.ofEpochSecond(seconds, nanoSeconds, ZoneOffset.UTC);
		SortedMap<LocalDateTime, StopList> subMap = issueTemplates.headMap(date);
		if(subMap.isEmpty())
			return new StopList();
		return subMap.get(subMap.lastKey());
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
		for(ProcessingFlag flag : flags)
		{
			token = applyProcess(token, flag);
			if(token == null)
				return null;
		}

		return token;
	}

	private Token applyProcess(Token token, ProcessingFlag flag)
	{
		switch (flag)
		{
			case SPELL_CORRECTION: return spell.process(token);
			case STOP_LIST:	return stopList(token);
			case SYNONYMS: return synonyms.process(token);
			case STEMMING: return stemmer.process(token);
		}
		return token;
	}

	private Token stopList(Token token)
	{
		token = stopListCommon.process(token);
		token = stopListGitHub.process(token);
		token = stopIssueTemplate.process(token);
		return token;
	}

}
