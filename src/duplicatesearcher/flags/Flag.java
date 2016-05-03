package duplicatesearcher.flags;

/**
 * Interface for program options that can be loaded through the command line
 * argument vector. The purpose of this interface, and its implementing classes,
 * is to simplify argument loading and validation with settings that are sent to
 * the main method at application launch.
 * 
 * @author Anton &Ouml;sterberg
 * @param <T> type for the flag parameter.
 *
 */
public interface Flag<T extends Comparable<T>>
{

	/**
	 * @return returns the short flag representation for this option that is
	 * combined with a single dash ("-").
	 */
	char getShortFlag();

	/**
	 * 
	 * @return returns the long flag representation for this option that is
	 * combined with a double dash ("--").
	 */
	String getLongFlag();

	/**
	 * 
	 * @return a description that describes what this option does.
	 */
	String getDescription();

	/**
	 * 
	 * @return the default value as the parameterized type <code>T</code>.
	 */
	T defaultValue();

	/**
	 * @return true if this flag takes an argument, else false.
	 */
	default boolean takesArgument()
	{
		return true;
	}

	/**
	 * Checks whether a {@link String} matches any of the options for this
	 * class.
	 * 
	 * @param input the {@link String} that should be checked whether it matched
	 * or not.
	 * @return true if the input is equal to the short flag or the long flag
	 * without the dash prefix.
	 */
	default boolean matches(final String input)
	{
		return input.equals("-" + getShortFlag()) || input.equals("--" + getLongFlag());
	}

	/**
	 * @return a text that is printed whenever a user calls the help flag, if
	 * such a flag exists for the implementing class or enumerate.
	 */
	default String helpDescription()
	{
		final StringBuilder str = new StringBuilder();
		str.append("-" + getShortFlag() + ", --" + getLongFlag() + "\n\t" + getDescription() + "\n");
		return str.toString();
	}

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

	/**
	 * Takes a {@link String} representation of the argument as it is received
	 * from the argument vector and returns a parsed version of type
	 * <code>T</code>.
	 * 
	 * @param arg the {@link String} that will be parsed.
	 * @return the parsed version of the input data.
	 */
	T parse(String arg);
}
