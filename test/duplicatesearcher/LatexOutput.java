package duplicatesearcher;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

import duplicatesearcher.Token;
import duplicatesearcher.analysis.frequency.TermFrequencyCounter;
import duplicatesearcher.processing.Stemmer;
import duplicatesearcher.processing.SynonymFinder;
import duplicatesearcher.processing.spellcorrecting.SpellCorrector;
import duplicatesearcher.processing.stoplists.StopList;

public class LatexOutput
{
	@Test
	public void blaha() throws IOException, ClassNotFoundException, InterruptedException
	{
		final String[] input = new String[]{"steps", "to", "reproduce", "start", "download", "upload", "a", "file",
				"with", "a", "size", "mb", "ive", "same", "issue", "with", "mb", "mb", "wait", "forever", "because",
				"telegram", "unable", "to", "finish", "step1", "shutdown", "telegram", "restart", "step1", "from",
				"the", "beginning", "because", "of", "resultant", "damaged", "file", "i", "may", "repeat", "step1",
				"many", "times", "note", "sometimes", "this", "problem", "doesnt", "occur", "it", "happens", "randomly",
				"this", "problem", "doesnt", "occur", "in", "android", "client", "expected", "behavior", "download",
				"upload", "whole", "file", "actual", "behavior", "stops", "at", "last", "bytes", "configuration",
				"operating", "system", "linux", "mint", "x64", "xfce", "microsoft", "windows", "x64", "microsoft",
				"windows", "x64", "version", "of", "telegram", "desktop", "update", "dev"};
		List<String> issueData = Arrays.asList(input);

		TermFrequencyCounter tfc = new TermFrequencyCounter();
		tfc.addAll(issueData);

		outputData("Tokenized", tfc, 4);

		final SpellCorrector largeDict = new SpellCorrector(new File("./dictionary/words.txt"));
		largeDict.addDictionary(new File("./dictionary/words2.txt"));

		TermFrequencyCounter tf2 = new TermFrequencyCounter();

		for(String str : input)
		{
			Token token;
			if(largeDict.isMisspelled(new Token(str)) && str.length() >= 5)
			{
				token = largeDict.correctWord(str, str.length() / 5);
			}
			else
				token = new Token(str);
			tf2.add(token);
		}

		outputData("Spell check", tf2, 4);
		
		StopList list1 = new StopList(new File("stoplists/long/ReqSimile.txt"));
		StopList list2 = new StopList(new File("stoplists/github/github.txt"));
		
		TermFrequencyCounter tf3 = new TermFrequencyCounter();
		tf3.add(tf2);
		
		for(Token token : tf3.getTokens())
		{
			final Token original = new Token(token);
			token = list1.process(token);
			token = list2.process(token);
			
			if(token == null)
				tf2.remove(original);
			else
				tf2.change(original, token);
		}
		
		outputData("Stop words list", tf2, 4);
		
		TermFrequencyCounter synonyms = new TermFrequencyCounter();
		synonyms.add(tf2);
		
		SynonymFinder s = new SynonymFinder();
		
		for(Token token : tf2.getTokens())
		{
			final Token processed = s.process(token);
			if(!token.equals(processed))
				synonyms.change(token, processed);
		}
		
		outputData("Synonyms", synonyms, 4);		
		
		TermFrequencyCounter stemmed = new TermFrequencyCounter();
		stemmed.add(synonyms);
		
		Stemmer stemmer = new Stemmer();
		
		for(Token token : synonyms.getTokens())
		{
			stemmer.setCurrentToken(token);
			stemmer.stem();
			final Token stemmedToken = stemmer.getCurrentToken();
			if(!token.equals(stemmedToken))
			{
				if(stemmed.getTokenFrequency(token) > 0)
					stemmed.change(token, stemmedToken);
			}
		}
		
		outputData("Stemming", stemmed, 4);

	}

	private void outputData(final String component, TermFrequencyCounter tfc, final int width)
	{
		SortedMap<Token, Integer> tokens = new TreeMap<Token, Integer>();
		for(Token tk : tfc.getTokens())
			tokens.put(tk, tfc.getTokenFrequency(tk));

		System.out.println("\n-------"+component+"-------\n");

		List<Token> tokenIndex = new ArrayList<Token>(tokens.keySet());
		
		final int height = (tokenIndex.size()/width)+1;
		
		final int[] indexes = new int[width];
		
		for(int i = 0; i < indexes.length; i++)
			indexes[i] = i*height;

		final StringBuilder output = new StringBuilder();
		
		for(int i = 0; i < height; i++)
		{
			output.append("\\\\\n");
			for(final int index : indexes)
			{
				if(index >= tokenIndex.size())
					break;
				final Token tk = tokenIndex.get(index);
				final int value = tokens.get(tk);
				output.append(tk + "\t\t\t& " + value + " ");
				output.append("&\t");
			}
			output.delete(output.length()-3, output.length()-1);
			increment(indexes);				
		}
		
		System.out.println(output.toString());
	}

	private void increment(int[] indexes)
	{
		for(int i = 0; i < indexes.length; i++)
			indexes[i] = indexes[i] + 1;
	}

}
