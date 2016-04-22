package research.experiment.datacollectiontools;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.egit.github.core.RepositoryId;

import duplicatesearcher.analysis.frequency.TermFrequencyCounter;

public class CollectionCounter extends TermFrequencyCounter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3234927772036876183L;
	private final Set<RepositoryId> savedRepos = new HashSet<RepositoryId>();
	
	public boolean inCollection(RepositoryId repo)
	{
		return savedRepos.contains(repo);
	}
	
	public void save(RepositoryId repo)
	{
		savedRepos.add(repo);
	}
	
}
