package duplicatesearcher.retrieval;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.egit.github.core.RepositoryId;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mantono.ghapic.Client;
import com.mantono.ghapic.Response;

public class FileDownloader
{
	private final static String BASE = "https://api.github.com/";
	private final String filePath;
	private final String repoPath;
	private final Map<String, LocalDateTime> commitTimestamp;
	private final Client client;

	public FileDownloader(RepositoryId repo, String path)
	{
		this.filePath = path;
		this.repoPath = repo.getOwner() + "/" + repo.getName();
		this.commitTimestamp = new HashMap<String, LocalDateTime>();

	}

	public String getFilePath(final String fileName) throws IOException, InterruptedException, ExecutionException, TimeoutException
	{
		Future<Response> request = client.submitRequest("/search/code?q=" + fileName + "+in:path+repo:" + repoPath);
		Response response = request.get(10, TimeUnit.SECONDS);
		List<String> body = response.getBody();
		
		//URL url = new URL(BASE + "search/code?q=" + fileName + "+in:path+repo:" + repoPath);
		String path = null;

//		try(BufferedReader reader = new BufferedReader(
//				new InputStreamReader(url.openStream(), "UTF-8")))
//		{
			for(String line : body)
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
		//}
		return path;
	}

	public String[] getShaHashes(String filePath) throws UnsupportedEncodingException, IOException
	{
		URL url = new URL(BASE + "repos/" + repoPath + "/commits?path=" + filePath);

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
				final String commitHash = jobj.get("sha").toString().replaceAll("\"", "");
				commits[i++] = commitHash;
				LocalDateTime timestamp = getTimeForCommit(jobj);
				commitTimestamp.put(commitHash, timestamp);
			}
			return commits;
		}
	}

	private LocalDateTime getTimeForCommit(JsonObject jobj)
	{
		JsonObject commitData = jobj.getAsJsonObject("commit");
		JsonObject authorData = commitData.getAsJsonObject("author");
		JsonElement dateElement = authorData.get("date");
		final String date = dateElement.getAsString();
		final ZonedDateTime formattedDate = ZonedDateTime.parse(date, DateTimeFormatter.ISO_ZONED_DATE_TIME);
		return formattedDate.toLocalDateTime();
	}

	public URL[] getRawUrls(String[] commits, String filePath) throws UnsupportedEncodingException, IOException
	{
		final URL[] filesToDownload = new URL[commits.length];
		
		for(int i = 0; i < commits.length; i++)
		{
			URL url = new URL(BASE + "repos/"+repoPath+"/contents/" + filePath + "?ref=" + commits[i]);
			
			try(BufferedReader reader = new BufferedReader(
					new InputStreamReader(url.openStream(), "UTF-8")))
			{
				String line = reader.readLine();
				JsonParser jp = new JsonParser();
				JsonElement e = jp.parse(line);
				JsonObject jo = e.getAsJsonObject();
				final String downloadUrl = jo.get("download_url").toString();
				final String urlCleaned = downloadUrl.replaceAll("\"", "");
				filesToDownload[i] = new URL(urlCleaned);
			}
		}
		return filesToDownload;
	}

	public int downloadFiles(URL[] urls, String[] commits) throws IOException
	{
		int i = 0;
		for(; i < urls.length; i++)
		{
			final LocalDateTime fileTime = commitTimestamp.get(commits[i]);
			final File file = new File(
					filePath + "/" + repoPath + "/" + fileTime.toEpochSecond(ZoneOffset.UTC));
			file.mkdirs();
			final Path fileName = file.toPath();
			try(InputStream in = urls[i].openStream())
			{
				Files.copy(in, fileName, StandardCopyOption.REPLACE_EXISTING);
			}
		}
		return i;
	}
	
	public int downloadAllVersionsOf(final String fileName) throws IOException, InterruptedException, ExecutionException, TimeoutException
	{
		final String filePath = getFilePath(fileName);
		final String[] shaHashes = getShaHashes(filePath);
		final URL[] urls = getRawUrls(shaHashes, filePath);
		final int retrieved = downloadFiles(urls, shaHashes);
		return retrieved;
	}

}
