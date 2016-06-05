package com.guide.guide;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import DataAccess.LocalRepository;
import Models.Place;
import Models.Type;
import Utilities.MapsUseful;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RadarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RadarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RadarFragment extends Fragment implements GoogleMap.OnMyLocationChangeListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private GoogleApiClient mGoogleApiClient;
    MapView mMapView;
    private GoogleMap googleMap;
    private Location myLocation;
    private Circle mCircle;
    private Marker mMarker;
    private int radius;
    private int radiusMargin;
    private ArrayList<String> types;
    private HashMap<Integer, Marker> markers;
    List<Place> places;

    private OnFragmentInteractionListener mListener;

    private MapsUseful mapsUseful;
    private LocalRepository localRepository;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RadarFragment newInstance(String param1, String param2) {
        RadarFragment fragment = new RadarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RadarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_radar, container, false);

        ((BaseActivityFragments) getActivity()).onRunFragment();

        mapsUseful = ((BaseActivityFragments) getActivity()).getMapsUseful();

        localRepository = new LocalRepository(getActivity().getApplicationContext());

        sharedPref = getActivity().getSharedPreferences(getResources().getString(R.string.main_store), 0);
        editor = sharedPref.edit();
        editor.putString(getResources().getString(R.string.curr_option), "radar");
        editor.putInt(getResources().getString(R.string.in_main_fragment), 1);
        editor.commit();
        radius = sharedPref.getInt(getResources().getString(R.string.radar_radius), 500);
        radiusMargin = (int)(radius*0.2);

        places = new ArrayList<Place>();
        types = new ArrayList<String>();
        Set<String> tempTypes = sharedPref.getStringSet(getResources().getString(R.string.types), null);
        if(tempTypes != null) {
            for (String type : tempTypes) {
                types.add(type);
            }
        }

        markers = new HashMap<Integer, Marker>();

        ((BaseActivityFragments) getActivity()).setTopTitle(getResources().getString(R.string.title_fragment_radar_pl));

//        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView = (MapView) getActivity().findViewById(R.id.mapView);

        googleMap = ((BaseActivityFragments) getActivity()).getGoogleMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationChangeListener(this);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);
        setUpMap();

        ImageButton filterButton = ((BaseActivityFragments) getActivity()).getBtn1TopMenu();
        filterButton.setVisibility(View.VISIBLE);
        filterButton.setImageResource(R.mipmap.filter);
        filterButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                FilterFragment filter = new FilterFragment();
                ((BaseActivityFragments) getActivity()).changeFragment(filter);
            }
        });

        //Przejście do listy wydarzeń (pozostałość po EventFragment)
//        ImageButton listButton = ((BaseActivityFragments) getActivity()).getBtn2TopMenu();
//        listButton.setVisibility(View.VISIBLE);
//        listButton.setImageResource(R.mipmap.list);
//        listButton.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                EventsListFragment listEvents = new EventsListFragment();
//                ((BaseActivityFragments) getActivity()).changeFragment(listEvents);
//            }
//        });

        //Przekazanie do aktywności, że fragment się załadował
        ((BaseActivityFragments)getActivity()).onLoadedFragment();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
//        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
//        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
//        mMapView.onLowMemory();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMyLocationChange(Location location) {
        Log.d("====CurrLocation====", location.getLatitude() + ", " + location.getLongitude() + "+++++++++++++++++++++++++++++++++++");
        if(myLocation == null) {
            myLocation = location;
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
            changeLocation(location);
        }
        float[] distance = new float[2];
        Location.distanceBetween( myLocation.getLatitude(), myLocation.getLongitude(),
                location.getLatitude(), location.getLongitude(), distance);
        if(distance[0] > 100) {
            changeLocation(location);
        }

    }

    private void changeLocation(Location location) {
        myLocation = location;
//        if(mMarker == null) {
//            mMarker = mapsUseful.drawMarkerCenter(googleMap, new LatLng(location.getLatitude(), location.getLongitude()), "", R.mipmap.dot);
//        }
        if (mCircle == null) {
            mCircle = mapsUseful.drawCircle(googleMap, new LatLng(location.getLatitude(), location.getLongitude()), radius);
        }
//        mapsUseful.updateMarker(mMarker, new LatLng(location.getLatitude(), location.getLongitude()));
        mapsUseful.updateCircle(mCircle, new LatLng(location.getLatitude(), location.getLongitude()));
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

        getNearbyPlaces();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for(Place place : places) {
            if(place.GetID() == Integer.parseInt(marker.getTitle())) {
                ((BaseActivityFragments) getActivity()).showPlaceInPopup(place);
            }
        }

        //pokazanie w okienku popup miejsca naciśniętego markera
//        ((BaseActivityFragments) getActivity()).showPlaceInPopup(Integer.parseInt(marker.getTitle()));
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        if(getActivity() != null)
            ((BaseActivityFragments) getActivity()).hidePopup();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument name and name
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #googleMap} is not null.
     */
    private void setUpMap()
    {
        //pobranie obecnej pozycji
        Location loc = mapsUseful.getCurrLocation(getActivity(), googleMap);
        if (loc == null)
        {
            Toast error = Toast.makeText(getActivity().getApplicationContext(),
                    "Włącz dostęp do twojej lokalizacji", Toast.LENGTH_LONG);
            error.show();
        }
        else
        {
//            mMarker = mapsUseful.drawMarkerCenter(googleMap, new LatLng(loc.getLatitude(), loc.getLongitude()), "", R.mipmap.dot);
            mCircle = mapsUseful.drawCircle(googleMap, new LatLng(loc.getLatitude(), loc.getLongitude()), radius);

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 13));


            getNearbyPlaces();
        }
    }

    //pokazanie miejsc w promieniu radaru
    private void getNearbyPlaces() {
        if(mCircle != null) {

            List<Place> places = this.places;
            List<Type> types = new ArrayList<Type>();
            types.add(new Type(1, "Kultura", "asd"));
            places.add(new Place(1, "Teatr", "", "", 1, types, new Date(), new Models.Location(17.0700558f, 51.1092305f, "Wrocław", "Jaks")));
            types = new ArrayList<Type>();
            types.add(new Type(2, "Kino", "asd"));
            places.add(new Place(2, "Kino", "", "", 1, types, new Date(), new Models.Location(17.0500558f, 51.1072305f, "Wrocław", "Jaks")));
            types = new ArrayList<Type>();
            types.add(new Type(3, "Obiekt sportowy", "asd"));
            places.add(new Place(3, "Stadion", "", "", 1, types, new Date(), new Models.Location(17.0500558f, 51.1102305f, "Wrocław", "Jaks")));
            types = new ArrayList<Type>();
            types.add(new Type(4, "Centrum handlowe", "asd"));
            places.add(new Place(5, "Galeria", "Bardzo fajne galeria, polecam...\n\n\nno", "", 1, types, new Date(), new Models.Location(17.03032f, 51.11950f, "Wrocław", "Jakas")));

            places = filerPlacesByTypes(places);
            for(Place place : places) {
                localRepository.insertPlace(place);
            }

            //pobierz i pokaż na mapie miejsca w promieniu radaru z bazy sqlite
            mapsUseful.showMarkersInCircle(googleMap, mCircle.getCenter(), (int)mCircle.getRadius(), radiusMargin, markers);


            //pokaż na mapie miejsca w promieniu radaru z miejsc zawartych w liście places
//            mapsUseful.showMarkersInCircle(googleMap, mCircle.getCenter(), (int) mCircle.getRadius(), radiusMargin, markers, places);

        }
    }

    //Przefiltrowanie miejsc ze względu na zaznaczone typy w filtrze
    public List<Place> filerPlacesByTypes(List<Place> places) {

        List<Place> filtered = new ArrayList<Place>();
        //jeśli zaznaczono w filtrze "WSZYTKIE" to zwróć wszystkie miejsca z listy places
        if(types.contains("WSZYSTKIE")) {
            filtered = places;
        }
        else {
            for (Place place : places) {
                for (Type type : place.GetTypes()) {
                    Log.d("Filtered", types + "------------------" + type + "=====" + types.contains(type));
                    if (types.contains(type) && !filtered.contains(place)) {
                        filtered.add(place);
                    }
                }
            }
        }
        return filtered;
    }

}
