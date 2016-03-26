package duplicatesearcher.retrieval;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;

public abstract class GitHubTask
{
	private final static int TICK_RATE = 720;
	private final GitHubClient client;
	private int sleepTime = 720;
	private LocalDateTime lastThrottleUpdate = LocalDateTime.now();
	private LocalDateTime rateResetTime;
	private int consumedRequests = -1;
	private float consumedRequestsSinceLastThrottle = 50;

	public GitHubTask(final GitHubClient client)
	{
		this.client = client;
		this.rateResetTime = LocalDateTime.now();
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

	protected void forcedSleep()
	{
		try
		{
			updateRemainingRequests();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(2);
		}
		final long thirtySeconds = 30 * 1000;
		while(rateResetTime.isAfter(LocalDateTime.now()))
			sleep(thirtySeconds);
	}

	private void updateRemainingRequests() throws IOException
	{
		final GitHubRequest updateRateLimit = new GitHubRequest();
		updateRateLimit.setUri("/rate_limit");
		final GitHubResponse response = client.get(updateRateLimit);
		final String resetTime = response.getHeader("X-RateLimit-Reset");
		final long resetTimeParsed = Long.parseLong(resetTime);
		rateResetTime = LocalDateTime.ofEpochSecond(resetTimeParsed, 0, ZoneOffset.ofHours(1));
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
		final double consumedRequestRate = getConsumedRequestsPercentrage();
		final double delta = getDelta() + 1;
		sleepTime = (int) (TICK_RATE * 2 * consumedRequestRate * delta);
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
