package duplicatesearcher.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import duplicatesearcher.StrippedIssue;
import duplicatesearcher.Token;
import duplicatesearcher.analysis.frequency.FrequencyCounter;
import duplicatesearcher.analysis.frequency.InverseDocumentFrequencyCounter;

public class Analyzer
{
	private final Weight weights;
	private final Collection<StrippedIssue> issues;
	private final InverseDocumentFrequencyCounter idfCounter;

	public Analyzer(final Collection<StrippedIssue> issues, final Weight weights)
	{
		this.issues = issues;
		this.weights = weights;
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
		idfCounter.add(issue.getNumber(), issue.getAll().getTokens());
	}

	public boolean add(final StrippedIssue issue)
	{
		return issues.add(issue);
	}

	public SortedSet<Duplicate> findDuplicates(final double threshold)
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

	public SortedSet<Duplicate> findDuplicates(final StrippedIssue issue, final double threshold)
	{
		final SortedSet<Duplicate> duplicates = new TreeSet<Duplicate>();

		final Map<Token, Double> query = weightMap(issue.getAll());
		Map<Token, Double> queryNormalized = new Normalizer(query).normalizeVector();

		for(StrippedIssue issueInCollection : issues)
		{
			if(issue.getNumber() <= issueInCollection.getNumber())
				continue;
			
			Map<Token, Double> issueWeight = weightMap(issueInCollection.getAll());
			issueWeight = new Normalizer(issueWeight).normalizeVector();

			final double similarity = vectorMultiplication(issueWeight, queryNormalized);

			if(similarity >= threshold)
				createDuplicate(issue, issueInCollection, similarity, duplicates);
		}

		return duplicates;
	}

	private double vectorMultiplication(Map<Token, Double> vector1, Map<Token, Double> vector2)
	{
		double similarity = 0;
		
		Set<Token> union = new HashSet<Token>(vector1.keySet());
		union.addAll(vector2.keySet());
		
		for(Token token : union)
		{
			double weight1, weight2;
			weight1 = weight2 = 0;
			if(vector1.containsKey(token))
				weight1 = vector1.get(token);
			if(vector2.containsKey(token))
				weight2 = vector2.get(token);
			similarity += weight1 * weight2;
		}
		
		return similarity;
	}

	private void createDuplicate(StrippedIssue issue1, StrippedIssue issue2, double similarity, Set<Duplicate> duplicates)
	{
		StrippedIssue master, duplicate;
		if(issue1.getNumber() < issue2.getNumber())
		{
			master = issue1;
			duplicate = issue2;
		}
		else
		{
			master = issue2;
			duplicate = issue1;
		}
		
		try
		{
			duplicates.add(new Duplicate(duplicate, master, similarity));
		}
		catch(IllegalArgumentException exception)
		{
			System.err.println("\n" + exception.getMessage());
			System.err.println("\t" + duplicate.getNumber() + " --> " + master.getNumber());
			System.err.println("\tSimilarity: " + similarity);
		}
	}

	private Map<Token, Double> weightMap(FrequencyCounter frequency)
	{
		final Set<Token> tokens = frequency.getTokens();
		final Map<Token, Double> queryWeight = new HashMap<Token, Double>(tokens.size());
		for(Token token : tokens)
		{
			final double tfWeight = frequency.getWeight(token);
			final double idfWeight = idfCounter.getWeight(token);
			final double tfIdfWeight = tfWeight * idfWeight;
			queryWeight.put(token, tfIdfWeight);
		}
		return queryWeight;
	}
}
