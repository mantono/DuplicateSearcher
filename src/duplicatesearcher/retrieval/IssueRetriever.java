package duplicatesearcher.retrieval;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.IssueService;

public class IssueRetriever implements IssueFetcher
{
	private final GitHubClient client;
	private final IssueService service;
	private final Map<String, String> filter = new HashMap<String, String>();
	private final String repoOwner;
	private final String repoName;
	
	public IssueRetriever(final GitHubClient client, final String repoOwner, final String repoName)
	{
		this.client = client;
		this.service = new IssueService(client);
		this.repoOwner = repoOwner;
		this.repoName = repoName;
	}

	@Override
	public Collection<Issue> getOpenIssues(int amount) throws IOException
	{
		filter.put("state", IssueService.STATE_OPEN);
		List<Issue> issues = new ArrayList<Issue>(amount);
		final PageIterator<Issue> issuePages = service.pageIssues(repoOwner, repoName, filter);
		while(issues.size() < amount && issuePages.hasNext())
		{
			checkQuota();
			issues.addAll(issuePages.next());
		}
		
		while(issues.size() > amount)
			issues.remove(issues.size()-1);
		
		return issues;
	}

	@Override
	public Set<Issue> getOpenIssues()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Issue> getClosedIssues(int amount)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Issue> getClosedIssues()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	private void checkQuota()
	{
		final int remainingRequests = client.getRemainingRequests(); 
		if(remainingRequests < 1 && remainingRequests != -1)
			throw new IllegalStateException("Request quota has been reached, cannot make a request to the API at the moment.");
	}

}
