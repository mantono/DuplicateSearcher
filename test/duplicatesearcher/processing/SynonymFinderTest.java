package duplicatesearcher.processing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;

import duplicatesearcher.Token;

public class SynonymFinderTest
{
	@Test
	public void testProcess() throws IOException, InterruptedException, ClassNotFoundException
	{
		SynonymFinder s = new SynonymFinder();
		final Token synonym = s.process(new Token("dog"));
		System.out.println(synonym);
	}
}
