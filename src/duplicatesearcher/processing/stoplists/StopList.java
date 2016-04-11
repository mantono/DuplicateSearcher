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
import duplicatesearcher.processing.Tokenizer;

public class StopList
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

	/**
	 * Removes all tokens that are present in the stop word list from the given input set.
	 * @param input the {@link Set} from which stop words will be removed.
	 * @return the amount of words that were removed.
	 */
	public int removeStopWords(Set<Token> input)
	{
		final int sizeBefore = input.size();
		input.removeAll(stopWords);
		return sizeBefore - input.size();
	}

}
