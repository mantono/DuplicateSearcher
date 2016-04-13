package duplicatesearcher;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;

import duplicatesearcher.analysis.Analyzer;
import duplicatesearcher.analysis.Duplicate;
import research.experiment.datacollectiontools.DatasetFileManager;

public class DuplicateSearcher
{
	private final RepositoryId repo;
	private final Map<Issue, List<Comment>> issueData;
	private final Set<StrippedIssue> processedIssues;
	private final IssueProcessor processor;
	private Analyzer analyzer;
	private SortedSet<Duplicate> duplicates;

	public DuplicateSearcher(final RepositoryId repo, final IssueProcessor processor) throws ClassNotFoundException, IOException
	{
		this.repo = repo;
		this.processor = processor;
		DatasetFileManager fileManager = new DatasetFileManager(repo);
		fileManager.load();
		this.issueData = fileManager.getDataset();
		this.processedIssues = new HashSet<StrippedIssue>(issueData.size());
	}
	
	public int processIssues()
	{
		processedIssues.clear();
		Iterator<Entry<Issue, List<Comment>>> iter = issueData.entrySet().iterator();
		
		while(iter.hasNext())
		{
			final Entry<Issue, List<Comment>> entry = iter.next();

			StrippedIssue createdIssue = processor.process(entry.getKey(), entry.getValue());
			
			if(createdIssue.isViable())
				processedIssues.add(createdIssue);

		}
		
		return processedIssues.size();
	}
	
	public int analyzeIssues(final double threshold)
	{
		analyzer = new Analyzer(processedIssues);
		duplicates = analyzer.findDuplicates(threshold);
		return duplicates.size();
	}
	
	public SortedSet<Duplicate> getDuplicates()
	{
		return duplicates;
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException
	{
		final LocalDateTime start = LocalDateTime.now();
		
		final RepositoryId repo = new RepositoryId(args[0], args[1]);
		final IssueProcessor processor = new IssueProcessor(ProcessingFlags.PARSE_COMMENTS);
		DuplicateSearcher searcher = new DuplicateSearcher(repo, processor);
		searcher.processIssues();
		searcher.analyzeIssues(0.5);
		
		final LocalDateTime end = LocalDateTime.now();
		System.out.println(searcher.getDuplicates());
		final Duration elpasedTime = Duration.between(start, end);
		System.out.println("Execution time:" + elpasedTime);
		System.out.println("Found duplicates: " + searcher.getDuplicates().size());
	}

}
