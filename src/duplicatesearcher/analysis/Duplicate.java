package duplicatesearcher.analysis;

import org.eclipse.egit.github.core.Issue;

import duplicatesearcher.StrippedIssue;

public class Duplicate implements Comparable<Duplicate>
{
	private final StrippedIssue duplicate;
	private final StrippedIssue master;
	private final double cosineSimilarity;

	public Duplicate(final StrippedIssue duplicate, final StrippedIssue master,
			final double similarity)
	{
		if (similarity > 1)
			throw new IllegalArgumentException("Similarity cannot be greater than 1.0");
		if (similarity < 0)
			throw new IllegalArgumentException("Similarity cannot be negative");
		if (duplicate.getNumber() < master.getNumber())
			throw new IllegalArgumentException(
					"Master issue must be created before the duplicate issue");
		if (duplicate.getNumber() == master.getNumber())
			throw new IllegalArgumentException("Go home, you're drunk...");

		this.duplicate = duplicate;
		this.master = master;
		this.cosineSimilarity = similarity;
	}

	public StrippedIssue getDuplicate()
	{
		return duplicate;
	}

	public StrippedIssue getMaster()
	{
		return master;
	}

	public double getSimilarity()
	{
		return cosineSimilarity;
	}

	@Override
	public String toString()
	{
		return cosineSimilarity + " ("+ duplicate.getNumber() + " --> " + master.getNumber() + ")";
	}

	@Override
	public int compareTo(Duplicate other)
	{
		return (int) Math.round((other.cosineSimilarity - this.cosineSimilarity) * 1000);
	}
}
