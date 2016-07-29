package dsv2;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import dsv2.analysis.TermFrequency;
import dsv2.analysis.VectorUnit;
import dsv2.processing.GithubFilter;
import dsv2.processing.Tokenizer;
import duplicatesearcher.Token;

public class Issue implements Serializable, VectorUnit<Token>, Comparable<Issue>
{
	private static final long serialVersionUID = 1L;
	private final LocalDateTime created;
	private LocalDateTime lastModified;
	private final int issueId, creatorId;
	private String title, body;
	private final TermFrequency<Token> tokens;
	private final ReentrantLock writeLock;

	public Issue(final int issue, final int creator, final LocalDateTime created, final LocalDateTime lastModified, final String title, final String body)
	{
		this.issueId = issue;
		this.creatorId = creator;
		this.created = created;
		this.lastModified = lastModified;
		this.title = title;
		this.body = body;
		this.tokens = new TermFrequency<Token>();
		generateTermFrequency();
		this.writeLock = new ReentrantLock();
	}

	public LocalDateTime getCreationTime()
	{
		return created;
	}

	public LocalDateTime getLastModified()
	{
		return lastModified;
	}

	public int getNumber()
	{
		return issueId;
	}

	public int getCreatorId()
	{
		return creatorId;
	}

	private void generateTermFrequency()
	{
		tokens.clear();
		final EnumSet<GithubFilter> filters = EnumSet.allOf(GithubFilter.class);
		Tokenizer tokenizer = new Tokenizer(filters);
		final Token[] tokenizedTitle = tokenizer.tokenize(title);
		final Token[] tokenizedBody = tokenizer.tokenize(body);
		tokens.addAll(tokenizedTitle);
		tokens.addAll(tokenizedBody);
	}

	public boolean setTitle(final LocalDateTime timeChanged, final String newTitle)
	{
		return setTitleAndBody(timeChanged, newTitle, body);
	}

	public boolean setTitle(final String newTitle)
	{
		return setTitle(LocalDateTime.now(ZoneId.of("UTC")), newTitle);
	}

	public String getTitle()
	{
		return title;
	}

	public boolean setBody(final LocalDateTime timeChanged, final String newBody)
	{
		return setTitleAndBody(timeChanged, title, newBody);
	}

	public boolean setBody(final String newBody)
	{
		return setBody(LocalDateTime.now(ZoneId.of("UTC")), newBody);
	}

	public String getBody()
	{
		return body;
	}

	private boolean setTitleAndBody(final LocalDateTime timeChanged, final String title, final String body)
	{
		try
		{
			if(writeLock.tryLock(500, TimeUnit.MILLISECONDS))
			{
				this.lastModified = timeChanged;
				if(timeChanged.isBefore(lastModified))
					return false;

				this.title = title;
				this.body = body;
				generateTermFrequency();

				return true;
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(writeLock.isHeldByCurrentThread())
				writeLock.unlock();
		}

		return false;
	}

	/**
	 * The amount of tokens that occur in the tokenized and filtered version of
	 * this issue. Note that this does not count unique occurrences, so the same
	 * token being present multiple times will increase the count.
	 * 
	 * @return the number of tokens that occurs in this issue.
	 */
	public int size()
	{
		return tokens.count();
	}
	
	public TermFrequency<Token> getTokens()
	{
		return tokens;
	}

	@Override
	public Map<Token, Integer> vectors()
	{
		return tokens.vectors();
	}
	
	@Override
	public String toString()
	{
		return issueId + ": " + title;
	}

	@Override
	public int compareTo(Issue other)
	{
		return this.issueId - other.issueId;
	}

	@Override
	public boolean equals(Object object)
	{
		if(!(object instanceof Issue))
			return false;

		final Issue other = (Issue) object;

		return this.issueId == other.issueId;
	}

	@Override
	public int hashCode()
	{
		return issueId;
	}

	/**
	 * Checks whether the current version of this issue has changed compared to a more recent version of this issue.
	 * @param other the other version of this issue to compare with.
	 * @return true if the other version of this issue is different from the current version.
	 */
	public boolean hasChanged(final Issue other)
	{
		if(this.issueId != other.issueId)
			throw new IllegalArgumentException("Comparing two different issues (" + this.issueId + " and "
					+ other.issueId + ") but this method is only intended for different versions of the same issue.");

		final int comp = this.lastModified.compareTo(other.lastModified);
		
		if(comp == 0)
			return false;
		else if(comp > 0)
			throw new IllegalArgumentException("Argument other is and older version of this issue instead of a more recent one.");

		return !this.tokens.equals(other.tokens);
	}
}
