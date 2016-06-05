package Models;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.List;

public class Event extends ObjectBase implements Serializable
{
	private Place place;
	private Date startDate;
	private Date endDate;
	private Time startTime;
	private Time endTime;

	public Event(int id, String name, String description, String link, int version, List<Type> types, Date changeDate, Date startDate, Date endDate, Time startTime, Time endTime, Place place)
	{
		super(id, name, description, link, version, types, changeDate);

		this.startDate = startDate;
		this.endDate = endDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.place = place;
	}

	public Place GetPlace()
	{
		return place;
	}

	public Date GetStartDate()
	{
		return startDate;
	}

	public Date GetEndDate()
	{
		return endDate;
	}

	public Time GetStartTime()
	{
		return startTime;
	}

	public Time GetEndTime()
	{
		return endTime;
	}

	public Location GetLocation()
	{
		return place.GetLocation();
	}

    @Override
    public String toString() {
        String temp = name + (place == null? "" : " - " + place.GetName());
        return temp;
    }
}
