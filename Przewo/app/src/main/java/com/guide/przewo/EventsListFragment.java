package com.guide.przewo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by MateuszAntczak on 2015-05-05.
 * Fragment obsługujący listę wydarzeń
 */
public class EventsListFragment extends Fragment {

    View view;
    public EventsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_events_list, container, false);
        return view;
    }

}
