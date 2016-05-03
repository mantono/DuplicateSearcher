package duplicatesearcher.flags;

/**
 * @author Anton &Ouml;sterberg
 */
public interface BooleanFlag extends Flag<Boolean>
{
	@Override
	default Boolean defaultValue()
	{
		return false;
	}

	@Override
	default Boolean getMinimumValue()
	{
		return false;
	}

	@Override
	default Boolean getMaximumValue()
	{
		return true;
	}

	@Override
	default boolean takesArgument()
	{
		return false;
	}
	
	@Override
	default Boolean parse(String arg)
	{
		return Boolean.parseBoolean(arg);
	}
}
