package com.guide.przewo;

import android.net.ConnectivityManager;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.guide.przewo.DataAccess.LocalRepository;
import com.guide.przewo.DataAccess.Outsource.ReadTask;
import com.guide.przewo.Models.Place;
import com.guide.przewo.Models.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adamczyk Mateusz on 2015-06-04.
 */
public class RouteManager {

    LocalRepository localRepository;

    public ReadTask readTask;

    MapManager mapManager;

    public int currentRouteIdForRadar;

    public RouteManager(LocalRepository localRepository, MapManager mapManager) {
        this.localRepository = localRepository;
        this.mapManager = mapManager;
    }


    public Route showRoute(int currentRouteId, boolean walking) {
        currentRouteIdForRadar = currentRouteId;
        Log.d("ShowRoute", currentRouteId + " ");
        if(currentRouteId != -1) {
            Route currentRoute = localRepository.getRouteDetails(currentRouteId);
            if(currentRoute != null) {
                drawRoute(currentRoute, true, walking);
                return currentRoute;
            }
        }
        return null;
    }

    //
    public void drawRoute(Route currentRoute, boolean optimize, boolean walking) {
        if(currentRoute.places.size() == 0)
            currentRoute.places = localRepository.getPlacesInRoute(currentRoute.id);
        for(Place place : currentRoute.places)
        {
            mapManager.drawMarker(mapManager.getMap(), place, false, true);
            Log.d("DrawRoutePlace", place.latitude + " " + place.longitude + " id: " + place.id + " name: " + place.name);
        }
        List<LatLng> positions = getPositions(currentRoute);
        if(positions.size() > 0) {
            String url = getMapsApiDirectionsUrl(positions, optimize, walking);
            readTask = new ReadTask(mapManager.getMap());
            readTask.execute(url);
            CameraPosition g= mapManager.getMap().getCameraPosition();
            CameraUpdate cameraUpdate = GetCameraUpdateForPositions(positions);
            mapManager.getMap().animateCamera(cameraUpdate);
        }
    }

    //get positions from Route
    public List<LatLng> getPositions(Route route){
        List<LatLng> positions = new ArrayList<LatLng>();
        if(route.places.size() == 0)
            route.places = localRepository.getPlacesInRoute(route.id);
        for(Place place : route.places){
            LatLng latLng = new LatLng(place.latitude, place.longitude);
            positions.add(latLng);

        }
        return positions;
    }

    public CameraUpdate GetCameraUpdateForPositions(List<LatLng> positions)
    {
        double minLat = Double.MAX_VALUE, minLon = Double.MAX_VALUE, maxLat = Double.MIN_VALUE, maxLon = Double.MIN_VALUE;

        for (LatLng pos : positions)
        {
            if (pos.longitude < minLon) minLon = pos.longitude;
            if (pos.longitude > maxLon) maxLon = pos.longitude;
            if (pos.latitude < minLat) minLat = pos.latitude;
            if (pos.latitude > maxLat) maxLat = pos.latitude;
        }
        LatLngBounds bounds = new LatLngBounds(new LatLng(minLat, minLon), new LatLng(maxLat, maxLon));
        return CameraUpdateFactory.newLatLngBounds(bounds, 200);
    }

    public String getMapsApiDirectionsUrl(List<LatLng> positions, boolean optimize, boolean walking) {
        String waypoints = "waypoints=optimize:"+optimize;

        for(int i=0; i< positions.size(); i++) {
            waypoints += "|" + positions.get(i).latitude + "," + positions.get(i).longitude;
        }

        String sensor = "sensor=false";
        String origin_dest = "origin=" + positions.get(0).latitude + "," + positions.get(0).longitude+
                "&destination="+ positions.get(positions.size()-1).latitude + ","
                + positions.get(positions.size()-1).longitude;
        String transit_mode = "mode=";
        if (walking)
        {
            transit_mode += "walking";
        }
        else
        {
            transit_mode += "driving";
        }
        String params = origin_dest + "&" + waypoints + "&" + sensor + "&" + transit_mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        Log.d("URL", url);
        return url;
    }

}
