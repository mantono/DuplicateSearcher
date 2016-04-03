package duplicatesearcher.analysis.frequency;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class TermFrequencyCounterTest
{
	private TermFrequencyCounter tfc;

	@Before
	public void setUp() throws Exception
	{
		tfc = new TermFrequencyCounter();
		tfc.add("one two two three three three");
	}

	@Test
	public void testGetWeight()
	{
		assertEquals(1, tfc.getWeight("one"), 0.00001);
		tfc.add("one");
		assertEquals(1.693147180, tfc.getWeight("one"), 0.00001);
		tfc.add("one");
		assertEquals(2.098612288, tfc.getWeight("one"), 0.00001);
	}
	
	@Test
	public void testGetWeightWithZeroTokens()
	{
		assertEquals(0, tfc.getWeight("zero"), 0.00001);
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
		assertEquals(1, tfc.getTokenFrequency("one"));
		assertEquals(2, tfc.getTokenFrequency("two"));
		assertEquals(3, tfc.getTokenFrequency("three"));	
	}

	@Test
	public void testGetTokens()
	{
		Set<String> tokens = tfc.getTokens();
		Set<String> expected = new HashSet<String>(6);
		expected.add("one");
		expected.add("two");
		expected.add("three");
		
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
		assertEquals(3, tfc.remove("three"));
		assertEquals(0, tfc.getTokenFrequency("three"));
	}

	@Test
	public void testChange()
	{
		assertEquals(2, tfc.getTokenFrequency("two"));
		assertTrue(tfc.change("two", "new"));
		assertFalse(tfc.change("two", "new"));
		assertEquals(0, tfc.getTokenFrequency("two"));
		assertEquals(2, tfc.getTokenFrequency("new"));
	}

	@Test
	public void testIncrement()
	{
		assertEquals(2, tfc.increment("one"));
	}
	
	@Test
	public void testIncrementNewToken()
	{
		assertEquals(1, tfc.increment("newToken"));
	}
	
	@Test
	public void testIncrementNull()
	{
		assertEquals(0, tfc.increment(null));
	}
	
	@Test
	public void testIncrementZeroLengthToken()
	{
		assertEquals(0, tfc.increment(""));
	}

}
