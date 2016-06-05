package com.guide.przewo.Models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ... Klasa w której trzymane są informacje nt. trasy ...
 */
public class Route extends ObjectBase implements Serializable
{
    @SerializedName("id_route")
    public int id;
    @SerializedName("places_ids")
    public List<Integer> placesIds;
    public List<Place> places;
    @SerializedName("tour_time")
    public double tour_time;
    public static List<Route> AllRoutes;

    public Route(int id, String name, String description, String url, double tour_time)
    {
        super(name, description, url, ObjectType.ROUTE);
        this.id = id;
        this.tour_time = tour_time;
    }

    public Route(int id, String name, String description, String url, List<Place> places, double tour_time)
    {
        this(id, name, description, url, tour_time);
        this.places = places;
        placesIds = new ArrayList<Integer>();
        if (places != null)
        {
            for (Place place : places)
            {
                placesIds.add(place.id);
            }
        }
    }

    public Place getPlace(int index)
    {
        return places.get(index);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
