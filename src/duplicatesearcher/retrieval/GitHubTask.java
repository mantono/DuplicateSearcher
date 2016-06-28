package duplicatesearcher.retrieval;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;

public abstract class GitHubTask
{
	private final static int TICK_RATE = 720;
	private final GitHubClient client;
	private int sleepTime = 720;
	private Instant lastThrottleUpdate = Instant.now();
	private Instant rateResetTime;
	private int consumedRequests = -1;
	private float consumedRequestsSinceLastThrottle = 50;
	private final DecimalFormat format = new DecimalFormat("#.####");

	public GitHubTask(final GitHubClient client)
	{
		this.client = client;
		this.rateResetTime = Instant.now();
	}

	public GitHubClient getClient()
	{
		return client;
	}

	protected void printProgress(int current, int total)
	{
		final float progress = (current / (float) total)*100;
		System.out.print("\nProgress: " + format.format(progress)+ "% ");
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
		while(rateResetTime.isAfter(Instant.now()))
			sleep(thirtySeconds);
	}

	private void updateRemainingRequests() throws IOException
	{
		final GitHubRequest updateRateLimit = new GitHubRequest();
		updateRateLimit.setUri("/rate_limit");
		final GitHubResponse response = client.get(updateRateLimit);
		final String resetTime = response.getHeader("X-RateLimit-Reset");
		final long resetTimeParsed = Long.parseLong(resetTime);
		rateResetTime = Instant.ofEpochSecond(resetTimeParsed);
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
		final double delta = getDelta() + 1;
		sleepTime = (int) (TICK_RATE * 2 * getConsumedRequestsPercentrage() * delta);
	}
	
	protected void printStats()
	{
		System.out.print("| Consumed API requests: " + format.format(getConsumedRequestsPercentrage() * 100) + "% ");
		System.out.print("| Sleep time between requests: " + sleepTime + " ms ");		
	}

	private double getDelta()
	{
		final float consumedRequestsSinceLastThrottle = getConsumedRequests() - consumedRequests;
		final long elapsedTimeSinceLastThrottle = lastThrottleUpdate.until(LocalDateTime.now(), ChronoUnit.MILLIS);
		double timeBetweenEachRequest = elapsedTimeSinceLastThrottle / consumedRequestsSinceLastThrottle;
		if(consumedRequests == -1)
			timeBetweenEachRequest = 720;

		lastThrottleUpdate = Instant.now();
		consumedRequests = getConsumedRequests();

		final double delta = TICK_RATE / timeBetweenEachRequest;

		if(delta < 0)
			return 1;

		return delta;
	}
}
