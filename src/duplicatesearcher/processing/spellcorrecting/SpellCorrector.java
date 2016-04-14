package duplicatesearcher.processing.spellcorrecting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import duplicatesearcher.Token;
import duplicatesearcher.processing.Tokenizer;


public class SpellCorrector {
	static LevenshteinDistance lev; 
	static HashSet<Token> words;
	
	public SpellCorrector(final File dictionaryFile, int threshold) throws IOException {
		lev = new LevenshteinDistance(threshold);
		
		if(!dictionaryFile.exists())
			throw new NoSuchFileException("File " + dictionaryFile.getCanonicalPath() + " could not be found.");
		readFileContent(dictionaryFile);
	}
	

	private void readFileContent(File dictionaryFile) throws IOException
	{
		final Path path = Paths.get(dictionaryFile.toURI());
		List<String> listLines = Files.readAllLines(path);
		for(String line : listLines)
		{
			final Tokenizer tokenizer = new Tokenizer(line);
			for(Token token : tokenizer.getTokens())
				words.add(token);
		}
	}
	
	public static List<Token> correctWords(List<Token> list){
		List<Token> correctedWords = new ArrayList<>();
		
		for(Token word : list){
			correctedWords.add(correctWord(word));
		}
		
		return correctedWords;
	}
	
	public static Token correctWord(Token textSubject){
		Token tmp = textSubject;
		int newDistance;
		int closestDistance = Integer.MAX_VALUE;
		
		for(Token word : words){
			newDistance = lev.apply(textSubject, word);
			
			if(newDistance == -1)
				continue;
			else if(newDistance == 0)
				return textSubject;
			else if(newDistance<closestDistance){
				tmp = word;
				closestDistance = newDistance;
			}
		}
		
		return tmp;
	}	
}
