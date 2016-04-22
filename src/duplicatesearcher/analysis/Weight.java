package duplicatesearcher.analysis;

public class Weight
{
	private final double title, body, comments, all, labels;
	
	public Weight(final double title, final double body, final double comments, final double all, final double labels)
	{
		if(title < 0 || body < 0 || comments < 0 || all < 0 || labels < 0)
			throw new IllegalArgumentException("Negative weights are not allowed");
		
		double sumDivider = title + body + comments + all + labels;
		this.title = title/sumDivider;
		System.out.println(this.title);
		this.body = body/sumDivider;
		System.out.println(this.body);
		this.comments = comments/sumDivider;
		System.out.println(this.comments);
		this.all = all/sumDivider;
		System.out.println(this.all);
		this.labels = labels/sumDivider;
		System.out.println(this.labels);
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
