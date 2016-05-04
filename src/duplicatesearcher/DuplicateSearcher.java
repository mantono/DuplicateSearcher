package duplicatesearcher;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;

import duplicatesearcher.analysis.Analyzer;
import duplicatesearcher.analysis.Duplicate;
import duplicatesearcher.analysis.Weight;
import duplicatesearcher.flags.BooleanFlag;
import duplicatesearcher.flags.Flag;
import duplicatesearcher.flags.IssueComponent;
import duplicatesearcher.flags.IssueComponentFlagLoader;
import duplicatesearcher.flags.ProcessingFlag;
import duplicatesearcher.flags.ProcessingFlagLoader;
import duplicatesearcher.flags.SettingsLoader;
import research.experiment.ExperimentEvaluator;
import research.experiment.Report;
import research.experiment.datacollectiontools.DatasetFileManager;
import research.experiment.datacollectiontools.ExperimentSetGenerator;

public class DuplicateSearcher
{
	private final Map<Issue, List<Comment>> issueData;
	private final Set<StrippedIssue> processedIssues;
	private final IssueProcessor processor;
	private Analyzer analyzer;
	private Set<Duplicate> duplicates;

	public DuplicateSearcher(final Map<Issue, List<Comment>> corpus, final IssueProcessor processor) throws ClassNotFoundException, IOException
	{
		this.processor = processor;
		this.issueData = corpus;
		this.processedIssues = new HashSet<StrippedIssue>(issueData.size());
	}

	public int processIssues() throws IOException
	{
		processedIssues.clear();
		Iterator<Entry<Issue, List<Comment>>> iter = issueData.entrySet().iterator();
		
		final double finished = issueData.entrySet().size();
		
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

	public int analyzeIssues(final double threshold, Weight weight)
	{
		analyzer = new Analyzer(processedIssues, weight);
		duplicates = analyzer.findDuplicates(threshold);
		return duplicates.size();
	}

	public Set<Duplicate> getDuplicates()
	{
		return duplicates;
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException, URISyntaxException
	{

		final RepositoryId repo = new RepositoryId(args[0], args[1]);
		DatasetFileManager data = new DatasetFileManager(repo);
		data.load();
		ExperimentSetGenerator exGen = new ExperimentSetGenerator(repo, data.getDataset());
		
		ProcessingFlagLoader pfl = new ProcessingFlagLoader();
		pfl.applyAgrumentVector(args);
		EnumSet<ProcessingFlag> flags = pfl.getSettings();
		
		IssueComponentFlagLoader icfl = new IssueComponentFlagLoader();
		icfl.applyAgrumentVector(args);
		EnumMap<IssueComponent, Double> weighting = icfl.getSettings();
		
		final IssueProcessor processor = new IssueProcessor(repo, flags);
		exGen.generateRandomIntervalSet(1000, 0.3f, 0.6f);

		DuplicateSearcher searcher = new DuplicateSearcher(exGen.getGeneratedCorpus(), processor);
		
		final LocalDateTime startProcessing = LocalDateTime.now();
		searcher.processIssues();

		final LocalDateTime endProcessing = LocalDateTime.now();
		final Duration elpasedTimeProcessing = Duration.between(startProcessing, endProcessing);
		System.out.println("\nProcessing time: " + elpasedTimeProcessing);
		
		final LocalDateTime startAnalysis = endProcessing;

		searcher.analyzeIssues(0.6, new Weight(weighting));

		final LocalDateTime endAnalysis = LocalDateTime.now();
		final Duration elpasedTimeAnalysis = Duration.between(startAnalysis, endAnalysis);
		System.out.println("\nAnalysis time: " + elpasedTimeAnalysis);
		final Duration elpasedTime = Duration.between(startProcessing, endAnalysis);
		
		System.out.println("Execution time:" + elpasedTime);
			
		Report report = new Report(flags, weighting, repo, elpasedTimeProcessing, elpasedTimeAnalysis, exGen, searcher.getDuplicates());
		report.buildFile();
	}

}
