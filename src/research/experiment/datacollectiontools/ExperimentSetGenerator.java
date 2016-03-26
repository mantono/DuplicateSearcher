package research.experiment.datacollectiontools;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.RepositoryId;

/**
 * Create a set on which our experiments can be conducted.
 */
public class ExperimentSetGenerator
{
	private final SecureRandom random = new SecureRandom();
	private final RegexFinder duplicateParser;
	private final Map<Issue, List<Comment>> allIssues;
	private final Map<Integer, Issue> idIssueMap;
	private Set<Issue> closedIssues, openIssues, nonDuplicates, duplicates, generatedCorpus;

	public ExperimentSetGenerator(final RepositoryId repo, final Map<Issue, List<Comment>> issuesWithcomments)
	{
		this.duplicateParser = new RegexFinder(repo);
		this.allIssues = filterPullRequests(issuesWithcomments);
		this.idIssueMap = createIdIssueMap(allIssues.keySet());
	}

	private Map<Issue, List<Comment>> filterPullRequests(Map<Issue, List<Comment>> input)
	{
		Map<Issue, List<Comment>> filteredMap = new HashMap<Issue, List<Comment>>(input);
		System.out.println("Map size before filtering pull requests: " + filteredMap.size()); 
		
		final Iterator<Entry<Issue, List<Comment>>> iter = filteredMap.entrySet().iterator();
		while(iter.hasNext())
		{
			final Entry<Issue, List<Comment>> entry = iter.next();
			final PullRequest pullRequest = entry.getKey().getPullRequest();
			if(pullRequest != null && pullRequest.getId() != 0)
				iter.remove();
		}
		System.out.println("Map size after filtering pull requests: " + filteredMap.size());
		
		return filteredMap;
	}

	private Map<Integer, Issue> createIdIssueMap(Set<Issue> issues)
	{
		Map<Integer, Issue> issueMap = new HashMap<Integer, Issue>(issues.size());
		for(Issue issue : issues)
			issueMap.put(issue.getNumber(), issue);
		return issueMap;
	}

	public void generateSet(final int size, final float duplicateRatio)
	{
		if (duplicateRatio > 1)
			throw new IllegalArgumentException();
		duplicates = findKnownDuplicates();
		nonDuplicates = findNonDuplicates();
		assert duplicates.size() + nonDuplicates.size() == allIssues.size() : "Size doesn't add upp";

		generatedCorpus = new HashSet<Issue>((int) (size * 1.1));
		final int duplicateAmount = (int) (size * duplicateRatio);

		generatedCorpus.addAll(getRandomElements(duplicates, duplicateAmount/2));
		generatedCorpus.addAll(getMasterIssues());
		generatedCorpus.addAll(getRandomElements(nonDuplicates, size - duplicateAmount));
	}
	
	public void generateRandomIntervalSet(final int size, final float minDuplicateRatio, final float maxDuplicateRatio)
	{
		float ratio = 0;
		while(ratio < minDuplicateRatio)
			ratio = random.nextFloat()*maxDuplicateRatio;
		generateSet(size, ratio);
	}

	private Collection<? extends Issue> getMasterIssues()
	{
		Set<Issue> masterIssues = new HashSet<Issue>();
		for(Issue issue : duplicates)
		{
			List<Comment> commentsForIssue = allIssues.get(issue.getNumber());
			final int master = findMaster(commentsForIssue);
			if(master != -1)
				masterIssues.add(idIssueMap.get(master));
		}
		
		return masterIssues;
	}

	private int findMaster(List<Comment> commentsForIssue)
	{
		for(Comment comment : commentsForIssue)
		{
			if(duplicateParser.commentContainsDupe(comment))
			{
				final List<Integer> issueNumbers = duplicateParser.getIssueNumber(comment);
				if(issueNumbers.size() == 1)
					return issueNumbers.get(0);
				if(issueNumbers.size() > 1)
					return findOldestIssue(issueNumbers);
			}
		}
		return -1;
	}

	private int findOldestIssue(List<Integer> issueNumbers)
	{
		return Collections.min(issueNumbers);
	}

	private static Collection<? extends Issue> getRandomElements(Set<Issue> set, int amount)
	{
		if(set.isEmpty())
			throw new IllegalArgumentException("Given set is empty.");
		List<Issue> listFromSet = new ArrayList<Issue>(set);
		Collections.shuffle(listFromSet);
		return listFromSet.subList(0, amount);
	}

	private Set<Issue> findNonDuplicates()
	{
		Set<Issue> filteredNonDuplicates = new HashSet<Issue>(idIssueMap.values());
		filteredNonDuplicates.removeAll(duplicates);
		return filteredNonDuplicates;
	}

	private Set<Issue> findKnownDuplicates()
	{
		duplicates = new HashSet<Issue>(allIssues.size() / 2);
		Iterator<Issue> iter = idIssueMap.values().iterator();
		while (iter.hasNext())
		{
			final Issue issue = iter.next();
			List<Comment> issueComments = allIssues.get(issue.getNumber());
			if (duplicateParser.isTaggedAsDuplicate(issueComments) || isLabeledAsDuplicates(issue))
				duplicates.add(issue);
		}
		return duplicates;
	}

	private boolean isLabeledAsDuplicates(Issue issue)
	{
		// TODO fix me
		return false;
	}

	public Set<Issue> getGeneratedCorpus()
	{
		return generatedCorpus;
	}

}
