package research.experiment.datacollectiontools;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.egit.github.core.*;
import org.junit.Before;
import org.junit.Test;

public class RegexFinderTest
{
	private RepositoryId repo = new RepositoryId("mantono", "DuplicateSearcher");
	private RegexFinder finder = new RegexFinder(repo);
	private Comment noDupeNoRef, dupe1NoRef, dupe2NoRef, noDupeRef1, noDupeRef2, noDupeBadRefDupeInName, dupeBadRef, duplicate1, duplicate2;

	@Before
	public void setUpNoDupes() throws Exception
	{
		noDupeNoRef = new Comment();
		noDupeNoRef.setBody("This comment is whacko.");

		dupe1NoRef = new Comment();
		dupe1NoRef.setBody("This issue is not a dupe.");

		dupe2NoRef = new Comment();
		dupe2NoRef.setBody("This issue is not a duplicate either.");

		noDupeRef1 = new Comment();
		noDupeRef1.setBody("This comment has link to another issue https://github.com/mantono/DuplicateSearcher/issues/5");
		
		noDupeRef2 = new Comment();
		noDupeRef2.setBody("This comment has link to another issue https://github.com/mantono/duplicateSearcher/issues/5");
		
		noDupeBadRefDupeInName = new Comment();
		noDupeBadRefDupeInName.setBody("This comment has link to another issue https://github.com/duplicate/duplicateSearcher/issues/5");

		dupeBadRef = new Comment();
		dupeBadRef.setBody("This comment has the word duplicate in it and links to another issue https://github.com/mantono/Dimensions/issues/1 but it is in another repository.");
	}

	@Before
	public void setupDupes() throws Exception
	{
		duplicate1 = new Comment();
		duplicate1.setBody("This is a dupe https://github.com/mantono/DuplicateSearcher/issues/1");

		duplicate2 = new Comment();
		duplicate2.setBody("This is a duplicate of https://github.com/mantono/DuplicateSearcher/issues/3");
	}

	@Test
	public void testIsTaggedAsDuplicate()
	{
		assertFalse(finder.isTaggedAsDuplicate(noDupeNoRef));
		assertFalse(finder.isTaggedAsDuplicate(dupe1NoRef));
		assertFalse(finder.isTaggedAsDuplicate(dupe2NoRef));
		assertFalse(finder.isTaggedAsDuplicate(noDupeRef1));
		assertFalse(finder.isTaggedAsDuplicate(dupeBadRef));

		assertTrue(finder.isTaggedAsDuplicate(duplicate1));
		assertTrue(finder.isTaggedAsDuplicate(duplicate2));
	}

	@Test
	public void testHasReferenceToOtherIssue()
	{
		assertFalse(finder.hasReferenceToOtherIssue(noDupeNoRef));
		assertFalse(finder.hasReferenceToOtherIssue(dupe1NoRef));
		assertFalse(finder.hasReferenceToOtherIssue(dupe2NoRef));
		assertFalse(finder.hasReferenceToOtherIssue(dupeBadRef));

		assertTrue(finder.hasReferenceToOtherIssue(noDupeRef1));
		assertTrue(finder.hasReferenceToOtherIssue(duplicate1));
		assertTrue(finder.hasReferenceToOtherIssue(duplicate1));
	}

	@Test
	public void testCommentContainsDupe()
	{
		assertFalse(finder.commentContainsDupe(noDupeNoRef));
		assertFalse(finder.commentContainsDupe(noDupeRef1));
		assertFalse(finder.commentContainsDupe(noDupeRef2));
		assertFalse(finder.commentContainsDupe(noDupeBadRefDupeInName));

		assertTrue(finder.commentContainsDupe(dupe1NoRef));
		assertTrue(finder.commentContainsDupe(dupe2NoRef));
		assertTrue(finder.commentContainsDupe(dupeBadRef));
		assertTrue(finder.commentContainsDupe(duplicate1));
		assertTrue(finder.commentContainsDupe(duplicate2));
	}

	@Test
	public void testGetIssueNumber()
	{
		assertEquals(-1, finder.getIssueNumber(noDupeNoRef));
		assertEquals(-1, finder.getIssueNumber(dupe1NoRef));
		assertEquals(-1, finder.getIssueNumber(dupe2NoRef));
		assertEquals(-1, finder.getIssueNumber(dupeBadRef));

		assertEquals(5, finder.getIssueNumber(noDupeRef1));
		assertEquals(1, finder.getIssueNumber(duplicate1));
		assertEquals(3, finder.getIssueNumber(duplicate2));
	}

}
