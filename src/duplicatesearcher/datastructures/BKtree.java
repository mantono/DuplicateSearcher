package duplicatesearcher.datastructures;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import duplicatesearcher.Token;
import duplicatesearcher.processing.spellcorrecting.LevenshteinDistance;

public class BKtree
{
	private final static LevenshteinDistance LEVENSHTEIN = new LevenshteinDistance();
	private final Token word;
	private final Map<Integer, BKtree> children = new TreeMap<Integer, BKtree>();

	public BKtree(final Token word)
	{
		this.word = word;
	}

	public boolean insert(final Token word)
	{
		final int distance = LEVENSHTEIN.apply(this.word, word);
		
		if(distance == 0)
			return false;
		
		if(children.containsKey(distance))
		{
			final BKtree child = children.get(distance);
			return child.insert(word);
		}
		else
		{
			children.put(distance, new BKtree(word));
			return true;
		}
	}
	
	public SortedMap<Integer, List<CharSequence>> find(final CharSequence misspelledWord, final int maxDistance)
	{
		return find(new TreeMap<Integer, List<CharSequence>>(), misspelledWord, maxDistance);
	}

	public SortedMap<Integer, List<CharSequence>> find(final SortedMap<Integer, List<CharSequence>> values, final CharSequence misspelledWord, final int maxDistance)
	{
		final int distanceToRoot = LEVENSHTEIN.apply(word, misspelledWord);
		final int lowerBound = distanceToRoot - maxDistance;
		final int upperBound = distanceToRoot + maxDistance;		
		
		if(distanceToRoot <= maxDistance)
		{
			if(!values.containsKey(distanceToRoot))
				values.put(distanceToRoot, new LinkedList<CharSequence>());
			List<CharSequence> list = values.get(distanceToRoot);
			list.add(word);
			
			if(distanceToRoot == 1)
				return values;
		}
		
		if(children.isEmpty())
			return values;
		
		Iterator<Integer> iter = children.keySet().iterator();	
		
		while(iter.hasNext())
		{
			final int childKey = iter.next();
			if(withinBounds(lowerBound, upperBound, childKey))
			{
				final BKtree subtree = children.get(childKey);
				subtree.find(values, misspelledWord, maxDistance);
			}
		}
		
		return values;
	}

	private boolean withinBounds(int lowerBound, int upperBound, int childKey)
	{
		final boolean passLowerBound = childKey >= lowerBound && childKey > 0;
		final boolean passUpperBound = childKey <= upperBound;
		return passLowerBound && passUpperBound;
	}

	@Override
	public int hashCode()
	{
		return word.hashCode();
	}
}
