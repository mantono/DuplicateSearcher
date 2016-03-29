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
		final FrequencyCounter fc = new FrequencyCounter(input);
		final Map<String, Integer> frequency = fc.getTokenFrequency();
		
		final Map<String, Integer> expected = new HashMap<String, Integer>();
		expected.put("one", 1);
		expected.put("two", 2);
		expected.put("three", 3);
		
		assertEquals(expected, frequency);
	}

}
