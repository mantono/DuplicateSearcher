package research.experiment.datacollectiontools;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;

import duplicatesearcher.StrippedIssue;
import duplicatesearcher.retrieval.GitHubTestClient;
import duplicatesearcher.retrieval.IssueRetriever;

public class DownloadDataSets
{

	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		final GitHubClient client = new GitHubTestClient();
		final RepositoryId repoInfo = new RepositoryId(args[0], args[1]);
		
		IssueRetriever retriever = new IssueRetriever(client, repoInfo);
		Map<Issue, List<Comment>> issues = retriever.getIssues();
		
		System.out.println(issues.size());
		
		DatasetFileManager files = new DatasetFileManager(repoInfo, issues);
		files.save();
	}

}
