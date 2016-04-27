package duplicatesearcher.retrieval;

import java.io.IOException;

import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
		GitHubRequest request = new GitHubRequest();
		request.setResponseContentType("application/json");
		request.setUri("/search/code/?q="+ fileName + "in:path+repo:" + repo.getOwner() + "/" + repo.getName());
		GitHubResponse response = client.get(request);
		JsonObject jsonResponse = (JsonObject) response.getBody();
		JsonArray jsonArray = jsonResponse.getAsJsonArray("item");
		final String path = jsonArray.get(2).getAsString();
		return path;
	}
	
	
}
