package duplicatesearcher.processing.spellcorrecting;

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
import duplicatesearcher.analysis.frequency.TermFrequencyCounter;
import duplicatesearcher.processing.TokenProcessor;
import duplicatesearcher.processing.Tokenizer;


public class SpellCorrector implements TokenProcessor {
	private final LevenshteinDistance lev;
	private final HashSet<Token> dictionary;
	
	public SpellCorrector(final File dictionaryFile, int threshold) throws IOException {
		this.lev = new LevenshteinDistance(threshold);
		this.dictionary = new HashSet<Token>();
		
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
				dictionary.add(token);
		}
	}
	
	@Override
	public int process(final TermFrequencyCounter tokens){
		Set<Token> issueTokensCopy = new HashSet<Token>(tokens.getTokens());
		int spellCorrections = 0;
		
		for(Token token : issueTokensCopy)
		{
			if(!isMisspelled(token))
				continue;
			Token tokenSpellCorrected = correctWord(token);
			if(!token.equals(tokenSpellCorrected))
			{
				tokens.change(token, tokenSpellCorrected);
				spellCorrections++;
			}
		}
		
		return spellCorrections;
	}
	
	/**
	 * Check if a {@link CharSequence} is misspelled.
	 * It is considered correctly spelled if it exists in this
	 * instance's dictionary.
	 * @param token the input that will be checked.
	 * @return true if the input token is not correctly spelled,
	 * else false.
	 */
	public boolean isMisspelled(final CharSequence token){
		return !dictionary.contains(token);
	}
	
	public Token correctWord(Token textSubject){
		Token tmp = textSubject;
		int newDistance;
		int closestDistance = Integer.MAX_VALUE;
		
		for(Token word : dictionary){
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
