package research.experiment.datacollectiontools;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;

import duplicatesearcher.retrieval.CommentRetriever;
import duplicatesearcher.retrieval.GitHubTestClient;
import duplicatesearcher.retrieval.IssueFetcher;
import duplicatesearcher.retrieval.IssueRetriever;

public class DownloadDataSets
{

	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		final GitHubClient client = new GitHubTestClient();
		final RepositoryId repoInfo = new RepositoryId("golang", "go");

//		Redan gjort!		
//		IssueFetcher retriever = new IssueRetriever(client, repoInfo);
//		
//		final Collection<Issue> resultOpen = retriever.getOpenIssues();
//		final DatasetFileManager saveOpen = new DatasetFileManager(repoInfo, resultOpen);
//		saveOpen.save("_open");
//		
//		final Collection<Issue> resultClosed = retriever.getClosedIssues();
//		final DatasetFileManager saveClosed = new DatasetFileManager(repoInfo, resultClosed);
//		saveClosed.save("_closed");
		
		final DatasetFileManager fileClosedIssue = new DatasetFileManager(repoInfo);
		fileClosedIssue.load("_closed");
		Set<Issue> closedIssues = new HashSet(fileClosedIssue.getDataset());
		CommentRetriever commentRetriever = new CommentRetriever(client, repoInfo, closedIssues);
		
		final DatasetFileManager
	}

}
