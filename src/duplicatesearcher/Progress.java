package duplicatesearcher;

import java.text.DecimalFormat;

public class Progress
{
	private final static DecimalFormat FORMAT = new DecimalFormat("#.###");
	private final double finished;
	private final int multiplier;
	private int progress = 0;
	
	public Progress(final double finished)
	{
		this(finished, 1);
	}
	
	public Progress(final int finished)
	{
		this((double) finished, 1);
	}
	
	public Progress(final double finished, final int multiplier)
	{
		this.finished = finished;
		this.multiplier = multiplier;
	}
	
	public Progress(final int finished,  final int multiplier)
	{
		this((double) finished, multiplier);
	}
	
	public final int increment()
	{
		return ++progress;
	}
	
	public void print()
	{
		if(progress % multiplier == 0)
			System.out.print(".");
		else
			return;
		
		if(progress % (multiplier*10) == 0)
			System.out.print(" ");
		if(progress % (multiplier*100) == 0)
		{
			final double completed = (progress/finished)*100;
			System.out.println(" ["+FORMAT.format(completed)+"%]");
		}
	}
}
