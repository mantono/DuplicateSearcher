package dsv2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.mantono.ghapic.Repository;

public class IssueStorage implements Runnable
{
	private final Repository repo;
	private final File repoContent;
	private final FileSaver saver;
	private Set<Issue> issues, issueDataOnDisk;

	public IssueStorage(Repository repository)
	{
		this.repo = repository;
		this.repoContent = new File("repos/" + repo.getOwner() + "/" + repo.getName() + ".iss");
		this.saver = new FileSaver(repoContent);
	}

	public void save()
	{
		if(issues.equals(issueDataOnDisk))
			return;
		saver.save(issues);
	}

	public Set<Issue> load()
	{
		issues = new HashSet<Issue>();

		if(repoContent.exists())
		{
			try(FileInputStream fis = new FileInputStream(repoContent);
					ObjectInputStream ois = new ObjectInputStream(fis);)
			{
				issues = (Set<Issue>) ois.readObject();
				issueDataOnDisk = new HashSet<Issue>(issues);
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
