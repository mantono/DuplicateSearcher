package duplicatesearcher.processing.stoplists;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;

import duplicatesearcher.retrieval.FileDownloader;
import research.experiment.datacollectiontools.ApiClient;

public class TemplateLoader
{
	private final RepositoryId repo;
	private final Path templatePath;
	private final static String DIR = "issue_templates";
	
	public TemplateLoader(RepositoryId repo) throws URISyntaxException
	{
		this.repo = repo;
		final String repoPath = repo.getOwner() + File.separator + repo.getName();
		this.templatePath = new File(DIR + File.separator + repoPath).toPath();
	}
	
	public SortedMap<LocalDateTime, StopList> retrieveStopList() throws IOException
	{
		if(Files.exists(templatePath))
			return loadFiles(templatePath);
		FileDownloader downloader = new FileDownloader(repo, DIR);
		final int downloaded = downloader.downloadAllVersionsOf("ISSUE_TEMPLATE");
		if(downloaded == 0)
			System.out.println("No ISSUE_TEMPLATE found for " + repo);
		return loadFiles(templatePath);
	}
	
	private SortedMap<LocalDateTime, StopList> loadFiles(Path templatePath) throws IOException
	{
		SortedMap<LocalDateTime, StopList> stoplists = new TreeMap<LocalDateTime, StopList>();
		File dir = templatePath.toFile();
		  File[] directoryListing = dir.listFiles();
		  if (directoryListing != null)
		    for (File child : directoryListing)
		    	loadFile(child, stoplists);
		  
		return stoplists;
	}

	private void loadFile(File listFile, SortedMap<LocalDateTime, StopList> stoplists) throws IOException
	{
		final StopList list = new StopList(listFile);
		final long unixEpoch = Long.parseLong(listFile.getName());
		final LocalDateTime date = LocalDateTime.ofEpochSecond(unixEpoch, 0, ZoneOffset.UTC);
		stoplists.put(date, list);
	}
}
