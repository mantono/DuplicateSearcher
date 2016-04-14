package duplicatesearcher;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TokenTest
{
	private final static Token ONE = new Token("one");
	private final static Token TWO = new Token("two");
	private final static Token THREE = new Token("three");

	@Test
	public void testHashCode()
	{
		Token token1 = new Token("one");
		Token token2 = new Token("two");
		
		assertFalse(token1.hashCode() == token2.hashCode());
		
		Token token1_ = new Token("one");
		
		assertEquals(token1.hashCode(), token1_.hashCode());
		
	}

	@Test(expected=IllegalArgumentException.class)
	public void testTokenWithBadInput()
	{
		Token token = new Token("BAD");
	}

	@Test
	public void testEqualsObject()
	{
		assertTrue(ONE.equals(new Token("one")));
		assertFalse(ONE.equals(TWO));
	}
	
	@Test
	public void testEqualsWithString()
	{
		final String string = "test";
		final Token token = new Token("test");
		assertTrue(token.equals(string));
		assertEquals(string.hashCode(), token.hashCode());
	}
	
	@Test
	public void testEqualsWithCharSequence()
	{
		final String string = "test";
		final CharSequence token = new Token("test");
		assertTrue(token.equals(string));
		assertEquals(string.hashCode(), token.hashCode());
	}

}
