package com.guide.przewo.DataAccess;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.guide.przewo.DataAccess.Interfaces.IDataRepository;
import com.guide.przewo.Models.Category;
import com.guide.przewo.Models.Event;
import com.guide.przewo.Models.Place;
import com.guide.przewo.Models.Route;
import com.guide.przewo.Models.SearchCriteria;
import com.guide.przewo.Models.SearchResult;
import com.guide.przewo.Models.Transport;
import com.guide.przewo.Models.Type;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class LocalRepository implements IDataRepository
{
	SQLiteHelper sqLiteHelper;

    public LocalRepository(Context context)
    {
        sqLiteHelper = new SQLiteHelper(context);
    }

	public Place getPlaceDetails(int placeId)
	{
		Place place = sqLiteHelper.getPlaceWithId(placeId);

		return place;
	}

	public Event getEventDetails(int eventId)
	{
		Event event = sqLiteHelper.getEventWithId(eventId);

		return event;
	}

	public Route getRouteDetails(int routeId)
	{
		Route route = sqLiteHelper.getRouteWithID(routeId);

		return route;
	}

	public void addOrUpdatePlaces(List<Place> places)
	{
		for(Place place : places)
		{
			sqLiteHelper.addOrUpdatePlace(place);
		}
	}

	public void addOrUpdateEvents(List<Event> events)
	{
		for(Event event : events)
		{
			sqLiteHelper.addOrUpdateEvent(event);
		}
	}

	public void addOrUpdateRoutes(List<Route> routes)
	{
		for(Route route : routes)
		{
			sqLiteHelper.addOrUpdateRoute(route);
		}
	}

	public void addOrUpdateTypes(List<Type> types)
	{
		for(Type type : types)
		{
			sqLiteHelper.addOrUpdatePlaceType(type);
		}
	}

	public void addOrUpdateEventCategories(List<Category> categories)
	{
		for(Category category : categories)
		{
			sqLiteHelper.addOrUpdateEventCategory(category);
		}
	}

	public void updateTransport(List<Transport> transports)
	{
		sqLiteHelper.clearTransport();
		Log.d("dupa", transports.size()+"");
		for(Transport t : transports)
		{
			sqLiteHelper.addTransport(t);
		}
	}

	public Iterable<Transport> getTransport() {
		return sqLiteHelper.getTransport();
	}

	public Collection<SearchResult> searchFor(SearchCriteria criteria)
	{
		Collection<SearchResult> searchResults = sqLiteHelper.getSearchResult(criteria);

		return searchResults;
	}

	public Iterable<Place> getAllPlaces(SearchCriteria criteria) {
		List<Place> places;

		places = sqLiteHelper.getAllPlaces();

		return places;
	}

	public Iterable<Place> getNearbyPlaces(LatLng location, float distanceInMetres, SearchCriteria criteria) {
        List<Place> places;
        double distanceInDeg = distanceInMetres / 110574;
        float latMax = (float) (location.latitude + distanceInDeg);
        float latMin = (float) (location.latitude - distanceInDeg);
        float longMax = (float) (location.longitude + distanceInDeg);
        float longMin = (float) (location.longitude - distanceInDeg);

        places = sqLiteHelper.getPlacesWithFromRange(latMax, latMin, longMax, longMin);

        return places;
    }

	public Iterable<Place> getNearbyPlaces(LatLng location, float distanceInMetres, List<Type> choosenTypes, List<Category> choosenCategories, SearchCriteria criteria) {
		List<Place> places;
		double distanceInDeg = distanceInMetres / 110574;
		float latMax = (float) (location.latitude + distanceInDeg);
		float latMin = (float) (location.latitude - distanceInDeg);
		float longMax = (float) (location.longitude + distanceInDeg);
		float longMin = (float) (location.longitude - distanceInDeg);

		places = sqLiteHelper.getPlacesWithFromRange(latMax, latMin, longMax, longMin, choosenTypes, choosenCategories);

		return places;
	}

    public List<Type> getAllPlacesTypes()
	{
        List<Type> types = sqLiteHelper.getAllPlacesTypes();

        return types;
    }

	public List<Category> getAllEventCategories()
	{
		List<Category> categories = sqLiteHelper.getAllEventsCategories();

		return categories;
	}

    public List<Route> getAllRoutes()
	{
        return sqLiteHelper.getAllRoutes();
    }

	public List<Place> getPlacesInRoute(int routeId) {
		return sqLiteHelper.getPlacesInRoute(routeId);
	}

	public List<Place> getPlacesOfType(int type)
	{
		return sqLiteHelper.getPlacesOfTypes(Arrays.asList(type));
	}

	public Place getInterestingPlace()
	{
		List<Place>  interestingPlaces =  sqLiteHelper.getPlacesOfTypes(Arrays.asList(17));
		Place closest = null;
		double closestDistance = 0;

		for (Place place : interestingPlaces)
		{
			double newDistance = place.getDistance();

			if (closest == null || newDistance < closestDistance)
			{
				closestDistance = newDistance;
				closest = place;
			}
		}
		return closest;
	}

	public List<Event> getEventsInPlace(int placeId)
	{
		return sqLiteHelper.getEventsInPlace(placeId);
	}

	public Place getRandomPlace() {
		return sqLiteHelper.getRandomPlace();
	}
}
