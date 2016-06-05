package com.guide.przewo;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.guide.przewo.DataAccess.App;
import com.guide.przewo.DataAccess.LocalRepository;
import com.guide.przewo.Models.Place;
import com.guide.przewo.Models.Route;

/**
 * Created by MateuszAntczak on 2015-06-25.
 */
public class PlaceFragment extends Fragment implements View.OnClickListener {
    private MainActivity mainActivity;
    View view;
    ViewPager viewPager;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    Fragment currentFragment = null;
    Fragment detailsFragment;
    Fragment eventsFragment;
    Fragment routesFragment;
    FragmentManager fragmentManager;
    LocalRepository repository;

    public PlaceFragment() {
    }

    public PlaceFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_place, container, false);
        sharedPref = getActivity().getSharedPreferences(getResources().getString(R.string.main_store), 0);
        editor = sharedPref.edit();

        Button detailsButton = (Button) view.findViewById(R.id.detailsButton);
        detailsButton.setBackgroundColor(Color.parseColor("#53749D"));
        detailsButton.setOnClickListener(this);
        detailsButton.setTextColor(Color.WHITE);
        Button eventsButton = (Button) view.findViewById(R.id.eventsButton);
        eventsButton.setBackgroundColor(Color.LTGRAY);
        eventsButton.setOnClickListener(this);
        eventsButton.setTextColor(Color.WHITE);
        Button routesButton = (Button) view.findViewById(R.id.routesButton);
        routesButton.setBackgroundColor(Color.LTGRAY);
        routesButton.setOnClickListener(this);
        routesButton.setTextColor(Color.WHITE);

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        ImageAdapter adapter = new ImageAdapter(getActivity());
        viewPager.setAdapter(adapter);

        ImageButton locationButton = (ImageButton) view.findViewById(R.id.locationButton);
        locationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                RadarManager.isRadarBlocked = true;
                mainActivity.getMap().clear();
                SharedPreferences sharedPref;
                sharedPref = getActivity().getSharedPreferences(getActivity().getResources().getString(R.string.main_store), 0);
                sharedPref.edit().putInt("openPlace", sharedPref.getInt(getResources().getString(R.string.current_marker), -1)).commit();
                terminateFragment();
                ((MainActivity) getActivity()).onResume();
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    ImageButton bt = (ImageButton) getActivity().findViewById(R.id.search_button);
                    bt.setVisibility(View.VISIBLE);
                    terminateFragment();
                    return true;
                }
                return false;
            }
        });

        int placeId = sharedPref.getInt(getResources().getString(R.string.current_marker), -1);

        Place place = ((App) getActivity().getApplicationContext()).getLocalRepository().getPlaceDetails(placeId);

        if(place!=null)
            eventsButton.setText("Wydarzenia (" + place.eventCount + ")");
        int routeCount = 0;
        if (Route.AllRoutes != null)
        {
            for (Route route : Route.AllRoutes)
            {
                if (route.placesIds.contains(placeId))
                {
                    routeCount++;
                }
            }
        }

        routesButton.setText("Trasy (" + routeCount + ")");
        updateInformation();
        return view;
    }

    public void terminateFragment(){
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void updateInformation()
    {
        int placeId = sharedPref.getInt(getResources().getString(R.string.current_marker), -1);

        Place place = ((App) getActivity().getApplicationContext()).getLocalRepository().getPlaceDetails(placeId);
        detailsFragment = new PlaceDetailsFragment(place);
        eventsFragment = new EventListFragment(placeId);
        routesFragment = new RouteListFragment(placeId);

        fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().add(R.id.fragment_place_container, detailsFragment).commit();

        if (placeId != -1)
        {
            Place placeObject = App.LOCALREP.getPlaceDetails(placeId);

            if (placeObject.photos.isEmpty()) ((ViewGroup) viewPager.getParent()).removeView(viewPager);
        }
    }

    public void setRepository(LocalRepository repository){
        this.repository = repository;
    }


    @Override
    public void onClick(View v) {
        Button detailsButton = (Button)getView().findViewById(R.id.detailsButton);
        Button eventsButton = (Button)getView().findViewById(R.id.eventsButton);
        Button routesButton = (Button)getView().findViewById(R.id.routesButton);

        switch (v.getId()){
            case R.id.detailsButton:
                fragmentManager.beginTransaction().remove(eventsFragment).commit();
                fragmentManager.beginTransaction().remove(routesFragment).commit();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_place_container, detailsFragment)
                        .commit();

                detailsButton.setBackgroundColor(Color.parseColor("#53749D"));
                eventsButton.setBackgroundColor(Color.LTGRAY);
                routesButton.setBackgroundColor(Color.LTGRAY);
                break;
            case R.id.eventsButton:
                fragmentManager.beginTransaction().remove(detailsFragment).commit();
                fragmentManager.beginTransaction().remove(routesFragment).commit();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_place_container, eventsFragment)
                        .commit();

                detailsButton.setBackgroundColor(Color.LTGRAY); eventsButton.setBackgroundColor(Color.parseColor("#53749D")); routesButton.setBackgroundColor(Color.LTGRAY);
                break;
            case R.id.routesButton:
                fragmentManager.beginTransaction().remove(detailsFragment).commit();
                fragmentManager.beginTransaction().remove(eventsFragment).commit();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_place_container, routesFragment)
                        .commit();

                detailsButton.setBackgroundColor(Color.LTGRAY); eventsButton.setBackgroundColor(Color.LTGRAY); routesButton.setBackgroundColor(Color.parseColor("#53749D"));
                break;
            default: break;
        }
    }




}
