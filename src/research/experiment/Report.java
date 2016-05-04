package research.experiment;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.egit.github.core.RepositoryId;

import research.experiment.datacollectiontools.ExperimentSetGenerator;
import duplicatesearcher.flags.ProcessingFlag;
import duplicatesearcher.StrippedIssue;
import duplicatesearcher.Token;
import duplicatesearcher.analysis.Duplicate;
import duplicatesearcher.flags.IssueComponent;
import duplicatesearcher.analysis.frequency.TermFrequencyCounter;

public class Report{
	EnumSet<ProcessingFlag> flagSet;
	EnumMap<IssueComponent, Double> weights;
	RepositoryId repoId;
	Duration processing;
	Duration analysis;
	ExperimentSetGenerator exSetGenerator;
	Set<Duplicate> foundDuplicates;
	ExperimentEvaluator exEval;
	List<String> reportList;
	
	public Report(EnumSet<ProcessingFlag> flagSet, EnumMap<IssueComponent, Double> weights, RepositoryId repoId,
			Duration processing, Duration analysis, ExperimentSetGenerator exSetGenerator, Set<Duplicate> foundDuplicates){
		this.flagSet = flagSet;
		this.weights = weights;
		this.repoId = repoId;
		this.processing = processing;
		this.analysis = analysis;
		this.exSetGenerator = exSetGenerator;
		this.foundDuplicates = foundDuplicates;
		this.exEval = new ExperimentEvaluator(foundDuplicates,
				exSetGenerator.getCorpusDuplicates());
	}

	public List<String> buildHTML()
	{
		reportList = new ArrayList<String>();
		final String header = "<!DOCTYPE html><html><head><link rel='stylesheet' type='text/css' href='../../style.css'><title>" + repoId + "</title></head><body>";
		
		final StringBuilder links = new StringBuilder("<p>");
		links.append("<a href='#truepositives'>True positives</a><br/>");
		links.append("<a href='#falsepositives'>False positives</a><br/>");
		links.append("<a href='#falsenegatives'>False negatives</a><br/>");
		links.append("</p>");
		
		final StringBuilder parameters = new StringBuilder("<fieldset><legend>Parameters</legend>");
		parameters.append("<p>Processing flags: " + flagSet + "</p>");
		parameters.append("<p>Weights: " + weights + "</p>");
		parameters.append("</fieldset>");
		
		final StringBuilder result = new StringBuilder("<fieldset><legend>Result</legend>");
		result.append("<p>True positives: "+exEval.getTruePositives().size()+"</p>");
		result.append("<p>False positives: "+exEval.getFalsePositives().size()+"</p>");
		result.append("<p>False negatives: "+exEval.getFalseNegatives().size()+"</p>");
		result.append("<p>Precision: " + exEval.calculatePrecision() + "</p>");
		result.append("<p>Recall: " + exEval.calculateRecall() + "</p>");
		result.append("<p>F1-score: " + exEval.calculateF1Score() + "</p>");
		result.append("</fieldset>");
		
		final StringBuilder performance = new StringBuilder("<fieldset><legend>Performance</legend>");
		performance.append("<p>Processing time: " + processing.toString() + "<br/>");
		performance.append("Analysis time: " + analysis.toString() + "</p>");
		performance.append("</fieldset>");
		
		final int corpusSize = exSetGenerator.getGeneratedCorpus().size();
		final double corpusDuplicatesSize = exSetGenerator.getCorpusDuplicates().size();
		final double corpusDuplicateRatio = corpusDuplicatesSize/corpusSize;
		
		final StringBuilder corpusData = new StringBuilder("<fieldset><legend>Experiment set data</legend>");
		corpusData.append("<p>Experiment set size: " + corpusSize + " issues</p>");
		corpusData.append("<p>Duplicates in experiment set: "+corpusDuplicatesSize+"</p>");
		corpusData.append("<p>Duplicate ratio in experiment: "+corpusDuplicateRatio+"</p>");
		corpusData.append("</fieldset>");

		final String truePos = createTable(exEval.getTruePositives(), "True Positives");
		final String falsePos = createTable(exEval.getFalsePositives(), "False Positives");
		final String falseNeg = createTable(exEval.getFalseNegatives(), "False Negatives");

		reportList.add(header);
		reportList.add(links.toString());
		reportList.add(parameters.toString());
		reportList.add(result.toString());
		reportList.add(performance.toString());
		reportList.add(corpusData.toString());
		reportList.add(truePos);
		reportList.add(falsePos);
		reportList.add(falseNeg);

		reportList.add("</body></html>");

		return reportList;
	}

	private String createTable(Set<Duplicate> dupes, String title)
	{
		final String anchor = title.replaceAll("\\s", "").toLowerCase();
		final StringBuilder table = new StringBuilder("<p><table id='" + anchor + "'>");
		final String header = ("<tr><th colspan='4'>" + title + "</th></tr>");
		table.append(header);

		for(Duplicate duplicatePair : dupes)
		{
			final StrippedIssue duplicate = duplicatePair.getDuplicate();
			final StrippedIssue master = duplicatePair.getMaster();

			table.append("<tr><td colspan = \"4\">");
			table.append("<h4 class=\"similarity\">" + duplicatePair.getSimilarity() + "</h4>");
			table.append("</td></tr>");

			table.append("<tr><td>Component</td>");
			table.append(createLink(duplicate.getNumber()));
			table.append(createLink(master.getNumber()));
			table.append("<td>Common</td>");

			for(IssueComponent ic : IssueComponent.values())
			{
				table.append("<tr>");
				table.append("<td>" + ic.toString() + "</td>");

				final TermFrequencyCounter termsDupe = duplicate.getComponent(ic);
				termsDupe.remove("token123456789");
				table.append("<td>" + termsDupe + "</td>");

				final TermFrequencyCounter termsMaster = master.getComponent(ic);
				termsMaster.remove("token123456789");
				table.append("<td>" + termsMaster + "</td>");

				final Set<Token> common = termsDupe.getTokens();
				common.retainAll(termsMaster.getTokens());
				table.append("<td>" + common + "</td>");

				table.append("</tr>");
			}

		}

		table.append("</table></p>");

		return table.toString();
	}

	private String createLink(int number)
	{
		return "<td><a href='http://github.com/" + repoId.getOwner() + "/" + repoId.getName()
				+ "/issues/" + number + "'>" + number + "</a></td>";
	}

	public void buildFile()
	{
		List<String> reportList = buildHTML();

		Path file = Paths
				.get("reports/" + repoId.getOwner() + "/" + repoId.getName() + "/" + "report.html");
		try
		{
			if(!Files.exists(file, new LinkOption[0]))
			{
				Files.createDirectories(
						Paths.get("reports/" + repoId.getOwner() + "/" + repoId.getName()),
						new FileAttribute<?>[0]);
				Files.createFile(file, new FileAttribute[0]);
			}
			Files.write(file, reportList, Charset.forName("UTF-8"));
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
