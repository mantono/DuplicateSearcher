package dsv2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class FileSaver
{
	private final File file;
	
	FileSaver(final File file)
	{
		this.file = file;
	}
	
	public long save(Object object)
	{
		try
		{
			File dir = dirForFile();
			if(!dir.exists())
				dir.mkdirs();
			if(!file.exists())
				file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.close();
			return file.length();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	private File dirForFile()
	{
		final String fullName = file.toString();
		final int lastIndex = fullName.lastIndexOf(File.separator);
		final String path = fullName.substring(0, lastIndex);
		return new File(path);
	}
}
