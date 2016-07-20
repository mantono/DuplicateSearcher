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

	@Override
	public Map<Token, Integer> vectors()
	{
		return tokens.vectors();
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

		if(this.issueId != other.issueId)
			return false;

		if(this.creatorId != other.creatorId)
			return false;

		return this.tokens.equals(other.tokens);
	}

	@Override
	public int hashCode()
	{
		final byte prime = 13;
		int hash = 1;

		hash += issueId;
		hash *= prime;
		hash += creatorId;
		hash *= prime;
		hash += tokens.hashCode();
		hash *= prime;

		return hash;
	}
}
