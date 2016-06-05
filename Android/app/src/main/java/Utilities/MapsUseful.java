package Utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.guide.guide.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import DataAccess.LocalRepository;
import Models.Event;
import Models.Place;


public class MapsUseful extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    Location currLocation;
    Context appContext;

    LocalRepository localRepository;

    public MapsUseful(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        currLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        appContext = context;

        localRepository = new LocalRepository(appContext);
        localRepository.Init(appContext);
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public GoogleMap getGoogleMap(View view, Bundle savedInstanceState, int id) {
        return getGoogleMapWithTop(view, savedInstanceState, id, -1);
    }

    public GoogleMap getGoogleMapWithTop(View view, Bundle savedInstanceState, int id, int idTopMenu) {
        MapView mMapView = (MapView) view.findViewById(id);

        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(appContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(idTopMenu != -1) {
            final LinearLayout l = (LinearLayout) view.findViewById(idTopMenu);
            final View fView = view;
            final int fId = id;
            final int fIdTopMenu = idTopMenu;
            ViewTreeObserver observer = l.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    changePositionOfLocationButton(fView, fId, fIdTopMenu);
                    l.getViewTreeObserver().removeGlobalOnLayoutListener(
                            this);
                }
            });
        }

        return mMapView.getMap();
    }

    public void changePositionOfLocationButton(View view, int id, int idTopMenu) {
        MapView mMapView = (MapView) view.findViewById(id);
        View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1"))
                .getParent())
                .findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        int marginTop = 10;
        LinearLayout top = (LinearLayout) view.findViewById(idTopMenu);
        marginTop += top.getMeasuredHeight();
        rlp.setMargins(0, marginTop, 10, 0);
    }

    public Location getCurrLocation(Activity currActivity, GoogleMap googleMap) {
        LocationManager locationManager = (LocationManager) currActivity.getSystemService(Context.LOCATION_SERVICE);
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
        if(!gps_enabled || !network_enabled)
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
                currLocation = loc;
            }
            else
            {
                return null;
            }
        }
        else {
            currLocation = loc;
        }
        return currLocation;
    }

    public void updateCircle(Circle mCircle, LatLng position)
    {
        mCircle.setCenter(position);
    }

    public Circle drawCircle(GoogleMap googleMap, LatLng position, int radius)
    {
        double radiusInMeters = radius;
        int strokeColor = 0xfff1c40f;
        int shadeColor = 0x44f1c40f;

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(1);
        Circle mCircle = googleMap.addCircle(circleOptions);

        return mCircle;
    }

    public Circle drawCircle(GoogleMap googleMap, LatLng position, int radius, int ringColor, int fillColor)
    {
        double radiusInMeters = radius;
        int strokeColor = ringColor;
        int shadeColor = fillColor;

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(1);
        Circle mCircle = googleMap.addCircle(circleOptions);

        return mCircle;
    }

    public void updateMarker(Marker mMarker, LatLng position)
    {
        mMarker.setPosition(position);
    }

    public Marker drawMarker(GoogleMap googleMap, LatLng position, String title)
    {
        MarkerOptions markerOptions = new MarkerOptions().position(position).title(title).icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));
        Marker mMarker = googleMap.addMarker(markerOptions);

        return mMarker;
    }

    public Marker drawMarker(GoogleMap googleMap, LatLng position, String title, int icon)
    {
        MarkerOptions markerOptions = new MarkerOptions().position(position).title(title).icon(BitmapDescriptorFactory.fromResource(icon));
        Marker mMarker = googleMap.addMarker(markerOptions);

        return mMarker;
    }

    public Marker drawMarker(GoogleMap googleMap, LatLng position, String title, BitmapDescriptor icon)
    {
        MarkerOptions markerOptions = new MarkerOptions().position(position).title(title).icon(icon);
        Marker mMarker = googleMap.addMarker(markerOptions);

        return mMarker;
    }

    public Marker drawMarker(GoogleMap googleMap, Place place)
    {
        Log.d("PLACETYPES", "+++++++++++++" + place.GetTypes().get(0).id + place.GetTypes().get(0).name);
        Bitmap immutableMarker = BitmapFactory.decodeResource(appContext.getResources(), appContext.getResources().getIdentifier("place_type" + place.GetTypes().get(0).id, "mipmap", appContext.getPackageName()));
        //Bitmap marker = BitmapDescriptorFactory.fromResource(chooseIcon(place.GetTypes().get(0)));
        Bitmap marker = immutableMarker.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(marker);

//        int eventCount = place.getEventCount();
        int eventCount = place.eventsCount;
        if(eventCount > 0)
        {
            if(eventCount > 5) eventCount = 5;
            Bitmap eventCountBitmap = BitmapFactory.decodeResource(appContext.getResources(), appContext.getResources().getIdentifier("event" + eventCount, "mipmap", "com.guide.guide"));

            canvas.drawBitmap(eventCountBitmap, 0, 30, new Paint());
        }

        Log.d("DRAWMARKER", "+++++++++SASASGAGSAGSGD");
        MarkerOptions markerOptions = new MarkerOptions().position(place.GetLocation().GetPoint()).title(Integer.toString(place.GetID())).icon(BitmapDescriptorFactory.fromBitmap(marker));
        Marker mMarker = googleMap.addMarker(markerOptions);

        return mMarker;
    }

    public Marker drawMarkerCenter(GoogleMap googleMap, LatLng position, String title, int icon)
    {
        MarkerOptions markerOptions = new MarkerOptions().position(position).title(title).icon(BitmapDescriptorFactory.fromResource(icon)).anchor(0.5f, 0.5f);
        Marker mMarker = googleMap.addMarker(markerOptions);

        return mMarker;
    }

    public HashMap<Integer, Marker> makeMarkers(GoogleMap googleMap, List<Place> places, HashMap<Integer, Marker> markers) {
        if(markers == null) {
            markers = new HashMap<Integer, Marker>();
        }
        for(Place place:places) {
            if(!markers.containsKey(place.GetID())) {
//                int icon = chooseIcon(place.GetTypes().get(0).name);
                markers.put(place.GetID(), drawMarker(googleMap, place));
            }
        }
        return markers;
    }

    public HashMap<Integer, Marker> showMarkersInCirclePlace(GoogleMap googleMap, Place center, int radius, int radiusMargin, HashMap<Integer, Marker> markers) {
        HashMap<Integer, Marker> markersList = new HashMap<Integer, Marker>();
        HashMap<String,List<Place>> places = getPlacesInCircle(center.GetLocation().GetPoint(), radius, radiusMargin);
        if(places.size() > 0) {
            markers = makeMarkers(googleMap, places.get("in"), markers);
            markers = makeMarkers(googleMap, places.get("onMargin"), markers);
            markers = makeMarkers(googleMap, places.get("out"), markers);

            for (Place place : places.get("in")) {
                if(place.GetID() != center.GetID()) {
                    Marker marker = (Marker) markers.get(place.GetID());
                    marker.setVisible(true);
                    changeIcon(marker, chooseDisIcon(place.GetTypes().get(0).name));
                    markersList.put(place.GetID(), marker);
                }
                else {
                    Marker marker = (Marker) markers.get(place.GetID());
                    marker.setVisible(true);
                    changeIcon(marker, chooseIcon(place.GetTypes().get(0).name));
                    markersList.put(place.GetID(), marker);
                }
            }
            for (Place place : places.get("onMargin")) {
                Marker marker = (Marker) markers.get(place.GetID());
                marker.setVisible(true);
                changeIcon(marker, chooseDisIcon(place.GetTypes().get(0).name));
                markersList.put(place.GetID(), marker);
            }
            for (Place place : places.get("out")) {
                Marker marker = (Marker) markers.get(place.GetID());
                marker.setVisible(false);
            }
        }
        return markersList;
    }

    public HashMap<Integer, Marker> showMarkersInCirclePlace(GoogleMap googleMap, Place center, int radius, int radiusMargin, HashMap<Integer, Marker> markers, List<Place> placesList) {
        HashMap<Integer, Marker> markersList = new HashMap<Integer, Marker>();
        HashMap<String,List<Place>> places = getPlacesInCircle(center.GetLocation().GetPoint(), radius, radiusMargin, placesList);
        Log.d("PlacesList...", "..................." + places.size());
        if(places.size() > 0) {
            markers = makeMarkers(googleMap, places.get("in"), markers);
            markers = makeMarkers(googleMap, places.get("onMargin"), markers);
            markers = makeMarkers(googleMap, places.get("out"), markers);

            for (Place place : places.get("in")) {
                if(place.GetID() != center.GetID()) {
                    Marker marker = (Marker) markers.get(place.GetID());
                    marker.setVisible(true);
                    changeIcon(marker, chooseDisIcon(place.GetTypes().get(0).name));
                    markersList.put(place.GetID(), marker);
                }
                else {
                    Marker marker = (Marker) markers.get(place.GetID());
                    marker.setVisible(true);
                    changeIcon(marker, chooseIcon(place.GetTypes().get(0).name));
                    markersList.put(place.GetID(), marker);
                }
            }
            for (Place place : places.get("onMargin")) {
                Marker marker = (Marker) markers.get(place.GetID());
                marker.setVisible(true);
                changeIcon(marker, chooseDisIcon(place.GetTypes().get(0).name));
                markersList.put(place.GetID(), marker);
            }
            for (Place place : places.get("out")) {
                Marker marker = (Marker) markers.get(place.GetID());
                marker.setVisible(false);
            }
        }
        return markersList;
    }

    public HashMap<Integer, Marker> showMarkersInCircle(GoogleMap googleMap, LatLng center, int radius, int radiusMargin, HashMap<Integer, Marker> markers) {
        HashMap<Integer, Marker> markersList = new HashMap<Integer, Marker>();
        HashMap<String,List<Place>> places = getPlacesInCircle(center, radius, radiusMargin);
        if(places.size() > 0) {
            markers = makeMarkers(googleMap, places.get("in"), markers);
            markers = makeMarkers(googleMap, places.get("onMargin"), markers);
            markers = makeMarkers(googleMap, places.get("out"), markers);

            for (Place place : places.get("in")) {
                Marker marker = (Marker) markers.get(place.GetID());
                marker.setVisible(true);
                changeIcon(marker, chooseIcon(place.GetTypes().get(0).name));
                markersList.put(place.GetID(), marker);
            }
            for (Place place : places.get("onMargin")) {
                Marker marker = (Marker) markers.get(place.GetID());
                marker.setVisible(true);
                changeIcon(marker, chooseDisIcon(place.GetTypes().get(0).name));
                markersList.put(place.GetID(), marker);
            }
            for (Place place : places.get("out")) {
                Marker marker = (Marker) markers.get(place.GetID());
                marker.setVisible(false);
            }
        }
        return markersList;
    }

    public HashMap<Integer, Marker> showMarkersInCircle(GoogleMap googleMap, LatLng center, int radius, int radiusMargin, HashMap<Integer, Marker> markers, List<Place> placesList) {
        HashMap<Integer, Marker> markersList = new HashMap<Integer, Marker>();
        HashMap<String,List<Place>> places = getPlacesInCircle(center, radius, radiusMargin, placesList);
        Log.d("PlacesList...", "..................." + places.size());
        if(places.size() > 0) {
            markers = makeMarkers(googleMap, places.get("in"), markers);
            markers = makeMarkers(googleMap, places.get("onMargin"), markers);
            markers = makeMarkers(googleMap, places.get("out"), markers);

            for (Place place : places.get("in")) {
                Marker marker = (Marker) markers.get(place.GetID());
                marker.setVisible(true);
//                changeIcon(marker, chooseIcon(place.GetTypes().get(0).name));
                changeIcon(marker, place, true);
                markersList.put(place.GetID(), marker);
            }
            for (Place place : places.get("onMargin")) {
                Marker marker = (Marker) markers.get(place.GetID());
                marker.setVisible(true);
//                changeIcon(marker, chooseDisIcon(place.GetTypes().get(0).name));
                changeIcon(marker, place, false);
                markersList.put(place.GetID(), marker);
            }
            for (Place place : places.get("out")) {
                Marker marker = (Marker) markers.get(place.GetID());
                marker.setVisible(false);
            }
        }
        return markersList;
    }

    public HashMap<String, List<Place>> getPlacesInCircle(LatLng center, int radius, int radiusMargin, List<Place> placesList) {
        HashMap<String, List<Place>> places = new HashMap<String, List<Place>>();
        List<Place> in = new ArrayList<Place>();
        List<Place> onMargin = new ArrayList<Place>();
        List<Place> out = new ArrayList<Place>();
        if(placesList.size() > 0) {
            for(Place place : placesList) {
                float[] distance = new float[2];
                LatLng placeLoc = place.GetLocation().GetPoint();
                Location.distanceBetween(placeLoc.latitude, placeLoc.longitude,
                        center.latitude, center.longitude, distance);
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

    public HashMap<String, List<Place>> getPlacesInCircle(LatLng center, int radius, int radiusMargin) {
        HashMap<String, List<Place>> places = new HashMap<String, List<Place>>();
        List<Place> in = new ArrayList<Place>();
        List<Place> onMargin = new ArrayList<Place>();
        List<Place> out = new ArrayList<Place>();
        List<Place> placesList = localRepository.GetNearbyPlaces(center, radius+radiusMargin, null);
        if(placesList.size() > 0) {
            for(Place place : placesList) {
                float[] distance = new float[2];
                LatLng placeLoc = place.GetLocation().GetPoint();
                Location.distanceBetween(placeLoc.latitude, placeLoc.longitude,
                        center.latitude, center.longitude, distance);
                Log.d("Markers", placeLoc.latitude + " " + placeLoc.longitude + " - " + center.latitude + " " + center.longitude + " ))))))))))))))))))))))))))))))" + distance[0]);
                if (distance[0] > radius + radiusMargin) {
                    Log.d("Markers", "out ))))))))))))))))))))))))))))))" + distance[0]);
                    out.add(place);
                } else if (distance[0] > radius) {
                    Log.d("Markers", "margin ))))))))))))))))))))))))))))))");
                    onMargin.add(place);
                } else if (distance[0] <= radius) {
                    Log.d("Markers", "in ))))))))))))))))))))))))))))))");
                    in.add(place);
                }
            }
        }

        places.put("in", in);
        places.put("onMargin", onMargin);
        places.put("out", out);
        return places;
    }

    public HashMap<String, List<Event>> getEventsInCircle(LatLng center, int radius, int radiusMargin) {
        HashMap<String, List<Event>> events = new HashMap<String, List<Event>>();
        List<Event> in = new ArrayList<Event>();
        List<Event> onMargin = new ArrayList<Event>();
        List<Event> out = new ArrayList<Event>();
        List<Event> eventsList = localRepository.GetNearbyEvents(center, radius+radiusMargin, null);
        Log.d("EventsListSize", eventsList.size() + " ----99999999999999999999");
        if(eventsList.size() > 0) {
            for(Event event : eventsList) {
                float[] distance = new float[2];
                LatLng placeLoc = event.GetPlace().GetLocation().GetPoint();
                Location.distanceBetween(placeLoc.latitude, placeLoc.longitude,
                        center.latitude, center.longitude, distance);
                if (distance[0] > radius + radiusMargin) {
                    out.add(event);
                } else if (distance[0] > radius) {
                    onMargin.add(event);
                } else if (distance[0] <= radius) {
                    in.add(event);
                }
            }
        }

        events.put("in", in);
        events.put("onMargin", onMargin);
        events.put("out", out);

        return events;
    }

    public String getMapsApiDirectionsUrl(List<LatLng> positions, boolean optimize) {
        String waypoints = "waypoints=optimize:"+optimize;

        for(int i=0; i< positions.size(); i++) {
            waypoints += "|" + positions.get(i).latitude + "," + positions.get(i).longitude;
        }

        String sensor = "sensor=false";
        String origin_dest = "origin=" + positions.get(0).latitude + "," + positions.get(0).longitude+
                "&destination="+ positions.get(positions.size()-1).latitude + ","
                + positions.get(positions.size()-1).longitude;
        String params = origin_dest + "&" + waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        Log.d("URL", url);
        return url;
    }

    public void changeIcon(Marker marker, int icon) {
        marker.setIcon(BitmapDescriptorFactory.fromResource(icon));
    }

    public void changeIcon(Marker marker, Place place, boolean isActive) {

        Bitmap immutableMarker = null;
        Bitmap finalMarker = null;

        int eventCount = place.getEventCount();

        if(eventCount > 0)
            immutableMarker = BitmapFactory.decodeResource(appContext.getResources(), appContext.getResources().getIdentifier("place_type" + place.GetTypes().get(0).id, "mipmap", appContext.getPackageName()));

        if(isActive)
        {
            finalMarker = immutableMarker;
        }
        else
        {
            finalMarker = BitmapFactory.decodeResource(appContext.getResources(), appContext.getResources().getIdentifier("place_type" + place.GetTypes().get(0).id + "_dis", "mipmap", appContext.getPackageName()));
        }


        if(eventCount > 0 && isActive)
        {
            finalMarker = immutableMarker.copy(Bitmap.Config.ARGB_8888, true);

            Canvas canvas = new Canvas(finalMarker);

            if(eventCount > 5)
                eventCount = 5;

            Bitmap eventCountBitmap = BitmapFactory.decodeResource(appContext.getResources(), appContext.getResources().getIdentifier("event" + eventCount, "mipmap", "com.guide.guide"));

            canvas.drawBitmap(eventCountBitmap, 40, 70, new Paint());
        }

        marker.setIcon(BitmapDescriptorFactory.fromBitmap(finalMarker));
    }

    public int chooseIcon(String type) {
        Log.d("TYP+++++", "-------------------------------" + type);
        switch(type) {
            case "Restauracja":
                return R.mipmap.place_type1;
            case "Muzeum":
                return R.mipmap.place_type2;
            case "Natura":
                return R.mipmap.place_type3;
            case "Centrum handlowe":
                return R.mipmap.place_type4;
//            case "Teatr":
//                return R.mipmap.place_type5;
            case "Kino":
                return R.mipmap.place_type6;
            case "Obiekt sportowy":
                return R.mipmap.place_type8;
            case "Kultura":
                return R.mipmap.place_type10;
            case "Obiekt sakralny":
                return R.mipmap.place_type11;
            default:
                return R.mipmap.marker;
        }
    }

    public int chooseEventIcon(int count) {

        switch(count) {
            case 0:
                return -1;
            case 1:
                return R.mipmap.event1;
            case 2:
                return R.mipmap.event2;
            case 3:
                return R.mipmap.event3;
            case 4:
                return R.mipmap.event4;
            default:
                return R.mipmap.event5;
        }
    }

    //choose disable (gray) icon
    public int chooseDisIcon(String type) {
        Log.d("TYP+++++", "-------------------------------" + type);
        switch(type) {
            case "Restauracja":
                return R.mipmap.place_type1_dis;
            case "Muzeum":
                return R.mipmap.place_type2_dis;
            case "Natura":
                return R.mipmap.place_type3_dis;
            case "Centrum handlowe":
                return R.mipmap.place_type4_dis;
//            case "Teatr":
//                return R.mipmap.place_type5_dis;
            case "Kino":
                return R.mipmap.place_type6_dis;
            case "Obiekt sportowy":
                return R.mipmap.place_type8_dis;
            case "Kultura":
                return R.mipmap.place_type10_dis;
            case "Obiekt sakralny":
                return R.mipmap.place_type11_dis;
            default:
                return R.mipmap.marker_dis;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class GPSException extends Exception {
        public GPSException(String message) {
            super(message);
        }
    }

    public class NetworkException extends Exception {
        public NetworkException(String message) {
            super(message);
        }
    }
}
