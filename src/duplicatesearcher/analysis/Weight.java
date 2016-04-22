package duplicatesearcher.analysis;

public class Weight
{
	private final double title, body, comments, all, labels, code;
	
	public Weight(final double title, final double body, final double comments, final double labels, final double code)
	{
		this(title, body, comments, code, labels, 0);
	}
	
	public Weight(final double title, final double body, final double comments, final double labels, final double code, final double all)
	{
		if(title < 0 || body < 0 || comments < 0 || all < 0 || labels < 0 || code < 0)
			throw new IllegalArgumentException("Negative weights are not allowed");
		
		double sumDivider = title + body + comments + all + labels + code;
		this.title = title/sumDivider;
		this.body = body/sumDivider;
		this.comments = comments/sumDivider;
		this.all = all/sumDivider;
		this.labels = labels/sumDivider;
		this.code = code/sumDivider;
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
	
	public double code()
	{
		return code;
	}
}
