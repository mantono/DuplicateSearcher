package duplicatesearcher.processing;

import static org.junit.Assert.*;

import org.junit.Test;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import duplicatesearcher.Token;

public class StemmerTest
{

	@Test
	public void testTests()
	{
		Stemmer stemmer = new Stemmer();
		final Token input = new Token("tests");
		final Token expected = new Token("test");
		stemmer.setCurrentToken(input);
		stemmer.stem();
		final Token output = stemmer.getCurrentToken();
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testTesting()
	{
		Stemmer stemmer = new Stemmer();
		final Token input = new Token("testing");
		final Token expected = new Token("test");
		stemmer.setCurrentToken(input);
		stemmer.stem();
		final Token output = stemmer.getCurrentToken();
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testSpeak()
	{
		Stemmer stemmer = new Stemmer();
		final Token input = new Token("speak");
		final Token expected = new Token("speak");
		stemmer.setCurrentToken(input);
		stemmer.stem();
		final Token output = stemmer.getCurrentToken();
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testSpoke()
	{
		Stemmer stemmer = new Stemmer();
		final Token input = new Token("spoke");
		final Token expected = new Token("spoke");
		stemmer.setCurrentToken(input);
		stemmer.stem();
		final Token output = stemmer.getCurrentToken();
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testSpoken()
	{
		Stemmer stemmer = new Stemmer();
		final Token input = new Token("spoken");
		final Token expected = new Token("spoken");
		stemmer.setCurrentToken(input);
		stemmer.stem();
		final Token output = stemmer.getCurrentToken();
		
		assertEquals(expected, output);
	}
	
	@Test
	public void testTest()
	{
		Stemmer stemmer = new Stemmer();
		final Token input = new Token("test");
		stemmer.setCurrentToken(input);
		stemmer.stem();
		final Token output = stemmer.getCurrentToken();
		
		assertEquals(input, output);
	}

}
