package research.experiment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.egit.github.core.RepositoryId;

import duplicatesearcher.DuplicateSearcher;
import duplicatesearcher.flags.ProcessingFlag;

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
		final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(4, 8, 3500, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(200));
		threadPool.prestartAllCoreThreads();
		for(int i = 0; i < 32; i++)
		{
			for(Dataset ds : Dataset.values())
			{
				String[] extendedArgs;
				if(i != 0)
				{
					EnumSet<ProcessingFlag> flags = ProcessingFlag.setOf(i);
					extendedArgs = new String[args.length + flags.size()];
					int n = 0;
					for(ProcessingFlag flag : flags)
						extendedArgs[n++] = flag.getLongFlag();
					for(; n < extendedArgs.length; n++)
						extendedArgs[n] = args[n-flags.size()];
				}
				else
				{
					extendedArgs = args;
				}
				Experiment ex = new Experiment(ds.getRepo(), extendedArgs);
				threadPool.execute(ex);
			}
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
