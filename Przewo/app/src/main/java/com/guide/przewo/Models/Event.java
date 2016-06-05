package com.guide.przewo.Models;


import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Event extends ObjectBase implements Serializable
{
	@SerializedName("id_event")
	public int id;
	@SerializedName("id_place")
	public int idPlace;
	public Place place;
	@SerializedName("start_time")
	public DateTime startTime;
	@SerializedName("end_time")
    public DateTime endTime;
	@SerializedName("categories_ids")
	public List<Integer> categoriesIds;
	@SerializedName("photos")
	public List<String> photos;
	public List<Category> categories;

	public static List<Category> AllEventCategories;
	public static String[] Months = {"stycznia", "lutego", "marca", "kwietnia", "maja", "czerwca", "lipca", "sierpnia", "września", "października", "listopada", "grudnia"};

	public Event(int id, String name, String description, String url, List<Integer> categoriesIds, List<String> photos, DateTime startTime, DateTime endTime, int idPlace)
	{
		super(name, description, url, ObjectType.EVENT);
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.idPlace = idPlace;

		this.categoriesIds = categoriesIds;
		this.photos = photos;

		categories = new ArrayList<Category>();
		for(Category category : AllEventCategories)
		{
			if (categoriesIds.contains(category.id))
			{
				categories.add(category);
			}
		}
	}

	public void updatePlace(Place place)
	{
		this.place = place;
	}

	@Override
	public String toString()
	{
		return startTime.toString("HH:mm dd.MM.yy") + " - "
				+ name + (place != null?" (" + place.name+ ")":"") ;
	}

	public String getDateInHumanFormat()
	{
		long hoursToEvent = (startTime.getMillis() - (new DateTime().getMillis())) / 3600000;

		if (hoursToEvent > 0)
		{
			if (hoursToEvent < 8)
			{
				return "Już o " + getTime(startTime) +"!";
			}
		}

		return startTime.dayOfMonth().getAsString() + " " + Months[startTime.getMonthOfYear() - 1] + " " +  String.format("%02d", Integer.parseInt(startTime.hourOfDay().getAsString())) + ":" +  String.format("%02d", Integer.parseInt(startTime.minuteOfHour().getAsString()));
	}


	public String getEndDateInHumanFormat()
	{
		long hoursToEvent = (endTime.getMillis() - (new DateTime().getMillis())) / 3600000;

		if (hoursToEvent > 0)
		{
			if (hoursToEvent < 8)
			{
				return "Już o " + getTime(endTime);
			}
		}

		return endTime.dayOfMonth().getAsString() + " " + Months[endTime.getMonthOfYear() - 1] + " " +  String.format("%02d", Integer.parseInt(endTime.hourOfDay().getAsString())) + ":" +  String.format("%02d", Integer.parseInt(endTime.minuteOfHour().getAsString()));
	}

	private String getTime(DateTime time)
	{
		DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");

		return time.toString(dtf);
	}
}
