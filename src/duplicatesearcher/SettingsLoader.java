package duplicatesearcher;

import java.util.EnumMap;

public class SettingsLoader<T extends Enum<T> & ProgramOption>
{
	private final Class<T> enumClass;
	private final EnumMap<T, String> settings;

	public SettingsLoader(Class<T> enumClass)
	{
		this.enumClass = enumClass;
		this.settings = loadDefaultSettings();
	}

	private EnumMap<T, String> loadDefaultSettings()
	{
		final EnumMap<T, String> defaultSettings = new EnumMap<T, String>(enumClass);
		for(T op : values())
			defaultSettings.put(op, op.defaultValue());
		return defaultSettings;
	}

	private T getOption(String string)
	{
		for(T option : values())
			if(option.matches(string))
				return option;
		return null;
	}

	private T[] values()
	{
		return enumClass.getEnumConstants();
	}

	public void applyAgrumentVector(String[] args)
	{
		for(int i = 0; i < args.length; i += 2)
		{
			T option = getOption(args[i]);
			if(option == null)
			{
				System.err.println("Argument " + args[i]
						+ " is not a valid flag. See --help for options.");
				System.exit(1);
			}
			else if(option.equals(getOption("--help")))
			{
				for(T op : values())
					System.out.println(op);
				System.exit(0);
			}
			try
			{
				settings.put(option, args[i + 1]);
			}
			catch(ArrayIndexOutOfBoundsException exception)
			{
				System.err.println("Flag " + args[i] + " requires an argument.");
				System.exit(3);
			}
		}
		validateSettings(settings);
	}

	private void validateSettings(final EnumMap<T, String> settings)
	{
		for(T setting : settings.keySet())
		{
			final String value = settings.get(setting);
			if(!setting.inRange(value))
			{
				System.err.println("Argument " + value + " is not valid for flag "
						+ setting.getLongFlag() + ", valid range is " + setting.getMinimumValue()
						+ " - " + setting.getMaximumValue() + ".");
				System.exit(4);
			}
		}
	}

	public EnumMap<T, String> getSettings()
	{
		return settings;
	}
}
