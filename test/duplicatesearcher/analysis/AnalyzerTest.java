package duplicatesearcher.analysis;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;
import org.junit.Test;

import duplicatesearcher.IssueProcessor;
import duplicatesearcher.StrippedIssue;
import duplicatesearcher.flags.ProcessingFlag;
import research.experiment.datacollectiontools.DatasetFileManager;

public class AnalyzerTest
{

	@Test
	public void testFindDuplicatesStrippedIssueDouble() throws ClassNotFoundException, IOException
	{
		RepositoryId repo = new RepositoryId("mantono", "DuplicateSearcher");
		DatasetFileManager data = new DatasetFileManager(repo);
		data.load();
		Map<Issue, List<Comment>> dataMap = data.getDataset();
		System.out.println(dataMap.size());

		HashSet<StrippedIssue> issues = new HashSet<StrippedIssue>(dataMap.size());
		Iterator<Entry<Issue, List<Comment>>> iter = dataMap.entrySet().iterator();

		final IssueProcessor processor = new IssueProcessor(ProcessingFlag.PARSE_COMMENTS, ProcessingFlag.STOP_LIST_COMMON);
		
		while(iter.hasNext())
		{
			final Entry<Issue, List<Comment>> entry = iter.next();

			StrippedIssue createdIssue = processor.process(entry.getKey(), entry.getValue());
			
			if(createdIssue.isViable())
				issues.add(createdIssue);

		}

		final Analyzer analyzer = new Analyzer(issues);
		Set<Duplicate> result = analyzer.findDuplicates(0.1);

		System.out.print(result);

		System.out.println("*** FINISHED ***");

	}

}
