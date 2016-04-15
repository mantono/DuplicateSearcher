package duplicatesearcher.processing;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import duplicatesearcher.Token;

public class TokenizerTest
{
	@Test
	public void testGetProcessedDataSimple()
	{
		final String input = "This is four tokens.";
		Tokenizer tokenizer = new Tokenizer(input);

		final Token[] output = tokenizer.getTokens();
		final Token[] expected = new Token[]{new Token("this"), new Token("is"), new Token("four"),
				new Token("tokens")};
		assertArrayEquals(expected, output);
	}

	@Test
	public void testGetProcessedDataComplex()
	{
		final String input = "* * * Well then, 'THIS' could \n certainly \t @prove__ to be a-challenge...\0";
		Tokenizer tokenizer = new Tokenizer(input);

		final Token[] output = tokenizer.getTokens();
		final Token[] expected = new Token[]{new Token("well"), new Token("then"), new Token("this"),
				new Token("could"), new Token("certainly"), new Token("prove"), new Token("to"), new Token("be"),
				new Token("a"), new Token("challenge")};
		assertArrayEquals(expected, output);
	}

	@Test
	public void testPurge()
	{
		final String input = "this ::is:: the;: ;way: it @all looks in //the  end... just''* #words%";
		final String expected = "this   is   the    way  it  all looks in   the  end    just     words ";

		Tokenizer tokenizer = new Tokenizer("");
		final String output = tokenizer.purge(input);

		assertEquals(expected, output);
	}
	
	@Test
	public void testPurgeWithApostrophe()
	{
		final String input = "We're and we'll, because it's our destiny. Shouldn't it be?";
		final String expected = "We are and we will, because it our destiny. Shouldnt it be?";
		
		Tokenizer tokenizer = new Tokenizer("");
		final String output = tokenizer.removeApostrophes(input);
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testPurgeWithHttpsUrl()
	{
		final String input = "Here is a link https://github.com/mantono/DuplicateSearcher that we want to be removed.";
		final String expected = "Here is a link that we want to be removed";
		
		Tokenizer tokenizer = new Tokenizer("");
		final String output = tokenizer.purge(input);
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testPurgeWithHttpUrl()
	{
		final String input = "Here is a link http://github.com/mantono/DuplicateSearcher that we want to be removed.";
		final String expected = "Here is a link that we want to be removed";
		
		Tokenizer tokenizer = new Tokenizer("");
		final String output = tokenizer.purge(input);
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testPurgeWithUrlAndDashes()
	{
		final String input = "Here is a link github.com/mantono/DuplicateSearcher that we want to be removed.";
		final String expected = "Here is a link that we want to be removed";
		
		Tokenizer tokenizer = new Tokenizer("");
		final String output = tokenizer.purge(input);
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testPurgeWithUrl()
	{
		final String input = "Here is a link github.com that we want to be removed.";
		final String expected = "Here is a link that we want to be removed";
		
		Tokenizer tokenizer = new Tokenizer("");
		final String output = tokenizer.purge(input);
		
		assertEquals(expected, output);
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
	
	@Test
	public void testRemoveEmojis()
	{
		final String input = ":8ball: keep this :+1: :-1: :100: :and this:";
		final String expected = "  keep this       :and this:";
		Tokenizer tokenizer = new Tokenizer(input);
		final String output = tokenizer.removeEmojis(input);
		assertEquals(expected, output);
	}
}
