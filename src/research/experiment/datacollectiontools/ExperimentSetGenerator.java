package research.experiment.datacollectiontools;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;

/**
 * Create a set on which our experiments can be conducted.
 */
public class ExperimentSetGenerator
{
	private final SecureRandom random = new SecureRandom();
	private final Map<Integer, List<Comment>> comments;
	private Set<Issue> closedIssues, openIssues, allIssues, nonDuplicates, duplicates, generatedCorpus;

	public ExperimentSetGenerator(final Set<Issue> issues, final Map<Integer, List<Comment>> issuesWithcomments)
	{
		this.allIssues = issues;
		this.comments = issuesWithcomments;
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
			// TODO some regexp magic here!
		}
		
		return masterIssues;
	}

	private static Collection<? extends Issue> getRandomElements(Set<Issue> set, int amount)
	{
		List<Issue> listFromSet = new ArrayList<Issue>(set);
		Collections.shuffle(listFromSet);
		return listFromSet.subList(0, amount);
	}

	private Set<Issue> findNonDuplicates()
	{
		Set<Issue> filteredNonDuplicates = new HashSet<Issue>(allIssues);
		filteredNonDuplicates.removeAll(duplicates);
		return filteredNonDuplicates;
	}

	private Set<Issue> findKnownDuplicates()
	{
		duplicates = new HashSet<Issue>(comments.size() / 2);
		Iterator<Issue> iter = allIssues.iterator();
		while (iter.hasNext())
		{
			final Issue issue = iter.next();
			List<Comment> issueComments = comments.get(issue.getNumber());
			if (isTaggedAsDuplicate(issueComments))
				duplicates.add(issue);
		}
		return duplicates;
	}

	private boolean isTaggedAsDuplicate(List<Comment> issueComments)
	{
		for (Comment c : issueComments)
		{
			if (c.getBody().contains("dupe") || c.getBody().contains("duplicate"))
				if (c.getBodyHtml().contains("https://github.com/"))
					return true;
			// TODO improve!
			// https://github.com/$USER/$REPO/issues/INTEGER
		}

		return false;
	}

	public Set<Issue> getGeneratedCorpus()
	{
		return generatedCorpus;
	}

}
