package duplicatesearcher.processing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;

import duplicatesearcher.Token;

public class SynonymFinderTest
{

	@Ignore
	@Test
	public void testGetSynonym() throws IOException, InterruptedException
	{
		SynonymFinder s = new SynonymFinder();
		s.testDict();
		Collection<String> syns = s.getSynonym("phone");
		System.out.println(syns);
	}

	@Test
	public void testProcess() throws IOException, InterruptedException
	{
		SynonymFinder s = new SynonymFinder();
		final Token synonym = s.process(new Token("rigor"));
		System.out.println(synonym);
	}

}
