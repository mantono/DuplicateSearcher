package duplicatesearcher.analysis;

import dsv2.Issue;
import duplicatesearcher.StrippedIssue;

public class Duplicate implements Comparable<Duplicate>
{
	private final Issue duplicate;
	private final Issue master;
	private final double cosineSimilarity;

	public Duplicate(final Issue duplicate, final Issue master,
			final double similarity)
	{
		if (similarity > 1.00000001)
			throw new IllegalArgumentException("Similarity cannot be greater than 1.0");
		if (similarity < 0)
			throw new IllegalArgumentException("Similarity cannot be negative");
		if (duplicate.getNumber() == master.getNumber())
			throw new IllegalArgumentException("Go home, you're drunk...");

		if (duplicate.getNumber() > master.getNumber())
		{
			this.duplicate = duplicate;
			this.master = master;
		}
		else
		{
			this.duplicate = master;
			this.master = duplicate;
		}
		this.cosineSimilarity = similarity;
	}

	public Issue getDuplicate()
	{
		return duplicate;
	}

	public Issue getMaster()
	{
		return master;
	}

	public double getSimilarity()
	{
		return cosineSimilarity;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		if(!(obj instanceof Duplicate))
			return false;
		Duplicate other = (Duplicate) obj;
		
		final boolean sameMaster = this.master.getNumber() == other.getMaster().getNumber();
		final boolean sameDuplicate = this.duplicate.getNumber() == other.getDuplicate().getNumber();
		
		return sameMaster && sameDuplicate;
	}
	
	@Override
	public int hashCode()
	{
		int hashCode = 11;
		hashCode += master.getNumber();
		hashCode *= 11;
		hashCode += duplicate.getNumber();
		
		return hashCode;
	}

	@Override
	public String toString()
	{
		return cosineSimilarity + " ("+ duplicate.getNumber() + " --> " + master.getNumber() + ")";
	}

	@Override
	public int compareTo(Duplicate other)
	{
		final int cosineDiffernce = (int) Math.round((other.cosineSimilarity - this.cosineSimilarity) * 10_000_000);
		if(cosineDiffernce != 0)
			return cosineDiffernce;
		return this.master.getNumber() - other.master.getNumber();
	}
}
