package com.guide.przewo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guide.przewo.Models.Place;


/**
 * Created by MateuszAntczak on 2015-06-25.
 */
public class PlaceDetailsFragment extends Fragment {

    View view;
    private Place place;
    public PlaceDetailsFragment()
    {
        // Required empty public constructor
    }

    public PlaceDetailsFragment(Place place)
    {
        this.place = place;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_place_details, container, false);

        if (place != null)
        {
            TextView placeDescription = (TextView)view.findViewById(R.id.pDesc);
            TextView placeAddress = (TextView)view.findViewById(R.id.pAddress);
            TextView placeDistance = (TextView)view.findViewById(R.id.pDistance);
            placeDescription.setText(place.description);
            placeAddress.setText(place.getAddress());
            placeDistance.setText(place.getDistanceInHumarFormat());
        }
        else
        {
            Log.d("Błąd", "Próba wypełnienia szczegółów miejsca przez null");
        }

        return view;
    }


}
