package com.guide.przewo;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.guide.przewo.DataAccess.LocalRepository;
import com.guide.przewo.Models.Category;
import com.guide.przewo.Models.Place;
import com.guide.przewo.Models.Route;
import com.guide.przewo.Models.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Adamczyk Mateusz on 2015-06-04.
 */
public class RadarManager
{

    private final RouteManager routeManager;
    LocalRepository localRepository;
    static boolean isRadarBlocked = false;
    MapManager mapManager;

    public RadarManager(LocalRepository localRepository, MapManager mapManager, RouteManager routeManager) {
        this.localRepository = localRepository;
        this.mapManager = mapManager;
        this.routeManager = routeManager;
    }

    public void showNearbyPlaces(Circle circle, HashMap<Integer, Marker> markers)
    {
        if (isRadarBlocked)
            return;

        if(circle != null) {
            //pobierz i pokaż na mapie miejsca w promieniu radaru z bazy sqlite
//            int radiusMargin = (int) (0.2*circle.getRadius());
            int radiusMargin = 0;
            showMarkersInCircle(circle.getCenter(), (int)circle.getRadius(), radiusMargin, markers);
        }
    }

    public void showNearbyPlaces(LatLng center, int radius, boolean forPlace, HashMap<Integer, Marker> markers)
    {
        if (isRadarBlocked)
            return;

        if(center != null) {
            //pobierz i pokaż na mapie miejsca w promieniu radaru z bazy sqlite
            int radiusMargin = (int) (0.2*radius);
            showMarkersInCircle(center, radius, radiusMargin, forPlace, markers);
        }
    }

    public void showNearbyPlaces(Circle circle, List<Type> choosenTypes, List<Category> choosenCategories, HashMap<Integer, Marker> markers)
    {
        if (isRadarBlocked)
            return;

        if(circle != null) {
            //pobierz i pokaż na mapie miejsca w promieniu radaru z bazy sqlite
//            int radiusMargin = (int) (0.2*circle.getRadius());
            int radiusMargin = 0;
            showMarkersInCircle(circle.getCenter(), (int)circle.getRadius(), radiusMargin, choosenTypes, choosenCategories, markers);
        }
    }

    public HashMap<Integer, Marker> showMarkersInCircle(LatLng center, int radius, int radiusMargin, HashMap<Integer, Marker> markers)
    {
        if (isRadarBlocked)
            return new HashMap<Integer, Marker>();

        HashMap<Integer, Marker> markersList = new HashMap<Integer, Marker>();
        HashMap<String,List<Place>> places = getPlacesInCircle(center, radius, radiusMargin);
        if(places.size() > 0) {
            markers = makeMarkers(places.get("in"), markers);
            markers = makeMarkers(places.get("onMargin"), markers);
            markers = makeMarkers(places.get("out"), markers);

            for (Place place : places.get("in")) {
                Marker marker = (Marker) markers.get(place.id);
                marker.setVisible(true);
                mapManager.changeIcon(marker, place, true);
                markersList.put(place.id, marker);
            }
            for (Place place : places.get("onMargin")) {
                Marker marker = (Marker) markers.get(place.id);
                marker.setVisible(true);
                mapManager.changeIcon(marker, place, false);
                markersList.put(place.id, marker);
            }
            for (Place place : places.get("out")) {
                Marker marker = (Marker) markers.get(place.id);
                marker.setVisible(false);
            }
        }
        return markersList;
    }

    public HashMap<Integer, Marker> showMarkersInCircle(LatLng center, int radius, int radiusMargin, boolean forPlace, HashMap<Integer, Marker> markers)
    {
        if (isRadarBlocked)
            return new HashMap<Integer, Marker>();

        HashMap<Integer, Marker> markersList = new HashMap<Integer, Marker>();
        HashMap<String,List<Place>> places = getPlacesInCircle(center, radius, radiusMargin);
        if(places.size() > 0) {
            markers = makeMarkersDisable(places.get("in"), markers);

            for (Place place : places.get("in")) {
                Marker marker = (Marker) markers.get(place.id);
                marker.setVisible(true);
                mapManager.changeIcon(marker, place, false);
                markersList.put(place.id, marker);
            }
        }
        return markersList;
    }

    public HashMap<Integer, Marker> showMarkersInCircle(LatLng center, int radius, int radiusMargin, List<Type> choosenTypes, List<Category> choosenCategories, HashMap<Integer, Marker> markers)
    {
        if (isRadarBlocked)
            return new HashMap<Integer, Marker>();

        HashMap<Integer, Marker> markersList = new HashMap<Integer, Marker>();
        HashMap<String,List<Place>> places = getPlacesInCircle(center, radius, radiusMargin, choosenTypes, choosenCategories);
        if(places.size() > 0) {
            markers = makeMarkers(places.get("in"), markers);
            markers = makeMarkers(places.get("onMargin"), markers);
            markers = makeMarkers(places.get("out"), markers);

            for (Place place : places.get("in")) {
                Marker marker = (Marker) markers.get(place.id);
                marker.setVisible(true);
                mapManager.changeIcon(marker, place, true);
                markersList.put(place.id, marker);
            }
            for (Place place : places.get("onMargin")) {
                Marker marker = (Marker) markers.get(place.id);
                marker.setVisible(true);
                mapManager.changeIcon(marker, place, false);
                markersList.put(place.id, marker);
            }
            for (Place place : places.get("out")) {
                Marker marker = (Marker) markers.get(place.id);
                marker.setVisible(false);
            }
        }
        return markersList;
    }

    public HashMap<String, List<Place>> getPlacesInCircle(LatLng center, int radius, int radiusMargin)
    {
        if (isRadarBlocked)
            return new HashMap<String, List<Place>>();

        HashMap<String, List<Place>> places = new HashMap<String, List<Place>>();
        List<Place> in = new ArrayList<Place>();
        List<Place> onMargin = new ArrayList<Place>();
        List<Place> out = new ArrayList<Place>();

        Route currentRoute = localRepository.getRouteDetails(routeManager.currentRouteIdForRadar);

        if(currentRoute != null)
            currentRoute.places = localRepository.getPlacesInRoute(currentRoute.id);

        List<Place> placesList = (List<Place>) localRepository.getNearbyPlaces(center, radius + radiusMargin, null);
        Log.d("Places", placesList.size() + " ------------");
        if(placesList.size() > 0) {
            outerLoop:
            for(Place place : placesList) {

                if(currentRoute != null)
                {
                    for(Place innerPlace : currentRoute.places)
                        if(innerPlace.id == place.id)
                            continue outerLoop;
                }

                float[] distance = new float[2];
                Location.distanceBetween(place.latitude, place.longitude,
                        center.latitude, center.longitude, distance);
//                Log.d("Markers", place.latitude + " " + place.longitude + " - " + center.latitude + " " + center.longitude + " = " + distance[0]);
                if (distance[0] > radius + radiusMargin) {
                    out.add(place);
                } else if (distance[0] > radius) {
                    onMargin.add(place);
                } else if (distance[0] <= radius) {
                    in.add(place);
                }
            }
        }

        places.put("in", in);
        places.put("onMargin", onMargin);
        places.put("out", out);
        return places;
    }

    public HashMap<String, List<Place>> getPlacesInCircle(LatLng center, int radius, int radiusMargin, List<Type> choosenTypes, List<Category> choosenCategories)
    {
        if (isRadarBlocked)
            return new HashMap<String, List<Place>>();

        HashMap<String, List<Place>> places = new HashMap<String, List<Place>>();
        List<Place> in = new ArrayList<Place>();
        List<Place> onMargin = new ArrayList<Place>();
        List<Place> out = new ArrayList<Place>();

        Route currentRoute = localRepository.getRouteDetails(routeManager.currentRouteIdForRadar);

        if(currentRoute != null)
            currentRoute.places = localRepository.getPlacesInRoute(currentRoute.id);

        List<Place> placesList = (List<Place>) localRepository.getNearbyPlaces(center, radius + radiusMargin, choosenTypes, choosenCategories, null);
        Log.d("Places", placesList.size() + " ------------");
        if(placesList.size() > 0) {
            outerLoop:
            for(Place place : placesList) {

                if(currentRoute != null)
                {
                    for(Place innerPlace : currentRoute.places)
                        if(innerPlace.id == place.id)
                            continue outerLoop;
                }

                float[] distance = new float[2];
                Location.distanceBetween(place.latitude, place.longitude,
                        center.latitude, center.longitude, distance);
//                Log.d("Markers", place.latitude + " " + place.longitude + " - " + center.latitude + " " + center.longitude + " = " + distance[0]);
                if (distance[0] > radius + radiusMargin) {
                    out.add(place);
                } else if (distance[0] > radius) {
                    onMargin.add(place);
                } else if (distance[0] <= radius) {
                    in.add(place);
                }
            }
        }

        places.put("in", in);
        places.put("onMargin", onMargin);
        places.put("out", out);
        return places;
    }

    public HashMap<Float, Place> getPlacesInCircleWithDistance(LatLng center, int radius)
    {
        if (isRadarBlocked)
            return new HashMap<Float, Place>();

        HashMap<Float, Place> places = new HashMap<Float, Place>();

        List<Place> placesList = (List<Place>) localRepository.getNearbyPlaces(center, radius, null);
        Log.d("Places", placesList.size() + " ------------");
        if(placesList.size() > 0) {
            for(Place place : placesList) {
                float[] distance = new float[2];
                Location.distanceBetween(place.latitude, place.longitude,
                        center.latitude, center.longitude, distance);
//                Log.d("Markers", place.latitude + " " + place.longitude + " - " + center.latitude + " " + center.longitude + " = " + distance[0]);
                if (distance[0] <= radius) {
                    places.put(distance[0], place);
                }
            }
        }
        return places;
    }

    public HashMap<Integer, Marker> makeMarkers(List<Place> places, HashMap<Integer, Marker> markers)
    {
        if (isRadarBlocked)
            return new HashMap<Integer, Marker>();

        if(markers == null) {
            markers = new HashMap<Integer, Marker>();
        }
        for(Place place:places) {
            if(!markers.containsKey(place.id)) {
                markers.put(place.id, mapManager.drawMarker(mapManager.getMap(), place, false, false));
            }
        }
        return markers;
    }

    public HashMap<Integer, Marker> makeMarkersDisable(List<Place> places, HashMap<Integer, Marker> markers)
    {
        if (isRadarBlocked)
            return new HashMap<Integer, Marker>();

        if(markers == null) {
            markers = new HashMap<Integer, Marker>();
        }
        for(Place place:places) {
            if(!markers.containsKey(place.id)) {
                markers.put(place.id, mapManager.drawMarkerDisable(mapManager.getMap(), place, false, false));
            }
        }
        return markers;
    }

}
