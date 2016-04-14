package duplicatesearcher.spellcorrecting;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import duplicatesearcher.processing.spellcorrecting.SpellCorrector;

public class SpellCorrectorTest {
	private SpellCorrector sc;
	
	@Before
	public void setUp(){
		sc = new SpellCorrector();
	}
	
	@Test
	public void correctSimpleWordTest(){
		String test = "hejs";
		test = sc.correctWord(test);
		assertEquals("hej", test);
	}	
	
	@Test
	public void correctAboveThresholdWordTest(){
		String test = "hejsanhejsan";
		test = sc.correctWord(test);
		assertEquals("hejsanhejsan", test);
	}	
	
	@Test
	public void correctWordListTest(){
		List<String> list = new ArrayList<String>();
		List<String> expected = new ArrayList<String>();
		
		list.add("hejs"); //hej
		list.add("slanka"); //slank
		list.add("gurkansa"); //oförändrad
		list.add("gurkan"); //gurka
		list.add("regeringen");//oförändrad
		list.add("issueprocessor");//oförändrad
		
		list = sc.correctWords(list);
		
		expected.add("hej");
		expected.add("slank");
		expected.add("gurkansa");
		expected.add("gurka");
		expected.add("regeringen");
		expected.add("issueprocessor");
		
		assertEquals(expected, list);
		
	}
	
	
}