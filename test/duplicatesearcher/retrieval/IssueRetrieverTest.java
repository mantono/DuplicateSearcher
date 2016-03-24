package duplicatesearcher.retrieval;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.junit.Ignore;
import org.junit.Test;

import research.experiment.datacollectiontools.DatasetFileManager;

@Ignore("Unit tests using network resources should not be run in batch tests.")
public class IssueRetrieverTest
{
	private final static GitHubClient CLIENT = new GitHubTestClient();
	
	@Test
	public void testGetOpenIssuesInt() throws IOException
	{
		final int amount = 11;
		final RepositoryId repoInfo = new RepositoryId("golang", "go");
		IssueFetcher retriever = new IssueRetriever(CLIENT, repoInfo);
		Collection<Issue> result = retriever.getOpenIssues(amount);

		assertEquals(amount, result.size());
		final Issue firstIssue = result.iterator().next();
		assertEquals("open", firstIssue.getState());
		printRemainingRequests();
	}

	@Test
	public void testGetOpenIssues() throws IOException
	{
		final RepositoryId repoInfo = new RepositoryId("mantono", "DuplicateSearcher");
		IssueFetcher retriever = new IssueRetriever(CLIENT, repoInfo);
		Collection<Issue> result = retriever.getOpenIssues();

		assertTrue(0 < result.size());
		final Issue firstIssue = result.iterator().next();
		assertEquals("open", firstIssue.getState());
		printRemainingRequests();
	}

	@Test
	public void testGetClosedIssuesInt() throws IOException
	{
		final int amount = 11;
		final RepositoryId repoInfo = new RepositoryId("golang", "go");
		IssueFetcher retriever = new IssueRetriever(CLIENT, repoInfo);
		Collection<Issue> result = retriever.getClosedIssues(amount);

		assertEquals(amount, result.size());
		final Issue firstIssue = result.iterator().next();
		assertEquals("closed", firstIssue.getState());
		printRemainingRequests();
	}

	@Test
	public void testGetClosedIssues() throws IOException
	{
		final RepositoryId repoInfo = new RepositoryId("mantono", "DuplicateSearcher");
		IssueFetcher retriever = new IssueRetriever(CLIENT, repoInfo);
		Collection<Issue> result = retriever.getClosedIssues();

		assertTrue(0 < result.size());
		final Issue firstIssue = result.iterator().next();
		assertEquals("closed", firstIssue.getState());
		printRemainingRequests();
	}

	private void printRemainingRequests()
	{
		System.out.println(CLIENT.getRemainingRequests());
	}
}
