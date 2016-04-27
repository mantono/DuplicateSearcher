package duplicatesearcher;

/**
 * Interface for program options that can be loaded through the command line
 * argument vector. The purpose of this interface, and its implementing classes,
 * is to simplify argument loading and validation with settings that are sent to
 * the main method at application launch.
 * 
 * @author Anton &Ouml;sterberg
 *
 */
public interface ProgramOption
{
	/**
	 * @return the lowest value that this option allows.
	 */
	int getMinimumValue();

	/**
	 * @return the highest value that this option allows.
	 */
	int getMaximumValue();

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
	 * @return the default value.
	 */
	String defaultValue();

	/**
	 * 
	 * @return the default value as an <code>int</code>, if it is possible to
	 * represent it in such a way.
	 */
	default int getIntOfDefaultValue()
	{
		return Integer.parseInt(defaultValue());
	}

	/**
	 * Check whether a value is within the allowed range for this option.
	 * 
	 * @param value the value that should be checked.
	 * @return true if it greater than or equal to the lowest allowed value and
	 * is less than or equal to the highest allowed value. If not, it will
	 * return false. If the value does not have a minimum and maximum allowed
	 * value, it will return true.
	 */
	default boolean inRange(String stringValue)
	{
		if(getMinimumValue() == getMaximumValue())
			return true;
		final int value = Integer.parseInt(stringValue);
		return value >= getMinimumValue() && value <= getMaximumValue();
	}
	
	/**
	 * Check whether a value is within the allowed range for this option.
	 * 
	 * @param value the value that should be checked.
	 * @return true if it greater than or equal to the lowest allowed value and
	 * is less than or equal to the highest allowed value. If not, it will
	 * return false. If the value does not have a minimum and maximum allowed
	 * value, it will return true.
	 */
	default boolean inRange(int value)
	{
		return value >= getMinimumValue() && value <= getMaximumValue();
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
}
