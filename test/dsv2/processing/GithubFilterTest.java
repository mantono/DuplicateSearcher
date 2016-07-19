package dsv2.processing;

import static org.junit.Assert.*;

import java.util.EnumSet;

import org.junit.Test;

import duplicatesearcher.Token;

public class GithubFilterTest
{
	@Test
	public void testGetProcessedDataSimple()
	{
		EnumSet<GithubFilter> filter = EnumSet.allOf(GithubFilter.class);
		final String input = "This is four tokens.";
		Tokenizer tokenizer = new Tokenizer(filter);

		final Token[] output = tokenizer.tokenize(input);
		final Token[] expected = new Token[]{new Token("this"), new Token("is"), new Token("four"),
				new Token("tokens")};
		assertArrayEquals(expected, output);
	}

	@Test
	public void testGetProcessedDataComplex()
	{
		final String input = "* * * Well then, 'THIS' could \n certainly \t @prove-- to be a-challenge...\0";
		EnumSet<GithubFilter> filter = EnumSet.allOf(GithubFilter.class);
		Tokenizer tokenizer = new Tokenizer(filter);

		final Token[] output = tokenizer.tokenize(input);
		final Token[] expected = new Token[]{new Token("well"), new Token("then"), new Token("this"),
				new Token("could"), new Token("certainly"), new Token("prove"), new Token("to"), new Token("be"),
				new Token("a"), new Token("challenge")};

		assertArrayEquals(expected, output);
	}

	@Test
	public void testRemoveURLsWithHttpsUrl()
	{
		final String input = "Here is a link https://github.com/mantono/DuplicateSearcher that we want to be removed.";
		final String expected = "Here is a link  that we want to be removed.";

		EnumSet<GithubFilter> filter = EnumSet.of(GithubFilter.URL);
		Tokenizer tokenizer = new Tokenizer(filter);
		final String output = tokenizer.filter(input);

		assertEquals(expected, output);
	}

	@Test
	public void testRemoveURLsWithDashUrl()
	{
		final String input = "https://commons.apache.org/sandbox/commons-text/jacoco/org.apache.commons.text.similarity/LevenshteinDistance.java.html";
		final String expected = "";

		EnumSet<GithubFilter> filter = EnumSet.of(GithubFilter.URL);
		Tokenizer tokenizer = new Tokenizer(filter);
		final String output = tokenizer.filter(input);

		assertEquals(expected, output);
	}

	@Test
	public void testRemoveURLsWithDashInDomain()
	{
		final String input = "http://test-this.com/resource";
		final String expected = "";

		EnumSet<GithubFilter> filter = EnumSet.of(GithubFilter.URL);
		Tokenizer tokenizer = new Tokenizer(filter);
		final String output = tokenizer.filter(input);

		assertEquals(expected, output);
	}

	@Test
	public void testRemoveURLsWithHttpUrl()
	{
		final String input = "Here is a link http://github.com/mantono/DuplicateSearcher that we want to be removed.";
		final String expected = "Here is a link  that we want to be removed.";

		EnumSet<GithubFilter> filter = EnumSet.of(GithubFilter.URL);
		Tokenizer tokenizer = new Tokenizer(filter);
		final String output = tokenizer.filter(input);

		assertEquals(expected, output);
	}

	@Test
	public void testRemoveURLsWithUrlAndDashes()
	{
		final String input = "Here is a link github.com/mantono/DuplicateSearcher that we want to be removed.";
		final String expected = "Here is a link  that we want to be removed.";

		EnumSet<GithubFilter> filter = EnumSet.of(GithubFilter.DOMAIN);
		Tokenizer tokenizer = new Tokenizer(filter);
		final String output = tokenizer.filter(input);

		assertEquals(expected, output);
	}

	@Test
	public void testRemoveURLsWithDomainName()
	{
		final String input = "Here is a link github.com that we want to be removed.";
		final String expected = "Here is a link  that we want to be removed.";

		EnumSet<GithubFilter> filter = EnumSet.of(GithubFilter.DOMAIN);
		Tokenizer tokenizer = new Tokenizer(filter);
		final String output = tokenizer.filter(input);

		assertEquals(expected, output);
	}

	@Test
	public void testRemoveEmojis()
	{
		final String input = ":8ball: :speak_no_evil: keep this :+1: :-1: :100: :and this:";
		final String expected = "  keep this    :and this:";
		EnumSet<GithubFilter> filter = EnumSet.of(GithubFilter.EMOJI);
		Tokenizer tokenizer = new Tokenizer(filter);
		final String output = tokenizer.filter(input);
		assertEquals(expected, output);
	}

	@Test
	public void testRemoveNumbers()
	{
		EnumSet<GithubFilter> filter = EnumSet.of(GithubFilter.NUMBER);
		Tokenizer tokenizer = new Tokenizer(filter);
		final String regularIntegers = "1 2 3";
		String result = tokenizer.filter(regularIntegers);
		assertEquals("  ", result);

		final String decimals = "2.0 3.11";
		result = tokenizer.filter(decimals);
		assertEquals(" ", result);

		final String commaDecimals = "2,0 3,11";
		result = tokenizer.filter(commaDecimals);
		assertEquals(" ", result);

		final String commaDelimiteer = "2,300,000 4,111";
		result = tokenizer.filter(commaDecimals);
		assertEquals(" ", result);

		final String dotDelimiteer = "2.300.000 4.111";
		result = tokenizer.filter(commaDecimals);
		assertEquals(" ", result);

		final String variables = "v14 token2";
		result = tokenizer.filter(variables);
		assertEquals("v14 token2", result);
	}

	@Test
	public void testRemoveUsername()
	{
		EnumSet<GithubFilter> filter = EnumSet.of(GithubFilter.USERNAME);
		Tokenizer tokenizer = new Tokenizer(filter);

		final String userMentions = "@a-asdas-asd-asd @eu1289 @s-s @mantono";
		String result = tokenizer.filter(userMentions);
		assertEquals("   ", result);

		final String notUsernames = "my.email@adress.com @-notValidUsername @notvalid- ";
		result = tokenizer.filter(notUsernames);
		assertEquals("my.email@adress.com @-notValidUsername @notvalid- ", result);
	}

}
