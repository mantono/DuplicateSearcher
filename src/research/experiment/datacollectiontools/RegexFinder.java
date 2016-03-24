package research.experiment.datacollectiontools;

import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Issue;

public class RegexFinder
{
	private final IRepositoryIdProvider repository;

	public RegexFinder(final IRepositoryIdProvider repo)
	{
		this.repository = repo;
	}

	/**
	 * Check if a {@link List} of {@link Comment} objects contains a
	 * {@link Comment} which has the word <em>dupe</em> or <em>duplicate</em>
	 * <strong>and</strong> has a reference to another issue within the same
	 * project repository.
	 * 
	 * @param issueComments list of comment that will be checked.
	 * @return true if it is tagged as a duplicate, else false.
	 */
	public boolean isTaggedAsDuplicate(List<Comment> issueComments)
	{
		for(Comment comment : issueComments)
			if(isTaggedAsDuplicate(comment))
				return true;

		return false;
	}

	/**
	 * Check if a {@link Comment} contains the word <em>dupe</em> or
	 * <em>duplicate</em> <strong>and</strong> has a reference to another issue
	 * within the same project repository.
	 * 
	 * @param comment
	 * @return true if it is tagged as a duplicate, else false.
	 */
	public boolean isTaggedAsDuplicate(Comment comment)
	{
		final boolean containsDupeKeyword = commentContainsDupe(comment);
		final boolean hasReference = hasReferenceToOtherIssue(comment);
		return containsDupeKeyword && hasReference;
	}

	/**
	 * Check if a {@link Comment} contains a reference to another issue in the
	 * same repository.
	 * 
	 * @param comment the comment that will be checked.
	 * @return true if a valid reference is found, else false.
	 */
	public boolean hasReferenceToOtherIssue(Comment comment)
	{
		// TODO improve with regex!
		// https://github.com/$USER/$REPO/issues/INTEGER
		final String body = comment.getBody();
		return body.contains("https://github.com/") && body.contains("/issues/");
	}

	/**
	 * Parse a {@link Comment} to see if it contains either the word
	 * <em>dupe</dupe> or <em>duplicate</em>.
	 * 
	 * @param comment to be checked.
	 * @return true if either key words are found, else false.
	 */
	public boolean commentContainsDupe(Comment comment)
	{
		// TODO Fix BUG in this code. If the link to another reference contains one
		// of the keywords, this method will return true, event tough only typed
		// occurrences should count as hits.
		final String content = comment.getBody().toLowerCase();
		return content.contains("dupe") || content.contains("duplicate");
	}

	/**
	 * Parse and retrieves the {@link Issue} identification number from a
	 * reference to another issue in a {@link Comment}.
	 * 
	 * @param comment that will be parsed.
	 * @return the identification number, or <code>-1</code> if no valid
	 * reference was found.
	 */
	public int getIssueNumber(Comment comment)
	{
		// TODO Auto-generated method stub
		return -1;
	}
}
