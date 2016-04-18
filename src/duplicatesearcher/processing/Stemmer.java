package duplicatesearcher.processing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.tartarus.snowball.ext.englishStemmer;

import duplicatesearcher.Token;
import duplicatesearcher.analysis.frequency.TermFrequencyCounter;

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
	public int process(TermFrequencyCounter counter)
	{
		Set<Token> issueTokensCopy = new HashSet<Token>(counter.getTokens());
		Iterator<Token> tokens = issueTokensCopy.iterator();
		int stemmedElements = 0;
		while(tokens.hasNext())
		{
			final Token token = tokens.next();
			setCurrentToken(token);
			stem();
			final Token stemmedToken = getCurrentToken();
			if(!token.equals(stemmedToken))
			{
				counter.change(token, stemmedToken);
				stemmedElements++;
			}
		}
		
		return stemmedElements;
	}
}
