package duplicatesearcher.processing.stoplists;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import duplicatesearcher.Token;
import duplicatesearcher.processing.TokenProcessor;
import duplicatesearcher.processing.Tokenizer;

public class StopList implements TokenProcessor
{
	private final Set<Token> stopWords = new HashSet<Token>();

	public StopList(final File stopListFile) throws IOException
	{
		if(!stopListFile.exists())
			throw new NoSuchFileException("File " + stopListFile.getCanonicalPath() + " could not be found.");
		readFileContent(stopListFile);
	}

	private void readFileContent(File stopListFile) throws IOException
	{
		final Path path = Paths.get(stopListFile.toURI());
		List<String> listLines = Files.readAllLines(path);
		for(String line : listLines)
		{
			final Tokenizer tokenizer = new Tokenizer(line);
			for(Token token : tokenizer.getTokens())
				stopWords.add(token);
		}
	}

	@Override
	public Token process(Token token)
	{
		if(!stopWords.contains(token))
			return token;
		return null;
	}

	/**
	 * Retrieves the content of this stop list.
	 * 
	 * @return a set of tokens which consists of the current stop list.
	 */
	public Set<Token> getStopWords()
	{
		return stopWords;
	}
}
