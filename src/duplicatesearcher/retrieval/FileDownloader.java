package duplicatesearcher.retrieval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.LocalDateTime;

import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import research.experiment.datacollectiontools.ApiClient;

public class FileDownloader
{
	private final RepositoryId repo;
	private final GitHubClient client = new ApiClient();

	public FileDownloader(RepositoryId repo)
	{
		this.repo = repo;
	}

	public String getFilePath(final String fileName) throws IOException
	{
		URL url = new URL(
				"https://api.github.com/search/code?q=ISSUE_TEMPLATE+in:path+repo:mantono/DuplicateSearcher");
		String path = null;

		try(BufferedReader reader = new BufferedReader(
				new InputStreamReader(url.openStream(), "UTF-8")))
		{
			for(String line; (line = reader.readLine()) != null;)
			{
				String[] splitted = line.split("\\{");
				for(String s : splitted)
				{
					String[] values = s.split(",");
					for(String output : values)
					{
						if(output.contains("\"path\":\""))
						{
							path = output.split(":")[1].replaceAll("\"", "");
							break;
						}
					}
				}
			}
		}
		return path;
	}

	public String[] getShaHashes(String filePath) throws UnsupportedEncodingException, IOException
	{
		URL url = new URL("https://api.github.com/repos/mantono/DuplicateSearcher/commits?path=" + filePath);
		
		
		try(BufferedReader reader = new BufferedReader(
				new InputStreamReader(url.openStream(), "UTF-8")))
		{
			String line = reader.readLine();
			JsonParser jp = new JsonParser();
			JsonElement e = jp.parse(line);
			JsonArray ja = e.getAsJsonArray();
			final String[] commits = new String[ja.size()];
			
			int i = 0;
			for(JsonElement element : ja)
			{
				JsonObject jobj = element.getAsJsonObject();
				commits[i++] = jobj.get("sha").toString().replaceAll("\"", "");
			}
			return commits;
		}
	}

	public URL[] getRawUrls(String[] commits, String filePath) throws UnsupportedEncodingException, IOException
	{
		final URL[] filesToDownload = new URL[commits.length];
		
		for(int i = 0; i < commits.length; i++)
		{
			URL url = new URL("https://api.github.com/repos/mantono/DuplicateSearcher/contents/" + filePath + "?ref=" + commits[i]);
			
			try(BufferedReader reader = new BufferedReader(
					new InputStreamReader(url.openStream(), "UTF-8")))
			{
				String line = reader.readLine();
				JsonParser jp = new JsonParser();
				JsonElement e = jp.parse(line);
				JsonObject jo = e.getAsJsonObject();
				final String downloadUrl = jo.get("download_url").toString();
				filesToDownload[i] = new URL(downloadUrl);
			}
		}
		return filesToDownload;
	}

	public int downloadFiles(URL[] urls) throws IOException
	{
		int i = 0;
		for(URL url : urls)
		{
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			final String fileName = "issue_template/" + repo.getOwner() + File.pathSeparator + repo.getName() +  File.pathSeparator + LocalDateTime.now();
			FileOutputStream fos = new FileOutputStream(fileName);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			i++;
		}
		return i;
	}

}
