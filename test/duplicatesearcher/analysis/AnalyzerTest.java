package duplicatesearcher.analysis;

import static org.junit.Assert.*;

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

import com.sun.xml.internal.ws.api.ha.HaInfo;

import duplicatesearcher.StrippedIssue;

import research.experiment.datacollectiontools.DatasetFileManager;

public class AnalyzerTest
{

	@Test
	public void testFindDuplicatesStrippedIssueDouble() throws ClassNotFoundException, IOException
	{
		RepositoryId repo = new RepositoryId("telegramdesktop", "tdesktop");
		DatasetFileManager data = new DatasetFileManager(repo);
		data.load();
		Map<Issue, List<Comment>> dataMap = data.getDataset();
		
		HashSet<StrippedIssue> issues = new HashSet<StrippedIssue>(dataMap.size());
		Iterator<Entry<Issue, List<Comment>>> iter = dataMap.entrySet().iterator();
		
		while(iter.hasNext())
		{
			final Entry<Issue, List<Comment>> entry = iter.next();
			issues.add(new StrippedIssue(entry.getKey(), entry.getValue()));
		}
		
		final Analyzer analyzer = new Analyzer(issues);
		Set<Duplicate> result = analyzer.findDuplicates(0.5);
		
		System.out.print(result);
	}

}
