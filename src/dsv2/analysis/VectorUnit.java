package dsv2.analysis;

import java.util.Map;

public interface VectorUnit<T>
{
	/**
	 * @return the numbers of occurrences of objects of type T
	 */
	Map<T, Integer> vectors();
}
