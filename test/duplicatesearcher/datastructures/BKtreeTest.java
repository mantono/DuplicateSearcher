package duplicatesearcher.datastructures;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

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
		SortedMap<Integer, List<CharSequence>> foundWords = tree.find("test", 1);
		assertTrue(foundWords.get(foundWords.firstKey()).contains("test"));
		
		foundWords = tree.find("abc", 1);
		assertTrue(foundWords.toString(), foundWords.get(foundWords.firstKey()).contains(("abc")));
	}
	
	@Test
	public void testFindWordWithDistanceTwo()
	{
		SortedMap<Integer, List<CharSequence>> foundWords = tree.find("test", 2);
		assertTrue(foundWords.get(foundWords.firstKey()).contains("test"));
		assertEquals(1, foundWords.size());
		
		foundWords = tree.find("ab", 2);
		assertTrue(foundWords.get(1).contains("a"));
		assertTrue(foundWords.get(0).contains("ab"));
		assertTrue(foundWords.get(1).contains("abc"));
		assertEquals(2, foundWords.size());
	}
}
