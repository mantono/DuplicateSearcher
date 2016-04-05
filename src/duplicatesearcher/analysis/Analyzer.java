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
import duplicatesearcher.Token;
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
			addTokenData(issue);
	}

	private void addTokenData(StrippedIssue issue)
	{
		idfCounter.add(issue.getNumber(), issue.wordSet());
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
		for(StrippedIssue issue : issues)
			duplicates.addAll(findDuplicates(issue, threshold));

		return duplicates;
	}

	public Set<Duplicate> findDuplicates(final StrippedIssue issue, final double threshold)
	{
		final SortedSet<Duplicate> duplicates = new TreeSet<Duplicate>();

		final Map<Token, Double> queryWeight = weightMap(issue);

		for(StrippedIssue issueInCollection : issues)
		{
			if(issue.getNumber() == issueInCollection.getNumber())
				continue;
			Map<Token, Double> issueWeight = weightMap(issueInCollection);
			Map<Token, Double> queryNormalized = new HashMap<Token, Double>(queryWeight);

			try
			{
				normalizeVector(queryNormalized);
				normalizeVector(issueWeight);
			}
			catch(ArithmeticException exception)
			{
				System.out.println("Error on normalizing vectors for " + issue.getNumber() + " and "
						+ issueInCollection.getNumber());
				System.out.println(exception.getMessage());
				continue;
			}

			Set<Token> union = new HashSet<Token>(queryWeight.keySet());
			union.addAll(issueWeight.keySet());

			double similarity = 0;
			for(Token token : union)
			{
				double weight1, weight2;
				weight1 = weight2 = 0;
				if(queryNormalized.containsKey(token))
					weight1 = queryNormalized.get(token);
				if(issueWeight.containsKey(token))
					weight2 = issueWeight.get(token);
				similarity += weight1 * weight2;
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
				try
				{
					duplicates.add(new Duplicate(duplicate, master, similarity));
				}
				catch(IllegalArgumentException exception)
				{
					System.out.println("\n" + exception.getMessage());
					System.out.println("Master ID: " + master.getNumber());
					System.out.println("Duplicate ID: " + duplicate.getNumber());
					System.out.println("Cosine similarity: " + similarity);
					System.out.println("\t" + issueWeight);
					System.out.println("\t" + queryWeight);
					System.exit(1);
				}
			}
		}

		return duplicates;
	}

	private void normalizeVector(Map<Token, Double> queryToNormalize)
	{
		double sum = 0;
		for(Entry<Token, Double> pair : queryToNormalize.entrySet())
		{
			double squared = Math.pow(pair.getValue(), 2);
			sum += squared;
		}

		final double divider = Math.sqrt(sum);

		for(Entry<Token, Double> pair : queryToNormalize.entrySet())
		{
			double normalized = pair.getValue() / divider;
			if(normalized == 0 || Double.isNaN(normalized))
				queryToNormalize.put(pair.getKey(), 0.0);
			else
				queryToNormalize.put(pair.getKey(), normalized);
		}
		
	}

	private Map<Token, Double> weightMap(StrippedIssue issue)
	{
		final Set<Token> tokens = issue.wordSet();
		final Map<Token, Double> queryWeight = new HashMap<Token, Double>(tokens.size());
		for(Token token : tokens)
		{
			final double tfWeight = issue.getWeight(token);
			final double idfWeight = idfCounter.getWeight(token);
			final double tfIdfWeight = tfWeight * idfWeight;
			queryWeight.put(token, tfIdfWeight);
		}
		return queryWeight;
	}
}
