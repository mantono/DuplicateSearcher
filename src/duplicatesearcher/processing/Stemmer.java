package duplicatesearcher.processing;

import org.tartarus.snowball.ext.englishStemmer;

import duplicatesearcher.Token;

public class Stemmer extends englishStemmer implements TokenProcessor
{
	public Token getCurrentToken()
	{
		return new Token(super.getCurrent());
	}

	public void setCurrentToken(Token token)
	{
		super.setCurrent(token.toString());
	}

	@Override
	public Token process(Token token)
	{
		setCurrentToken(token);
		stem();
		return getCurrentToken();
	}
}
