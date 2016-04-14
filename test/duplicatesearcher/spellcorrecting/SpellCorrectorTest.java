package duplicatesearcher.spellcorrecting;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import duplicatesearcher.Token;
import duplicatesearcher.processing.spellcorrecting.SpellCorrector;

public class SpellCorrectorTest
{
	private SpellCorrector sc;

	@Before
	public void setUp() throws IOException
	{
		sc = new SpellCorrector(new File("dictionary/test.txt"), 2);
	}

	@Test
	public void correctSimpleWordTest()
	{
		Token test = new Token("hejs");
		test = sc.correctWord(test);
		assertEquals(new Token("hej"), test);
	}

	@Test
	public void correctAboveThresholdWordTest()
	{
		Token test = new Token("hejsanhejsan");
		test = sc.correctWord(test);
		assertEquals(new Token("hejsanhejsan"), test);
	}

	@Test
	public void correctWordListTest()
	{
		List<Token> list = new ArrayList<Token>();
		List<Token> expected = new ArrayList<Token>();

		list.add(new Token("hejs")); // hej
		list.add(new Token("slanka")); // slank
		list.add(new Token("gurkansa")); // of�r�ndrad
		list.add(new Token("gurkan")); // gurka
		list.add(new Token("regeringen"));// of�r�ndrad
		list.add(new Token("issueprocessor"));// of�r�ndrad

		list = sc.correctWords(list);

		expected.add(new Token("hej"));
		expected.add(new Token("slank"));
		expected.add(new Token("gurkansa"));
		expected.add(new Token("gurka"));
		expected.add(new Token("regeringen"));
		expected.add(new Token("issueprocessor"));

		assertEquals(expected, list);

	}

	@Test
	public void tokenTest()
	{
		assertEquals(new Token("a"), new Token("a"));
	}

}