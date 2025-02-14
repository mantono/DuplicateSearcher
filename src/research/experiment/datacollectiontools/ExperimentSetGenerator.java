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
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.RepositoryId;

import duplicatesearcher.StrippedIssue;
import duplicatesearcher.analysis.Duplicate;

/**
 * Create a set on which our experiments can be conducted.
 */
public class ExperimentSetGenerator
{
	private final SecureRandom random = new SecureRandom();
	private final RegexFinder duplicateParser;
	private final Map<Issue, List<Comment>> allIssues;
	private final Map<Integer, Issue> idIssueMap;
	private final RepositoryId repo;
	private Set<Issue> closedNonDuplicates, nonDuplicates, duplicates, generatedCorpus;
	private Set<Duplicate> duplicatesInGeneratedCorpus;

	public ExperimentSetGenerator(final RepositoryId repo, final Map<Issue, List<Comment>> issuesWithcomments)
	{
		this.duplicateParser = new RegexFinder(repo);
		this.allIssues = issuesWithcomments;
		this.idIssueMap = createIdIssueMap(allIssues.keySet());
		this.repo = repo;
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
		
		closedNonDuplicates = filterOpenIssues(nonDuplicates);

		generatedCorpus = new HashSet<Issue>((int) (size * 1.1));
		final int duplicateAmount = (int) (size * duplicateRatio);
		
		if(duplicates.isEmpty() && duplicateRatio > 0)
			throw new IllegalArgumentException("Requested duplicates in the generated set, but this corpus contains no identified duplicates.");

		generatedCorpus.addAll(getRandomElements(duplicates, duplicateAmount/2));
		generatedCorpus.addAll(getMasterIssues(generatedCorpus));
		duplicatesInGeneratedCorpus = createDuplicateSet(generatedCorpus);
		generatedCorpus.addAll(getRandomElements(closedNonDuplicates, size - generatedCorpus.size()));
	}

	private Set<Issue> filterOpenIssues(Set<Issue> issues)
	{
		final Set<Issue> closedIssues = new HashSet<Issue>(issues);
		final Iterator<Issue> iter = closedIssues.iterator();
		while(iter.hasNext())
		{
			final Issue issue = iter.next();
			final boolean open = issue.getState().equals("open");
			if(open)
				iter.remove();
		}
		
		return closedIssues;
	}

	private Set<Duplicate> createDuplicateSet(Set<Issue> dupes)
	{
		duplicatesInGeneratedCorpus = new HashSet<Duplicate>();
		for(final Issue issue : generatedCorpus)
		{
			final Issue master = getMasterForIssue(issue);
			if(master == null)
				continue;
			if(master.getNumber() == issue.getNumber())
				continue;
			final Duplicate dupe = new Duplicate(new StrippedIssue(issue), new StrippedIssue(master), 1.0);
			duplicatesInGeneratedCorpus.add(dupe);
		}
		return duplicatesInGeneratedCorpus;
	}

	public void generateRandomIntervalSet(final int size, final float minDuplicateRatio, final float maxDuplicateRatio)
	{
		final float spanRandom = maxDuplicateRatio - minDuplicateRatio;
		if(spanRandom < 0)
			throw new IllegalArgumentException("Min is more than max");
		if(spanRandom == 0)
			generateSet(size, minDuplicateRatio);
		else
		{
			float ratio = random.nextFloat()*spanRandom + minDuplicateRatio;
			generateSet(size, ratio);
		}
	}

	private Collection<? extends Issue> getMasterIssues(Set<Issue> duplicateSet)
	{
		Set<Issue> masterIssues = new HashSet<Issue>();
		for(Issue issue : duplicateSet)
		{
			List<Comment> commentsForIssue = allIssues.get(issue);
			final int master = findMaster(commentsForIssue);
			if(master != -1)
			{
				final Issue masterIssue = idIssueMap.get(master);
				if(masterIssue == null)
					continue;
				masterIssues.add(masterIssue);
			}
		}
		
		return masterIssues;
	}
	
	private Issue getMasterForIssue(Issue issue)
	{
		final int masterId = findMaster(allIssues.get(issue));
		return idIssueMap.get(masterId);
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
		if(amount > listFromSet.size())
			amount = listFromSet.size();
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
		Iterator<Entry<Issue, List<Comment>>> iter = allIssues.entrySet().iterator();
		while(iter.hasNext())
		{
			final Entry<Issue, List<Comment>> entry = iter.next();
			final Issue issue = entry.getKey();
			final List<Comment> issueComments = entry.getValue();
			if(duplicateParser.isTaggedAsDuplicate(issueComments))
				duplicates.add(issue);
		}
		return duplicates;
	}
	
	public Set<Duplicate> getDuplicates()
	{
		Set<Duplicate> dupes = new HashSet<Duplicate>(duplicates.size());
		for(Issue issue : duplicates)
		{
			final Issue master = getMasterForIssue(issue);
			if(master == null)
				continue;
			if(master.getNumber() == issue.getNumber())
				continue;
			StrippedIssue masterStripped = new StrippedIssue(master, allIssues.get(master));
			StrippedIssue duplicateStripped = new StrippedIssue(issue, allIssues.get(issue));
			final Duplicate duplicate = new Duplicate(duplicateStripped, masterStripped, 1);
			dupes.add(duplicate);
		}
		
		return dupes;
	}

	public Set<Duplicate> getCorpusDuplicates()
	{
		return duplicatesInGeneratedCorpus;
	}

	public static boolean isLabeledAsDuplicates(Issue issue)
	{
		List<Label> labels = issue.getLabels();
		
		for(Label label : labels)
		{
			final String labelName = label.getColor().toLowerCase();
			if(labelName.equals("duplicate"))
				return true;
			if(labelName.equals("dupe"))
				return true;
		}
		
		return false;
	}

	public Map<Issue, List<Comment>> getGeneratedCorpus()
	{
		Map<Issue, List<Comment>> corpus = new HashMap<Issue, List<Comment>>();
		for(Issue issue : generatedCorpus)
			corpus.put(issue, allIssues.get(issue));
		return corpus;
	}
	
	public void printCorpusData()
	{
		final float duplicateRatio = duplicates.size()/(float) allIssues.size();
		
		System.out.println("*** Corpus Statistics ***");
		System.out.println(" - " + repo.getOwner() + "/" + repo.getName());
		System.out.println("  - Non duplicate issues: " + nonDuplicates.size());
		System.out.println("  - Duplicate issues: " + duplicates.size());
		System.out.println("  - Total amount of issues: " + allIssues.size());
		System.out.println("  - Duplicate ratio: **" + duplicateRatio*100 + "%**");
	}

}
