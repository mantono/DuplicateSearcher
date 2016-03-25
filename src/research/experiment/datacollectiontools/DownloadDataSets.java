package research.experiment.datacollectiontools;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;

import duplicatesearcher.retrieval.CommentRetriever;
import duplicatesearcher.retrieval.GitHubTestClient;
import duplicatesearcher.retrieval.IssueRetriever;

public class DownloadDataSets
{

	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		final GitHubClient client = new GitHubTestClient();
		final RepositoryId repoInfo = new RepositoryId(args[0], args[1]);
		
		IssueRetriever retriever = new IssueRetriever(client, repoInfo);
		Collection<Issue> open = retriever.getOpenIssues();
		Collection<Issue> closed = retriever.getClosedIssues();
		
		Set<Issue> allIssues = new HashSet<Issue>(closed);
		allIssues.addAll(open);
		System.out.println(allIssues.size());
		
		CommentRetriever commentRetriever = new CommentRetriever(client, repoInfo, allIssues);
		commentRetriever.findIssuesWithComments();
		commentRetriever.downloadComments();
		Map<Issue, List<Comment>> issuesAndComments = commentRetriever.getIssuesAndComments();
		
		DatasetFileManager files = new DatasetFileManager(repoInfo, issuesAndComments);
		files.save();
	}

}
