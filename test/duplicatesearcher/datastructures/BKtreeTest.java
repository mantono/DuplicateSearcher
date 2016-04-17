package duplicatesearcher.datastructures;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import duplicatesearcher.processing.spellcorrecting.LevenshteinDistance;

public class BKtreeTest
{
	private BKtree tree;
	private final LevenshteinDistance lev = new LevenshteinDistance(2);

	@Before
	public void setUp() throws Exception
	{
		this.tree = new BKtree("test");
		tree.insert("a");
		tree.insert("ab");
		tree.insert("abc");
		tree.insert("xyz");
	}

	@Test
	public void testInsert()
	{
		assertFalse(tree.insert("test"));
		assertTrue(tree.insert("testa"));
		assertFalse(tree.insert("testa"));
		assertTrue(tree.insert("tes"));
	}
	
	@Test
	public void testFindSameWord()
	{
		Collection<CharSequence> foundWords = tree.find("test", 1);
		assertTrue(foundWords.contains("test"));
		
		foundWords = tree.find("abc", 1);
		assertTrue(foundWords.toString(), foundWords.contains("abc"));
	}
	
	@Test
	public void testFindWordWithDistanceTwo()
	{
		Collection<CharSequence> foundWords = tree.find("test", 2);
		assertTrue(foundWords.contains("test"));
		assertEquals(1, foundWords.size());
		
		foundWords = tree.find("ab", 2);
		assertTrue(foundWords.contains("a"));
		assertTrue(foundWords.contains("ab"));
		assertTrue(foundWords.contains("abc"));
		assertEquals(3, foundWords.size());
	}
}
