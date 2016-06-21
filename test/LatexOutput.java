import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

import duplicatesearcher.Token;
import duplicatesearcher.analysis.frequency.TermFrequencyCounter;
import duplicatesearcher.processing.spellcorrecting.SpellCorrector;

public class LatexOutput
{
	@Test
	public void blaha() throws IOException
	{
		final String[] input = new String[]{"steps", "to", "reproduce", "start", "download", "upload", "a", "file", "with", "a", "size", "mb", "ive", "same", "issue", "with", "mb", "mb", "wait", "forever", "because", "telegram", "unable", "to", "finish", "step1", "shutdown", "telegram", "restart", "step1", "from", "the", "beginning", "because", "of", "resultant", "damaged", "file", "i", "may", "repeat", "step1", "many", "times", "note", "sometimes", "this", "problem", "doesnt", "occur", "it", "happens", "randomly", "this", "problem", "doesnt", "occur", "in", "android", "client", "expected", "behavior", "download", "upload", "whole", "file", "actual", "behavior", "stops", "at", "last", "bytes", "configuration", "operating", "system", "linux", "mint", "x64", "xfce", "microsoft", "windows", "x64", "microsoft", "windows", "x64", "version", "of", "telegram", "desktop", "update", "dev"};
		List<String> issueData = Arrays.asList(input);
		
		TermFrequencyCounter tfc = new TermFrequencyCounter();
		tfc.addAll(issueData);
		
		outputData(tfc);
		
		final SpellCorrector largeDict = new SpellCorrector(new File("/a/oberon-home1/h13/anos3557/workspace/DuplicateSearcher/dictionary/words.txt"));
		largeDict.addDictionary(new File("/a/oberon-home1/h13/anos3557/workspace/DuplicateSearcher/dictionary/words2.txt"));
		
		TermFrequencyCounter tf2 = new TermFrequencyCounter();
		
		for(String str : input)
		{
			Token token;
			if(largeDict.isMisspelled(new Token(str)) && str.length() >= 5)
			{
				token = largeDict.correctWord(str, str.length()/5);
			}
			else
				token = new Token(str);
			tf2.add(token);
		}
		
		outputData(tf2);
		
	}
	
	private void outputData(TermFrequencyCounter tfc)
	{
		SortedMap<Token, Integer> tokens = new TreeMap<Token, Integer>();
		for(Token tk : tfc.getTokens())
			tokens.put(tk, tfc.getTokenFrequency(tk));
		
		System.out.println("\n-------------");
		
		int i = 0;
		for(Token tk : tokens.keySet())
		{
			if(i++ % 4 == 0)
				System.out.print("\\\\\n");
			final int value = tokens.get(tk);
			System.out.print(tk + "\t\t& " + value + " ");
			if(i % 4 != 0)
				System.out.print("&\t\t");
		}
	}

}
