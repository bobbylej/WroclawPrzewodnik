package com.guide.przewo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.guide.przewo.DataAccess.App;
import com.guide.przewo.DataAccess.LocalRepository;
import com.guide.przewo.Models.Category;
import com.guide.przewo.Models.Place;
import com.guide.przewo.Models.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapManager implements LocationListener, GoogleMap.OnMyLocationChangeListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener
{
    private GoogleMap map;

    private Context context;

    public Location currentLocation;

    public Circle circle;

    public HashMap<Integer, Marker> markers;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    RouteManager routeManager;

    public RadarManager radarManager;

    MainActivity mainActivity;

    private int radius;

    public MapManager(GoogleMap map, Context context, MainActivity mainActivity)
    {
        this.context = context;
        sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.main_store), 0);
        editor = sharedPref.edit();
        LocalRepository localRepository = ((App) context).getLocalRepository();

        radius = sharedPref.getInt(context.getResources().getString(R.string.radar_radius), 3000);

        routeManager = new RouteManager(localRepository, this);
        radarManager = new RadarManager(localRepository, this, routeManager);
        this.mainActivity = mainActivity;
        setUpMapIfNeeded(map);
        map.setOnMarkerClickListener(this);

    }

    public GoogleMap getMap()
    {
        return map;
    }

    private void setUpMap()
    {
        map.clear();
        map.setMyLocationEnabled(true);
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);//use of location services by firstly defining location manager.
        Location loc = null;
        String provider = lm.getBestProvider(new Criteria(), true);
        Log.d("a", "1");
        if(provider != null)
        {
            loc = lm.getLastKnownLocation(provider);
        }

        Log.d("a", "2");
        if (loc == null)
        {
            loc = getCurrLocation(map);
        }

        Log.d("a", "3");
        if (loc == null)
        {
            loc = new Location("Wroclaw");
            loc.setLatitude(51.10816);
            loc.setLongitude(17.03035);
        }
        Log.d("a", "4");
        Place.setCurrentLocation(loc);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 12));
        Log.d("a", "5");
    }

    public void setUpMapIfNeeded(GoogleMap map)
    {
        if (this.map == null)
        {
            this.map = map; //((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            if (this.map != null)
            {
                setUpMap();
            }
        }
    }

    public Location getCurrLocation(GoogleMap googleMap) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();


        boolean gps_enabled = false, network_enabled = false;
        try
        {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch(Exception e)
        {
            return null;
        }
        if(!gps_enabled) // || !network_enabled)
        {
            return null;
        }

//        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Location loc = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
//        Location loc = googleMap.getMyLocation();
        if (loc == null)
        {

            loc = googleMap.getMyLocation();

            if(loc != null) {
                currentLocation = loc;
            }
            else
            {
                return null;
            }
        }
        else {
            currentLocation = loc;
        }
        return currentLocation;
    }

    // draw radar circle
    public Circle drawCircle(LatLng position, int radius)
    {
        double radiusInMeters = radius;
        int strokeColor = 0xfff1c40f;
        int shadeColor = 0x44f1c40f;

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(1);
        Circle mCircle = this.map.addCircle(circleOptions);

        return mCircle;
    }

    public Circle drawCircleForPlace(LatLng position, int radius)
    {
        double radiusInMeters = radius;
        int strokeColor = 0xfff1c40f;
        int shadeColor = 0x44f1c40f;

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(1);
        Circle mCircle = this.map.addCircle(circleOptions);

        return mCircle;
    }

    public void drawMarkers(GoogleMap googleMap, List<Place> places)
    {
        for(Place place : places)
        {
            drawMarker(googleMap, place, false, false);
        }
    }

    //draw marker for Place
    public Marker drawMarker(GoogleMap googleMap, Place place, boolean setCamera, boolean drawingForRoute)
    {
        Bitmap immutableMarker = place.types.size() > 0 ? getPlaceIcon(place.types.get(0).id, true, drawingForRoute) : getPlaceIcon(999, true, drawingForRoute);
        Bitmap marker = immutableMarker.copy(Bitmap.Config.ARGB_8888, true);


        Canvas canvas = new Canvas(marker);

        int eventCount = place.eventCount;

        if(eventCount > 0)
        {
            if(eventCount > 5) eventCount = 5;
            Bitmap eventCountBitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("event" + eventCount, "drawable", context.getPackageName()));

            canvas.drawBitmap(eventCountBitmap, 33, 72, new Paint());
        }

        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(place.latitude, place.longitude)).title(Integer.toString(place.id)).icon(BitmapDescriptorFactory.fromBitmap(marker));
        Marker mMarker = googleMap.addMarker(markerOptions);

        if (setCamera)
        {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.latitude, place.longitude), 13));
        }

        return mMarker;
    }

    public Marker drawMarkerDisable(GoogleMap googleMap, Place place, boolean setCamera, boolean drawingForRoute)
    {
        Bitmap immutableMarker = place.types.size() > 0 ? getPlaceIcon(place.types.get(0).id, false, drawingForRoute) : getPlaceIcon(999, false, drawingForRoute);
        Bitmap marker = immutableMarker.copy(Bitmap.Config.ARGB_8888, true);


        Canvas canvas = new Canvas(marker);

        int eventCount = place.eventCount;

        if(eventCount > 0)
        {
            if(eventCount > 5) eventCount = 5;
            Bitmap eventCountBitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("event" + eventCount, "drawable", context.getPackageName()));

            canvas.drawBitmap(eventCountBitmap, 33, 72, new Paint());
        }

        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(place.latitude, place.longitude)).title(Integer.toString(place.id)).icon(BitmapDescriptorFactory.fromBitmap(marker));
        Marker mMarker = googleMap.addMarker(markerOptions);

        if (setCamera)
        {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.latitude, place.longitude), 13));
        }

        return mMarker;
    }

    public void changeIcon(Marker marker, Place place, boolean isActive) {

        Bitmap immutableMarker = null;
        Bitmap finalMarker = null;

        int eventCount = place.eventCount;

        immutableMarker = finalMarker = place.types.size() > 0 ? getPlaceIcon(place.types.get(0).id, isActive, false) : getPlaceIcon(999, isActive, false);

        if(eventCount > 0 && isActive)
        {
            finalMarker = immutableMarker.copy(Bitmap.Config.ARGB_8888, true);

            Canvas canvas = new Canvas(finalMarker);

            if(eventCount > 5)
                eventCount = 5;

            Bitmap eventCountBitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("event" + eventCount, "drawable", context.getPackageName()));

            canvas.drawBitmap(eventCountBitmap, 33, 72, new Paint());
        }

        marker.setIcon(BitmapDescriptorFactory.fromBitmap(finalMarker));
    }

    private Bitmap getPlaceIcon(int typeId, boolean isActive, boolean drawingForRoute)
    {
        Bitmap result = null;
        if(drawingForRoute)
            result = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("place_type_route" + typeId, "drawable", context.getPackageName()));
        else
            result = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("place_type" + typeId + (isActive ? "" : "_dis"), "drawable", context.getPackageName()));

        if( result == null)
        {
            if(drawingForRoute)
                result = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("marker_route", "drawable", context.getPackageName()));
            else
                result = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("marker" + (isActive ? "" : "_dis"), "drawable", context.getPackageName()));
        }
        return result;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        setUpMap();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }

    @Override
    public void  onMyLocationChange(Location location) {
//        Log.d("MyLoc", location.getLatitude()+", "+ location.getLongitude());
        if(RadarManager.isRadarBlocked)
        {
            return;
        }

        if(circle == null || currentLocation == null || !circle.isVisible()) {
            currentLocation = location;

            if(sharedPref.getInt("showRoute", -1) == -1)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12));

            if(sharedPref.getBoolean("filterPlaces", false)) {
                changeLocation(location, getChosenTypes(), null);
            }
            else {
                changeLocation(location);
            }
        }
        float[] distance = new float[2];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                location.getLatitude(), location.getLongitude(), distance);
        if(distance[0] > 50) {
            if(sharedPref.getBoolean("filterPlaces", false)) {
                changeLocation(location, getChosenTypes(), null);
            }
            else {
                changeLocation(location);
            }
        }
    }

    public void changeLocation(Location location) {
        if(location != null && !RadarManager.isRadarBlocked) {
            currentLocation = location;
            if (circle == null) {
                radius = sharedPref.getInt("radar_radius", 3000);
                circle = drawCircle(new LatLng(location.getLatitude(), location.getLongitude()), radius);
            }
            if (!circle.isVisible()) {
                circle.setVisible(true);
            }
            circle.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));

            showNearbyPlaces();
        }
    }

    public void changeLocation(Location location, List<Type> choosenTypes, List<Category> choosenCategories)
    {
        if(location != null && !RadarManager.isRadarBlocked) {
            currentLocation = location;
            if (circle == null )
            {
                radius = sharedPref.getInt("radar_radius", 3000);
                circle = drawCircle(new LatLng(location.getLatitude(), location.getLongitude()), radius);
            }
            if (!circle.isVisible()) {
                circle.setVisible(true);
            }
            circle.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));

            showNearbyPlaces(choosenTypes, choosenCategories);
        }
    }

    public void showNearbyPlaces()
    {
        if (!RadarManager.isRadarBlocked)
        radarManager.showNearbyPlaces(circle, null);
    }

    public void showNearbyPlaces(LatLng center, int radius, boolean forPlace)
    {
        if (!RadarManager.isRadarBlocked)
            radarManager.showNearbyPlaces(center, radius, forPlace, null);
    }

    public void showNearbyPlaces(List<Type> choosenTypes, List<Category> choosenCategories)
    {
        if (!RadarManager.isRadarBlocked)
        radarManager.showNearbyPlaces(circle, choosenTypes, choosenCategories, null);
    }

    public void showRoute(int currentRouteId, boolean walking)
    {
        routeManager.showRoute(currentRouteId, walking);
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int placeId = Integer.valueOf(marker.getTitle());
        mainActivity.showPlaceInPopup(placeId, true);
        return true;
    }

    public void clearMap() {
        map.clear();
        currentLocation = null;
        circle = null;
        changeLocation(getCurrLocation(map));
    }

    public List<Type> getChosenTypes() {
        String chosenTypesIdsString = sharedPref.getString("chosenTypes", null);
        List<Type> chosenTypes = new ArrayList<>();
        if(chosenTypesIdsString != null) {
            String[] chosenTypesIdsStringArray = chosenTypesIdsString.split(",");
            for (int i = 0; i < chosenTypesIdsStringArray.length; i++) {
                if (MainActivity.isInteger(chosenTypesIdsStringArray[i])) {
                    for (Type type : Place.AllPlacesTypes) {
                        if (type.id == Integer.parseInt(chosenTypesIdsStringArray[i])) {
                            chosenTypes.add(type);
                        }
                    }
                }
            }
        }
        return chosenTypes;
    }
}
