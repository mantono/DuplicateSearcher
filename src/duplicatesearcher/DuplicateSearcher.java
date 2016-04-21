package duplicatesearcher;

import java.io.IOException;
import java.text.DecimalFormat;
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
	private Set<Duplicate> duplicates;

	public DuplicateSearcher(final RepositoryId repo, final IssueProcessor processor) throws ClassNotFoundException, IOException
	{
		this.repo = repo;
		this.processor = processor;
		DatasetFileManager fileManager = new DatasetFileManager(repo);
		fileManager.load();
		this.issueData = fileManager.getDataset();
		this.processedIssues = new HashSet<StrippedIssue>(issueData.size());
	}

	public int processIssues() throws IOException
	{
		processedIssues.clear();
		Iterator<Entry<Issue, List<Comment>>> iter = issueData.entrySet().iterator();
		
		final double finished = issueData.entrySet().size();
		int processedIssueCount = 0;
		
		System.out.println("\nPROCESSING ISSUES");
		Progress progress = new Progress(finished, 5);
		
		while(iter.hasNext())
		{
			final Entry<Issue, List<Comment>> entry = iter.next();

			StrippedIssue createdIssue = processor.process(entry.getKey(), entry.getValue());

			if(createdIssue.isViable())
				processedIssues.add(createdIssue);
			
			progress.increment();
			progress.print();
		}
		
		System.out.print("\n");

		return processedIssues.size();
	}



	public int analyzeIssues(final double threshold)
	{
		analyzer = new Analyzer(processedIssues);
		duplicates = analyzer.findDuplicates(threshold);
		return duplicates.size();
	}

	public Set<Duplicate> getDuplicates()
	{
		return duplicates;
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException
	{

		final RepositoryId repo = new RepositoryId(args[0], args[1]);
		final IssueProcessor processor = new IssueProcessor(
				ProcessingFlags.PARSE_COMMENTS,
				ProcessingFlags.SPELL_CORRECTION,
				ProcessingFlags.STOP_LIST_COMMON,
				ProcessingFlags.STOP_LIST_GITHUB,
				ProcessingFlags.SYNONYMS,
				ProcessingFlags.STEMMING,
				ProcessingFlags.FILTER_BAD
				);
		DuplicateSearcher searcher = new DuplicateSearcher(repo, processor);
		
		final LocalDateTime startProcessing = LocalDateTime.now();
		searcher.processIssues();
		final LocalDateTime endProcessing = LocalDateTime.now();
		final Duration elpasedTimeProcessing = Duration.between(startProcessing, endProcessing);
		System.out.println("\nProcessing time: " + elpasedTimeProcessing);
		
		final LocalDateTime startAnalysis = endProcessing;
		searcher.analyzeIssues(0.4);
		final LocalDateTime endAnalysis = LocalDateTime.now();
		final Duration elpasedTimeAnalysis = Duration.between(startAnalysis, endAnalysis);
		System.out.println("\nAnalysis time: " + elpasedTimeAnalysis);
		
		System.out.println("\n");
		for(Duplicate d : searcher.getDuplicates())
			System.out.println(d);
		final Duration elpasedTime = Duration.between(startProcessing, endAnalysis);
		System.out.println("Execution time:" + elpasedTime);
		System.out.println("Found duplicates: " + searcher.getDuplicates().size());
	}

}
