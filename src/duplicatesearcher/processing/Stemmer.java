package duplicatesearcher.processing;

import org.tartarus.snowball.ext.englishStemmer;

import duplicatesearcher.Token;

public class Stemmer extends englishStemmer
{
	public Token getCurrentToken()
	{
		return new Token(super.getCurrent());
	}
	
	public void setCurrentToken(Token token)
	{
		super.setCurrent(token.toString());
	}
}
