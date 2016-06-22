package duplicatesearcher.analysis.frequency;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import duplicatesearcher.Token;

public class TermFrequencyCounterTest
{
	private TermFrequencyCounter tfc;
	private final static Token ONE = new Token("one");
	private final static Token TWO = new Token("two");
	private final static Token THREE = new Token("three");

	@Before
	public void setUp()
	{
		tfc = new TermFrequencyCounter();
		tfc.add("one two two three three three");
	}

	@Test
	public void testGetWeight()
	{
		assertEquals(1, tfc.getWeight(ONE), 0.00001);
		tfc.add("one");
		assertEquals(1.693147180, tfc.getWeight(ONE), 0.00001);
		tfc.add("one");
		assertEquals(2.098612288, tfc.getWeight(ONE), 0.00001);
	}
	
	@Test
	public void testGetWeightWithZeroTokens()
	{
		assertEquals(0, tfc.getWeight(new Token("zero")), 0.00001);
	}

	@Test
	public void testAdd()
	{
		TermFrequencyCounter terms = new TermFrequencyCounter();
		final int added = terms.add("one two two three three three");
		assertEquals(6, added);
	}

	@Test
	public void testGetTokenFrequency()
	{
		assertEquals(1, tfc.getTokenFrequency(ONE));
		assertEquals(2, tfc.getTokenFrequency(TWO));
		assertEquals(3, tfc.getTokenFrequency(THREE));	
	}

	@Test
	public void testGetTokens()
	{
		Set<Token> tokens = tfc.getTokens();
		Set<Token> expected = new HashSet<Token>(6);
		expected.add(ONE);
		expected.add(TWO);
		expected.add(THREE);
		
		assertEquals(expected, tokens);
	}

	@Test
	public void testSize()
	{
		assertEquals(3, tfc.size());
	}

	@Test
	public void testClear()
	{
		assertEquals(3, tfc.size());
		tfc.clear();
		assertEquals(0, tfc.size());
	}

	@Test
	public void testRemove()
	{
		assertEquals(3, tfc.remove(THREE));
		assertEquals(0, tfc.getTokenFrequency(new Token("three")));
	}

	@Test
	public void testChange()
	{
		assertEquals(2, tfc.getTokenFrequency(TWO));
		assertTrue(tfc.change(TWO, new Token("new")));
		assertFalse(tfc.change(TWO, new Token("new")));
		assertEquals(0, tfc.getTokenFrequency(TWO));
		assertEquals(2, tfc.getTokenFrequency(new Token("new")));
	}

	@Test
	public void testIncrement()
	{
		assertEquals(2, tfc.increment(ONE));
	}
	
	@Test
	public void testIncrementNewToken()
	{
		assertEquals(1, tfc.increment(new Token("newtoken")));
	}
	
	@Test
	public void testIncrementNull()
	{
		assertEquals(0, tfc.increment(null));
	}
	
	@Test
	public void testIncrementZeroLengthToken()
	{
		assertEquals(0, tfc.increment(new Token("")));
	}
	
	@Test
	public void testChangeAndCorrectSum()
	{
		final Token from = new Token("from");
		final Token to = new Token("to");
		
		TermFrequencyCounter tf = new TermFrequencyCounter();
		tf.add(from);
		tf.add(from);
		tf.add(to);
		tf.add(to);
		
		assertEquals(2, tf.getTokenFrequency(from));
		assertEquals(2, tf.getTokenFrequency(to));
		
		tf.change(from, to);
		assertEquals(4, tf.getTokenFrequency(to));
	}

}
