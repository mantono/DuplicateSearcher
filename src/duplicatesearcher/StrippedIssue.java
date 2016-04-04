package duplicatesearcher;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.rmi.CORBA.Tie;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;

import duplicatesearcher.analysis.frequency.FrequencyCounter;
import duplicatesearcher.analysis.frequency.TermFrequencyCounter;

/**
 * StrippedIssue is a simplified version of {@link Issue}, containing only the
 * data that is crucial to similarity analysis. All textual data is stored in a
 * way that makes more sense from a statistical point of view rather than a
 * semantical.
 * 
 */
public class StrippedIssue
{
	private final int number, userId;
	private final Date dateCreated;
	private final Set<Label> labels;
	private final TermFrequencyCounter title, body, comments;
	private boolean flaggedBad = false;

	public StrippedIssue(final Issue issue, final List<Comment> comments)
	{
		this.number = issue.getNumber();
		this.dateCreated = issue.getCreatedAt();
		this.userId = issue.getUser().getId();
		this.labels = new HashSet<Label>(issue.getLabels());

		this.title = new TermFrequencyCounter();
		title.add(issue.getTitle());
		
		this.body = new TermFrequencyCounter();
		body.add(issue.getBody());
		
		this.comments = mapStrings(comments);
	}

	private TermFrequencyCounter mapStrings(List<Comment> commentData)
	{
		final TermFrequencyCounter freq = new TermFrequencyCounter();
		for(Comment comment : commentData)
			freq.add(comment.getBody());
		
		return freq;
	}

	/**
	 * Check if this issue contains enough textual data to actually be analyzed
	 * and compared to other issues (after stop lists and are applied)
	 * 
	 * @return true if it is considered viable for analysis, else false.
	 */
	public boolean isViable()
	{
		return !flaggedBad;
	}

	/**
	 * @return the number
	 */
	public int getNumber()
	{
		return number;
	}
	
	public double getWeight(final Token token)
	{
		return body.getWeight(token);
	}

	public Set<Token> wordSet()
	{
		Set<Token> tokens = new HashSet<Token>(body.size()*2);
		tokens.addAll(title.getTokens());
		tokens.addAll(body.getTokens());
		tokens.addAll(comments.getTokens());
		return tokens;
	}
}
