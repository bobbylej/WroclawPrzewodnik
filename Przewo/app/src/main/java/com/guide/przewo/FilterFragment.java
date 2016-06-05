package com.guide.przewo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.guide.przewo.DataAccess.LocalRepository;
import com.guide.przewo.Models.Category;
import com.guide.przewo.Models.Type;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Ustawienia radaru oraz filtry
 */
public class FilterFragment extends Fragment
{
    private MapManager mapManager;
    private OnFragmentInteractionListener mListener;
    View v;
    private long mLastClickTime = 0;

    LocalRepository localRepository;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    private int radius;
    private List<Type> types;
    private List<Type> chosenTypes;
    private List<Category> categories;
    private List<Category> chosenCategories;

    public FilterFragment() {
        // Required empty public constructor
    }

    public FilterFragment(MapManager mapManager)
    {
        this.mapManager = mapManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_filter, container, false);

        localRepository = new LocalRepository(getActivity().getApplicationContext());

        sharedPref = getActivity().getSharedPreferences(getResources().getString(R.string.main_store), 0);
        editor = sharedPref.edit();

        /*
        types = new ArrayList<Type>();
        types.addAll(localRepository.getAllPlacesTypes());
        //fillTypes(); //create checkboxes for types

        chosenTypes = new ArrayList<Type>();
        */

        radius = sharedPref.getInt("radar_radius", 3000);

//        chosenTypes = ((MainActivity)getActivity()).chosenTypes;
//        chosenCategories = ((MainActivity)getActivity()).choosenCategories;

        /*
        Button checkAllBtn = (Button) v.findViewById(R.id.checkAllButton);
        checkAllBtn.setVisibility(View.GONE);
        checkAllBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkAllTypes();
            }
        });

        Button uncheckAllBtn = (Button) v.findViewById(R.id.uncheckAllButton);
        uncheckAllBtn.setVisibility(View.GONE);
        uncheckAllBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                uncheckAllTypes();
            }
        });
        */



        /*
        getChosenTypes(); //get checked types from shared pref
        checkTypes(); //check checkboxes of types which was before chosen
        */
        changeRadius(); //add change radius listener

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
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
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
                /*
                mapManager.clearMap();
                mapManager.showNearbyPlaces(chosenTypes, chosenCategories);
                */
                ((MainActivity)getActivity()).getMap().clear();
                editor.putBoolean("needToResume", true);
                editor.commit();
            }

            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                String radiusText = df.format((double) progress / 1000) + " km";
                radiusFilterText.setText(radiusText);
                radius = progress;

                editor.putInt(getResources().getString(R.string.radar_radius), radius);
                editor.commit();
            }
        });
    }

    public List<Type> getTypes() {
        List<Type> types = localRepository.getAllPlacesTypes();
        if(types != null) {
            this.types = types;
        }
        return types;
    }

    public void fillTypes() {
        final LinearLayout typesBox = (LinearLayout) getView().findViewById(R.id.typesBox);
        for(final Type type : types) {
            final CheckBox cb = new CheckBox(getActivity());
            cb.setText(type.name);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        chosenTypes.add(type);
                        Log.d("filltypes1", "");
                        saveCheckedTypes();
                    } else {
                        while (chosenTypes.contains(type)) {
                            chosenTypes.remove(type);
                        }
                        Log.d("filltypes2","");
                        saveCheckedTypes();
                    }
                }
            });
            typesBox.addView(cb);
        }
    }

    public void checkTypes() {
        if(chosenTypes != null && chosenTypes.size() > 0) {
            LinearLayout typesBox = (LinearLayout) getView().findViewById(R.id.typesBox);
            for (int i = 0; i < typesBox.getChildCount(); i++) {
                View v = typesBox.getChildAt(i);
                if (v instanceof CheckBox) {
//                        for (Type type : chosenTypes) {
                        for (int j=0; j<chosenTypes.size(); j++) {
                            Type type = chosenTypes.get(j);
                            if (type.name.equals(((CheckBox) v).getText())) {
                                ((CheckBox) v).setChecked(true);
                            }
                        }
                }
            }
        }
    }

    public void getChosenTypes() {
        String chosenTypesIdsString = sharedPref.getString("chosenTypes", null);
        chosenTypes = new ArrayList<>();
        if(chosenTypesIdsString != null) {
            String[] chosenTypesIdsStringArray = chosenTypesIdsString.split(",");
            for (int i = 0; i < chosenTypesIdsStringArray.length; i++) {
                if (isInteger(chosenTypesIdsStringArray[i])) {
//                    if(types.get(Integer.parseInt(chosenTypesIdsStringArray[i])).id == Integer.parseInt(chosenTypesIdsStringArray[i])) {
//                        chosenTypes.add(types.get(Integer.parseInt(chosenTypesIdsStringArray[i])));
//                    }
//                    else {
                        for (Type type : types) {
                            if (type.id == Integer.parseInt(chosenTypesIdsStringArray[i])) {
                                chosenTypes.add(type);
                                Log.d("Type", type.id + " - id");
                            }
                        }
//                    }
                }
            }
        }
    }

    public void saveCheckedTypes() {
//        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//            return;
//        }
        mLastClickTime = SystemClock.elapsedRealtime();

        String checkedTypesIdsString = "";
        DateTime now = new DateTime();
        for(Type type : chosenTypes) {
            checkedTypesIdsString += type.id + ",";
        }
        sharedPref.edit().putString("chosenTypes", checkedTypesIdsString).commit();
        mapManager.clearMap();
        mapManager.showNearbyPlaces(chosenTypes, chosenCategories);
        Log.d("Minelo ms:", now.getMillis() - (new DateTime().getMillis()) + " ");
    }

    public void checkAllTypes() {
            LinearLayout typesBox = (LinearLayout) getView().findViewById(R.id.typesBox);
            for (int i = 0; i < typesBox.getChildCount(); i++) {
                View v = typesBox.getChildAt(i);
                if (v instanceof CheckBox) {
                    ((CheckBox) v).setChecked(true);
                }
            }
        chosenTypes = new ArrayList<>();
        chosenTypes.addAll(types);
        Log.d("checkall","");
        saveCheckedTypes();
    }

    public void uncheckAllTypes() {
        LinearLayout typesBox = (LinearLayout) getView().findViewById(R.id.typesBox);
        for (int i = 0; i < typesBox.getChildCount(); i++) {
            View v = typesBox.getChildAt(i);
            if (v instanceof CheckBox) {
                ((CheckBox) v).setChecked(false);
            }
        }
        chosenTypes = new ArrayList<>();
        Log.d("uncheckall","");
        saveCheckedTypes();
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    public View getView() {
        return v;
    }
}
