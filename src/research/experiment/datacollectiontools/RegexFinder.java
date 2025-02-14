package research.experiment.datacollectiontools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;

public class RegexFinder
{
	private final RepositoryId repository;

	public RegexFinder(final RepositoryId repo)
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
		final Matcher matcher = parseCommentForReferences(comment);
		return matcher.find();
	}

	private Matcher parseCommentForReferences(Comment comment)
	{
		final String regex = "#\\d+";
		final Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(comment.getBody());
	}

	/**
	 * Parse a {@link Comment} to see if it contains either the word
	 * <em>dupe</dupe> or <em>duplicate</em>.
	 * 
	 * @param comment to be checked.
	 * @return true if either keywords are found, else false.
	 */
	public boolean commentContainsDupe(Comment comment)
	{
		final String content = comment.getBody().toLowerCase();
		final String regex = "(\\b)[Dd]up(e|licate)\\b(?!/)";

		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(content);
		return matcher.find();
	}

	/**
	 * Parse and retrieves the {@link Issue} identification number from a
	 * reference to another issue in a {@link Comment}.
	 * 
	 * @param comment that will be parsed.
	 * @return a {@link List} of all the found identification numbers for issues
	 * being references to. An empty list is returned if the parsed comment does
	 * not contain any references to other issues.
	 */
	public List<Integer> getIssueNumber(Comment comment)
	{
		final String input = comment.getBody();
		final Matcher matcher = parseCommentForReferences(comment);
		final List<Integer> ids = new ArrayList<Integer>();

		while(matcher.find())
		{
			final String occurence = input.substring(matcher.start(), matcher.end());
			final String[] divided = occurence.split("#");
			final String number = divided[divided.length - 1];
			final int id = Integer.parseInt(number);
			ids.add(id);
		}

		return ids;
	}
}
