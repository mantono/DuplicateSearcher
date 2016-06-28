package duplicatesearcher.retrieval;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.egit.github.core.RepositoryId;
import org.junit.Ignore;
import org.junit.Test;

public class FileDownloaderTest
{

	@Ignore
	@Test
	public void testFilePath() throws IOException, InterruptedException, ExecutionException, TimeoutException
	{
		final RepositoryId repo = new RepositoryId("mantono", "DuplicateSearcher");
		FileDownloader downloader = new FileDownloader(repo, "issue_templates");
		final String path = downloader.getFilePath("ISSUE_TEMPLATE");
		assertEquals("/ISSUE_TEMPLATE", path);
	}
	
	@Test
	public void testGetShaHashes() throws IOException
	{
		final RepositoryId repo = new RepositoryId("mantono", "DuplicateSearcher");
		FileDownloader downloader = new FileDownloader(repo, "issue_templates");
		final int commits = downloader.getShaHashes("/ISSUE_TEMPLATE").length;
	}
	
	@Test
	public void testGetUrls() throws UnsupportedEncodingException, IOException
	{
		final String[] commits  = new String[]{"93317b3eb184ae5f98198fb3617e5c7fd23a3f00", "ea43d177b2d451b93237291b81df80d9b405e977"};
		final RepositoryId repo = new RepositoryId("mantono", "DuplicateSearcher");
		FileDownloader downloader = new FileDownloader(repo, "issue_templates");
		final URL[] urls = downloader.getRawUrls(commits, "/ISSUE_TEMPLATE");
	}
	
	@Test
	public void testDownloadFiles() throws UnsupportedEncodingException, IOException
	{
		final RepositoryId repo = new RepositoryId("mantono", "DuplicateSearcher");
		FileDownloader downloader = new FileDownloader(repo, "issue_templates");
		final String[] commits = downloader.getShaHashes("/ISSUE_TEMPLATE");
		final URL[] urls = downloader.getRawUrls(commits, "/ISSUE_TEMPLATE");
		final int downloadedFiles = downloader.downloadFiles(urls, commits);
	}
		
	

}
