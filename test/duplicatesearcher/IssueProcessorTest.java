package duplicatesearcher;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.EnumSet;

import org.eclipse.egit.github.core.RepositoryId;
import org.junit.Test;

import duplicatesearcher.flags.ProcessingFlag;

public class IssueProcessorTest
{

	@Test
	public void testSomeTokens() throws ClassNotFoundException, IOException, InterruptedException, URISyntaxException
	{
		final RepositoryId repo = new RepositoryId("mantono", "DuplicateSearcher");
		final EnumSet<ProcessingFlag> flags = EnumSet.of(
				ProcessingFlag.SPELL_CORRECTION,
				ProcessingFlag.STOP_LIST_COMMON,
				ProcessingFlag.STOP_LIST_GITHUB,
				ProcessingFlag.SYNONYMS,
				ProcessingFlag.STEMMING,
				ProcessingFlag.FILTER_BAD);
		IssueProcessor isp = new IssueProcessor(repo, flags);


		final Token mhz = new Token("mhz");
		final Token crash = new Token("crash");
		final Token spelled = new Token("spelled");
		final Token sorry = new Token("sorry");
		final Token please = new Token("please");
		final Token of = new Token("of");

		assertEquals(mhz, isp.process(mhz));
		assertEquals(crash, isp.process(crash));
		assertEquals(new Token("spell"), isp.process(spelled));
		assertEquals(null, isp.process(sorry));
		assertEquals(null, isp.process(please));
		assertEquals(null, isp.process(of));
	}

}
