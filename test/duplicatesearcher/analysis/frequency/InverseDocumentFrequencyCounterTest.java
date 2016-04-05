package duplicatesearcher.analysis.frequency;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import duplicatesearcher.Token;
import duplicatesearcher.processing.Tokenizer;

public class InverseDocumentFrequencyCounterTest
{
	private InverseDocumentFrequencyCounter idfc;

	@Before
	public void setUp() throws Exception
	{
		idfc = new InverseDocumentFrequencyCounter();
	}

	@Test
	public void testGetWeight()
	{
		Set<Token> title = convertStrings("Graph based data structure");
		Set<Token> body = convertStrings("one two two three three three");
		Set<Token> comment = convertStrings("x y z");
		
		idfc.add(1, title);
		idfc.add(1, body);
		idfc.add(1, comment);
		
		assertEquals(1, idfc.getWeight(new Token("one")), 0.0000001);
		assertEquals(1, idfc.getWeight(new Token("three")), 0.0000001);
		
		idfc.add(1, new Token("one"));
		assertEquals(1, idfc.getWeight(new Token("one")), 0.0000001);
		idfc.add(2, new Token("foo"));		
		assertEquals(1.30102999566, idfc.getWeight(new Token("one")), 0.0000001);
		idfc.add(3, new Token("bar"));		
		assertEquals(1.4771212547, idfc.getWeight(new Token("one")), 0.0000001);

	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNonPositiveId()
	{
		Set<String> issueContent = new HashSet<String>(1);
		idfc.add(0, new Token("foo bar"));
	}

	@Test
	public void testAdd()
	{
		Set<Token> title = convertStrings("Graph based data structure");
		Set<Token> body = convertStrings("one two two three three three");
		Set<Token> comment = convertStrings("x y z");
		
		assertEquals(4, idfc.add(1, title));
		assertEquals(0, idfc.add(1, title));
		assertEquals(3, idfc.add(1, body));
		assertEquals(0, idfc.add(1, body));
		assertEquals(3, idfc.add(1, comment));
		assertEquals(0, idfc.add(1, comment));
		
		assertEquals(4, idfc.add(2, title));
	}
	
	private Set<Token> convertStrings(final String input)
	{
		Tokenizer tokenizer = new Tokenizer(input);
		Set<Token> tokens = new HashSet<Token>(Arrays.asList(tokenizer.getTokens()));
		return tokens;
	}

}
