package duplicatesearcher.retrieval;

import org.eclipse.egit.github.core.client.GitHubClient;

public abstract class GitHubTask
{
	private final GitHubClient client;
	
	public GitHubTask(final GitHubClient client)
	{
		this.client = client;
	}

	public GitHubClient getClient()
	{
		return client;
	}
	
	protected void printProgress(final String task, int current, int total)
	{
		final double progress = current / (double) total;
		System.out.println("Progress " + task + ": " + progress);
	}

	protected int checkQuota()
	{
		final int remainingRequests = client.getRemainingRequests(); 
		if(remainingRequests < 1 && remainingRequests != -1)
			throw new IllegalStateException("Request quota has been reached, cannot make a request to the API at the moment.");
		return remainingRequests;
	}
	
	protected void autoThrottle()
	{
		final int remainingRequests = checkQuota();
		if(remainingRequests > 4000)
		{
			System.out.println("No sleep needed.");
			return;
		}
		final double requestsConsumedRate = remainingRequests / 5000.0;
		final int oneHour = 3600;
		final long sleepTime = (long) ((1-requestsConsumedRate)*oneHour)*1000;
		try
		{
			System.out.print("Thread will sleep for " + sleepTime/1000 + " seconds.");
			Thread.sleep(sleepTime);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
