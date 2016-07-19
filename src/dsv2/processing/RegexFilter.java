package dsv2.processing;

public interface RegexFilter
{
	String regex();
	default String substitute()
	{
		return "";
	}
}
