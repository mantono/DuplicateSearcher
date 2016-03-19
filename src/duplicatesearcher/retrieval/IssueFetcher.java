package duplicatesearcher.retrieval;

import java.util.Set;

import org.eclipse.egit.github.core.Issue;

public interface IssueFetcher
{
	Set<Issue> getOpenIssues(int amount);
	Set<Issue> getOpenIssues();
	Set<Issue> getClosedIssues(int amount);
	Set<Issue> getClosedIssues();
}
