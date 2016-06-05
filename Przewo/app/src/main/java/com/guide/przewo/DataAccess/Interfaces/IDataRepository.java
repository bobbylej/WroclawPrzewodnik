package com.guide.przewo.DataAccess.Interfaces;


import com.google.android.gms.maps.model.LatLng;
import com.guide.przewo.Models.Event;
import com.guide.przewo.Models.Place;
import com.guide.przewo.Models.Route;
import com.guide.przewo.Models.SearchCriteria;
import com.guide.przewo.Models.SearchResult;

import java.util.Collection;

public interface IDataRepository
{
	Place getPlaceDetails(int id);
	Event getEventDetails(int id);
	Route getRouteDetails(int id);
	Iterable<Place> getNearbyPlaces(LatLng location, float distanceInMetres, SearchCriteria criteria);
	Collection<SearchResult> searchFor(SearchCriteria criteria);
}
