package research.experiment.humanceiling;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;

import research.experiment.datacollectiontools.DatasetFileManager;
import research.experiment.datacollectiontools.ExperimentSetGenerator;

public class HcGenerator
{
	private final RepositoryId repo;
	private final int participants;
	private Set<Issue> issues;

	public HcGenerator(RepositoryId repo, int participants)
	{
		this.repo = repo;
		this.participants = participants;
	}

	public int generateTestSet(int size, float minDuplicateRatio, float maxDuplicateRatio)
			throws ClassNotFoundException, IOException
	{
		DatasetFileManager data = new DatasetFileManager(repo);
		data.load();
		ExperimentSetGenerator esg = new ExperimentSetGenerator(repo, data.getDataset());
		esg.generateRandomIntervalSet(size, minDuplicateRatio, maxDuplicateRatio);
		issues = esg.getGeneratedCorpus().keySet();
		return issues.size();
	}

	public HtmlIssues[] getHtmlIssues() throws IOException
	{
		final SecureRandom rand = new SecureRandom();
		HtmlIssues[] output = new HtmlIssues[participants];
		for(int i = 0; i < output.length; i++)
		{
			final List<Issue> randIssues = new ArrayList<Issue>(issues);
			Collections.shuffle(randIssues, rand);
			output[i] = new HtmlIssues(randIssues);
		}
		return output;
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException
	{
		final RepositoryId repo = new RepositoryId(args[0], args[1]);
		final int participants = Integer.parseInt(args[2]);
		HcGenerator hc = new HcGenerator(repo, participants);
		final int setSize = Integer.parseInt(args[3]);
		final float minDupeRatio = Float.parseFloat(args[4]);
		final float maxDupeRatio = Float.parseFloat(args[5]);
		hc.generateTestSet(setSize, minDupeRatio, maxDupeRatio);
		saveDataToDisk(repo, hc.getHtmlIssues());
	}

	private static void saveDataToDisk(final RepositoryId repoId, HtmlIssues[] htmlIssues)
	{
		final String path = "human_ceiling/" + repoId.getOwner() + "/" + repoId.getName() + "/";
		try
		{
			for(int i = 0; i < htmlIssues.length; i++)
			{
				Path file = Paths.get(path + "hcData" + i + ".html");
				if(!Files.exists(file, new LinkOption[0]))
				{
					Files.createDirectories(Paths.get(path), new FileAttribute<?>[0]);
					Files.createFile(file, new FileAttribute[0]);
				}
				Files.write(file, htmlIssues[i].getData(), Charset.forName("UTF-8"));
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

	}

}
