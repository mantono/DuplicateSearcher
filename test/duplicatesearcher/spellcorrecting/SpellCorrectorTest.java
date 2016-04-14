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

public class SpellCorrectorTest {
	private SpellCorrector sc;
	
	@Before
	public void setUp() throws IOException{
			sc = new SpellCorrector(new File("dictionary/dict.txt"), 2);
	}
	
	@Test
	public void correctSimpleWordTest(){
		Token test = new Token("hejs");
		test = sc.correctWord(test);
		assertEquals(new Token("hej"), test);
	}	
	
	@Test
	public void correctAboveThresholdWordTest(){
		Token test = new Token("hejsanhejsan");
		test = sc.correctWord(test);
		assertEquals(new Token("hejsanhejsan"), test);
	}	
	
	@Test
	public void correctWordListTest(){
		List<Token> list = new ArrayList<Token>();
		List<Token> listTest = new ArrayList<Token>();
		
		list.add(new Token("hejs")); //hej
		list.add(new Token("slanka")); //slank
		list.add(new Token("gurkansa")); //oförändrad
		list.add(new Token("gurkan")); //gurka
		list.add(new Token("regeringen"));//oförändrad
		list.add(new Token("issueprocessor"));//oförändrad
		
		list = sc.correctWords(list);
		
		listTest.add(new Token("hej"));
		listTest.add(new Token("slank"));
		listTest.add(new Token("gurkansa"));
		listTest.add(new Token("gurka"));
		listTest.add(new Token("regeringen"));
		listTest.add(new Token("issueprocessor"));
		
		assertEquals(list, listTest);
		
	}
	
	@Test
	public void tokenTest(){
		assertEquals(new Token("a"), new Token("a"));
	}
	
	
}