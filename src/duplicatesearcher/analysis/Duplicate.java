package duplicatesearcher.analysis;

import org.eclipse.egit.github.core.Issue;

public class Duplicate
{
	private final Issue duplicate;
	private final Issue master;
	private final double similarity;
	
	public Duplicate(final Issue duplicate, final Issue master, final double similarity)
	{
		if(similarity > 1)
			throw new IllegalArgumentException("Similarity cannot be greater than 1.0");
		if(similarity < 0)
			throw new IllegalArgumentException("Similarity cannot be negative");
		if(duplicate.getNumber() < master.getNumber())
			throw new IllegalArgumentException("Master issue must be created before the duplicate issue");
		if(duplicate.getNumber() == master.getNumber())
			throw new IllegalArgumentException("Go home, you're drunk...");
		
		this.duplicate = duplicate;
		this.master = master;
		this.similarity = similarity;
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
		return similarity;
	}
}
