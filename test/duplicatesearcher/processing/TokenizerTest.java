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
		final String input = "* * * Well then, 'THIS' could \n certainly \t @prove-- to be a-challenge...\0";
		Tokenizer tokenizer = new Tokenizer(input);

		final Token[] output = tokenizer.getTokens();
		final Token[] expected = new Token[]{new Token("well"), new Token("then"), new Token("this"),
				new Token("could"), new Token("certainly"), new Token("prove"), new Token("to"), new Token("be"),
				new Token("a"), new Token("challenge")};
		
		for(Token t : output)
			System.out.println(t);
		
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
	public void testRemoveApostrophe()
	{
		final String input = "We're and we'll, because it's our destiny. Shouldn't it be?";
		final String expected = "We are and we will, because it our destiny. Shouldnt it be?";
		
		Tokenizer tokenizer = new Tokenizer("");
		final String output = tokenizer.removeApostrophes(input);
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testRemoveURLsWithHttpsUrl()
	{
		final String input = "Here is a link https://github.com/mantono/DuplicateSearcher that we want to be removed.";
		final String expected = "Here is a link  that we want to be removed.";
		
		Tokenizer tokenizer = new Tokenizer("");
		final String output = tokenizer.removeURLs(input);
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testRemoveURLsWithHttpUrl()
	{
		final String input = "Here is a link http://github.com/mantono/DuplicateSearcher that we want to be removed.";
		final String expected = "Here is a link  that we want to be removed.";
		
		Tokenizer tokenizer = new Tokenizer("");
		final String output = tokenizer.removeURLs(input);
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testRemoveURLsWithUrlAndDashes()
	{
		final String input = "Here is a link github.com/mantono/DuplicateSearcher that we want to be removed.";
		final String expected = "Here is a link  that we want to be removed.";
		
		Tokenizer tokenizer = new Tokenizer("");
		final String output = tokenizer.removeURLs(input);
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testRemoveURLsWithDomainName()
	{
		final String input = "Here is a link github.com that we want to be removed.";
		final String expected = "Here is a link  that we want to be removed.";
		
		Tokenizer tokenizer = new Tokenizer("");
		final String output = tokenizer.removeURLs(input);
		
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
	
	@Test
	public void testRemoveNumbers()
	{
		Tokenizer tokenizer = new Tokenizer("");
		final String regularIntegers = "1 2 3";
		String result = tokenizer.removeNumbers(regularIntegers);
		assertEquals("  ", result);
		
		final String decimals = "2.0 3.11";
		result = tokenizer.removeNumbers(decimals);
		assertEquals(" ", result);
		
		final String commaDecimals = "2,0 3,11";
		result = tokenizer.removeNumbers(commaDecimals);
		assertEquals(" ", result);
		
		final String commaDelimiteer = "2,300,000 4,111";
		result = tokenizer.removeNumbers(commaDecimals);
		assertEquals(" ", result);
		
		final String dotDelimiteer = "2.300.000 4.111";
		result = tokenizer.removeNumbers(commaDecimals);
		assertEquals(" ", result);
		
		final String variables = "v14 token2";
		result = tokenizer.removeNumbers(variables);
		assertEquals("v14 token2", result);
	}
	
	@Test
	public void testRemoveUsername()
	{
		Tokenizer tokenizer = new Tokenizer("");
		
		final String userMentions = "@a-asdas-asd-asd @eu1289 @s-s @mantono";
		String result = tokenizer.removeUsernames(userMentions);
		assertEquals("   ", result);
		
		final String notUsernames = "my.email@adress.com @-notValidUsername @notvalid- ";
		result = tokenizer.removeUsernames(notUsernames);
		assertEquals("my.email@adress.com @-notValidUsername @notvalid- ", result);
	}
}
