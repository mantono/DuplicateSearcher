package duplicatesearcher;

import java.io.Serializable;

import duplicatesearcher.processing.Tokenizer;

/**
 * A Token is a {@link String} which is consists of nothing other than numbers
 * and/or lower case letters a-z, as defined by the class {@link Tokenizer}.
 *
 */
public class Token implements Serializable, Comparable<Token>, CharSequence
{
	private final String token;

	public Token(final String input)
	{
		if(!Tokenizer.isToken(input))
			throw new IllegalArgumentException(input + " is not a valid token.");
		this.token = input;
	}

	@Override
	public char charAt(int index)
	{
		return token.charAt(index);
	}

	@Override
	public int length()
	{
		return token.length();
	}

	@Override
	public CharSequence subSequence(int beginIndex, int endIndex)
	{
		return token.subSequence(beginIndex, endIndex);
	}

	@Override
	public int compareTo(Token other)
	{
		return token.compareTo(other.token);
	}

	@Override
	public int hashCode()
	{
		return token.hashCode();
	}

	@Override
	public boolean equals(Object object)
	{
		if(object == null)
			return false;
		if(!(object instanceof CharSequence))
			return false;
		
		CharSequence chars = (CharSequence) object;
		if(chars.length() != token.length())
			return false;
		
		for(int i = 0; i < chars.length(); i++)
			if(chars.charAt(i) != token.charAt(i))
				return false;
		
		return true;
	}
	
	@Override
	public String toString()
	{
		return token;
	}

}
