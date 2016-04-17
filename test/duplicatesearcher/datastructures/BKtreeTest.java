package duplicatesearcher.datastructures;

import static org.junit.Assert.*;

import java.util.ArrayList;

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
		tree.insert("a");
	}

	@Test
	public void testHashCode()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testBKtreeCharSequence()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testBKtreeCharSequenceInt()
	{
		fail("Not yet implemented");
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
	public void testFind()
	{
		tree.insert("test");
		tree.insert("testxxx");
		tree.insert("testzzz");
		
		System.out.println(tree.find(lev, new ArrayList<CharSequence>(), "test"));
		System.out.println(tree.find(lev, new ArrayList<CharSequence>(), "testa"));
		System.out.println(tree.find(lev, new ArrayList<CharSequence>(), "testxxx"));
	}

	@Test
	public void testCompareTo()
	{
		fail("Not yet implemented");
	}

}
