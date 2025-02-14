package research.experiment.datacollectiontools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.egit.github.core.client.GitHubClient;

public class ApiClient extends GitHubClient
{
	private final String token = readTokenFromFile();

	public ApiClient()
	{
		super.setOAuth2Token(token);
	}

	private String readTokenFromFile()
	{
		try(final FileReader fileReader = new FileReader(".token"))
		{
			final BufferedReader bfReader = new BufferedReader(fileReader);
			return bfReader.readLine();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		return "BadToken";
	}
	
	public String getToken()
	{
		return token;
	}
	
}
