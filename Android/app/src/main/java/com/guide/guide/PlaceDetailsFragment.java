package com.guide.guide;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import DataAccess.LocalRepository;
import Models.Location;
import Models.Place;
import Models.Route;
import Models.Type;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.guide.guide.PlaceDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.guide.guide.PlaceDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaceDetailsFragment extends Fragment implements AbsListView.OnItemClickListener
{
    private OnFragmentInteractionListener mListener;
    View v;

    LocalRepository localRepository;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String currOption;
    private int id;
    private Place place;
    private List<Route> routes;

    private AbsListView mListView;
    private ListAdapter mAdapter;


    // TODO: Rename and change types and number of parameters
    public static PlaceDetailsFragment newInstance() {
        PlaceDetailsFragment fragment = new PlaceDetailsFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    public PlaceDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_details_place, container, false);

        ((BaseActivityFragments) getActivity()).onRunFragment();

        ((BaseActivityFragments) getActivity()).hideBottomMenu();
        ((BaseActivityFragments) getActivity()).setTopTitle(getResources().getString(R.string.title_fragment_details_place_pl));

        localRepository = new LocalRepository(getActivity().getApplicationContext());

        sharedPref = getActivity().getSharedPreferences(getResources().getString(R.string.main_store), 0);
        editor = sharedPref.edit();

//        id = sharedPref.getInt(getResources().getString(R.string.place_id_detail), -1);
        Bundle bundle = getArguments();
        if(bundle != null && place == null ){
//        if(id != -1) {
//        if(id != -1 && localRepository.GetPlaceDetails(id) != null) {
//            place = localRepository.GetPlaceDetails(id);

//            List<Type> types = new ArrayList<Type>();
//            types.add(new Type(1,"Centrum handlowe",""));
//            place = new Place(5, "Galeria", "Bardzo fajne galeria, polecam...\n\n\nno", "", 1, types, new Date(), new Models.Location(51.11950f, 17.03032f, "Wrocław", "Plac Dominikański 1"), "Pon-Sob 06:00-21:00\nNiedziela 06:00-20:00");

            place = (Place) bundle.getSerializable("Place");

            fillRoutes();

            TextView title = (TextView) getView().findViewById(R.id.title);
            title.setText(place.GetName());

            TextView address = (TextView) getView().findViewById(R.id.address);
            address.setText(place.GetLocation().GetCity() + "\n" + place.GetLocation().GetStreet());

            TextView openTime = (TextView) getView().findViewById(R.id.openTime);
            openTime.setText(place.GetOpenTime());

            TextView desc = (TextView) getView().findViewById(R.id.description);
            desc.setText(place.GetDescription());

            TableLayout tagsBox = (TableLayout) getView().findViewById(R.id.tagsBox);
            for(final Type type : place.GetTypes()) {
                Button tag = makeTag(type.name);
                tagsBox.addView(tag);
            }

            // Set the adapter
            mAdapter = new ArrayAdapter<Route>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, routes);
            mListView = (AbsListView) getView().findViewById(R.id.routesList);
            ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

            // Set OnItemClickListener so we can be notified on item clicks
            mListView.setOnItemClickListener(this);

            ImageButton locButton = ((BaseActivityFragments) getActivity()).getBtn1TopMenu();
            locButton.setVisibility(View.VISIBLE);
            locButton.setImageResource(R.mipmap.location);
            locButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    editor.putString(getResources().getString(R.string.prev_fragment), "PlaceDetailsFragment");
                    editor.commit();

                    PlaceRadarFragment frag = new PlaceRadarFragment();
                    Bundle bundles = new Bundle();
                    bundles.putSerializable("Place", place);
                    frag.setArguments(bundles);
                    ((BaseActivityFragments) getActivity()).changeFragment(frag);
                }
            });
        }


        //Przekazanie do aktywności, że fragemt się załadował
        ((BaseActivityFragments)getActivity()).onLoadedFragment();

        return v;
    }

    public Button makeTag(final String text) {
        Button tag = new Button(getActivity());
        tag.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tag.setText(text);
        tag.setTextColor(Color.parseColor("#202020"));
        tag.setBackgroundColor(Color.TRANSPARENT);
        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment frag = new SearchFragment();
                Bundle bundles = new Bundle();
                bundles.putSerializable("Search", text);
                frag.setArguments(bundles);
                ((BaseActivityFragments) getActivity()).changeFragment(frag);
            }
        });

        return tag;
    }

    private void fillRoutes() {
        Location Wroclaw = new Location(51.108f, 17.0303f,"Wrocław", "ulica1");
        Location Park = new Location(51.11940f, 17.04120f,"Wrocław", "ulica1");
        Location Hala = new Location(51.10694f, 17.07694f,"Wrocław", "ulica1");
        Location SkyTower = new Location(51.09398f, 17.01992f,"Wrocław", "ulica1");
        Location Galeria = new Location(51.11950f, 17.03032f,"Wrocław", "ulica1");
        Date data = new Date();

        List<Type> types1 = new ArrayList<Type>(Arrays.asList(new Type(1,"Restauracja","")));
        Place wroclawP = new Place(1,"Wrocław","somedescription","somelink",1, types1,data,Wroclaw);
        List<Type> types2 = new ArrayList<Type>(Arrays.asList(new Type(1,"Natura","")));
        Place parkP = new Place(2,"Park","somedescription","somelink",1, types2,data,Park);
        List<Type> types3 = new ArrayList<Type>(Arrays.asList(new Type(1,"Inne","")));
        Place halaP = new Place(3,"Hala stulecia","somedescription","somelink",1, types3,data,Hala);
        List<Type> types4 = new ArrayList<Type>(Arrays.asList(new Type(1,"Centrum handlowe","")));
        Place skytowerP = new Place(4,"Sky Tower","somedescription","somelink",1, types4,data,SkyTower);
        Place galeriaP = place;

        ArrayList<Place> sample_places1 = new ArrayList<Place>(Arrays.asList(wroclawP, halaP, skytowerP, galeriaP));
        ArrayList<Place> sample_places2 = new ArrayList<Place>(Arrays.asList(wroclawP, galeriaP, parkP));

        Route sample_route1 = new Route(1, "Przykladowa 1", "sample description", sample_places1,1);
        Route sample_route2 = new Route(2, "Przykladowa 2", "sample description", sample_places2,1);
        routes = new ArrayList<Route>(Arrays.asList(sample_route1, sample_route2));
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {

            android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            RouteFragment frag = new RouteFragment();

            Bundle bundles = new Bundle();
            Route chosenRoute = routes.get(position);
            bundles.putSerializable("Route", chosenRoute);
            frag.setArguments(bundles);
            ((BaseActivityFragments) getActivity()).changeFragment(frag);


        }
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public View getView() {
        return v;
    }
}
