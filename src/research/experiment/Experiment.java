package research.experiment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;

import duplicatesearcher.DuplicateSearcher;
import duplicatesearcher.flags.ProcessingFlag;
import research.experiment.datacollectiontools.DatasetFileManager;
import research.experiment.datacollectiontools.ExperimentSetGenerator;

public class Experiment implements Runnable
{
	private final RepositoryId repo;
	private final ExperimentSetGenerator exGen;
	private final String[] args;

	public Experiment(RepositoryId repo, ExperimentSetGenerator experimentSetGenerator, final String[] args)
	{
		this.repo = repo;
		this.exGen = experimentSetGenerator;
		this.args = args;
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException
	{
		final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(8, 8, 4500,
				TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(200));
		threadPool.prestartAllCoreThreads();
		Map<Dataset, ExperimentSetGenerator> dataset = generateDatasets();
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
						extendedArgs[n] = args[n - flags.size()];
				}
				else
				{
					extendedArgs = args;
				}
				Experiment ex = new Experiment(ds.getRepo(), dataset.get(ds), extendedArgs);
				threadPool.execute(ex);
			}
		}
	}

	private static Map<Dataset, ExperimentSetGenerator> generateDatasets() throws ClassNotFoundException, IOException
	{
		final Map<Dataset, ExperimentSetGenerator> datasets = new HashMap<Dataset, ExperimentSetGenerator>(5);
		
		for(Dataset project : Dataset.values())
		{
			DatasetFileManager data = new DatasetFileManager(project.getRepo());
			data.load();
			ExperimentSetGenerator exGen = new ExperimentSetGenerator(project.getRepo(), data.getDataset());
			exGen.generateSet(4000, 0.4f);
			datasets.put(project, exGen);
		}
		
		return datasets;
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
				allArgs[i + 2] = args[i];
			DuplicateSearcher.mainWithCorpus(exGen, allArgs);
		}
		catch(ClassNotFoundException | IOException | InterruptedException | URISyntaxException e)
		{
			e.printStackTrace();
		}
	}

}
