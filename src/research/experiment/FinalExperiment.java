package research.experiment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;

import duplicatesearcher.DuplicateSearcher;
import research.experiment.datacollectiontools.DatasetFileManager;

public class FinalExperiment
{
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException, URISyntaxException
	{
		final RepositoryId repo = new RepositoryId("WhisperSystems", "Signal-Android");
		DatasetFileManager data = new DatasetFileManager(repo);
		data.load();
		Map<Issue, List<Comment>> allIssues = data.getDataset();
		Map<Issue, List<Comment>> evalCorpus = filter(allIssues);
		
		final String[] allArgs = new String[]{"WhisperSystems", "Signal-Android", "--filter", "--stemming", "-S", "-T", "-Y", "--title", "1.5", "--body", "1.0", "--labels", "0.5", "--comments", "0.5", "--code", "0.5", "--all", "0.5"};
		DuplicateSearcher.mainWithGeneratedCorpus(evalCorpus, allArgs);
		
	}

	private static Map<Issue, List<Comment>> filter(Map<Issue, List<Comment>> allIssues)
	{
		assert !allIssues.isEmpty();
		final int[] issueIds = new int[] {4, 41, 80, 127, 234, 243, 369, 410, 432, 436, 439, 451, 535, 556, 576, 650, 653, 700, 727, 798, 809, 953, 959, 964, 1041, 1063, 1101, 1156, 1189, 1191, 1211, 1237, 1329, 1393, 1402, 1481, 1496, 1539, 1582, 1679, 1692, 1705, 1896, 1981, 1982, 2026, 2154, 2222, 2356, 2372, 2410, 2473, 2646, 2651, 2659, 2872, 2891, 2916, 2991, 3003, 3082, 3224, 3301, 3324, 3327, 3495, 3534, 3546, 3547, 3573, 3641, 3698, 3771, 3790, 4101, 4104, 4124, 4205, 4338, 4409, 4420, 4430, 4437, 4508, 4638, 4674, 4760, 4838, 4862, 4956, 5029, 5037, 5090, 5094, 5155, 5163, 5194, 5243, 5251, 5335};
		Map<Issue, List<Comment>> filtered = new HashMap<Issue, List<Comment>>(100);
		for(int id : issueIds)
		{
			for(Entry<Issue, List<Comment>> pair : allIssues.entrySet())
			{
				if(pair.getKey().getNumber() == id)
				{
					filtered.put(pair.getKey(), pair.getValue());
					continue;
				}
			}
		}
		assert filtered.size() == 100: filtered.size();
		return filtered;
	}
}
