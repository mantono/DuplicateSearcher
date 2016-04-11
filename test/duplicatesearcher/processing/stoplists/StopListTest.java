package duplicatesearcher.processing.stoplists;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import duplicatesearcher.Token;

public class StopListTest
{
	private StopList list1, list2;

	@Before
	public void setUp() throws Exception
	{
		list1 = new StopList(new File("stoplists/test/list1"));
		list2 = new StopList(new File("stoplists/test/list2"));
	}

	@Test(expected=NoSuchFileException.class)
	public void testNonExistingFileForConstructor() throws IOException
	{
		new StopList(new File("/dev/null/get/lost"));
	}

	@Test
	public void testRemoveStopWordsAllWordsOnOneLine()
	{
		Set<Token> tokens = new HashSet<Token>();
		tokens.add(new Token("hello"));
		tokens.add(new Token("test"));
		tokens.add(new Token("and"));
		tokens.add(new Token("if"));
		tokens.add(new Token("but"));
		tokens.add(new Token("yes"));
		
		assertEquals(6, tokens.size());
		final int removedWords = list1.removeStopWords(tokens);
		assertEquals(4, removedWords);
		assertEquals(2, tokens.size());
	}
	
	@Test
	public void testRemoveStopWordsEachWordOnSeparateLine()
	{
		Set<Token> tokens = new HashSet<Token>();
		tokens.add(new Token("hello"));
		tokens.add(new Token("by"));
		tokens.add(new Token("and"));
		tokens.add(new Token("if"));
		tokens.add(new Token("but"));
		tokens.add(new Token("yes"));
		
		assertEquals(6, tokens.size());
		final int removedWords = list2.removeStopWords(tokens);
		assertEquals(2, removedWords);
		assertEquals(4, tokens.size());
	}

	@Test
	public void testGetStopWords()
	{
		Set<Token> tokens = new HashSet<Token>();
		tokens.add(new Token("and"));
		tokens.add(new Token("if"));
		tokens.add(new Token("but"));
		tokens.add(new Token("yes"));
		tokens.add(new Token("no"));
		
		assertEquals(tokens, list1.getStopWords());
	}

}
