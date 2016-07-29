package dsv2;

import duplicatesearcher.Token;

public class TokenWeight implements Comparable<TokenWeight>
{
	private final Token token;
	private final double weight;

	public TokenWeight(Token token, double weight)
	{
		this.token = token;
		this.weight = weight;
	}
	
	@Override
	public int compareTo(TokenWeight other)
	{
		final double diff = other.weight - this.weight;
		if(diff < 0)
			return -1;
		if(diff > 0)
			return 1;
		return 0;
	}
	
	public Token getToken()
	{
		return token;
	}
	
	public double getWeight()
	{
		return weight;
	}
}
