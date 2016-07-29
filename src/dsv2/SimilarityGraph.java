package dsv2;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import dsv2.analysis.Analyzer;
import dsv2.analysis.InverseDocumentFrequency;
import duplicatesearcher.Token;
import duplicatesearcher.analysis.Duplicate;
import graphProject.Edge;
import graphProject.concurrent.ConcurrentGraph;

public class SimilarityGraph extends ConcurrentGraph<Issue> implements Serializable
{
	private final static long serialVersionUID = 0L;
	private final static float CONNECT_THRESHOLD = 0.1f;
	private final Map<Token, Issue> tokenNodes;
	private final Map<Integer, Issue> issues;
	private final InverseDocumentFrequency<Token> idfc;
	private final Analyzer<Token> analyzer;
	private int latestIssue = -1;

	public SimilarityGraph(Collection<Issue> issues)
	{
		this.idfc = new InverseDocumentFrequency<Token>();
		this.tokenNodes = new ConcurrentHashMap<Token, Issue>();
		this.analyzer = new Analyzer<Token>(idfc);
		this.issues = new HashMap<Integer, Issue>();
	}

	@Override
	public boolean add(final Issue issue)
	{
		if(contains(issue))
		{
			remove(issue);
			idfc.remove(issue.getNumber(), issue.vectors());
		}

		if(super.add(issue))
		{
			idfc.add(issue.getNumber(), issue.vectors().keySet());
			measureSimilarity(issue);
			if(issue.getNumber() > latestIssue)
				latestIssue = issue.getNumber();
			issues.put(issue.getNumber(), issue);

			return true;
		}
		return false;
	}
	
	public Issue getIssue(final int id)
	{
		return issues.get(id);
	}

	private void measureSimilarity(Issue issue)
	{
		final Map<Token, Double> weights = analyzer.weightAndNormalize(issue);
		PriorityQueue<TokenWeight> tokens = new PriorityQueue<TokenWeight>();
		for(Entry<Token, Double> entry : weights.entrySet())
		{
			final Token key = entry.getKey();
			final Double value = entry.getValue();
			final TokenWeight weight = new TokenWeight(key, value);
			tokens.add(weight);
		}

		int connectedNodes = 0;
		while(connectedNodes < 5 && !tokens.isEmpty())
		{
			final Token token = tokens.poll().getToken();
			final Issue issueWithToken = tokenNodes.get(token);
			if(issueWithToken != null)
			{
				final double similarity = analyzer.cosineSimilarity(issue, issueWithToken);
				if(similarity > CONNECT_THRESHOLD)
				{
					final double weight = 1 / similarity;
					if(connect(issue, issueWithToken, weight))
						connectedNodes++;
				}
			}
			else
			{
				tokenNodes.put(token, issue);
			}
		}
	}

	public SortedSet<Duplicate> findDuplicates(final Issue issue, final double threshold, final double walkingThreshold)
	{
		SortedSet<Duplicate> duplicates = new TreeSet<Duplicate>();
		Queue<Issue> nodesToVisit = new ArrayDeque<Issue>();
		Set<Issue> visitedNodes = new HashSet<Issue>();
		nodesToVisit.add(issue);
		visitedNodes.add(issue);
		
		while(!nodesToVisit.isEmpty())
		{
			final Issue currentNode = nodesToVisit.poll();
			List<Edge<Issue>> edges = getEdgesFor(currentNode);
			for(Edge<Issue> edge : edges)
			{
				final Issue destination = edge.getDestination();
				if(visitedNodes.contains(destination))
					continue;
				final double similarity = analyzer.cosineSimilarity(issue, destination);
				if(similarity >= threshold)
				{
					final Duplicate duplicate = new Duplicate(issue, destination, similarity);
					duplicates.add(duplicate);
				}

				if(similarity >= walkingThreshold)
				{
					nodesToVisit.add(destination);
				}
			}
			visitedNodes.add(currentNode);
		}
		
		System.out.println("Searched through " + (visitedNodes.size() - 1) + " issues.");

		return duplicates;
	}

	public Issue findOriginal(final Issue issue, final double threshold, final double walkingThreshold, final int maxPathLength)
	{
		return null;
	}

	public int latestIssue()
	{
		return latestIssue;
	}
}
