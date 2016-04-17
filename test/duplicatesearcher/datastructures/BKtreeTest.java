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
		assertFalse(tree.insert(lev, "test"));
		assertTrue(tree.insert(lev, "testa"));
		assertFalse(tree.insert(lev, "testa"));
		assertTrue(tree.insert(lev, "tes"));
	}
	
	@Test
	public void testFind()
	{
		LevenshteinDistance levInsert = new LevenshteinDistance();
		tree.insert(levInsert, "test");
		tree.insert(levInsert, "testxxx");
		tree.insert(levInsert, "testzzz");
		
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
