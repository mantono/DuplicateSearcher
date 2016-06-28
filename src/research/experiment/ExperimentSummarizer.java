package research.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExperimentSummarizer
{
	private static final Pattern TRUE_POSITIVES = Pattern.compile("<p>True positives: \\d+</p>");
	private static final Pattern FALSE_POSITIVES = Pattern.compile("<p>False positives: \\d+</p>");
	private static final Pattern FALSE_NEGATIVES = Pattern.compile("<p>False negatives: \\d+</p>");
	private static final Pattern[] METRICS = new Pattern[]{TRUE_POSITIVES, FALSE_POSITIVES, FALSE_NEGATIVES};
	private static final String HEADER = ", True Positives, False Positives, False Negatives\n";

	private final File[] files;
	private final StringBuilder csvData;

	public ExperimentSummarizer(File[] files)
	{
		this.files = files;
		this.csvData = new StringBuilder(33 * 25);
		csvData.append(HEADER);
	}

	public static void main(String[] args) throws IOException
	{
		File folder = new File(args[0]);
		final File[] files = folder.listFiles();
		Arrays.sort(files);

		ExperimentSummarizer exs = new ExperimentSummarizer(files);

		exs.analyze();
		
		if(args.length < 2)
			exs.save(System.out);
		else
			exs.save(new FileOutputStream(new File(args[1])));
	}

	private void save(OutputStream out) throws IOException
	{
		out.write(csvData.toString().getBytes());
	}

	private void analyze() throws IOException
	{

		for(File file : files)
		{
			if(!hasFileExtension(file, "html"))
				continue;

			final String flags = getFlags(file.getName());
			
			csvData.append(flags +", ");

			for(Pattern pattern : METRICS)
			{
				BufferedReader input = new BufferedReader(new FileReader(file));
				String line;
				while((line = input.readLine()) != null)
				{
					Matcher m = pattern.matcher(line);
					if(m.find())
					{
						final int start = m.start();
						final int end = m.end();
						final String metric = line.substring(start+pattern.toString().length()-7, end-4);
						csvData.append(metric + ", ");
					}
				}
				input.close();
			}
			
			csvData.deleteCharAt(csvData.length()-1);
			csvData.append('\n');
		}
	}

	private String getFlags(String name)
	{
		final String noHtml = name.replaceAll("html", "");
		final String noSeparators = noHtml.replaceAll("[_\\.]", "");
		final String noNumber = noSeparators.replaceAll("\\d", "");
		if(noNumber.isEmpty())
			return "-";
		return noNumber;
	}

	public static boolean hasFileExtension(File file, String extension)
	{
		final String pattern = "([^\\s]+(\\.(?i)(" + extension + "))$)";
		return Pattern.matches(pattern, file.getName());
	}

}
