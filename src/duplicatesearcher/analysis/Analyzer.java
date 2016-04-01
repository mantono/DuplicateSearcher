package duplicatesearcher.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.egit.github.core.Issue;

import duplicatesearcher.StrippedIssue;
import duplicatesearcher.analysis.frequency.FrequencyCounter;
import duplicatesearcher.analysis.frequency.InverseDocumentFrequencyCounter;

public class Analyzer
{
	private final Collection<StrippedIssue> issues;
	private final InverseDocumentFrequencyCounter idfCounter;
	
	public Analyzer(final Collection<StrippedIssue> issues)
	{
		this.issues = issues;
		this.idfCounter = new InverseDocumentFrequencyCounter();
		analyzeData(issues);
	}
	
	private void analyzeData(Collection<StrippedIssue> issueData)
	{
		for(StrippedIssue issue : issueData)
			idfCounter.add(issue);
		
	}
	
	public boolean add(final StrippedIssue issue)
	{
		return issues.add(issue);
	}

	public Set<Duplicate> findDuplicates(final double threshold)
	{
		if(threshold > 1)
			throw new IllegalArgumentException("Threshold cannot be greater than 1.0");
		if(threshold < 0)
			throw new IllegalArgumentException("Threshold cannot be negative");
		
		final SortedSet<Duplicate> duplicates = new TreeSet<Duplicate>();
		for(StrippedIssue issue: issues)
			duplicates.addAll(findDuplicates(issue, threshold));
		
		return duplicates;
	}
	
	public Set<Duplicate> findDuplicates(final StrippedIssue issue, final double threshold)
	{
		final SortedSet<Duplicate> duplicates = new TreeSet<Duplicate>();
		
		final Map<String, Double> queryWeight = weightMap(issue);
		
		for(StrippedIssue issueInCollection : issues)
		{
			if(issue.getNumber() == issueInCollection.getNumber())
				continue;
			Map<String, Double> issueWeight = weightMap(issueInCollection);
			Map<String, Double> queryNormalized = new HashMap<String, Double>(queryWeight);
			
			normalizeVector(queryNormalized);
			normalizeVector(issueWeight);
			
			Set<String> union = new HashSet<String>(queryWeight.keySet());
			union.addAll(issueWeight.keySet());
			
			double similarity = 0;
			for(String token : union)
			{
				double weight1, weight2;
				weight1 = weight2 = 0;
				if(queryNormalized.containsKey(token))
					weight1 = queryNormalized.get(token);
				if(issueWeight.containsKey(token))
					weight2 = issueWeight.get(token);
				similarity += weight1*weight2;
			}
			
			if(similarity >= threshold)
			{
				StrippedIssue master, duplicate;
				if(issue.getNumber() < issueInCollection.getNumber())
				{
					master = issue;
					duplicate = issueInCollection;
				}
				else
				{
					master = issueInCollection;
					duplicate = issue;
				}
				try{
				duplicates.add(new Duplicate(duplicate, master, similarity));
				}
				catch(IllegalArgumentException exception)
				{
					System.out.println(exception.getMessage());
					System.out.println("Master ID: " + master.getNumber());
					System.out.println("Duplicate ID: " + duplicate.getNumber());
					System.out.println("Cosine similarity: " + similarity);
					System.exit(1);
				}
			}
		}
			
		return duplicates;
	}
	
	private void normalizeVector(Map<String, Double> queryNormalized)
	{
		double sum = 0;
		for(Entry<String, Double> pair : queryNormalized.entrySet())
		{
			double squared = Math.pow(pair.getValue(), 2);
			queryNormalized.put(pair.getKey(), squared);
			sum += squared;
		}
		
		final double divider = Math.sqrt(sum);
		
		for(Entry<String, Double> pair : queryNormalized.entrySet())
		{
			double normalized = pair.getValue()/divider;
			queryNormalized.put(pair.getKey(), normalized);
		}		
	}

	private Map<String, Double> weightMap(StrippedIssue issue)
	{
		final Set<String> tokens = issue.wordSet();
		final Map<String, Double> queryWeight = new HashMap<String, Double>(tokens.size());
		for(String token : tokens)
		{
			final double tfWeight = issue.getWeight(token);
			final double idfWeight = idfCounter.getWeight(token);
			final double tfIdfWeight = tfWeight*idfWeight;
			queryWeight.put(token, tfIdfWeight);
		}
		return queryWeight;
	}
}
