package duplicatesearcher.retrieval;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.junit.BeforeClass;
import org.junit.Test;

public class CommentRetrieverTest
{
	private final static GitHubClient CLIENT = new GitHubTestClient();
	private static Set<Issue> issues;

	@BeforeClass
	public static void setup()
	{
		IssueRetriever ret = new IssueRetriever(CLIENT, "golang", "go");
		issues = new HashSet<Issue>(2000);
		issues.addAll(ret.getOpenIssues(200));
		issues.addAll(ret.getClosedIssues(1800));
	}

	@Test
	public void testAll() throws IOException
	{
		RepositoryId repo = new RepositoryId("golang", "go");
		final CommentRetriever cr = new CommentRetriever(CLIENT, repo, issues);
		int foundIssuesWithComments = cr.findIssuesWithComments();
		assertTrue(foundIssuesWithComments > 0);
		int downloadedComments = cr.downloadComments();
		assertTrue(downloadedComments >= foundIssuesWithComments);
		
		Map<Issue, List<Comment>> comments = cr.getIssuesAndComments();		
		assertEquals(foundIssuesWithComments, comments.size());
		
		System.out.println(comments);
		System.out.println(CLIENT.getRemainingRequests());
	}

}
