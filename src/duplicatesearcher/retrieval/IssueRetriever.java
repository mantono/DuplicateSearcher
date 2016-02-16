package duplicatesearcher.retrieval;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;

public class IssueRetriever
{
	private final Set<Issue> openIssues = new HashSet<Issue>(40);
	private final Set<Issue> closedIssues = new HashSet<Issue>(30);
	private final Map<String, String> filter = new HashMap<String, String>();
	private final GitHubClient client = new GitHubClient();
	private final IssueService service;
	
	public IssueRetriever(final String token) throws IOException
	{
		client.setOAuth2Token(token);
		service = new IssueService(client);
		service.getIssues();
	}
	
	public IssueRetriever(final String username, final String password)
	{
		client.setCredentials(username, password);
		service = new IssueService(client);
		client.getRequestLimit();
	}
	
	public List<Issue> getOpenIssues(final String repoOwner, final String repo) throws IOException
	{
		checkQuota();
		filter.put("state", IssueService.STATE_OPEN);
		return service.getIssues(repoOwner, repo, filter);
	}
	
	public List<Issue> getClosedIssues(final String repoOwner, final String repo) throws IOException
	{
		checkQuota();
		filter.put("state", IssueService.STATE_CLOSED);
		return service.getIssues(repoOwner, repo, filter);
	}
	
	private void checkQuota()
	{
		final int remainingRequests = client.getRemainingRequests(); 
		if(remainingRequests < 1 && remainingRequests != -1)
			throw new IllegalStateException("Request quota has been reached, cannot make a request to the API at the moment.");
	}

	public static void main(String[] args) throws IOException
	{
		if(args.length == 0)
		{
			System.err.println("Missing argument for authentication token");
			System.exit(1);
		}
		
		final IssueRetriever ret = new IssueRetriever(args[0]);

		List<Issue> issues = ret.getOpenIssues("mantono", "BachelorThesis");
		System.out.println("\nOpen issues");
		ret.printIssues(issues);

		issues = ret.getClosedIssues("mantono", "BachelorThesis");
		System.out.println("\nClosed issues");
		ret.printIssues(issues);
	}
	
	private void printIssues(final Collection<Issue> issues)
	{
		for(Issue i : issues)
		{	
			System.out.println("\t" + i.getTitle());
			System.out.print("\t\tLabels: ");
			for(Label label : i.getLabels())
				System.out.print(label.getName() + " ");
			System.out.println("\n");
		}
	}
}
