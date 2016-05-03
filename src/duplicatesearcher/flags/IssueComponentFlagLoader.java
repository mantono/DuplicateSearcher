package duplicatesearcher.flags;

import java.util.EnumMap;

public class IssueComponentFlagLoader
{
	private EnumMap<IssueComponent, Double> settings = new EnumMap<IssueComponent, Double>(IssueComponent.class);

	private EnumMap<IssueComponent, Double> loadDefaultSettings()
	{
		final EnumMap<IssueComponent, Double> defaultSettings = new EnumMap<IssueComponent, Double>(IssueComponent.class);
		for(IssueComponent op : values())
			defaultSettings.put(op, op.defaultValue());
		return defaultSettings;
	}

	private IssueComponent getOption(String string)
	{
		for(IssueComponent option : values())
			if(option.matches(string))
				return option;
		return null;
	}

	private IssueComponent[] values()
	{
		return IssueComponent.values();
	}

	public void applyAgrumentVector(String[] args)
	{
		for(int i = 0; i < args.length; i++)
		{
			IssueComponent option = getOption(args[i]);
			if(args[i].equals("--help") || args[i].equals("-h"))
			{
				for(IssueComponent op : values())
					System.out.println(op.helpDescription());
				System.exit(0);
			}
			else if(option == null)
			{
				continue;
			}
			try
			{
				settings.put(option, Double.parseDouble(args[i + 1]));
				i++;
			}
			catch(ArrayIndexOutOfBoundsException exception)
			{
				System.err.println("Flag " + args[i] + " requires an argument.");
				System.exit(3);
			}
		}
		validateSettings(settings);
	}

	private void validateSettings(final EnumMap<IssueComponent, Double> settings)
	{
		for(IssueComponent setting : settings.keySet())
		{
			final Double value = settings.get(setting);
			if(!setting.inRange(value))
			{
				System.err.println("Argument " + value + " is not valid for flag "
						+ setting.getLongFlag() + ", valid range is " + setting.getMinimumValue()
						+ " - " + setting.getMaximumValue() + ".");
				System.exit(4);
			}
		}
	}

	public EnumMap<IssueComponent, Double> getSettings()
	{
		return settings;
	}
}
