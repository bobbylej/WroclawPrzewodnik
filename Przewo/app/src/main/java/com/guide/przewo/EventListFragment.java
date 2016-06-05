package com.guide.przewo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.guide.przewo.DataAccess.LocalRepository;
import com.guide.przewo.Models.Event;
import com.guide.przewo.Models.ObjectType;
import com.guide.przewo.Models.Place;
import com.guide.przewo.Models.SearchResult;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


//import android.app.Fragment;

/**
 * Created by MateuszAntczak on 2015-05-05.
 * Fragment obsługujący listę wydarzeń
 */


public class EventListFragment extends Fragment implements AbsListView.OnItemClickListener
{

    private OnFragmentInteractionListener mListener;
    private AbsListView mListView;
    private ListAdapter mAdapter;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    LocalRepository localRepository;
    Location currentLocation;
    private List<SearchResult> events = new ArrayList<SearchResult>();
    private int idPlace = -1;

    public EventListFragment()
    {

    }

    public EventListFragment(int idPlace)
    {
        this.idPlace = idPlace;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        sharedPref = getActivity().getSharedPreferences(getResources().getString(R.string.main_store), 0);
        editor = sharedPref.edit();

        localRepository = new LocalRepository(getActivity().getApplicationContext());

        events = new ArrayList<SearchResult>();

        if (idPlace == -1)
        {
            currentLocation = Place.getCurrentLocation();
            List<Place> places = (List<Place>) localRepository.getNearbyPlaces(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 10000, null);

            for (Place place : places)
            {
                List<Event> placesEvents = localRepository.getEventsInPlace(place.id);

                if (placesEvents != null)
                {
                    for (Event event : placesEvents)
                    {
                        event.place = place;
                        DecimalFormat four = new DecimalFormat("#0.00");
                        String distance = four.format(place.getDistance());
                        events.add( new SearchResult(event, ObjectType.EVENT, event.getDateInHumanFormat(), place.name + " (" + distance + " km)"));
                    }
                }
            }
        }
        else
        {
            Place place = localRepository.getPlaceDetails(idPlace);

            List<Event> eventsInPlace = localRepository.getEventsInPlace(place.id);
            DecimalFormat four = new DecimalFormat("#0.00");
            for(Event event : eventsInPlace)
            {
                events.add(new SearchResult(event, ObjectType.EVENT, event.getDateInHumanFormat() + " do " + event.getEndDateInHumanFormat()));
            }
        }


        Collections.sort(events, new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                DateTime dateTime1 = ((Event)((SearchResult) o1).object).startTime;
                DateTime dateTime2 = ((Event)((SearchResult) o2).object).startTime;

                return dateTime1.isBefore(dateTime2) ? 1 : -1;
            }
        });

        if (events.isEmpty())
        {
        events.add(new SearchResult("Brak wydarzeń", ObjectType.TAG, R.drawable.events));
        }

        mAdapter = new SearchResultAdapter(getActivity(), events);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routes_list, container, false);

        sharedPref = getActivity().getSharedPreferences(getResources().getString(R.string.main_store), 0);
        editor = sharedPref.edit();

        getActivity().setTitle(getResources().getString(R.string.title_fragment_event_list_pl));

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);

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
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener && idPlace == -1)
        {
            Event chosenEvent = (Event) events.get(position).object;
            editor.putInt(getResources().getString(R.string.current_event), chosenEvent.id);
            editor.commit();
            chosenEvent.place = localRepository.getPlaceDetails(chosenEvent.idPlace);
            //DrawerFragment drfrag = new DrawerFragment();
            FragmentManager fragmentManager = getFragmentManager();

            fragmentManager.popBackStack();
            ((MainActivity) getActivity()).showEvent(chosenEvent.idPlace);
        }
    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument name and name
        void onFragmentInteraction(String id);
    }
}
