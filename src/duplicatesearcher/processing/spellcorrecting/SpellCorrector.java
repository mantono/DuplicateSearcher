package duplicatesearcher.processing.spellcorrecting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import duplicatesearcher.Token;
import duplicatesearcher.analysis.frequency.TermFrequencyCounter;
import duplicatesearcher.datastructures.BKtree;
import duplicatesearcher.processing.TokenProcessor;
import duplicatesearcher.processing.Tokenizer;


public class SpellCorrector implements TokenProcessor {
	private final HashSet<Token> dictionary;
	private final BKtree tree;
	private final Map<Token, Token> corrections = new HashMap<Token, Token>();
	
	public SpellCorrector(final File dictionaryFile) throws IOException {
		this.dictionary = new HashSet<Token>();
		this.tree = new BKtree(new Token("word"));
		
		if(!dictionaryFile.exists())
			throw new NoSuchFileException("File " + dictionaryFile.getCanonicalPath() + " could not be found.");
		addDictionary(dictionaryFile);
	}
	

	public int addDictionary(File dictionaryFile) throws IOException
	{
		final Path path = Paths.get(dictionaryFile.toURI());
		List<String> listLines = Files.readAllLines(path);
		int addedWords = 0;
		for(String line : listLines)
		{
			final Tokenizer tokenizer = new Tokenizer(line);
			for(Token token : tokenizer.getTokens())
				if(dictionary.add(token) && tree.insert(line))
					addedWords++;
		}
		return addedWords;
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
				corrections.put(token, tokenSpellCorrected);
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
	public boolean isMisspelled(final Token token){
		return !dictionary.contains(token);
	}
	
	public Token correctWord(CharSequence textSubject){
		final int threshold = (int) Math.round(Math.log(textSubject.length()-0.2)); 
		SortedMap<Integer, List<CharSequence>> foundWords = tree.find(textSubject, threshold);
		return tokenFrom(foundWords, textSubject);
	}
	
	public Token correctWord(CharSequence textSubject, final int threshold){
		SortedMap<Integer, List<CharSequence>>foundWords = tree.find(textSubject, threshold);
		return tokenFrom(foundWords, textSubject);
	}


	private Token tokenFrom(SortedMap<Integer, List<CharSequence>> foundWords, CharSequence textSubject)
	{
		if(foundWords.isEmpty())
			return new Token(textSubject);
		List<CharSequence> listOfBestWords = foundWords.get(foundWords.firstKey());
		final Token correctToken = new Token(listOfBestWords.get(0));
		return correctToken;
	}
	
	public Map<Token, Token> getCorrections()
	{
		return corrections;
	}

}
