package research.experiment;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.egit.github.core.RepositoryId;

import duplicatesearcher.DuplicateSearcher;

public class Experiment implements Runnable
{
	private final RepositoryId repo;
	private final String[] args;
	
	public Experiment(RepositoryId repo, final String[] args)
	{
		this.repo = repo;
		this.args = args;
	}
	
	public static void main(String[] args)
	{
		for(Dataset ds : Dataset.values())
		{
			Experiment ex = new Experiment(ds.getRepo(), args);
			Thread thread = new Thread(ex);
			thread.setName(ds.toString());
			thread.start();
		}
	}

	@Override
	public void run()
	{
		try
		{
			String[] allArgs = new String[args.length + 2];
			allArgs[0] = repo.getOwner();
			allArgs[1] = repo.getName();
			for(int i = 0; i < args.length; i++)
				allArgs[i+2] = args[i];
			DuplicateSearcher.main(allArgs);
		}
		catch(ClassNotFoundException | IOException | InterruptedException | URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
