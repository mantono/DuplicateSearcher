package duplicatesearcher.analysis.frequency;

import java.io.Serializable;

import duplicatesearcher.Token;

/**
 * This abstract class counts the occurrences of a String token in an issue.
 *
 */
public interface FrequencyCounter extends Serializable
{	
	int getTokenFrequency(final Token token);
	double getWeight(final Token token);
}
