package research.experiment.datacollectiontools;

import java.security.SecureRandom;
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
	private final Set<Issue> closedIssues, openIssues;
	private final Map<Issue, List<Comment>> comments;
	private Set<Issue> nonDuplicates, duplicates, generatedCorpus;
	
	public ExperimentSetGenerator(final Set<Issue> openIssues, final Set<Issue> closedIssues, final Map<Issue, List<Comment>> comments)
	{
		this.openIssues = openIssues;
		this.closedIssues = closedIssues;
		this.comments = comments;
	}

	public void generateSet(final int size, final float duplicateRatio)
	{
		duplicates = findKnownDuplicates();
		nonDuplicates = findNonDuplicates();
	}
	
	private Set<Issue> findNonDuplicates()
	{
		// TODO Auto-generated method stub
		return null;
	}

	private Set<Issue> findKnownDuplicates()
	{
		Iterator<Entry<Issue, List<Comment>>> iter = comments.entrySet().iterator();
		while(iter.hasNext())
		{
			List<Comment> issueComments = iter.next().getValue();
			if(isTaggedAsDuplicate(iter.next().getValue()))
				duplicates
		}
	}

//	private Set<Integer> findIssuesWithComments()
//	{
//		final Set<Integer> hasComments = new HashSet<Integer>(closedIssues.size());
//		for(Issue i : closedIssues)
//			if(i.getComments() > 0)
//				hasComments.add(i.getNumber());
//	}

	public Set<Issue> getGeneratedCorpus()
	{
		return generatedCorpus;
	}
	
	
}
