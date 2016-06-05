package com.guide.guide;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DataAccess.LocalRepository;
import Models.Event;
import Models.Location;
import Models.Place;
import Models.Type;
import Utilities.MapsUseful;

//import android.app.Fragment;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class EventsListFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	// TODO: Rename and change types of parameters
	private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    /**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView mListView;
	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
     */
    private ListAdapter mAdapter;

    private GoogleApiClient mGoogleApiClient;
    MapsUseful mapsUseful;

    LocalRepository localRepository;

    private List<Event> events;
    private int radius;
    private int radiusMargin;
    private List<Integer> categories;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public EventsListFragment()
	{
	}

	// TODO: Rename and change types of parameters
	public static EventsListFragment newInstance(String param1, String param2)
	{
		EventsListFragment fragment = new EventsListFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//
//        // TODO: Change Adapter to display your content
//        mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
//                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);

        events = new ArrayList<Event>();
		Place place1 = new Place(1, "Miejsce 1", "Opis miejsca 1", "www.link.pierwszy.com", 1, new ArrayList<Type>() {{ add(new Type(1, "Kultura", "asd")); add(new Type(2, "Sztuka", "asd")); }}, null, new Location(Float.parseFloat("51.11568"), Float.parseFloat("17.02952"), "Wrocław", "Jedności Narodowej 66/18"));
		//Event event1 = new Event(1, "Wydarzenie 1", "Opis wydarzenia 1", "www.wydarzenie.jeden.com", 1, Arrays.asList("Kat1", "Kat2", "Kat3"), null, null, null, null, null, place1);
		//events.add(event1);

        mAdapter = new ArrayAdapter<Event>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, events);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);

        ((BaseActivityFragments) getActivity()).onRunFragment();

        sharedPref = getActivity().getSharedPreferences(getResources().getString(R.string.main_store), 0);
        editor = sharedPref.edit();
        editor.putString(getResources().getString(R.string.curr_option), "events");
        editor.putInt(getResources().getString(R.string.in_main_fragment), 1);
        editor.commit();
        radius = sharedPref.getInt(getResources().getString(R.string.events_radius), 500);
        radiusMargin = (int)(radius*0.2);

        ((BaseActivityFragments) getActivity()).setTopTitle(getResources().getString(R.string.title_fragment_events_pl));

        mapsUseful = new MapsUseful(getActivity().getApplicationContext());

        localRepository = new LocalRepository(getActivity().getApplicationContext());
        List<Type> types = new ArrayList<Type>();
        types.add(new Type(1, "Kultura", "asd"));
        types.add(new Type(2, "Opera", "asd"));
        Place place = new Place(1, "Teatr", "", "", 1, types, new Date(), new Models.Location(51.1092305f, 17.0700558f, "Wrocław", "Jaks"));
        localRepository.insertEvent(new Event(1, "NoweWydarzenie", "", "url", 1, types, new Date(), new Date(), new Date(), new Time(18), new Time(22), place));

        getNearbyEvents();

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);


        ImageButton filterButton = ((BaseActivityFragments) getActivity()).getBtn1TopMenu();
        filterButton.setVisibility(View.VISIBLE);
        filterButton.setImageResource(R.mipmap.filter);
        filterButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), EventsFilterActivity.class);
//                startActivity(intent);
                FilterFragment filter = new FilterFragment();
                ((BaseActivityFragments) getActivity()).changeFragment(filter);
//                EventsFragment filterEvents = new EventsFragment();
//                ((BaseActivityFragments) getActivity()).changeFragment(filterEvents);
            }
        });

        //Przycisk przejścia do mapy z wydarzeniami (pozostałość po EventFragment)
//        ImageButton mapButton = ((BaseActivityFragments) getActivity()).getBtn2TopMenu();
//        mapButton.setVisibility(View.VISIBLE);
//        mapButton.setImageResource(R.mipmap.radar);
//        mapButton.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                RadarFragment mapEvents = new RadarFragment();
//                ((BaseActivityFragments) getActivity()).changeFragment(mapEvents);
//            }
//        });

        //Przekazanie do aktywności, że fragemt się załadował
        ((BaseActivityFragments)getActivity()).onLoadedFragment();

        return view;
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
            EventDetailsFragment frag = new EventDetailsFragment();
            Bundle bundles = new Bundle();
            Event event = events.get(position);
            bundles.putSerializable("Event", event);
            frag.setArguments(bundles);
            ((BaseActivityFragments) getActivity()).changeFragment(frag);
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
//            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
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
        // TODO: Update argument name and name
        public void onFragmentInteraction(String id);
    }

    private void getNearbyEvents() {
/*
        GoogleMap googleMap = ((BaseActivityFragments) getActivity()).getGoogleMap();
        android.location.Location center = mapsUseful.getCurrLocation(getActivity(), googleMap);
        HashMap<String, List<Event>> events3lvl = mapsUseful.getEventsInCircle(new LatLng(center.getLatitude(), center.getLongitude()), radius, radiusMargin);
        events.addAll(events3lvl.get("in"));
        events.addAll(events3lvl.get("onMargin"));

        for(Event event : events) {
            Log.d("EventsList", event + " 0000000000000000000000000000");
        }

        events.add(localRepository.GetEventDetails(1));

        mAdapter = new ArrayAdapter<Event>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, events);*/
    }

}
