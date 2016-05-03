package duplicatesearcher.flags;

/**
 * @author Anton &Ouml;sterberg
 *
 * @param <T> a type that implements the {@link Comparable} interface, allowing
 * flags that has a limited span for valid options to be checked for being in
 * range.
 */
public interface ComparableFlag<T extends Comparable<T>> extends Flag<T>
{
	/**
	 * @return the lowest value that this option allows.
	 */
	T getMinimumValue();

	/**
	 * @return the highest value that this option allows.
	 */
	T getMaximumValue();

	/**
	 * Check whether a value is within the allowed range for this option.
	 * 
	 * @param value the value that should be checked.
	 * @return true if it greater than or equal to the lowest allowed value and
	 * is less than or equal to the highest allowed value. If not, it will
	 * return false. If the value does not have a minimum and maximum allowed
	 * value, it will return true.
	 */
	default boolean inRange(T value)
	{
		return value.compareTo(getMinimumValue()) >= 0 && value.compareTo(getMaximumValue()) <= 0;
	}
	
	@Override
	default boolean takesArgument()
	{
		return true;
	}

}
