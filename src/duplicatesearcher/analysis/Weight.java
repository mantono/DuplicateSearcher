package duplicatesearcher.analysis;

public class Weight
{
	private final double title, body, comments, code, labels;
	
	public Weight(final double title, final double body, final double comments, final double code, final double labels)
	{
		this.title = title;
		this.body = body;
		this.comments = comments;
		this.code = code;
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

	public double code()
	{
		return code;
	}

	public double labels()
	{
		return labels;
	}
}
