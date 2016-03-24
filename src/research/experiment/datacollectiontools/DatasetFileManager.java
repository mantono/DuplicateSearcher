package research.experiment.datacollectiontools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;

public class DatasetFileManager
{
	private final static String PATH = "./datasets";
	private Collection<Issue> issueData;
	private final RepositoryId repoInfo;

	public DatasetFileManager(final RepositoryId repoInfo)
	{
		this.repoInfo = repoInfo;
	}
	
	public DatasetFileManager(RepositoryId repoInfo, final Collection<Issue> data)
	{
		this(repoInfo);
		this.issueData = data;
	}
	
	public void load() throws IOException, ClassNotFoundException
	{
		final String fileName = getFileName();
		final FileInputStream fileStream = new FileInputStream(fileName);
		final ObjectInputStream objectStream = new ObjectInputStream(fileStream);
		Object object = objectStream.readObject();
		objectStream.close();
		issueData = (Collection<Issue>) object;
		System.out.println("Object loaded from  disk <-- " + fileName);
	}
	
	public void save() throws IOException
	{
		final String fileName = getFileName();
		createPath();
		final FileOutputStream fileStream = new FileOutputStream(fileName);
		final ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
		objectStream.writeObject(issueData);
		fileStream.close();
		System.out.println("Object saved to disk --> " + fileName);
	}
	
	private void createPath()
	{
		final String path = PATH + "/" + repoInfo.getOwner();
		new File(path).mkdirs();
	}

	private String getFileName()
	{
		return PATH + "/" + repoInfo.getOwner() + "/" + repoInfo.getName();
	}

	public Collection<Issue> getDataset()
	{
		return issueData;
	}

}
