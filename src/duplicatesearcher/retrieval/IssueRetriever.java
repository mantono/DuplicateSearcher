package duplicatesearcher.retrieval;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.IssueService;

public class IssueRetriever extends GitHubTask implements IssueFetcher
{
	private final IssueService service;
	private final Map<String, String> filter = new HashMap<String, String>();
	private final RepositoryId repo;
	
	public IssueRetriever(final GitHubClient client, final RepositoryId repo)
	{
		super(client);
		this.service = new IssueService(client);
		this.repo = repo;
	}

	@Override
	public Collection<Issue> getOpenIssues(int amount)
	{
		return getIssues(IssueService.STATE_OPEN, amount);
	}

	@Override
	public Collection<Issue> getOpenIssues()
	{
		return getOpenIssues(Integer.MAX_VALUE);
	}

	@Override
	public Collection<Issue> getClosedIssues(int amount)
	{
		return getIssues(IssueService.STATE_CLOSED, amount);
	}

	@Override
	public Collection<Issue> getClosedIssues()
	{
		return getClosedIssues(Integer.MAX_VALUE);
	}
	
	private Collection<Issue> getIssues(final String state, final int amount)
	{
		filter.put("state", state);
		
		List<Issue> issues;
		if(amount > 20_000)
			issues = new ArrayList<Issue>(1000);
		else
			issues = new ArrayList<Issue>(amount);
		
		final PageIterator<Issue> issuePages = service.pageIssues(repo.getOwner(), repo.getName(), filter);
		while(issues.size() < amount && issuePages.hasNext())
		{
			checkQuota();
			printProgress("downloading issues", issues.size(), amount);
			issues.addAll(issuePages.next());
		}
		
		while(issues.size() > amount)
			issues.remove(issues.size()-1);
		
		return issues;
	}
}
