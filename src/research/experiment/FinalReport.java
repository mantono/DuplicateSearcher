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
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.egit.github.core.RepositoryId;

import duplicatesearcher.StrippedIssue;
import duplicatesearcher.Token;
import duplicatesearcher.analysis.Duplicate;
import duplicatesearcher.analysis.frequency.TermFrequencyCounter;
import duplicatesearcher.flags.IssueComponent;
import duplicatesearcher.flags.ProcessingFlag;
import research.experiment.datacollectiontools.ExperimentSetGenerator;

public class FinalReport
{
	private final EnumSet<ProcessingFlag> flagSet;
	private final EnumMap<IssueComponent, Double> weights;
	private final double threshold;
	private final RepositoryId repoId;
	private final Duration processing;
	private final Duration analysis;
	private final Set<Duplicate> foundDuplicates;
	private List<String> reportList;

	public FinalReport(EnumSet<ProcessingFlag> flagSet, EnumMap<IssueComponent, Double> weights,
			double threshold, RepositoryId repoId, Duration processing, Duration analysis,
			Set<Duplicate> foundDuplicates)
	{
		this.flagSet = flagSet;
		this.weights = weights;
		this.threshold = threshold;
		this.repoId = repoId;
		this.foundDuplicates = foundDuplicates;
		this.processing = processing;
		this.analysis = analysis;
	}

	public List<String> buildHTML()
	{
		reportList = new ArrayList<String>();
		final String header = "<!DOCTYPE html><html><head><link rel='stylesheet' type='text/css' href='../../style.css'><title>"
				+ repoId + "</title></head><body>";

		final StringBuilder parameters = new StringBuilder("<fieldset><legend>Parameters</legend>");
		parameters.append("<p>Processing flags: " + flagSet + "</p>");
		parameters.append("<p>Weights: " + weights + "</p>");
		parameters.append("<p>Threshold: " + threshold + "</p>");
		parameters.append("</fieldset>");

		final StringBuilder result = new StringBuilder("<fieldset><legend>Result</legend>");
		result.append("<p>Found duplicates: " + foundDuplicates.size() + "</p>");
		result.append("</fieldset>");

		final StringBuilder performance = new StringBuilder(
				"<fieldset><legend>Performance</legend>");
		performance.append("<p>Processing time: " + processing.toString() + "<br/>");
		performance.append("Analysis time: " + analysis.toString() + "</p>");
		performance.append("</fieldset>");

		final String duplicates = createTable(foundDuplicates, "Found duplicates");

		reportList.add(header);
		reportList.add(parameters.toString());
		reportList.add(result.toString());
		reportList.add(performance.toString());
		reportList.add(duplicates);

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
				table.append("<td class='component'>" + ic.toString() + "</td>");

				final TermFrequencyCounter termsDupe = duplicate.getComponent(ic);

				termsDupe.remove("token123456789");
				table.append("<td>" + termsDupe + "</td>");

				final TermFrequencyCounter termsMaster = master.getComponent(ic);
				termsMaster.remove("token123456789");
				table.append("<td>" + termsMaster + "</td>");

				final Set<Token> common = new HashSet<Token>(termsDupe.getTokens());
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
				+ "/issues/" + number + "'>#" + number + "</a></td>";
	}

	public void buildFile()
	{
		List<String> reportList = buildHTML();
		String reportName = "";
		for(ProcessingFlag flag : flagSet)
		{
			reportName += flag.getShortFlag();
		}
		LocalDateTime time = LocalDateTime.now();
		reportName += "_" + threshold + "_" + time.toEpochSecond(ZoneOffset.UTC);

		Path file = Paths.get("reports/" + repoId.getOwner() + "/" + repoId.getName() + "/"
				+ reportName + ".html");
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
