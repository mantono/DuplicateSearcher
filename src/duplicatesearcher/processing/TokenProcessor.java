package duplicatesearcher.processing;

import java.util.Set;

/**
 * An interface for each component in the artifact
 *
 */
public interface TokenProcessor
{
	/**
	 * Retrieves the input data in its processed form.
	 * 
	 * @return a set of {@link String} elements after they have been processed.
	 */
	Set<String> getProcessedData();
}
