package com.guide.guide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import DataAccess.DataRepository;
import DataAccess.Interfaces.IDataRepository;
import DataAccess.LocalRepository;
import DataAccess.OnlineRepository;
import Models.Place;
import Utilities.MapsUseful;

public class BaseActivityFragments extends FragmentActivity
        implements RadarFragment.OnFragmentInteractionListener,RouteFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        EventsListFragment.OnFragmentInteractionListener, FilterFragment.OnFragmentInteractionListener,
        RouteDetailsFragment.OnFragmentInteractionListener,RouteListFragment.OnFragmentInteractionListener,
        PlaceDetailsFragment.OnFragmentInteractionListener, EventDetailsFragment.OnFragmentInteractionListener,
        PlaceRadarFragment.OnFragmentInteractionListener, GoogleApiClient.ConnectionCallbacks,
        OnConnectionFailedListener, GoogleMap.OnMapClickListener
{


	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
    LinearLayout topMenu;

    GoogleApiClient mGoogleApiClient;
    MapsUseful mapsUseful;
    GoogleMap googleMap;

    EventsListFragment events;
    RadarFragment radar;
    RouteFragment route;
    SearchFragment search;

    LocalRepository localRepository;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_activity);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		sharedPref = getSharedPreferences(getResources().getString(R.string.main_store), 0);
		editor = sharedPref.edit();

		initialiseBase();

//        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mapsUseful = new MapsUseful(getApplicationContext());
        mGoogleApiClient = mapsUseful.getGoogleApiClient();
        googleMap = mapsUseful.getGoogleMapWithTop(findViewById(R.id.baseLayout), savedInstanceState, R.id.mapView, R.id.topMenu);


        localRepository = new LocalRepository(getApplicationContext());
	}

	public void onBackPressed()
	{
        int inMainFragments = sharedPref.getInt(getResources().getString(R.string.in_main_fragment), 0);
        if(inMainFragments == 1) {
            Log.d("CDA", "ExitApp");
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
            startActivity(intent);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
        else {
            String currOption = sharedPref.getString(getResources().getString(R.string.curr_option), "");
            switch (currOption)
            {
                case "events":
                    runEvents();
                    break;
                case "route":
                    runRoute();
                    break;
                case "radar":
                    runRadar();
                    break;
                case "search":
                    runSearching();
                    break;
                default:
                    runRadar();
            }
        }
//        super.onBackPressed();
	}

	public void Test()
	{
		IDataRepository local = new LocalRepository(getApplicationContext());
		IDataRepository online = new OnlineRepository("http://piwko.usermd.net/api/places/1");

		DataRepository data = new DataRepository(local, online, getApplicationContext());

		data.IsAvailable();
	}

    public MapsUseful getMapsUseful() {
        return mapsUseful;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void runRoute()
    {
//		Intent intent = new Intent(this, RouteActivity.class);
//		startActivity(intent);
        Test();

//        RouteFragment route = new RouteFragment();
        changeFragment(route);
    }

	public void runEvents()
	{
//		Intent intent = new Intent(this, EventsActivity.class);
//		startActivity(intent);

//        EventsFragment events = new EventsFragment();
//        EventsListFragment events = new EventsListFragment();
        changeFragment(events);
	}

	public void runRadar()
	{
//		Intent intent = new Intent(this, RadarActivity.class);
//		startActivity(intent);

//        RadarFragment radar = new RadarFragment();
        changeFragment(radar);
	}

	public void runSearching()
	{
//		Intent intent = new Intent(this, SearchActivity.class);
//		startActivity(intent);

//        SearchFragment search = new SearchFragment();
        changeFragment(search);
	}

	public void initialiseBase()
	{
        events = new EventsListFragment();
        radar = new RadarFragment();
        route = new RouteFragment();
        search = new SearchFragment();

        topMenu = (LinearLayout) findViewById(R.id.topMenu);

		ImageButton routeButton = (ImageButton) findViewById(R.id.routeButton);
		routeButton.setOnClickListener(new View.OnClickListener()
		{

			public void onClick(View v)
			{
				runRoute();
			}
		});

		ImageButton eventsButton = (ImageButton) findViewById(R.id.eventsButton);
		eventsButton.setOnClickListener(new View.OnClickListener()
		{

			public void onClick(View v)
			{
				runEvents();
			}
		});

		ImageButton radarButton = (ImageButton) findViewById(R.id.radarButton);
		radarButton.setOnClickListener(new View.OnClickListener()
		{

			public void onClick(View v)
			{
				runRadar();
			}
		});

		ImageButton searchButton = (ImageButton) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new View.OnClickListener()
		{

			public void onClick(View v)
			{
				runSearching();
			}
		});

        String currOption = sharedPref.getString(getResources().getString(R.string.curr_option), "");
        switch (currOption)
        {
            case "events":
                runEvents();
                break;
            case "route":
                runRoute();
                break;
            case "radar":
                runRadar();
                break;
            case "search":
                runSearching();
                break;
            default:
                runRadar();
        }

        LinearLayout bottomMenu = (LinearLayout) findViewById(R.id.bottomMenu);
        bottomMenu.setVisibility(View.VISIBLE);
        LinearLayout bottomMenuBack = (LinearLayout) findViewById(R.id.bottomMenuBackground);
        bottomMenuBack.setVisibility(View.VISIBLE);

        checkCurrOption();
	}

	public void hideBottomMenu()
	{
//        topMenu = (LinearLayout) findViewById(R.id.topMenu);
//
//		String currOption = sharedPref.getString("currOption", "");

		LinearLayout bottomMenu = (LinearLayout) findViewById(R.id.bottomMenu);
		bottomMenu.setVisibility(View.INVISIBLE);
		LinearLayout bottomMenuBack = (LinearLayout) findViewById(R.id.bottomMenuBackground);
		bottomMenuBack.setVisibility(View.INVISIBLE);

	}
    public void showBottomMenu() {
        LinearLayout bottomMenu = (LinearLayout) findViewById(R.id.bottomMenu);
        bottomMenu.setVisibility(View.VISIBLE);
        LinearLayout bottomMenuBack = (LinearLayout) findViewById(R.id.bottomMenuBackground);
        bottomMenuBack.setVisibility(View.VISIBLE);
    }

    //zaznaczenie wybranej opcji
    public void checkCurrOption() {
        ImageButton routeButton = (ImageButton) findViewById(R.id.routeButton);
        ImageButton eventsButton = (ImageButton) findViewById(R.id.eventsButton);
        ImageButton radarButton = (ImageButton) findViewById(R.id.radarButton);
        ImageButton searchButton = (ImageButton) findViewById(R.id.searchButton);

        String currOption = sharedPref.getString("currOption", "");

        routeButton.setImageResource(R.mipmap.route);
        eventsButton.setImageResource(R.mipmap.events);
        radarButton.setImageResource(R.mipmap.radar);
        searchButton.setImageResource(R.mipmap.search);
        Log.d("======CurrOption======", "==================================" + currOption);
        switch (currOption)
        {
            case "events":
                eventsButton.setImageResource(R.mipmap.events_checked);
                break;
            case "route":
                routeButton.setImageResource(R.mipmap.route_checked);
                break;
            case "radar":
                radarButton.setImageResource(R.mipmap.radar_checked);
                break;
            case "search":
                searchButton.setImageResource(R.mipmap.search_checked);
                break;
        }
    }

	public void setTopTitle(String title)
	{
		TextView titleView = (TextView) findViewById(R.id.title);
		titleView.setText(title);
	}

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    //Wybierz 1 przycisk od prawej strony z Top Menu
    public ImageButton getBtn1TopMenu()
    {
        return (ImageButton) findViewById(R.id.imageButton1);
    }

    //Wybierz 2 przycisk od prawej strony z Top Menu
    public ImageButton getBtn2TopMenu()
    {
        return (ImageButton) findViewById(R.id.imageButton2);
    }

    //Zmień widoczny fragment na fragment f
    public void changeFragment(Fragment f)
    {
        hideKeyboard();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.fragment_container, f).commit();
    }

    public void clearTopMenu() {
        setTopTitle("");
        getBtn1TopMenu().setVisibility(View.INVISIBLE);
        getBtn2TopMenu().setVisibility(View.INVISIBLE);
    }

    public void showPlaceInPopup(final Place place) {

        RelativeLayout popup = (RelativeLayout) findViewById(R.id.popup);
        popup.setVisibility(View.VISIBLE);

        TextView title = (TextView) findViewById(R.id.popupTitle);
        title.setText(place.GetName());

//        ImageView image = (ImageView) findViewById(R.id.popupImage);
//        image.setImageResource(R.mipmap.map_events);

        Button more = (Button) findViewById(R.id.popupMore);
        more.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
//                editor.putInt(getResources().getString(R.string.place_id_detail), place.GetID());
//                editor.commit();
                PlaceDetailsFragment frag = new PlaceDetailsFragment();
                Bundle bundles = new Bundle();
                bundles.putSerializable("Place", place);
                frag.setArguments(bundles);
                changeFragment(frag);
            }
        });

//        ImageButton closeButton = (ImageButton) findViewById(R.id.popupTopBtn1);
//        closeButton.setVisibility(View.VISIBLE);
//        closeButton.setImageResource(R.mipmap.cross);
//        closeButton.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                hidePopup();
//            }
//        });
    }

    public void showPlaceInPopup(final int placeId) {
        if(localRepository.GetPlaceDetails(placeId) != null) {
            Place place = localRepository.GetPlaceDetails(placeId);

            RelativeLayout popup = (RelativeLayout) findViewById(R.id.popup);
            popup.setVisibility(View.VISIBLE);

            TextView title = (TextView) findViewById(R.id.popupTitle);
            title.setText(place.GetName());

//        ImageView image = (ImageView) findViewById(R.id.popupImage);
//        image.setImageResource(R.mipmap.map_events);

            Button more = (Button) findViewById(R.id.popupMore);
            more.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    //zapamiętanie które miejsce powinno się pojawić w szczegółach
                    editor.putInt(getResources().getString(R.string.place_id_detail), placeId);
                    editor.commit();
                    //uruchomienie fragmentu ze szczegółami miejsca
//                changeFragment();
                }
            });

            //Przycisk do zamknięcia okienka popup
//            ImageButton closeButton = (ImageButton) findViewById(R.id.popupTopBtn1);
//            closeButton.setVisibility(View.VISIBLE);
//            closeButton.setImageResource(R.mipmap.cross);
//            closeButton.setOnClickListener(new View.OnClickListener() {
//
//                public void onClick(View v) {
//                    hidePopup();
//                }
//            });
        }
    }

    public void hidePopup() {
        RelativeLayout popup = (RelativeLayout) findViewById(R.id.popup);
        popup.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFragmentInteraction(Uri uri)
    {

    }

    @Override
    public void onConnected(Bundle bundle)
    {

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

    }

    //Funkcja uruchamiana przy uruchomieniu fragmentu
    public void onRunFragment()
    {
        googleMap.clear();
        clearTopMenu();
        showBottomMenu();
        hidePopup();
        editor.putInt(getResources().getString(R.string.in_main_fragment), 0);
    }

    //Funkcja uruchamiana po załadowaniu fragmentu
    public void onLoadedFragment()
    {
        Log.d("Fragment", "Loaded");

        checkCurrOption();

    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }
}
