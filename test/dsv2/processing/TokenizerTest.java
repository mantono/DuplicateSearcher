package dsv2.processing;

import static org.junit.Assert.*;

import org.junit.Test;

public class TokenizerTest
{
	@Test
	public void testPurge()
	{
		final String input = "this ::is:: the;: ;way: it @all looks in //the  end... just''* #words%";
		final String expected = "this   is   the    way  it  all looks in   the  end    just     words ";
		
		final String output = Tokenizer.purge(input);

		assertEquals(expected, output);
	}

	@Test
	public void testSplitRegularSentence()
	{
		final String input = "This is four tokens.";
		final String[] output = Tokenizer.split(input);
		final String[] expected = {"This", "is", "four", "tokens."};

		assertArrayEquals(expected, output);
	}

	@Test
	public void testSplitSentenceWithMultipleWhitespace()
	{
		final String input = "  This sentence has   a  lot of  whitespace.  ";
		final String[] output = Tokenizer.split(input);
		final String[] expected = {"This", "sentence", "has", "a", "lot", "of", "whitespace."};

		assertArrayEquals(expected, output);
	}

	@Test
	public void testSplitSentenceWithNewLine()
	{
		final String input = "This sentence has two \n new line\n characters.";
		final String[] output = Tokenizer.split(input);
		final String[] expected = {"This", "sentence", "has", "two", "new", "line", "characters."};

		assertArrayEquals(expected, output);
	}

	@Test
	public void testIsToken()
	{
		assertTrue(Tokenizer.isToken("good"));
		assertFalse(Tokenizer.isToken("this one has whitespace"));
		assertFalse(Tokenizer.isToken(" "));
		assertFalse(Tokenizer.isToken("*bad*"));
		assertFalse(Tokenizer.isToken("_bad_"));
		assertFalse(Tokenizer.isToken("_bad_"));
		assertFalse(Tokenizer.isToken("bad."));
		assertFalse(Tokenizer.isToken("BAD"));
		assertFalse(Tokenizer.isToken(null));
	}

	
}
