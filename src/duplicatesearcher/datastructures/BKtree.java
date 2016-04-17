package duplicatesearcher.datastructures;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import duplicatesearcher.processing.spellcorrecting.LevenshteinDistance;

public class BKtree
{
	private final CharSequence word;
	private Map<Byte, BKtree> children;

	public BKtree(final CharSequence word)
	{
		this.word = word;
	}

	public BKtree(final CharSequence word, final int distance)
	{
		this.word = word;
	}

	public boolean insert(final LevenshteinDistance lev, final CharSequence word)
	{
		final byte distance = (byte) (int) lev.apply(this.word, word);
		if(distance == 0)
			return false;
		if(children == null)
		{
			children = new TreeMap<Byte, BKtree>();
			children.put((byte) distance, new BKtree(word, distance));
			return true;
		}
		else
		{
			final BKtree child = children.get(distance);
			return child.insert(lev, word);
		}
	}

	public Collection<CharSequence> find(LevenshteinDistance lev, final Collection<CharSequence> values, final CharSequence misspelledWord)
	{
		final int distanceToRoot = lev.apply(word, misspelledWord);
		final int lowerBound = distanceToRoot - lev.getThreshold();
		final int upperBound = distanceToRoot + lev.getThreshold();		
		
		if(distanceToRoot <= lev.getThreshold())
			values.add(word);
		
		if(children == null)
			return values;
		
		Iterator<Byte> iter = children.keySet().iterator();	
		
		while(iter.hasNext())
		{
			final byte childKey = iter.next();
			if(withinBounds(lowerBound, upperBound, childKey))
			{
				final BKtree subtree = children.get(childKey);
				subtree.find(lev, values, misspelledWord);
			}
//			final int wordDistance = lev.apply(word, misspelledWord);
//			if(wordDistance == -1)
//				continue;
//			if(wordDistance < childKey)
//				return word;
//			return children.get(childKey).find(lev, misspelledWord);
		}
		
		return values;
	}

	private boolean withinBounds(int lowerBound, int upperBound, byte childKey)
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
