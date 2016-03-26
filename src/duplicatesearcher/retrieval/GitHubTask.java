package duplicatesearcher.retrieval;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

import org.eclipse.egit.github.core.client.GitHubClient;

public abstract class GitHubTask
{
	private final static int TICK_RATE = 720;
	private final GitHubClient client;
	private int sleepTime = 720;
	private LocalDateTime lastThrottleUpdate = LocalDateTime.now();
	private int consumedRequests = -1;
	private float consumedRequestsSinceLastThrottle = 50;

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
		final float progress = current / (float) total;
		System.out.println("Progress " + task + ": " + progress);
	}

	protected int getConsumedRequests()
	{
		return client.getRequestLimit() - client.getRemainingRequests();
	}

	protected int checkQuota()
	{
		final long tenMinutes = 600 * 1000;
		final int remainingRequests = client.getRemainingRequests();

		if(remainingRequests < 100 && remainingRequests != -1)
		{
			System.out.println("Request quota has almost been reached (" + remainingRequests
					+ " requests left), cannot make a request to the API at the moment.");
			sleep(tenMinutes);
		}
		else if(remainingRequests < consumedRequestsSinceLastThrottle * 2)
		{
			System.out.println("At the current rate of request usage (" + consumedRequestsSinceLastThrottle
					+ ") we will soon use up our remaining request quota (" + remainingRequests + ").");
			System.out.println("Thread will pause for a brief moment");
			sleep(tenMinutes);
		}

		return remainingRequests;
	}

	private void sleep(final long sleepTime)
	{
		try
		{
			Thread.sleep(sleepTime);
		}
		catch(InterruptedException e)
		{
			System.err.println("Sleeping thread was interrupted.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	protected double getConsumedRequestsPercentrage()
	{
		final double requestsUsed = getConsumedRequests();
		return requestsUsed / client.getRequestLimit();
	}

	protected void threadSleep()
	{
		sleep(sleepTime);
	}

	protected void autoThrottle()
	{
		checkQuota();
		final double consumedRequestRate = getConsumedRequestsPercentrage();
		final double delta = getDelta() + 0.5;
		sleepTime = (int) (10 + TICK_RATE * consumedRequestRate * delta);
		System.out.println("Delta: " + delta);
		System.out.println("Used " + consumedRequestRate * 100 + "% of hourly request quota.");
		System.out.println("Thread will sleep for " + sleepTime + " milliseconds between each request.");

	}

	private double getDelta()
	{
		final float consumedRequestsSinceLastThrottle = getConsumedRequests() - consumedRequests;
		final long elapsedTimeSinceLastThrottle = lastThrottleUpdate.until(LocalDateTime.now(), ChronoUnit.MILLIS);
		double timeBetweenEachRequest = elapsedTimeSinceLastThrottle / consumedRequestsSinceLastThrottle;
		if(consumedRequests == -1)
			timeBetweenEachRequest = 720;

		System.out.println(consumedRequestsSinceLastThrottle + " requests done in "
				+ elapsedTimeSinceLastThrottle / 1000.0 + " seconds.");
		System.out.println("Averaging " + timeBetweenEachRequest + " milliseconds between each request.");

		lastThrottleUpdate = LocalDateTime.now();
		consumedRequests = getConsumedRequests();

		final double delta = TICK_RATE / timeBetweenEachRequest;

		if(delta < 0)
			return 1;

		return delta;
	}
}
