package research.experiment;

import org.eclipse.egit.github.core.RepositoryId;

public enum Dataset
{
	BOOTSTRAP("twbs", "bootstrap"),
	GO("golang", "go"),
	TELEGRAM("telegramdesktop", "tdesktop"),
	CUPS("apple", "cups"),
	RUST("rust-lang", "rust");
	
	private final RepositoryId repo;
	
	private Dataset(final String owner, final String name)
	{
		this.repo = new RepositoryId(owner, name);
	}

	/**
	 * @return the repo
	 */
	public RepositoryId getRepo()
	{
		return repo;
	}
}
