package duplicatesearcher.flags;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ProcessingFlagLoader
{
	private EnumSet<ProcessingFlag> flags;
	
	public void applyAgrumentVector(String[] args)
	{
		List<ProcessingFlag> flagList = new ArrayList<ProcessingFlag>(); 
		for(int i = 0; i < args.length; i++)
		{
			ProcessingFlag option = getOption(args[i]);
			if(args[i].equals("--help") || args[i].equals("-h"))
			{
				for(ProcessingFlag op : values())
					System.out.println(op.helpDescription());
				System.exit(0);
			}
			else if(option == null)
			{
				continue;
			}
			flagList.add(option);
		}
		if(flagList.isEmpty())
			flags = EnumSet.noneOf(ProcessingFlag.class);
		else
			flags = EnumSet.copyOf(flagList);
	}
	
	private ProcessingFlag[] values()
	{
		return ProcessingFlag.values();
	}
	
	private ProcessingFlag getOption(String string)
	{
		for(ProcessingFlag option : values())
			if(option.matches(string))
				return option;
		return null;
	}
	
	public EnumSet<ProcessingFlag> getSettings()
	{
		return flags;
	}
}
