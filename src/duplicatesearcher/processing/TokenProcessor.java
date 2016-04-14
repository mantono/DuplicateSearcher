package duplicatesearcher.processing;

import duplicatesearcher.Token;
import duplicatesearcher.analysis.frequency.TermFrequencyCounter;

/**
 * An interface for each component in the artifact that manipulates content in
 * issues.
 *
 */
public interface TokenProcessor
{
	/**
	 * 
	 * @param tokens a {@link TermFrequencyCounter} containing instances of
	 * {@link Token} which will be modified.
	 * @return the number of elements that were modified.
	 */
	int process(final TermFrequencyCounter tokens);
}
