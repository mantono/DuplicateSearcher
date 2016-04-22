package duplicatesearcher.analysis;

public class Weight
{
	private final double title, body, comments, all, labels;
	
	public Weight(final double title, final double body, final double comments, final double all, final double labels)
	{
		this.title = title;
		this.body = body;
		this.comments = comments;
		this.all = all;
		this.labels = labels;
	}

	public double title()
	{
		return title;
	}

	public double body()
	{
		return body;
	}

	public double comments()
	{
		return comments;
	}

	public double all()
	{
		return all;
	}

	public double labels()
	{
		return labels;
	}
}
