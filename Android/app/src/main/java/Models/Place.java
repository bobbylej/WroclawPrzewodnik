package Models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Place extends ObjectBase implements Serializable
{
	public Location location;
    public String openTime;
    public int eventsCount;

	public Place(int id, String name, String description, String link, int version, List<Type> types, Date changeDate, Location location)
	{
		super(id, name, description, link, version, types, changeDate);
		this.location = location;
        this.openTime = "";
        this.eventsCount = 0;
	}

    public Place(int id, String name, String description, String link, int version, List<Type> types, Date changeDate, Location location, int eventsCount)
    {
        super(id, name, description, link, version, types, changeDate);
        this.location = location;
        this.openTime = "";
        this.eventsCount = eventsCount;
    }

    public Place(int id, String name, String description, String link, int version, List<Type> types, Date changeDate, Location location, String openTime)
    {
        super(id, name, description, link, version, types, changeDate);
        this.location = location;
        this.openTime = openTime;
        this.eventsCount = 0;
    }

    public Place(int id, String name, String description, String link, int version, List<Type> types, Date changeDate, Location location, String openTime, int eventsCount)
    {
        super(id, name, description, link, version, types, changeDate);
        this.location = location;
        this.openTime = openTime;
        this.eventsCount = eventsCount;
    }

	public void CopyFrom(Place place)
	{
		id = place.id;
		name = place.name;
		description = place.description;
		link = place.link;
        openTime = place.openTime;
		version = place.version;
		types = place.types;
		changeDate = place.changeDate;
		location = place.location;
        openTime = place.openTime;
        eventsCount = place.eventsCount;
	}

    public int getEventCount()
    {
        return (new Random()).nextInt(6);
    }

	public Location GetLocation()
	{
		return location;
	}

    public String GetOpenTime() {
        return openTime;
    }
}
