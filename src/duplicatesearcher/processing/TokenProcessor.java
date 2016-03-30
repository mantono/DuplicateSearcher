package duplicatesearcher.processing;

import java.util.Set;

import duplicatesearcher.StrippedIssue;

/**
 * An interface for each component in the artifact that manipulates content in
 * issues.
 *
 */
public interface TokenProcessor
{
	/**
	 * Retrieves the input data in its processed form.
	 * 
	 * @return a set of {@link String} elements after they have been processed.
	 */
	StrippedIssue process(final StrippedIssue input);
}
