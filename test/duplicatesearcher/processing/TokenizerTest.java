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
	public void testGetProcessedDataSimple()
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
	public void testGetProcessedDataComplex()
	{
		final String input = "* * * Well then, 'THIS' could \n certainly \t @prove__ to be a-challenge...\0";
		Tokenizer tokenizer = new Tokenizer(input);
		
		final Set<String> output = tokenizer.getProcessedData();
		final Set<String> expected = new HashSet<String>(4);
		expected.add("well");
		expected.add("then");
		expected.add("this");
		expected.add("could");
		expected.add("certainly");
		expected.add("prove");
		expected.add("to");
		expected.add("be");
		expected.add("a");
		expected.add("challenge");
		
		assertEquals(expected, output);
	}

	@Test
	public void testPurge()
	{
		final String input = "this ::is:: the;: ;way: it @all looks in the  end... just''* #words%";
		final String expected = "this   is   the    way  it  all looks in the  end    just     words ";
		
		Tokenizer tokenizer = new Tokenizer("");
		final String output = tokenizer.purge(input);
		
		assertEquals(expected, output);
	}

	@Test
	public void testLowerCase()
	{
		final String input = "I AM KIND OF CRAZY SO I WRITE EVERYTHING IN CAPS!!";
		Tokenizer tokenizer = new Tokenizer(input);
		final String[] output = tokenizer.split(input);
		tokenizer.lowerCase(output);
		final String[] expected = {"i", "am", "kind", "of", "crazy", "so", "i", "write", "everything", "in", "caps!!"};

		assertArrayEquals(expected, output);
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
	public void testSplitSentenceWithHyphens()
	{
		final String input = "This-sentence-has a --lot-- - of - hyphens.-";
		Tokenizer tokenizer = new Tokenizer(input);
		final String[] output = tokenizer.split(input);
		final String[] expected = {"This", "sentence", "has", "a", "lot", "of", "hyphens."};

		assertArrayEquals(expected, output);
	}

	@Test
	public void testSplitSentenceWithUnderscore()
	{
		final String input = "This looks like a_C_variable.";
		Tokenizer tokenizer = new Tokenizer(input);
		final String[] output = tokenizer.split(input);
		final String[] expected = {"This", "looks", "like", "a", "C", "variable."};

		assertArrayEquals(expected, output);
	}

}
