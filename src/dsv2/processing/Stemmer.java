package dsv2.processing;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tartarus.snowball.ext.englishStemmer;

import dsv2.Issue;
import dsv2.analysis.TermFrequency;
import duplicatesearcher.Token;

public class Stemmer extends englishStemmer
{
	public int process(Issue issue)
	{
		final TermFrequency<Token> tf = issue.getTokens();
		final Set<Token> tokens = new HashSet<Token>(tf.vectors().keySet());
		int changedTokens = 0;
		for(Token token : tokens)
		{
			setCurrent(token.toString());
			stem();
			final String stemmed = getCurrent();
			final Token stemmedToken = new Token(stemmed);
			if(tf.change(token, stemmedToken))
				changedTokens++;			
		}
		
		return changedTokens;
	}
	
	public int process(Collection<Issue> issueCollection)
	{
		int changedIssues = 0;
		for(Issue issue : issueCollection)
		{
			if(process(issue) > 0)
				changedIssues++;
		}
		
		return changedIssues;
	}
}
