package com.guide.guide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import Models.Place;
import Models.Route;
import Models.Type;
import Utilities.MapsUseful;
import Utilities.ReadTask;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.guide.guide.RouteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.guide.guide.RouteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RouteFragment extends Fragment implements GoogleMap.OnMyLocationChangeListener,
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
    private GoogleMap googleMap; // Might be null if Google Play services APK is not available.
    private Marker mMarker;
    private ArrayList<Integer> categories;

    private OnFragmentInteractionListener mListener;

    MapsUseful mapsUseful;
    ReadTask downloadTask;

    Route currentRoute;

    private static final LatLng WROCLAW = new LatLng(51.10816, 17.03035);
    private static final LatLng PARK = new LatLng(51.11940, 17.04120);
    private static final LatLng HALA = new LatLng(51.10694, 17.07694);
    private static final LatLng SKYTOWER = new LatLng(51.09398, 17.01992);
    private static final LatLng GALERIA = new LatLng(51.11950, 17.03032);

    private static final LatLng MOJAPOZYCJA = new LatLng(51.110111, 17.031972);


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RouteFragment newInstance(String param1, String param2) {
        RouteFragment fragment = new RouteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RouteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View v = inflater.inflate(R.layout.fragment_route, container, false);
        View v = inflater.inflate(R.layout.fragment_events, container, false);

        ((BaseActivityFragments) getActivity()).onRunFragment();

        mapsUseful = new MapsUseful(getActivity().getApplicationContext());

        sharedPref = getActivity().getSharedPreferences(getResources().getString(R.string.main_store), 0);
        editor = sharedPref.edit();
        editor.putString(getResources().getString(R.string.curr_option), "route");
        editor.putInt(getResources().getString(R.string.in_main_fragment), 1);
        editor.commit();

        ((BaseActivityFragments) getActivity()).setTopTitle(getResources().getString(R.string.title_fragment_route_pl));

        mMapView = (MapView) getActivity().findViewById(R.id.mapView);

        googleMap = ((BaseActivityFragments) getActivity()).getGoogleMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationChangeListener(this);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);
        setUpMap();

        displayRoute();

        ImageButton listButton = ((BaseActivityFragments) getActivity()).getBtn1TopMenu();
        listButton.setVisibility(View.VISIBLE);
        listButton.setImageResource(R.mipmap.list);
        listButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                RouteListFragment listRoutes = new RouteListFragment();
                ((BaseActivityFragments) getActivity()).changeFragment(listRoutes);
            }
        });

        ImageButton listButton2 = ((BaseActivityFragments) getActivity()).getBtn2TopMenu();
        listButton2.setVisibility(View.VISIBLE);
        listButton2.setImageResource(R.mipmap.route_list);
        listButton2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                RouteDetailsFragment frag = new RouteDetailsFragment();

                Bundle bundles = new Bundle();
                bundles.putSerializable("Route", currentRoute);
                frag.setArguments(bundles);
                ((BaseActivityFragments) getActivity()).changeFragment(frag);

            }
        });


        //Przekazanie do aktywności, że fragment się załadował
        ((BaseActivityFragments)getActivity()).onLoadedFragment();

        return v;
    }

    protected void displayRoute(){

        Bundle bundle = getArguments();

        boolean optimize = true;
        List<LatLng> positions = new ArrayList<LatLng>();

        if(bundle == null && currentRoute == null ){
            List<Type> types = new ArrayList<Type>() {{ add(new Type(1, "Kultura", "asd")); }};
            Date data = new Date();
            Models.Location mojapozycja = new Models.Location(51.110111f, 17.031972f,"MOJAPOZYCJA", "ulica1");
            Place mojapozycjaP = new Place(1,"Wrocław","somedescription","somelink",1, types,data,mojapozycja);
            ArrayList<Place> sample_places1 = new ArrayList<Place>(Arrays.asList(mojapozycjaP));
            currentRoute = new Route(1, "Pozycja moja", "sample description", sample_places1,1);

        }else if(bundle != null ) {
            currentRoute= (Route) bundle.getSerializable("Route");
        }else{
        }

        showRoute(currentRoute, true);
    }

    private void showRoute(Route currentRoute, boolean optimize) {
        List<LatLng> positions = new ArrayList<LatLng>();
        //mapsUseful.drawMarker(googleMap, currentRoute.GetPlace(0).GetLocation().GetPoint(), "");
        mapsUseful.makeMarkers(googleMap, currentRoute.GetAllPlaces(), null);
        positions = addPositions(currentRoute);
//        drawMarkers(currentRoute);
        String url = mapsUseful.getMapsApiDirectionsUrl(positions, optimize);
        downloadTask = new ReadTask(googleMap);
        downloadTask.execute(url);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentRoute.GetPlace(0).GetLocation().GetPoint(), 13));
    }

    public List<LatLng> addPositions(Route route){
        List<LatLng> positionsToReturn = new ArrayList<LatLng>();
        for(int i=0;i<route.GetAllPlaces().size();i++){
            positionsToReturn.add(route.GetPlace(i).GetLocation().GetPoint());
        }
        return positionsToReturn;
    }

//    public void drawMarkers(Route route){
//        for(int i=0;i<route.GetAllPlaces().size();i++){
//            mapsUseful.drawMarker(googleMap, route.GetPlace(i).GetLocation().GetPoint(), currentRoute.GetPlace(i).GetLocation().toString());
//        }
//    }

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
        downloadTask.stopRoute();
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
        Location loc = mapsUseful.getCurrLocation(getActivity(), googleMap);
        if (loc == null)
        {
            Toast error = Toast.makeText(getActivity().getApplicationContext(),
                    "Włącz dostęp do Twojej lokalizacji", Toast.LENGTH_LONG);
            error.show();
        }
        else
        {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 13));
        }
    }

    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = ((BaseActivityFragments) getActivity()).getGoogleApiClient();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for(Place place : currentRoute.GetAllPlaces()) {
            if(place.GetID() == Integer.parseInt(marker.getTitle())) {
                ((BaseActivityFragments) getActivity()).showPlaceInPopup(place);
            }
        }

        //pokazanie w okienku popup miejsca naciśniętego markera
//        ((BaseActivityFragments) getActivity()).showPlaceInPopup(Integer.parseInt(marker.getTitle()));
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        ((BaseActivityFragments) getActivity()).hidePopup();
    }


}