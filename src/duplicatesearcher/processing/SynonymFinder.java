package duplicatesearcher.processing;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import duplicatesearcher.Token;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

public class SynonymFinder implements TokenProcessor
{
	private final IRAMDictionary dict;

	public SynonymFinder() throws IOException, InterruptedException
	{
		// construct the URL to the Wordnet dictionary directory
		String wnhome = System.getenv(" WNHOME ");
		String path = wnhome + File.separator + " dict ";

		// construct the dictionary object and open it
		this.dict = new RAMDictionary(new File("/a/oberon-home1/h13/anos3557/workspace/DuplicateSearcher/lib/WordNet-3.0/dict"), ILoadPolicy.NO_LOAD);
		dict.open();
		dict.load(true);
	}

	public void testDict() throws IOException
	{
		// construct the URL to the Wordnet dictionary directory
		//String wnhome = System.getenv("WNHOME");
		//String path = wnhome + File.separator + "dict";
		//URL url = new URL("file", null, path);
		// construct the dictionary object and open it
		//IDictionary dict = new Dictionary(url);
		dict.open();
		// look up first sense of the word " dog "
		IIndexWord idxWord = dict.getIndexWord(" dog ", POS.NOUN);
		IWordID wordID = idxWord.getWordIDs().get(0);
		IWord word = dict.getWord(wordID);
		System.out.println(" Id = " + wordID);
		System.out.println(" Lemma = " + word.getLemma());
		System.out.println(" Gloss = " + word.getSynset().getGloss());
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
		
		return new Token(synonyms.first().toLowerCase());
	}

}
