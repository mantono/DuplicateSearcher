package dsv2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.mantono.ghapic.Repository;

public class GraphStorage implements Runnable
{
	private final Repository repo;
	private final File repoContent;
	private final FileSaver saver;
	private SimilarityGraph issues, issueDataOnDisk;

	public GraphStorage(Repository repository)
	{
		this.repo = repository;
		this.repoContent = new File("repos/" + repo.getOwner() + "/" + repo.getName() + ".gph");
		this.saver = new FileSaver(repoContent);		
	}

	public void save()
	{
			if(issueDataOnDisk != null && issues.size() == issueDataOnDisk.size())
				return;
			saver.save(issues);
	}

	public SimilarityGraph load()
	{
		issues = new SimilarityGraph(new ArrayList<>(0));

		if(repoContent.exists())
		{
			try(FileInputStream fis = new FileInputStream(repoContent);
					ObjectInputStream ois = new ObjectInputStream(fis);)
			{
				issues = (SimilarityGraph) ois.readObject();
				issueDataOnDisk = new SimilarityGraph(issues);
			}
			catch(IOException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}

		return issues;
	}

	@Override
	public void run()
	{
		save();
	}

}
