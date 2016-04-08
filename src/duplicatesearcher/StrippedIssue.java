package duplicatesearcher;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class StrippedIssue implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5824632540290288567L;
	private final int number, userId;
	private final Date dateCreated, dateUpdated;
	private final Set<Label> labels;
	private final TermFrequencyCounter all, title, body, comments;
	private final boolean closed;
	private final String state;
	private boolean flaggedBad = false;

	public StrippedIssue(final Issue issue, final List<Comment> comments)
	{
		this.number = issue.getNumber();
		this.dateCreated = issue.getCreatedAt();
		this.dateUpdated = issue.getUpdatedAt();
		this.userId = issue.getUser().getId();
		this.labels = new HashSet<Label>(issue.getLabels());
		
		this.closed = issue.getClosedAt() != null;
		this.state = issue.getState();

		this.title = new TermFrequencyCounter();
		title.add(issue.getTitle());
		
		this.body = new TermFrequencyCounter();
		body.add(issue.getBody());
		
		this.comments = mapStrings(comments);
		
		this.all = new TermFrequencyCounter();
		this.all.add(title);
		this.all.add(body);
		this.all.add(this.comments);
	}
	
	public StrippedIssue(int number, int userId, Date created, Date updated, Set<Label> labels, String title, String body, Collection<Comment> comments, boolean closed, String state)
	{
		this.number = number;
		this.userId = userId;
		
		this.dateCreated = created;
		this.dateUpdated = updated;
		
		this.labels = labels;
		
		this.title = new TermFrequencyCounter();
		this.title.add(title);
		
		this.body = new TermFrequencyCounter();
		this.body.add(body);
		this.comments = mapStrings(comments);
		
		this.all = new TermFrequencyCounter();
		this.all.add(title);
		this.all.add(body);
		this.all.add(this.comments);
		
		this.closed = closed;
		this.state = state;
	}

	private TermFrequencyCounter mapStrings(Collection<Comment> commentData)
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
		if(title.size() + body.size() < 8)
			System.out.println("Possible low quality/non-viable issue: " + number);
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
		return all.getWeight(token);
	}
	
	public FrequencyCounter getAll()
	{
		return all;
	}
	
	public FrequencyCounter getTitle()
	{
		return title;
	}
	
	public FrequencyCounter getBody()
	{
		return body;
	}
	
	public FrequencyCounter getComments()
	{
		return comments;
	}
	{
	}

	public Set<Label> getLabels()
	{
		return labels;
	}

	public int getUserId()
	{
		return userId;
	}

	public Date getDateUpdated()
	{
		return dateUpdated;
	}

	public Date getDateCreated()
	{
		return dateCreated;
	}

	public boolean isClosed()
	{
		return closed;
	}

	public String getState()
	{
		return state;
	}
	
	@Override
	public String toString()
	{
		return "" + number;
	}
}
