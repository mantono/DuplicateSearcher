package dsv2;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
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

import duplicatesearcher.analysis.Duplicate;
import graphProject.Graph;

public class DuplicateSearcher
{

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, ExecutionException
	{
		
		final String repoOwner = args[0];
		final String repoName = args[1];
		final Repository repo = new Repository(repoOwner, repoName);

		final Runtime runtime = Runtime.getRuntime();
		
		final GraphStorage graphData = new GraphStorage(repo);
		final Thread threadGraph = new Thread(graphData);
		runtime.addShutdownHook(threadGraph);
		SimilarityGraph graph = graphData.load();		
		
		final Client client = new Client();
		Future<Response> response = client.submitRequest("repos/"+ repoOwner + "/" + repoName +"/issues?q=sort=created&direction=desc&state=all");
		
		final int issuesOnGithub = getIssueCount(response);
		int page = 1+(graph.size()/100);
		
		while(graph.size() < issuesOnGithub)
		{
			Resource issueRequest = new Resource(Verb.GET, "repos/"+ repoOwner + "/" + repoName +"/issues?q=sort=created&direction=asc&state=all&page="+page+"&per_page=100");
			Future<Response> issueFuture = client.submitRequest(issueRequest);
			Set<Issue> issues = parseIssues(issueFuture);
			graph.addAll(issues);
			page++;
		}
		
		final Issue anyIssue = graph.getIssue(5);
		
		SortedSet<Duplicate> duplicates = graph.findDuplicates(anyIssue, 0.4, 0.35);
		
		System.out.println("Done");
		System.out.println(duplicates);
		System.exit(0);
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
			final JsonNode jsonIssue = node.get(i);
			final int number = jsonIssue.get("number").asInt();
			final int user = jsonIssue.get("user").get("id").asInt();
			final String createdValue = jsonIssue.get("created_at").asText();
			final String modifiedValue = jsonIssue.get("updated_at").asText();
			final LocalDateTime created = LocalDateTime.parse(createdValue, DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC")));
			final LocalDateTime modified = LocalDateTime.parse(modifiedValue, DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC")));
			final String title = jsonIssue.get("title").asText();
			final String body = jsonIssue.get("body_text").asText();
			final Issue issue = new Issue(number, user, created, modified, title, body);
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
