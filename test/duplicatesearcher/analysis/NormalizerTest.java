package duplicatesearcher.analysis;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import duplicatesearcher.Token;

public class NormalizerTest
{
	private Map<Token, Double> weights;
	
	@Before
	public void setup()
	{
		weights = new HashMap<Token, Double>(6);
		weights.put(new Token("x"), 0.8);
		weights.put(new Token("y"), 0.6);
		weights.put(new Token("z"), 0.1);
		weights.put(new Token("a"), 0.0);
		weights.put(new Token("b"), 3.14159);
		weights.put(new Token("c"), 0.05);
	}

	@Test
	public void test()
	{
		Map<Token, Double> normalizedMap = Normalizer.normalizeVector(weights);
		double sum = 0;
		
		for(Double value : normalizedMap.values())
			sum += Math.pow(value, 2);
		
		assertEquals(1, sum, 0.00001);
	}

}
