package duplicatesearcher.retrieval;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Unit tests using network resources should not be run in batch tests.")
public class IssueRetrieverTest
{
	private final static String TOKEN = readTokenFromFile();
	private GitHubClient client = new GitHubClient();

	@Before
	public void setup()
	{
		client.setOAuth2Token(TOKEN);
	}

	private static String readTokenFromFile()
	{
		try
		{
			final FileReader fileReader = new FileReader(".token");
			final BufferedReader bfReader = new BufferedReader(fileReader);
			return bfReader.readLine();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		return "BadToken";
	}

	@Test
	public void testGetOpenIssuesInt() throws IOException
	{
		final int amount = 11;
		IssueFetcher retriever = new IssueRetriever(client, "golang", "go");
		Collection<Issue> result = retriever.getOpenIssues(amount);

		assertEquals(amount, result.size());
		final Issue firstIssue = result.iterator().next();
		assertEquals("open", firstIssue.getState());
		printRemainingRequests();
	}

	@Test
	public void testGetOpenIssues() throws IOException
	{
		IssueFetcher retriever = new IssueRetriever(client, "mantono", "DuplicateSearcher");
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
		IssueFetcher retriever = new IssueRetriever(client, "golang", "go");
		Collection<Issue> result = retriever.getClosedIssues(amount);

		assertEquals(amount, result.size());
		final Issue firstIssue = result.iterator().next();
		assertEquals("closed", firstIssue.getState());
		printRemainingRequests();
	}

	@Test
	public void testGetClosedIssues() throws IOException
	{
		IssueFetcher retriever = new IssueRetriever(client, "mantono", "DuplicateSearcher");
		Collection<Issue> result = retriever.getClosedIssues();

		assertTrue(0 < result.size());
		final Issue firstIssue = result.iterator().next();
		assertEquals("closed", firstIssue.getState());
		printRemainingRequests();
	}

	private void printRemainingRequests()
	{
		System.out.println(client.getRemainingRequests());
	}
}
