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
import duplicatesearcher.analysis.IssueComponent;
import duplicatesearcher.analysis.frequency.TermFrequencyCounter;
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
	private final EnumSet<ProcessingFlags> flags;

	private final RepositoryId repo;
	private final StopList stopListCommon;
	private final StopList stopListGitHub;
	private final Stemmer stemmer = new Stemmer();
	private final SpellCorrector spell;
	private final SynonymFinder synonyms;
	private final Map<Token, Token> processedTokens = new HashMap<Token, Token>(120_000);
	private final SortedMap<LocalDateTime, StopList> issueTemplate;

	public IssueProcessor(RepositoryId repo, EnumSet<ProcessingFlags> flags) throws IOException, InterruptedException,
			ClassNotFoundException, URISyntaxException
	{
		this.flags = flags;
		this.repo = repo;
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
		if(hasFlag(ProcessingFlags.STOP_LIST_TEMPLATE_STATIC) || hasFlag(ProcessingFlags.STOP_LIST_TEMPLATE_DYNAMIC))
			this.issueTemplate = loadIssueTemplateStopList();
		else
			this.issueTemplate = null;
	}

	private SortedMap<LocalDateTime, StopList> loadIssueTemplateStopList() throws URISyntaxException
	{
		final String repoPath = repo.getOwner() + File.pathSeparator + repo.getName();
		final Path templatePath = Paths.get(new URI("issue_templates" + File.pathSeparator + repoPath));
		if(Files.exists(templatePath))
			return loadFiles(templatePath);
		FileDownloader downloader = new FileDownloader(repo);
		//return downloader.retrieve("ISSUE_TEMPLATE");
		return new TreeMap<LocalDateTime, StopList>();
	}

	private SortedMap<LocalDateTime, StopList> downloadTemplates() throws IOException
	{
		GitHubClient client = new ApiClient();
		SearchRepository search = new SearchRepository(repo.getOwner(), repo.getName());
		GitHubRequest request = new GitHubRequest();
		String requestUri = "https://api.github.com/search/code\\?q=ISSUE_TEMPLATE+in:path+repo:mantono/DuplicateSearcher";
		request.setUri(requestUri);
		GitHubResponse response = client.get(request);
		System.out.print(response.toString());
		// TODO Auto-generated method stub
		return null;
	}

	private SortedMap<LocalDateTime, StopList> loadFiles(Path templatePath)
	{
		final File issueTemplateFile = new File("issue_templates/" + repo.getOwner() + "_" + repo.getName() +  ".md");
		// TODO Auto-generated method stub
		return null;
	}

	public IssueProcessor(final RepositoryId repo, final ProcessingFlags... flags) throws IOException, InterruptedException,
			ClassNotFoundException, URISyntaxException
	{
		this(repo, EnumSet.copyOf(Arrays.asList(flags)));
	}

	public boolean hasFlag(ProcessingFlags flag)
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
			processFrequencyCounter(strippedIssue.getComponent(component));
		if(hasFlag(ProcessingFlags.FILTER_BAD))
			strippedIssue.checkQuality();			

		return strippedIssue;
	}

	private StopList getStopListForDate(Date dateCreated)
	{
		if(hasFlag(ProcessingFlags.STOP_LIST_TEMPLATE_STATIC))
		{
			if(stopIssueTemplate != null)
				return stopIssueTemplate;
			else
				return issueTemplates.get(issueTemplates.lastKey()); 
		}
		else if(hasFlag(ProcessingFlags.STOP_LIST_TEMPLATE_DYNAMIC))
		{
			final long seconds = dateCreated.getTime()/1000;
			final int nanoSeconds = (int) (dateCreated.getTime() % 1000) * 1000000;
			LocalDateTime date = LocalDateTime.ofEpochSecond(seconds, nanoSeconds, ZoneOffset.UTC);
			SortedMap<LocalDateTime, StopList> subMap = issueTemplates.headMap(date);
			if(subMap.isEmpty())
				return new StopList();
			return subMap.get(subMap.lastKey());
		}
		return null;
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
			case SPELL_CORRECTION: return spell.process(token);
			case STOP_LIST_COMMON: return stopListCommon.process(token);
			case STOP_LIST_GITHUB: return stopListGitHub.process(token);
			case STOP_LIST_TEMPLATE_STATIC: return stopIssueTemplate.process(token);
			case STOP_LIST_TEMPLATE_DYNAMIC: return stopIssueTemplate.process(token);
			case SYNONYMS: return synonyms.process(token);
			case STEMMING: return stemmer.process(token);
		}
		return token;
	}

}
