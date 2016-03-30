package duplicatesearcher;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class FrequencyCounterTest
{

	@Test
	public void testGetTokenFrequency()
	{
		final String input = "one two two three three three";
		final TermFrequencyCounter fc = new TermFrequencyCounter();
		final int added = fc.add(input);
		
		assertEquals(6, added);
		assertEquals(0, fc.getTokenFrequency("zero"));
		assertEquals(1, fc.getTokenFrequency("one"));
		assertEquals(2, fc.getTokenFrequency("two"));
		assertEquals(3, fc.getTokenFrequency("three"));
	}

}
