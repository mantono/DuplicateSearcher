package duplicatesearcher.analysis;

import java.util.Collection;
import java.util.Set;

import org.eclipse.egit.github.core.Issue;

public class Analyzer
{
	private final Collection<Issue> issues;
	
	public Analyzer(final Collection<Issue> issues)
	{
		this.issues = issues;
	}
	
	public Set<Duplicate> findDuplicates(final double threshold)
	{
		if(threshold > 1)
			throw new IllegalArgumentException("Threshold cannot be greater than 1.0");
		if(threshold < 0)
			throw new IllegalArgumentException("Threshold cannot be negative");
		
		return null;
	}
}
