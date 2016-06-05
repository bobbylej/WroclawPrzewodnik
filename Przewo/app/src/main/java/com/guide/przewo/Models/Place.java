package com.guide.przewo.Models;


import android.location.Location;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/*! Klasa w której trzymane są informacje nt. miejsca */
public class Place extends ObjectBase implements Serializable
{
	@SerializedName("id_place")
	public int id;
	public float latitude;
	public float longitude;
	public String city;
	public String street;
	@SerializedName("types_ids")
	public List<Integer> typesIds;
	@SerializedName("photos")
	public List<String> photos;
	public List<Type> types;
	@SerializedName("events_count")
    public int eventCount;

	public static List<Type> AllPlacesTypes = new ArrayList<>();
	private static Location currentLocation;

	public Place(int id, String name, String description, String url, List<Integer> typesIds, List<String> photos, float latitude, float longitude, String city, String street, int eventCount)
	{
		super(name, description, url, ObjectType.PLACE);
		this.id = id;
        this.latitude = latitude;
		this.longitude = longitude;
		this.city = city;
		this.street = street;
        this.eventCount = eventCount;
		this.typesIds = typesIds;
		this.photos = photos;

		types = new ArrayList<>();
		for(Type type : AllPlacesTypes)
		{
			if (typesIds.contains(type.id))
			{
				types.add(type);
			}
		}
	}

	@Override
	public String toString()
	{
		return name;
	}

	public static void setCurrentLocation(Location location)
	{
		currentLocation = location;
	}
	public static Location getCurrentLocation()
	{
		return currentLocation;
	}
	public String getDistanceInHumarFormat()
	{
		DecimalFormat four = new DecimalFormat("#0.00");
		return "Odległość: " + four.format(getDistance()) + " km";
	}

	public double getDistance()
	{
		return distance(currentLocation.getLatitude(), latitude, currentLocation.getLongitude(), longitude);
	}

	private static double distance(double lat1, double lat2, double lon1, double lon2)
	{
		final int R = 6371; // Radius of the earth

		Double latDistance = Math.toRadians(lat2 - lat1);
		Double lonDistance = Math.toRadians(lon2 - lon1);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c; // convert to km

		distance = Math.pow(distance, 2);

		return Math.sqrt(distance);
	}

	public String getAddress()
	{
		return street + System.getProperty ("line.separator") + city;
	}
}
