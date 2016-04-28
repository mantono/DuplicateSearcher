package duplicatesearcher;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;

import duplicatesearcher.analysis.IssueComponent;
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
	private final Map<IssueComponent, TermFrequencyCounter> componentCounters;
	private final boolean closed;
	private final String state;
	private boolean flaggedBad = false;

	public StrippedIssue(final Issue issue, final List<Comment> comments)
	{
		this.number = issue.getNumber();
		this.dateCreated = issue.getCreatedAt();
		this.dateUpdated = issue.getUpdatedAt();
		this.userId = issue.getUser().getId();
		
		this.closed = issue.getClosedAt() != null;
		this.state = issue.getState();
		
		this.componentCounters = new EnumMap<IssueComponent, TermFrequencyCounter>(IssueComponent.class);
		
		if(issue.getBody() == null)
			issue.setBody("");
		
		createTermCounters(issue, comments);
		removeLabelDuplicate();
	}

	private void removeLabelDuplicate()
	{
		TermFrequencyCounter labels = componentCounters.get(IssueComponent.LABELS);
		if(labels.getTokenFrequency(new Token("duplicate")) > 0)
			labels.remove("duplicate");
		else if(labels.getTokenFrequency(new Token("dupe")) > 0)
			labels.remove("dupe");
	}

	public StrippedIssue(final Issue issue)
	{
		this(issue, new LinkedList<Comment>());
	}

	private void createTermCounters(Issue issue, List<Comment> comments)
	{
		CodeExtractor ce = new CodeExtractor(issue, comments);
		Set<String> foundCode = ce.extractCode();
		final TermFrequencyCounter codeCounter = new TermFrequencyCounter();
		codeCounter.addAll(foundCode);
		this.componentCounters.put(IssueComponent.CODE, codeCounter);
		
		final TermFrequencyCounter titleCounter = new TermFrequencyCounter();
		titleCounter.add(issue.getTitle());
		this.componentCounters.put(IssueComponent.TITLE, titleCounter);
		
		final TermFrequencyCounter bodyCounter = new TermFrequencyCounter();
		bodyCounter.add(issue.getBody());
		this.componentCounters.put(IssueComponent.BODY, bodyCounter);
		
		final TermFrequencyCounter commentCounter = mapStrings(comments);	
		this.componentCounters.put(IssueComponent.COMMENTS, commentCounter);
		
		this.componentCounters.put(IssueComponent.LABELS, parseLabels(issue.getLabels()));
		
		final TermFrequencyCounter allCounter = new TermFrequencyCounter();
		allCounter.add(componentCounters.get(IssueComponent.TITLE));
		allCounter.add(componentCounters.get(IssueComponent.BODY));
		allCounter.add(componentCounters.get(IssueComponent.COMMENTS));
		this.componentCounters.put(IssueComponent.ALL, allCounter);
	}

	private TermFrequencyCounter mapStrings(Collection<Comment> commentData)
	{
		final TermFrequencyCounter freq = new TermFrequencyCounter();
		for(Comment comment : commentData)
			freq.add(comment.getBody());
		
		return freq;
	}

	private TermFrequencyCounter parseLabels(List<Label> labelsInList)
	{
		final TermFrequencyCounter labelCounter = new TermFrequencyCounter();
		for(Label label : labelsInList)
			labelCounter.add(label.getName().toLowerCase().replaceAll("\\s", ""));
		
		return labelCounter;
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
	
	public TermFrequencyCounter getComponent(IssueComponent component)
	{
		return componentCounters.get(component);
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

	public void checkQuality()
	{
		final int titleSize = componentCounters.get(IssueComponent.TITLE).size();
		final int bodySize = componentCounters.get(IssueComponent.BODY).size();
		flaggedBad = titleSize + bodySize < 8;
	}
}
