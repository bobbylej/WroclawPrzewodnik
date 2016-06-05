package com.guide.guide;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import DataAccess.LocalRepository;
import Models.Event;
import Models.Location;
import Models.Place;
import Models.Route;
import Models.Type;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.guide.guide.EventDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.guide.guide.EventDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailsFragment extends Fragment
{
    private OnFragmentInteractionListener mListener;
    View v;

    LocalRepository localRepository;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String currOption;
    private int id;
    private Event event;


    // TODO: Rename and change types and number of parameters
    public static EventDetailsFragment newInstance() {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_details_event, container, false);

        ((BaseActivityFragments) getActivity()).onRunFragment();

        ((BaseActivityFragments) getActivity()).hideBottomMenu();
        ((BaseActivityFragments) getActivity()).setTopTitle(getResources().getString(R.string.title_fragment_details_place_pl));

        localRepository = new LocalRepository(getActivity().getApplicationContext());

        sharedPref = getActivity().getSharedPreferences(getResources().getString(R.string.main_store), 0);
        editor = sharedPref.edit();

//        id = sharedPref.getInt(getResources().getString(R.string.place_id_detail), -1);
//        if(id != -1) {
        Bundle bundle = getArguments();
        if(bundle != null && event == null ){
//        if(id != -1 && localRepository.GetPlaceDetails(id) != null) {
//            place = localRepository.GetPlaceDetails(id);

//            List<Type> types = new ArrayList<Type>();
//            types.add(new Type(1,"Centrum handlowe",""));
//            Place place = new Place(5, "Galeria", "Bardzo fajne galeria, polecam...\n\n\nno", "", 1, types, new Date(), new Location(51.11950f, 17.03032f, "Wrocław", "Plac Dominikański 1"), "Pon-Sob 06:00-21:00\nNiedziela 06:00-20:00");
//            event = new Event(1, "Wydarzenie", "Ciekawe wydarzenie", "", 1, types, new Date(), new Date(), new Date(), new Time(16), new Time(24), place);

            event = (Event) bundle.getSerializable("Event");

            TextView title = (TextView) getView().findViewById(R.id.title);
            title.setText(event.GetName());

            if(event.GetPlace() != null) {
                TextView address = (TextView) getView().findViewById(R.id.address);
                address.setText(event.GetPlace().GetLocation().GetCity() + "\n" + event.GetPlace().GetLocation().GetStreet());
            }

            TextView openTime = (TextView) getView().findViewById(R.id.openTime);
            openTime.setText(event.GetStartDate() + " " + event.GetStartTime() + " - " + event.GetEndDate() + " " + event.GetEndTime());

            TextView desc = (TextView) getView().findViewById(R.id.description);
            desc.setText(event.GetDescription());

            TableLayout tagsBox = (TableLayout) getView().findViewById(R.id.tagsBox);
            for(final Type type : event.GetTypes()) {
                Button tag = makeTag(type.name);
                tagsBox.addView(tag);
            }

            if(event.GetPlace() != null) {
                ImageButton locButton = ((BaseActivityFragments) getActivity()).getBtn1TopMenu();
                locButton.setVisibility(View.VISIBLE);
                locButton.setImageResource(R.mipmap.location);
                locButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        editor.putString(getResources().getString(R.string.prev_fragment), "PlaceDetailsFragment");
                        editor.commit();

                        PlaceRadarFragment frag = new PlaceRadarFragment();
                        Bundle bundles = new Bundle();
                        bundles.putSerializable("Event", event);
                        bundles.putSerializable("EventPlace", event.GetPlace());
                        frag.setArguments(bundles);
                        ((BaseActivityFragments) getActivity()).changeFragment(frag);
                    }
                });
            }
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
