package dsv2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.fasterxml.jackson.databind.JsonNode;
import com.mantono.ghapic.Client;
import com.mantono.ghapic.Repository;
import com.mantono.ghapic.Resource;
import com.mantono.ghapic.Response;
import com.mantono.ghapic.Verb;

import dsv2.processing.Stemmer;
import duplicatesearcher.analysis.Duplicate;

public class DuplicateSearcher
{
	private final static float DEFAULT_THRESHOLD = 0.3f;

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, ExecutionException
	{
		try
		{
			final String repoOwner = args[0];
			final String repoName = args[1];
			final Repository repo = new Repository(repoOwner, repoName);

			final float threshold = getThreshold(args);

			final Runtime runtime = Runtime.getRuntime();
			final GraphStorage graphData = new GraphStorage(repo);
			final Thread graphThread = new Thread(graphData);
			runtime.addShutdownHook(graphThread);
			SimilarityGraph graph = graphData.load();

			final Client client = new Client();
			Future<Response> response = client.submitRequest("repos/" + repoOwner + "/" + repoName
					+ "/issues?q=sort=created&direction=desc&state=all");

			final int issuesOnGithub = getIssueCount(response);

			int page = 1 + (graph.size() / 100);

			while(graph.size() < issuesOnGithub)
			{
				Resource issueRequest = new Resource(Verb.GET, "repos/" + repoOwner + "/" + repoName
						+ "/issues?q=sort=created&direction=asc&state=all&page=" + page + "&per_page=100");
				Future<Response> issueFuture = client.submitRequest(issueRequest);
				Set<Issue> issues = parseIssues(issueFuture);
				issues = processIssues(issues);
				graph.addAll(issues);
				page++;
				System.out.println(graph.size() + " of " + issuesOnGithub);
				if(issues.isEmpty())
					break;
			}

			Issue issue;
			do
			{
				issue = readIssueIdFromInput(graph);
				if(issue == null)
					break;
				final long before = System.currentTimeMillis();
				SortedSet<Duplicate> duplicates = graph.findDuplicates(issue, threshold);
				final long after = System.currentTimeMillis();

				final long elapsedTime = after - before;

				System.out.println("Done (" + elapsedTime + " ms)");
				System.out.println("\t" + issue.getTitle());
				for(Duplicate dupe : duplicates)
				{
					System.out.print("\t" + dupe);
					if(dupe.getMaster().getNumber() == issue.getNumber())
						System.out.println("\t" + dupe.getDuplicate().getTitle());
					else
						System.out.println("\t" + dupe.getMaster().getTitle());
				}
			}
			while(issue != null);
		}
		catch(ExecutionException e0)
		{
			System.err.println("Unable to retrieve data for " + args[0] + "/" + args[1]);
			System.exit(1);
		}
		catch(NumberFormatException e1)
		{
			System.err.println("Not a valid float: " + args[2]);
			System.exit(2);
		}
		catch(ArrayIndexOutOfBoundsException e2)
		{
			System.err.println("Expected at least two arguements, got " + args.length);
			System.exit(3);
		}

		System.out.println("Exit.");
		System.exit(0);
	}

	private static float getThreshold(String[] args)
	{
		if(args.length > 2)
			return Float.parseFloat(args[2]);
		return DEFAULT_THRESHOLD;
	}

	private static Issue readIssueIdFromInput(SimilarityGraph graph)
	{
		Issue issue = null;
		while(issue == null)
		{
			String input = "";
			try
			{
				Scanner scanner = new Scanner(System.in);
				System.out.print("Issue number: ");
				if(scanner.hasNextLine())
					input = scanner.nextLine();
				if(input.length() == 0)
					return null;
				final int issueId = Integer.parseInt(input);
				if(issueId == 0)
					return null;
				issue = graph.getIssue(issueId);
			}
			catch(NumberFormatException e)
			{
				System.err.println(input + " is not an integer.");
			}
		}

		return issue;
	}

	private static Set<Issue> processIssues(Set<Issue> issues)
	{
		Stemmer stemmer = new Stemmer();
		stemmer.process(issues);
		return issues;
	}

	private static Set<Issue> parseIssues(Future<Response> future) throws InterruptedException, ExecutionException
	{
		while(!future.isDone())
			Thread.yield();
		Response response = future.get();
		final JsonNode node = response.getBody();
		Set<Issue> issues = new HashSet<Issue>(100);
		for(int i = 0; i < 100; i++)
		{			
			if(!node.has(i))
				break;
			
			final ZoneId utc = ZoneId.of("UTC");
			final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(utc);			
			final JsonNode jsonIssue = node.get(i);
			final int number = jsonIssue.get("number").asInt();
			final int user = jsonIssue.get("user").get("id").asInt();
			final String createdValue = jsonIssue.get("created_at").asText();
			final String modifiedValue = jsonIssue.get("updated_at").asText();
			final LocalDateTime created = LocalDateTime.parse(createdValue, formatter);
			final LocalDateTime modified = LocalDateTime.parse(modifiedValue, formatter);
			final String title = jsonIssue.get("title").asText();
			final String body = jsonIssue.get("body_text").asText();
			final String state = jsonIssue.get("state").asText();
			final boolean open = state.equals("open");
			final JsonNode pullRequestNode = jsonIssue.get("pull_request");
			final boolean pullRequest = pullRequestNode != null;

			final Issue issue = new Issue(number, user, created, modified, title, body, open, pullRequest);
			issues.add(issue);
		}

		return issues;
	}

	private static int getIssueCount(Future<Response> future) throws InterruptedException, ExecutionException
	{
		while(!future.isDone())
			Thread.yield();
		Response response = future.get();
		final JsonNode node = response.getBody();
		final JsonNode lastIssue = node.get(0);
		return lastIssue.get("number").asInt();
	}

}
