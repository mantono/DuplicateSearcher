package research.experiment.humanceiling;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;

public class HtmlIssues
{
	private final static File templateFile = new File("src/research/experiment/humanceiling/htmlTemplate");
	private final List<Issue> issues;
	private final List<String> data = new ArrayList<String>();
	private final String template;
	
	public HtmlIssues(List<Issue> randIssues) throws IOException
	{
		this.issues = randIssues;
		byte[] fileData = Files.readAllBytes(templateFile.toPath());
		this.template = new String(fileData, Charset.defaultCharset());
	}
	
	public List<String> getData()
	{
		data.add("<!DOCTYPE HTML><html><head><title>Data for Human Ceiling Test</title></head><body>");
		for(Issue i : issues)
		{
			String html = template;
			html = html.replaceAll("\\$title", i.getTitle());
			html = html.replaceAll("\\$number", "" + i.getNumber());
			html = html.replaceAll("\\$author", i.getUser().getLogin().toString());
			html = html.replaceAll("\\$date", i.getCreatedAt().toString());
			List<Label> labels = removeDupeLabels(i.getLabels());
			html = html.replaceAll("\\$labels", labels.toString());
			html = html.replaceAll("\\$body", i.getBody());
			data.add(html);
		}
		data.add("</body></html>");

		return data;
	}

	private List<Label> removeDupeLabels(List<Label> labels)
	{
		Iterator<Label> iter = labels.iterator();
		while(iter.hasNext())
		{
			final Label label = iter.next();
			final String labelName = label.getName().toLowerCase();
			if(labelName.equals("duplicate") || labelName.equals("dupe"))
			{
				iter.remove();
				break;
			}
		}
		return labels;
	}
}
