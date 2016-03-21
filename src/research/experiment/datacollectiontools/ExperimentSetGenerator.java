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

import com.sun.xml.internal.ws.api.pipe.NextAction;

/**
 * Create a set on which our experiments can be conducted.
 */
public class ExperimentSetGenerator
{
	private final SecureRandom random = new SecureRandom();
	private final Map<Issue, List<Comment>> comments;
	private Set<Issue> closedIssues, openIssues, nonDuplicates, duplicates, generatedCorpus;

	public ExperimentSetGenerator(final Map<Issue, List<Comment>> issuesWithcomments)
	{
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

		generatedCorpus.addAll(getRandomElements(duplicates, duplicateAmount));
		generatedCorpus.addAll(getRandomElements(nonDuplicates, size - duplicateAmount));
	}

	private static Collection<? extends Issue> getRandomElements(Set<Issue> set, int amount)
	{
		List<Issue> listFromSet = new ArrayList<Issue>(set);
		Collections.shuffle(listFromSet);
		return listFromSet.subList(0, amount);
	}

	private Set<Issue> findNonDuplicates()
	{
		Set<Issue> filteredNonDuplicates = new HashSet<Issue>(comments.keySet());
		filteredNonDuplicates.removeAll(duplicates);
		return filteredNonDuplicates;
	}

	private Set<Issue> findKnownDuplicates()
	{
		duplicates = new HashSet<Issue>(comments.size() / 2);
		Iterator<Entry<Issue, List<Comment>>> iter = comments.entrySet().iterator();
		while (iter.hasNext())
		{
			final Entry<Issue, List<Comment>> entry = iter.next();
			List<Comment> issueComments = iter.next().getValue();
			if (isTaggedAsDuplicate(issueComments))
				duplicates.add(entry.getKey());
		}
		return duplicates;
	}

	// private Set<Integer> findIssuesWithComments()
	// {
	// final Set<Integer> hasComments = new
	// HashSet<Integer>(closedIssues.size());
	// for(Issue i : closedIssues)
	// if(i.getComments() > 0)
	// hasComments.add(i.getNumber());
	// }

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
