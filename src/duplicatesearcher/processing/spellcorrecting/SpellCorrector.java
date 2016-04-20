package duplicatesearcher.processing.spellcorrecting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import duplicatesearcher.Token;
import duplicatesearcher.datastructures.BKtree;
import duplicatesearcher.processing.TokenProcessor;
import duplicatesearcher.processing.Tokenizer;


public class SpellCorrector implements TokenProcessor {
	private final HashSet<Token> dictionary;
	private final BKtree tree;
	
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
	public Token process(final Token token){
		if(!isMisspelled(token))
			return token;
		return correctWord(token);
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
		final int threshold = textSubject.length()/5;
		if(threshold == 0)
			return new Token(textSubject);
		
		SortedMap<Integer, List<CharSequence>> foundWords = tree.find(textSubject, threshold);
		return tokenFrom(foundWords, textSubject);
	}
	
	public Token correctWord(CharSequence textSubject, final int threshold){
		SortedMap<Integer, List<CharSequence>>foundWords = tree.find(textSubject, threshold);
		return tokenFrom(foundWords, textSubject);
	}

	private Token tokenFrom(SortedMap<Integer, List<CharSequence>> foundWords, CharSequence textSubject)
	{
		for(Entry<Integer, List<CharSequence>> entry : foundWords.entrySet())
			for(CharSequence word : entry.getValue())
				if(Tokenizer.isToken(word))
					return new Token(word);
		
		return new Token(textSubject);
	}
}
