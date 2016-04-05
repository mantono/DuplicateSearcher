package duplicatesearcher.retrieval;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.IssueService;

public class IssueRetriever extends GitHubTask
{
	private final IssueService service;
	private final Map<String, String> filter = new HashMap<String, String>();
	private final RepositoryId repo;
	private final int issueCount;
	private int iterations = 0;
	
	public IssueRetriever(final GitHubClient client, final RepositoryId repo)
	{
		super(client);
		this.service = new IssueService(client);
		this.repo = repo;
		this.issueCount = getAmountOfIssues();
	}
	
	private int getAmountOfIssues()
	{
		filter.put("state", "all");
		filter.put("sort", "created");
		filter.put("direction", "desc");
		
		final PageIterator<Issue> issuePages = service.pageIssues(repo.getOwner(), repo.getName(), filter);
		Collection<Issue> issues = issuePages.next();
		return getNumberOfHighestId(issues);
	}

	private int getNumberOfHighestId(Collection<Issue> issues)
	{
		int id = -1;
		
		for(Issue issue: issues)
			if(issue.getNumber() > id)
				id = issue.getNumber();
		
		return id;
	}

	public Map<Issue, List<Comment>> getIssues() throws IOException
	{
		System.out.println(repo + " contains " + issueCount + " issues and pull requests");
		filter.put("direction", "asc");
		Map<Issue, List<Comment>> issues = new HashMap<Issue, List<Comment>>(issueCount);
		List<Issue> issuesToProcess = new ArrayList<Issue>(30);
		
		int iterations = 0;
		final PageIterator<Issue> issuePages = service.pageIssues(repo.getOwner(), repo.getName(), filter);
		while(issuePages.hasNext())
		{
			printProgress(issues.size(), issueCount);
			printStats();
			
			issuesToProcess.addAll(issuePages.next());
			removePullRequests(issuesToProcess);
			issues.putAll(downloadComments(issuesToProcess));
			issuesToProcess.clear();
			
			sleep();
		}
		
		
		return issues;
	}
	
	private void sleep()
	{
		final int remainingRequests = getClient().getRemainingRequests();
		if(remainingRequests < 10 && remainingRequests != -1)
			forcedSleep();
		if(iterations++ % 50 == 0)
			autoThrottle();
		threadSleep();
	}

	private Map<Issue, List<Comment>> downloadComments(List<Issue> issuesToProcess) throws IOException
	{
		System.out.println("");
		
		final Map<Issue, List<Comment>> issues = new HashMap<Issue, List<Comment>>(150);
		for(Issue issue : issuesToProcess)
		{
			final List<Comment> commentsFromIssue = service.getComments(repo, issue.getNumber());
			if(commentsFromIssue == null)
				issues.put(issue, new ArrayList<Comment>(0));
			else
				issues.put(issue, commentsFromIssue);
			
			final int remainingRequests = getClient().getRemainingRequests();
			
			System.out.print(".");
			if(iterations % 10 == 0)
				System.out.print(" ");
			sleep();

		}
		
		return issues;
	}

	private void removePullRequests(List<Issue> issuesToProcess)
	{
		Iterator<Issue> iter = issuesToProcess.iterator();
		while(iter.hasNext())
		{
			final Issue issue = iter.next();
			if(issue.getPullRequest().getHtmlUrl() != null)
				iter.remove();
		}
	}
}
