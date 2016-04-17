package duplicatesearcher.spellcorrecting;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import duplicatesearcher.Token;
import duplicatesearcher.analysis.frequency.TermFrequencyCounter;
import duplicatesearcher.processing.spellcorrecting.SpellCorrector;

public class SpellCorrectorTest
{
	private SpellCorrector testDict;

	@Before
	public void setUp() throws IOException
	{
		testDict = new SpellCorrector(new File("dictionary/test.txt"));
	}

	@Test
	public void correctSimpleWordTest()
	{
		Token test = new Token("hejs");
		test = testDict.correctWord(test);
		assertEquals(new Token("hej"), test);
	}

	@Test
	public void correctAboveThresholdWordTest()
	{
		Token test = new Token("hejsanhejsan");
		test = testDict.correctWord(test);
		assertEquals(new Token("hejsanhejsan"), test);
	}

	@Test
	public void correctWordListTest()
	{
		TermFrequencyCounter tokens = new TermFrequencyCounter();
		Set<Token> expected = new HashSet<Token>();

		tokens.add("hejs"); // hej
		tokens.add("slanka"); // slank
		tokens.add("gurkansa"); // oförändrad
		tokens.add("gurkan"); // gurka
		tokens.add("regeringen");// oförändrad
		tokens.add("issueprocessor");// oförändrad

		final int changed = testDict.process(tokens);
		
		assertEquals(3, changed);

		expected.add(new Token("hej"));
		expected.add(new Token("slank"));
		expected.add(new Token("gurkansa"));
		expected.add(new Token("gurka"));
		expected.add(new Token("regeringen"));
		expected.add(new Token("issueprocessor"));

		assertEquals(expected, tokens.getTokens());

	}
	
	@Test
	public void isMisspelledTest()
	{
		assertTrue(testDict.isMisspelled(new Token("bla")));
		assertTrue(testDict.isMisspelled(new Token("felfelfel")));
		assertTrue(testDict.isMisspelled(new Token("")));
		assertFalse(testDict.isMisspelled(new Token("apelsin")));
		assertFalse(testDict.isMisspelled(new Token("gurka")));
		assertFalse(testDict.isMisspelled(new Token("slank")));
	}

	@Test
	public void tokenTest()
	{
		assertEquals(new Token("a"), new Token("a"));
	}

}