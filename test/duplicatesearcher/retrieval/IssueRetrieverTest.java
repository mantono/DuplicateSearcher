package duplicatesearcher.retrieval;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.junit.Before;
import org.junit.Test;

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
		final int amount = 250;
		IssueFetcher retriever = new IssueRetriever(client, "golang", "go");
		Collection<Issue> result = retriever.getOpenIssues(amount);
		
		assertEquals(amount, result.size());
		printRemainingRequests();
	}

	private void printRemainingRequests()
	{
		System.out.println(client.getRemainingRequests());
	}

	@Test
	public void testGetOpenIssues()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetClosedIssuesInt()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetClosedIssues()
	{
		fail("Not yet implemented");
	}

}
