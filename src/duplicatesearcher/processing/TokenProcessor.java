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
	 * Process a {@link Token}.
	 * @param token input that will be processed.
	 * @return a processed version of the original Token that was given as input.
	 */
	Token process(final Token token);
}
