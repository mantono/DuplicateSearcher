package duplicatesearcher.flags;

import java.util.EnumMap;

public class DoubleFlagLoader
{
	private final EnumMap<IssueComponent, Double> settings;

	public SettingsLoader()
	{
		this.settings = loadDefaultSettings();
	}

	private EnumMap<IssueComponent, Double> loadDefaultSettings()
	{
		final EnumMap<IssueComponent, Double> defaultSettings = new EnumMap<IssueComponent, Double>(IssueComponent.class);
		
		
		
		for(IssueComponent op : values())
			defaultSettings.put(op, op.defaultValue());
		return defaultSettings;
	}

	private IssueComponent getOption(String string)
	{
		for(IssueComponent option : IssueComponent.values())
			if(option.matches(string))
				return option;
		return null;
	}

//	private E[] values()
//	{
//		return enumClass.getEnumConstants();
//	}

	public void applyAgrumentVector(String[] args)
	{
		for(int i = 0; i < args.length; i++)
		{
			E option = getOption(args[i]);
			if(option == null)
			{
				continue;
//				System.err.println("Argument " + args[i]
//						+ " is not a valid flag. See --help for options.");
//				System.exit(1);
			}
			else if(option.equals(getOption("--help")))
			{
				for(E op : values())
					System.out.println(op);
				System.exit(0);
			}
			try
			{
				if(!option.takesArgument())
					settings.put(option, option.getMaximumValue());
				else
				{
					settings.put(option, option.parse(args[i + 1]));
					i++;
				}
			}
			catch(ArrayIndexOutOfBoundsException exception)
			{
				System.err.println("Flag " + args[i] + " requires an argument.");
				System.exit(3);
			}
		}
		validateSettings(settings);
	}

	private void validateSettings(final EnumMap<E, T> settings)
	{
		for(E setting : settings.keySet())
		{
			final T value = settings.get(setting);
			if(!setting.inRange(value))
			{
				System.err.println("Argument " + value + " is not valid for flag "
						+ setting.getLongFlag() + ", valid range is " + setting.getMinimumValue()
						+ " - " + setting.getMaximumValue() + ".");
				System.exit(4);
			}
		}
	}

	public EnumMap<E, T> getSettings()
	{
		return settings;
	}
}
