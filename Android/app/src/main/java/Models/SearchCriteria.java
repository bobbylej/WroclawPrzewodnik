package Models;

public class SearchCriteria
{
	private String query = "";
	private boolean onlyPlaces = false;
	private boolean onlyEvents = false;

	public String GetQuery()
	{
		return query;
	}

	public boolean SearchOnlyForPlaces()
	{
		return onlyPlaces;
	}

	public boolean SearchOnlyForEvents()
	{
		return onlyEvents;
	}
}
