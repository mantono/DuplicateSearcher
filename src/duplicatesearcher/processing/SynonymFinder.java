package duplicatesearcher.processing;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import duplicatesearcher.Token;
import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

public class SynonymFinder implements TokenProcessor
{
	private final IRAMDictionary dict;

	public SynonymFinder() throws IOException, InterruptedException
	{
		// TODO fix relative path
		this.dict = new RAMDictionary(new File("/a/oberon-home1/h13/anos3557/workspace/DuplicateSearcher/lib/WordNet-3.0/dict"), ILoadPolicy.NO_LOAD);
		dict.open();
		dict.load(true);
	}
	
	public Collection<String> getSynonym(String input)
	{
		IIndexWord idxWord = dict.getIndexWord(input, POS.NOUN);
		List<IWordID> wordID = idxWord.getWordIDs();

		List<String> synonyms = new LinkedList<String>();

		for (IWordID word : wordID)
		{
			IWord wordFromDict = dict.getWord(word);
			ISynset synset = wordFromDict.getSynset();

			for (IWord syn : synset.getWords())
			{
				synonyms.add(syn.getLemma());
			}
		}

		return synonyms;
	}

	@Override
	public Token process(Token token)
	{
		IIndexWord idxWord = dict.getIndexWord(token.toString(), POS.NOUN);
		if(idxWord == null)
			return token;
		List<IWordID> wordID = idxWord.getWordIDs();

		SortedSet<String> synonyms = new TreeSet<String>();
		
		for (IWordID word : wordID)
		{
			IWord wordFromDict = dict.getWord(word);
			ISynset synset = wordFromDict.getSynset();

			for (IWord syn : synset.getWords())
			{
				if(!Tokenizer.isToken(syn.getLemma()))
					continue;
				synonyms.add(syn.getLemma());
			}
		}
		
		if(synonyms.isEmpty())
			return token;
		
		final Token synonym = mostCommonWord(synonyms);
		
		return synonym;
	}

	private Token mostCommonWord(SortedSet<String> synonyms)
	{
		return new Token(synonyms.first());
	}

}
