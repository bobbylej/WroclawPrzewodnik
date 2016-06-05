package com.guide.przewo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.guide.przewo.Models.Route;

import java.util.ArrayList;
import java.util.List;

public class RouteListFragment extends Fragment implements AbsListView.OnItemClickListener
{
    ListView routesView;
    private List<Route> routes = new ArrayList<Route>();
    ArrayAdapter adapter;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    int idPlace = -1;

    public RouteListFragment()
    {
    }

    public RouteListFragment(int idPlace)
    {
        this.idPlace = idPlace;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (idPlace == -1)
        {
            routes = Route.AllRoutes;
        }
        else
        {
            for (Route route : Route.AllRoutes)
            {
                if (route.placesIds != null)
                {
                    if (route.placesIds.contains(idPlace))
                    {
                        boolean alreadyAdded = false;
                        for (Route added : routes)
                        {
                            if (added.id == route.id)
                            {
                                alreadyAdded = true;
                            }
                        }
                        if (alreadyAdded == false)
                        {
                            routes.add(route);
                        }
                    }
                }
                else
                {
                    Log.d("Błąd trasy!", "Brak miejsc w trasie " + route.toString());
                }
            }
        }

        sharedPref = getActivity().getSharedPreferences(getResources().getString(R.string.main_store), 0);
        editor = sharedPref.edit();

        if (routes.isEmpty())
        {
            routes.add(new Route(-1, "Brak tras zawierających to miejsce.", "", "", -1));
        }

        adapter = new RouteListAdapter(getActivity(), routes, getFragmentManager(), editor);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_routes_list, container, false);

        getActivity().setTitle(getResources().getString(R.string.title_fragment_route_list_pl));

        routesView = (ListView) view.findViewById(android.R.id.list);
        routesView.setAdapter(adapter); // otóż nie wiem czemu wywala nullpointer to ani listview, ani adapter

        routesView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (((TextView)view.findViewById(R.id.name)).getText().toString().toLowerCase().contains("brak") == false)
        {
            View toolbar = view.findViewById(R.id.details);

            // Creating the expand animation for the item

            for (int i = 0; i < parent.getChildCount(); i++)
            {
                if (parent.getChildAt(i).findViewById(R.id.details).getVisibility() == View.VISIBLE)
                {
                    View toHide = parent.getChildAt(i).findViewById(R.id.details);
                    ExpandAnimation hideAnimation = new ExpandAnimation(toHide, 100);
                    toHide.startAnimation(hideAnimation);
                }
            }

            ExpandAnimation expandAni = new ExpandAnimation(toolbar, 500);

            // Start the animation on the toolbar
            toolbar.startAnimation(expandAni);
        }
    }
}
