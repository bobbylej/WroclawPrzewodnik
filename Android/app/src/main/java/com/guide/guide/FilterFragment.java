package com.guide.guide;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import DataAccess.LocalRepository;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.guide.guide.FilterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.guide.guide.FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    View v;

    LocalRepository localRepository;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String currOption;
    private int radius;
    private List<String> types;
    private List<String> chosenTypes;
    private GoogleMap googleMap;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterFragment newInstance(String param1, String param2) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_filter, container, false);

        ((BaseActivityFragments) getActivity()).onRunFragment();

        googleMap = ((BaseActivityFragments) getActivity()).getGoogleMap();
        googleMap.setMyLocationEnabled(false);

        ((BaseActivityFragments) getActivity()).hideBottomMenu();
        ((BaseActivityFragments) getActivity()).setTopTitle(getResources().getString(R.string.title_fragment_filter_pl));

        localRepository = new LocalRepository(getActivity().getApplicationContext());

        sharedPref = getActivity().getSharedPreferences(getResources().getString(R.string.main_store), 0);
        editor = sharedPref.edit();

        types = new ArrayList<String>();
//        getTypes();
        types.add("WSZYSTKIE");
        types.add("Restauracja");
        types.add("Muzeum");
        types.add("Obiekt sportowy");
        types.add("Kino");
        types.add("Kultura");
        fillTypes(); //create checkboxes for types

        chosenTypes = new ArrayList<String>();
        currOption = sharedPref.getString(getResources().getString(R.string.curr_option), "");
        switch(currOption) {
            case "events":
                radius = sharedPref.getInt(getResources().getString(R.string.events_radius), 500);
                Set<String> tempCategories = sharedPref.getStringSet("categories", null);
                if(tempCategories != null) {
                    for (String cat : tempCategories) {
                        chosenTypes.add(cat);
                    }
//                    chosenTypes = Arrays.asList(Arrays.asList(tempCategories.toArray()).toArray(new String[tempCategories.toArray().length]));
                }
                break;
            case "radar":
                radius = sharedPref.getInt(getResources().getString(R.string.radar_radius), 500);
                Set<String> tempTypes = sharedPref.getStringSet("types", null);
                if(tempTypes != null) {
                    for (String type : tempTypes) {
                        chosenTypes.add(type);
                    }
                }
//                    chosenTypes = Arrays.asList(Arrays.asList(tempTypes.toArray()).toArray(new String[tempTypes.toArray().length]));
                break;
            default:
                radius = 500;
        }

        checkTypes(); //check checkboxes of types which was before chosen


        ImageButton acceptButton = ((BaseActivityFragments) getActivity()).getBtn1TopMenu();
        acceptButton.setVisibility(View.VISIBLE);
        acceptButton.setImageResource(R.mipmap.accept);
        acceptButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                saveFilter();
                goBack();
            }
        });

        changeRadius();

        //Przekazanie do aktywności, że fragemt się załadował
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

    private void changeRadius()
    {
        SeekBar radiusFilter = (SeekBar) getView().findViewById(R.id.radiusFilter);
        final TextView radiusFilterText = (TextView) getView().findViewById(R.id.radiusFilterText);
        radiusFilter.setProgress(radius);

        final DecimalFormat df = new DecimalFormat("0.#");

        String radiusText = df.format((double) radius / 1000) + " km";
        radiusFilterText.setText(radiusText);
        radiusFilter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {


            public void onStopTrackingTouch(SeekBar seekBar)
            {
                // TODO Auto-generated method stub
            }


            public void onStartTrackingTouch(SeekBar seekBar)
            {
                // TODO Auto-generated method stub
            }


            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                String radiusText = df.format((double) progress / 1000) + " km";
                radiusFilterText.setText(radiusText);
            }
        });
    }

    public void saveFilter()
    {
        SeekBar radiusFilter = (SeekBar) getView().findViewById(R.id.radiusFilter);
        for(String s : chosenTypes) {
            Log.d("Types", s + "++++++++++++++++++++++++++");
        }
        switch(currOption) {
            case "events":
                editor.putInt(getResources().getString(R.string.events_radius), radiusFilter.getProgress());
                Set<String> tempCategories = new HashSet<String>(chosenTypes);
                editor.putStringSet("categories", tempCategories);
                Set<String> tempTypes2 = new HashSet<String>(chosenTypes);
                editor.putStringSet("types", tempTypes2);
                editor.commit();
                break;
            case "radar":
                editor.putInt(getResources().getString(R.string.radar_radius), radiusFilter.getProgress());
                Set<String> tempTypes = new HashSet<String>(chosenTypes);
                editor.putStringSet("types", tempTypes);
                editor.commit();
                break;
        }
    }

    public List<String> getTypes() {
        List<String> temp = localRepository.getPlacesTypes();
        if(temp != null) {
            types = temp;
        }
        return types;
    }

    public void fillTypes() {
        final LinearLayout typesBox = (LinearLayout) getView().findViewById(R.id.typesBox);
        for(final String type : types) {
            final CheckBox cb = new CheckBox(getActivity());
            cb.setText(type);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        chosenTypes.add(type);
                        Log.d("typeBox", type + " was added");
                        for(String s : chosenTypes) {
                            Log.d("Types", s + "++++++++++++++++++++++++++");
                        }
                    } else {
                        while(chosenTypes.contains(type)) {
                            chosenTypes.remove(type);
                        }
                        Log.d("typeBox", type + " was removed");
                        for(String s : chosenTypes) {
                            Log.d("Types", s + "--------------------------");
                        }
                    }
                }
            });
            typesBox.addView(cb);
        }
    }

    public void checkTypes() {
        LinearLayout typesBox = (LinearLayout) getView().findViewById(R.id.typesBox);
        for (int i = 0; i < typesBox.getChildCount(); i++) {
            View v = typesBox.getChildAt(i);
            if (v instanceof CheckBox) {
                if(chosenTypes.contains(((CheckBox) v).getText())) {
                    ((CheckBox)v).setChecked(true);
                }
            }
        }
    }

    public void goBack() {
        switch(currOption) {
            case "events":
                EventsListFragment listEvents = new EventsListFragment();
                ((BaseActivityFragments) getActivity()).changeFragment(listEvents);
                break;
            case "radar":
                RadarFragment radar = new RadarFragment();
                ((BaseActivityFragments) getActivity()).changeFragment(radar);
                break;
            default:
                Intent intent = new Intent(getActivity(), BaseActivityFragments.class);
                startActivity(intent);
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
        public void onFragmentInteraction(Uri uri);
    }

    public View getView() {
        return v;
    }
}
