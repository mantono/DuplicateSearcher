package research.experiment.datacollectiontools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;

import research.experiment.datacollectiontools.DatasetFileManager;
import research.experiment.datacollectiontools.ObjectSerializer;

import duplicatesearcher.Progress;
import duplicatesearcher.StrippedIssue;
import duplicatesearcher.analysis.IssueComponent;
import duplicatesearcher.analysis.frequency.TermFrequencyCounter;

public class CollectionWordFrequency
{
	public final static File FILE = new File("dictionary/word_frequency");
	private final ObjectSerializer<CollectionCounter> objSer;
	private final CollectionCounter counter;

	public CollectionWordFrequency() throws ClassNotFoundException, IOException
	{
		this.objSer = new ObjectSerializer<CollectionCounter>(FILE);
		if(FILE.exists())
			this.counter = objSer.load();
		else
			this.counter = new CollectionCounter();
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws ClassNotFoundException, IOException
	{
		if(args.length == 0)
		{
			System.err.println("Argument vector is empty, exiting.");
			System.exit(1);
		}
		
		CollectionWordFrequency cwf = new CollectionWordFrequency();
		cwf.parseRepositoryData(args);
		cwf.save();
	}

	private void save() throws FileNotFoundException, IOException
	{
		objSer.save(counter, true);
	}

	private void parseRepositoryData(String[] args) throws ClassNotFoundException
	{
		for(String repoRawString : args)
		{
			if(!repoRawString.contains("/"))
			{
				System.err.println("Repository name is not in a valid format: \"" + repoRawString
						+ "\", skipping.");
				continue;
			}

			try
			{
				final String[] repoData = repoRawString.split("/");
				final RepositoryId repo = new RepositoryId(repoData[0], repoData[1]);
				if(counter.inCollection(repo))
					continue;
				final DatasetFileManager datasetLoader = new DatasetFileManager(repo);
				datasetLoader.load();
				parseMap(datasetLoader.getDataset());
				counter.save(repo);
			}
			catch(IOException exception)
			{
				System.err.println("Could not find any data for " + repoRawString);
			}
		}
	}

	private void parseMap(Map<Issue, List<Comment>> dataset)
	{
		final Progress parsingProgress = new Progress(dataset.size());
		Iterator<Entry<Issue, List<Comment>>> iter = dataset.entrySet().iterator();
		while(iter.hasNext())
		{
			final Entry<Issue, List<Comment>> entry = iter.next();
			final StrippedIssue issue = new StrippedIssue(entry.getKey(), entry.getValue());
			TermFrequencyCounter tfcIssue = issue.getComponent(IssueComponent.ALL);
			counter.add(tfcIssue);
			parsingProgress.increment();
			parsingProgress.print();
		}
	}
}
