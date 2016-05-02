package duplicatesearcher.analysis;

import java.util.EnumMap;
import java.util.Map;

import duplicatesearcher.flags.IssueComponent;

public class Weight
{
	private final Map<IssueComponent, Double> values = new EnumMap<IssueComponent, Double>(IssueComponent.class);
	
	public Weight(final double title, final double body, final double comments, final double labels, final double code)
	{
		this(title, body, comments, code, labels, 0);
	}
	
	public Weight(final double title, final double body, final double comments, final double labels, final double code, final double all)
	{
		if(title < 0 || body < 0 || comments < 0 || all < 0 || labels < 0 || code < 0)
			throw new IllegalArgumentException("Negative weights are not allowed");
		
		double sumDivider = title + body + comments + all + labels + code;
			
		values.put(IssueComponent.TITLE, title/sumDivider);
		values.put(IssueComponent.BODY, body/sumDivider);
		values.put(IssueComponent.COMMENTS, comments/sumDivider);
		values.put(IssueComponent.ALL, all/sumDivider);
		values.put(IssueComponent.LABELS, labels/sumDivider);
		values.put(IssueComponent.CODE, code/sumDivider);
	}
	
	public double getWeight(final IssueComponent component)
	{
		return values.get(component);
	}
}
