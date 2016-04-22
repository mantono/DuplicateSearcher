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
import duplicatesearcher.analysis.frequency.TermFrequencyCounter;

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
		Token output, input;

		input = new Token("hello");
		output = list1.process(input);
		assertEquals(input, output);

		input = new Token("test");
		output = list1.process(input);
		assertEquals(input, output);

		input = new Token("and");
		output = list1.process(input);
		assertEquals(null, output);

		input = new Token("if");
		output = list1.process(input);
		assertEquals(null, output);

		input = new Token("but");
		output = list1.process(input);
		assertEquals(null, output);

		input = new Token("yes");
		output = list1.process(input);
		assertEquals(null, output);
	}
	
	@Test
	public void testRemoveStopWordsEachWordOnSeparateLine()
	{
		Token output, input;

		input = new Token("hello");
		output = list2.process(input);
		assertEquals(input, output);

		input = new Token("by");
		output = list2.process(input);
		assertEquals(null, output);

		input = new Token("if");
		output = list2.process(input);
		assertEquals(input, output);

		input = new Token("but");
		output = list2.process(input);
		assertEquals(input, output);

		input = new Token("yes");
		output = list2.process(input);
		assertEquals(input, output);
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
