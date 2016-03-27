package duplicatesearcher.processing;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class TokenizerTest
{

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void testGetProcessedData()
	{
		final String input = "This is four tokens.";
		Tokenizer tokenizer = new Tokenizer(input);
		
		final Set<String> output = tokenizer.getProcessedData();
		final Set<String> expected = new HashSet<String>(4);
		expected.add("this");
		expected.add("is");
		expected.add("four");
		expected.add("tokens");
		
		assertEquals(expected, output);
	}

	@Test
	public void testPurge()
	{
		fail("Not yet implemented");
	}
	
	@Test
	public void testLowerCase()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testSplitRegularSentence()
	{
		final String input = "This is four tokens.";
		Tokenizer tokenizer = new Tokenizer(input);
		final String[] output = tokenizer.split(input);
		final String[] expected = {"This", "is", "four", "tokens."};
		
		assertArrayEquals(expected, output);
	}
	
	@Test
	public void testSplitSentenceWithMultipleWhitespace()
	{
		final String input = "  This sentence has   a  lot of  whitespace.  ";
		Tokenizer tokenizer = new Tokenizer(input);
		final String[] output = tokenizer.split(input);
		final String[] expected = {"This", "sentence", "has", "a", "lot", "of", "whitespace."};
		
		assertArrayEquals(expected, output);
	}
	
	@Test
	public void testSplitSentenceWithNewLine()
	{
		final String input = "This sentence has two \n new line\n characters.";
		Tokenizer tokenizer = new Tokenizer(input);
		final String[] output = tokenizer.split(input);
		final String[] expected = {"This", "sentence", "has", "two", "new", "line", "characters."};
		
		assertArrayEquals(expected, output);
	}
	
	@Test
	public void testSplitSentenceWithDash()
	{
		final String input = "We believe in fruit loops and/or AI.";
		Tokenizer tokenizer = new Tokenizer(input);
		final String[] output = tokenizer.split(input);
		final String[] expected = {"We", "believe", "in", "fruit", "loops", "and", "or", "AI."};
		
		assertArrayEquals(expected, output);
	}

	@Test
	public void testConvertToSet()
	{
		fail("Not yet implemented");
	}

}
