package duplicatesearcher.spellcorrecting;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import duplicatesearcher.Token;
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
		Token test = new Token("apelsiin");
		test = testDict.correctWord(test);
		assertEquals(new Token("apelsin"), test);
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
		assertSpell("hejs", "hejs"); // |"hejs"| < 5, so no spell correction
		assertSpell("slank", "slanka");
		assertSpell("gurkansa", "gurkansa");
		assertSpell("gurka", "gurkan");
		assertSpell("regeringen", "regeringen");
		assertSpell("issueprocessor", "issueprocessor");
	}
	
	private void assertSpell(final String expected, final String input)
	{
		final Token output = testDict.correctWord(input);
		assertEquals(expected, output.toString());
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
	
	@Ignore
	@Test
	public void performanceTest() throws IOException
	{
		final SpellCorrector largeDict = new SpellCorrector(new File("/usr/share/dict/words"));
		
		LocalDateTime startTime = LocalDateTime.now();
		
		Token token1 = largeDict.correctWord("ambigous", 2); //ambiguous
		Token token2 = largeDict.correctWord("kingdoom", 3); //kingdom
		Token token3 = largeDict.correctWord("heelp", 1); //help
		
		LocalDateTime endTime = LocalDateTime.now();
		Duration elpasedTime = Duration.between(startTime, endTime);
		
		// Check correctness
		assertEquals(new Token("ambiguous"), token1);
		assertEquals(new Token("kingdom"), token2);
		assertEquals(new Token("help"), token3);
		
		// Test performance
		assertTrue(elpasedTime.getSeconds() < 2);
		System.out.println(elpasedTime);
	}

}