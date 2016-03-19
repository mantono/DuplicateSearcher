package duplicatesearcher.retrieval;

import java.io.IOException;
import java.util.Collection;
import org.eclipse.egit.github.core.Issue;

public interface IssueFetcher
{
	Collection<Issue> getOpenIssues(int amount) throws IOException;

	Collection<Issue> getOpenIssues() throws IOException;

	Collection<Issue> getClosedIssues(int amount) throws IOException;

	Collection<Issue> getClosedIssues() throws IOException;
}
