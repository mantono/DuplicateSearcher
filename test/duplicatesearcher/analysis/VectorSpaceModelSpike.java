package duplicatesearcher.analysis;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class VectorSpaceModelSpike
{

	@Test
	public void test()
	{
		final String document1 = "a a a b c d x x";
		final String document2 = "b c c d x";
		final String document3 = "c d e f g b";
		
		final String query = "a a c d x";
		
		final TermFrequencyCounter termFreq = new TermFrequencyCounter();
		final InverseDocumentFrequencyCounter inverseDocFreq = new InverseDocumentFrequencyCounter();
		
		termFreq.add(document1);
		inverseDocFreq.add(document1);
		
	}

}
