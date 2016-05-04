package duplicatesearcher.analysis;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import duplicatesearcher.Progress;
import duplicatesearcher.StrippedIssue;
import duplicatesearcher.Token;
import duplicatesearcher.analysis.frequency.FrequencyCounter;
import duplicatesearcher.analysis.frequency.InverseDocumentFrequencyCounter;
import duplicatesearcher.analysis.frequency.TermFrequencyCounter;
import duplicatesearcher.flags.IssueComponent;

public class Analyzer
{
	private final Weight weights;
	private final Collection<StrippedIssue> issues;

	private final InverseDocumentFrequencyCounter idfCounter, labelIdfCounter;
	private Progress progress;

	public Analyzer(final Collection<StrippedIssue> issues, final Weight weights)
	{
		this.issues = issues;
		this.weights = weights;
		this.idfCounter = new InverseDocumentFrequencyCounter();
		this.labelIdfCounter = new InverseDocumentFrequencyCounter();
		analyzeData(issues);
	}

	private void analyzeData(Collection<StrippedIssue> issueData)
	{
		for(StrippedIssue issue : issueData)
			addTokenData(issue);
	}

	private void addTokenData(StrippedIssue issue)
	{
		idfCounter.add(issue.getNumber(), issue.getComponent(IssueComponent.ALL).getTokens());
		labelIdfCounter.add(issue.getNumber(), issue.getComponent(IssueComponent.LABELS).getTokens());
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

		final int finished = (issues.size()*issues.size())/2;
		progress = new Progress(finished, 5000);
		
		System.out.println("\nSEARCHING FOR DUPLICATES");
		
		final Set<Duplicate> duplicates = new HashSet<Duplicate>();
		for(StrippedIssue issue : issues)
			duplicates.addAll(findDuplicates(issue, threshold));

		return duplicates;
	}

	public Set<Duplicate> findDuplicates(final StrippedIssue issue, final double threshold)
	{
		final Set<Duplicate> duplicates = new HashSet<Duplicate>();
		
		double highestSimilarity = threshold;
		Duplicate bestMatch = null;
		
		final Map<IssueComponent, Map<Token, Double>> queryNormalized = createQueryMap(issue);

		for(StrippedIssue issueInCollection : issues)
		{			
			if(issue.getNumber() <= issueInCollection.getNumber())
				continue;
			
			progress.increment();
			progress.print();
			
			final int diffOnId = Math.abs(issue.getNumber() - issueInCollection.getNumber());
			
			if(issue.getUserId() == issueInCollection.getUserId() && diffOnId != 1)
				continue;
			
			double similarity = 0;
			
			for(IssueComponent comp : IssueComponent.values())
				similarity += calculateSimilarity(weights.getWeight(comp), issueInCollection.getComponent(comp), queryNormalized.get(comp));
			
			if(similarity >= highestSimilarity)
			{
				duplicates.remove(bestMatch);
				bestMatch = createDuplicate(issue, issueInCollection, similarity, duplicates);
				highestSimilarity = similarity;
			}
		}

		return duplicates;
	}

	private Map<IssueComponent, Map<Token, Double>> createQueryMap(final StrippedIssue issue)
	{
		final Map<IssueComponent, Map<Token, Double>> map = new EnumMap<IssueComponent, Map<Token, Double>>(IssueComponent.class);

		for(IssueComponent comp : IssueComponent.values())
		{
			final Map<Token, Double> query = weightMap(issue.getComponent(comp));
			Map<Token, Double> normalized = Normalizer.normalizeVector(query);
			map.put(comp, normalized);
		}
		
		return map;
	}

	private double calculateSimilarity(final double weight, final TermFrequencyCounter issue, Map<Token, Double> queryMap)
	{
		if(weight == 0)
			return 0;
		
		final Map<Token, Double> document = weightMap(issue);
		final Map<Token, Double> documentNormalized = Normalizer.normalizeVector(document);
		final double cosineSimilarity = weight*(vectorMultiplication(documentNormalized, queryMap));
		return cosineSimilarity;
	}

	private double vectorMultiplication(Map<Token, Double> vector1, Map<Token, Double> vector2)
	{
		double similarity = 0;
		
		Set<Token> intersection = new HashSet<Token>(vector1.keySet());
		intersection.retainAll(vector2.keySet());		
		for(Token token : intersection)
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

	private Duplicate createDuplicate(StrippedIssue issue1, StrippedIssue issue2, double similarity, Set<Duplicate> duplicates)
	{
		try
		{
			final Duplicate duplicate = new Duplicate(issue1, issue2, similarity); 
			duplicates.add(duplicate);
			return duplicate;
		}
		catch(IllegalArgumentException exception)
		{
			System.err.println("\n" + exception.getMessage());
			System.err.println("\t" + issue1.getNumber() + " --> " + issue2.getNumber());
			System.err.println("\tSimilarity: " + similarity);
		}
		return null;
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
