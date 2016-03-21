package duplicatesearcher.retrieval;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;

public class CommentRetriever extends GitHubTask
{
	private final IssueService issueService;
	private final IRepositoryIdProvider repo;
	private final Map<Issue, List<Comment>> comments;
	private final Set<Issue> issuesThatHasComments, allIssues;
	
	public CommentRetriever(final GitHubClient client, final IRepositoryIdProvider repo, final Set<Issue> issues)
	{
		super(client);
		this.repo = repo;
		this.issueService = new IssueService(client);
		this.comments = new HashMap<Issue, List<Comment>>(issues.size());
		this.issuesThatHasComments = new HashSet<Issue>(issues.size());
		this.allIssues = issues;
	}
	
	public int findIssuesWithComments()
	{
		issuesThatHasComments.clear();
		for(Issue i : allIssues)
			if(i.getComments() > 0)
				issuesThatHasComments.add(i);
		return issuesThatHasComments.size();
	}
	
	public int downloadComments() throws IOException
	{
		int i = 0;
		int iterations = 0;
		int end = issuesThatHasComments.size();
		for(Issue issue : issuesThatHasComments)
		{
			final List<Comment> commentsFromIssue = issueService.getComments(repo, issue.getNumber());
			comments.put(issue, commentsFromIssue);
			i += commentsFromIssue.size();
			iterations++;
			printProgress("downloading comments", iterations, end);
			if(iterations % 50 == 0)
				autoThrottle();
		}
		
		return i;
	}

	public Map<Issue, List<Comment>> getIssuesAndComments()
	{
		return comments;
	}
	
	
	
}
