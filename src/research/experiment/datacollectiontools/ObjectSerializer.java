package research.experiment.datacollectiontools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjectSerializer<T extends Serializable>
{
	private final File file;

	public ObjectSerializer(final File file)
	{
		this.file = file;
	}

	public boolean save(final Serializable object, final boolean overwrite) throws FileNotFoundException, IOException
	{
		if(file.exists() && !overwrite)
			return false;
		try(final FileOutputStream fileStream = new FileOutputStream(file))
		{
			final ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
			objectStream.writeObject(object);
			fileStream.close();
			return true;
		}
		finally
		{
			System.out.println("\nObject saved to disk --> " + file);
		}
	}

	public T load() throws ClassNotFoundException, IOException
	{
		try(final FileInputStream fileStream = new FileInputStream(file))
		{
			final ObjectInputStream objectStream = new ObjectInputStream(fileStream);
			Object loadedObject = objectStream.readObject();
			objectStream.close();
			return (T) loadedObject;
		}
		finally
		{
			System.out.println("\nObject loaded from disk <-- " + file);
		}
	}
}
