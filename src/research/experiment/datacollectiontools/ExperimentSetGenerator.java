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
import java.util.Set;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Issue;

/**
 * Create a set on which our experiments can be conducted.
 */
public class ExperimentSetGenerator
{
	private final SecureRandom random = new SecureRandom();
	private final RegexFinder duplicateParser;
	private final Map<Integer, List<Comment>> comments;
	private final Map<Integer, Issue> allIssues;
	private Set<Issue> closedIssues, openIssues, nonDuplicates, duplicates, generatedCorpus;

	public ExperimentSetGenerator(final IRepositoryIdProvider repo, final Set<Issue> issues, final Map<Integer, List<Comment>> issuesWithcomments)
	{
		this.duplicateParser = new RegexFinder(repo);
		this.allIssues = createMap(issues);
		this.comments = issuesWithcomments;
	}

	private Map<Integer, Issue> createMap(Set<Issue> issues)
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
		assert duplicates.size() + nonDuplicates.size() == comments.size() : "Size doesn't add upp";

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
			List<Comment> commentsForIssue = comments.get(issue.getNumber());
			final int master = findMaster(commentsForIssue);
			if(master != -1)
				masterIssues.add(allIssues.get(master));
		}
		
		return masterIssues;
	}

	private int findMaster(List<Comment> commentsForIssue)
	{
		for(Comment comment : commentsForIssue)
		{
			if(duplicateParser.commentContainsDupe(comment))
			{
				final int issueNumber = duplicateParser.getIssueNumber(comment);
				if(issueNumber != -1)
					return issueNumber;
			}
		}
		return -1;
	}

	private static Collection<? extends Issue> getRandomElements(Set<Issue> set, int amount)
	{
		List<Issue> listFromSet = new ArrayList<Issue>(set);
		Collections.shuffle(listFromSet);
		return listFromSet.subList(0, amount);
	}

	private Set<Issue> findNonDuplicates()
	{
		Set<Issue> filteredNonDuplicates = new HashSet<Issue>(allIssues.values());
		filteredNonDuplicates.removeAll(duplicates);
		return filteredNonDuplicates;
	}

	private Set<Issue> findKnownDuplicates()
	{
		duplicates = new HashSet<Issue>(comments.size() / 2);
		Iterator<Issue> iter = allIssues.values().iterator();
		while (iter.hasNext())
		{
			final Issue issue = iter.next();
			List<Comment> issueComments = comments.get(issue.getNumber());
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
