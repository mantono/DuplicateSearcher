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
import duplicatesearcher.processing.CodeExtractor;

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
	private TermFrequencyCounter all, title, body, comments, code, labels;
	private final boolean closed;
	private final String state;
	private boolean flaggedBad = false;

	public StrippedIssue(final Issue issue, final List<Comment> comments)
	{
		this.number = issue.getNumber();
		this.dateCreated = issue.getCreatedAt();
		this.dateUpdated = issue.getUpdatedAt();
		this.userId = issue.getUser().getId();
		this.labels = new TermFrequencyCounter();
		labels.addLabels(issue.getLabels());
		
		this.closed = issue.getClosedAt() != null;
		this.state = issue.getState();
		
		CodeExtractor ce = new CodeExtractor(issue, comments);
		Set<String> foundCode = ce.extractCode();
		this.code = new TermFrequencyCounter();
		this.code.addAll(foundCode);

		this.title = new TermFrequencyCounter();
		title.add(issue.getTitle());
		
		this.body = new TermFrequencyCounter();
		body.add(issue.getBody());
		
		this.comments = mapStrings(comments);
	}

	public StrippedIssue(final Issue issue)
	{
		this.number = issue.getNumber();
		this.dateCreated = issue.getCreatedAt();
		this.dateUpdated = issue.getUpdatedAt();
		this.userId = issue.getUser().getId();
		this.labels = new TermFrequencyCounter();
		labels.addLabels(issue.getLabels());
		
		this.closed = issue.getClosedAt() != null;
		this.state = issue.getState();
		
		this.title = new TermFrequencyCounter();
		title.add(issue.getTitle());
		
		this.body = new TermFrequencyCounter();
		body.add(issue.getBody());
		
		this.comments = new TermFrequencyCounter();
	}
	
	public StrippedIssue(int number, int userId, Date created, Date updated, Set<Label> labelSet, String title, String body, Collection<Comment> comments, boolean closed, String state)
	{
		this.number = number;
		this.userId = userId;
		
		this.dateCreated = created;
		this.dateUpdated = updated;
		
		this.labels = new TermFrequencyCounter();
		labels.addLabels(labelSet);
		
		this.title = new TermFrequencyCounter();
		this.title.add(title);
		
		this.body = new TermFrequencyCounter();
		this.body.add(body);
		this.comments = mapStrings(comments);
		
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
	
	public TermFrequencyCounter getAll()
	{
		return all;
	}
	
	public TermFrequencyCounter getTitle()
	{
		return title;
	}
	
	public TermFrequencyCounter getBody()
	{
		return body;
	}
	
	public TermFrequencyCounter getComments()
	{
		return comments;
	}
	
	public void removeComments()
	{
		comments = new TermFrequencyCounter();
		if(all != null && all.size() > 0)
		{
			all = new TermFrequencyCounter();
			all.add(title);
			all.add(body);
		}
	}
	
	public void createFrequencyCounterForAll()
	{
		all = new TermFrequencyCounter();
		all.add(title);
		all.add(body);
		all.add(this.comments);
	}

	public TermFrequencyCounter getLabels()
	{
		return labels;
	}
	
	public TermFrequencyCounter getCode()
	{
		return code;
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
