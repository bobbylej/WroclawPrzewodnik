package com.guide.przewo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.guide.przewo.DataAccess.App;
import com.guide.przewo.Models.Category;
import com.guide.przewo.Models.Place;
import com.guide.przewo.Models.Route;
import com.guide.przewo.Models.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*
 * Co tu sie ogolnie dzieje?
 * Ta aktywnosc odpowiada za ogarnianie tego wysuwanego menu i w zaleznosci od tego, co jest wybrane,
 * wyswietla odpowiednie rzeczy.
 */
public class MainActivity extends BaseActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        GoogleMap.OnMyLocationChangeListener,
        EventListFragment.OnFragmentInteractionListener, FilterFragment.OnFragmentInteractionListener
{
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Fragment currentFragment;
    private Fragment currentSliderFragment;
    private SlidingDrawer slidingDrawer;

    public MapManager mapManager;
    private GoogleMap map;
    private TransportManager transportManager;

    private long mLastClickTime = 0;

    public static Route currentRoute;
    public List<Place> places;
    public Location currentLocation;
    public List<Type> chosenTypes = new ArrayList<>();
    public List<Category> choosenCategories = new ArrayList<>();
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    boolean needToOpenRadar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences(getResources().getString(R.string.main_store), 0);
        editor = sharedPref.edit();

        slidingDrawer = (SlidingDrawer)findViewById(R.id.slidingD);

        this.map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        this.mapManager = new MapManager(map, getApplicationContext(), this);
        this.transportManager = new TransportManager(this);
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(this);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));


        ImageButton logo = (ImageButton) findViewById(R.id.search_button);
        logo.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        logo.setVisibility(View.VISIBLE);
        View v = (View) findViewById(R.id.fragment_place_slider_container);
        v.setVisibility(View.INVISIBLE);

        currentSliderFragment = null;

        needToOpenRadar = true;

        String chosenTypesIdsString = sharedPref.getString("chosenTypes", null);
        if(chosenTypesIdsString == null) {
            chosenTypes = Place.AllPlacesTypes;
            saveCheckedTypes();
        }
        else {
            getChosenTypes();
        }


        //showRoute(-1, true);
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public GoogleMap getMap() {
        return map;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("App", "onResume");
        mapManager.clearMap();
        mapManager.setUpMapIfNeeded(map);
        mapManager.clearMap();
        //getChosenTypes();

        if(checkForStaffOpeningNeed()) {
            needToOpenRadar = false;
        }
        else {
            needToOpenRadar = true;
        }

    }

    public boolean checkForStaffOpeningNeed()
    {
        boolean existPlaceToOpen = false;
        if (sharedPref.getInt("openPlace", -1) != -1)
        {
            int idToOpen = sharedPref.getInt("openPlace", -1);
            sharedPref.edit().putInt("openPlace", -1).commit();

            Place placeToOpen = ((App)getApplicationContext()).getLocalRepository().getPlaceDetails(idToOpen);
            mapManager.drawCircleForPlace(new LatLng(placeToOpen.latitude, placeToOpen.longitude), 1000);
            RadarManager.isRadarBlocked = false;
            mapManager.showNearbyPlaces(new LatLng(placeToOpen.latitude, placeToOpen.longitude), 1000, true);
            RadarManager.isRadarBlocked = true;
            mapManager.drawMarker(map, placeToOpen, true, false);
            showPlaceInPopup(placeToOpen.id, false);
            slidingDrawer = (SlidingDrawer) findViewById(R.id.slidingD);

            existPlaceToOpen = true;

        }

        if (sharedPref.getInt("showRoute", -1) != -1)
        {
            int idToOpen = sharedPref.getInt("showRoute", -1);
            sharedPref.edit().putInt("showRoute", -1).commit();
            editor.putInt(String.valueOf(R.string.current_route), idToOpen);
            boolean walking = sharedPref.getInt("walking", -1) == 1? true : false;
            sharedPref.edit().putInt("walking", -1).commit();
            showRoute(idToOpen, walking);
        }

        return existPlaceToOpen;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        currentFragment = null;

        if(position == 1)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStackImmediate();
            onResume();
            CameraUpdate center=
                    CameraUpdateFactory.newLatLng(new LatLng(map.getMyLocation().getLatitude(), (map.getMyLocation().getLongitude())));
            map.animateCamera(center);
        }
        if(position == 2)
        {
            currentFragment = new RouteListFragment();
        }
        if(position == 3)
        {
            currentFragment = new EventListFragment();
        }
        if(position == 4)
        {
            currentFragment = new FilterFragment(mapManager);
        }
        if(position == 5)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStackImmediate();
        }

        if (currentFragment != null)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();

            // pętla do wyczyszczenia BackStack (stos cofania fragmentów),
            // aby zawsze wracało do głównej aktywności
            for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
                fragmentManager.popBackStack();
            }

            fragmentManager.beginTransaction()
                    .replace(R.id.container, currentFragment)
                    .addToBackStack("main_fragments") /* stos służący do cofania za pomocą przycisku BACK */
                    .commit();
        }

    }

    public void onSectionAttached(int number)
    {
        RadarManager.isRadarBlocked = false;

        ImageButton bt = (ImageButton) findViewById(R.id.search_button);

        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                mapManager.clearMap();
                mapManager.showNearbyPlaces();
                bt.setVisibility(View.VISIBLE);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                mapManager.clearMap();
                mapManager.showNearbyPlaces();
                bt.setVisibility(View.VISIBLE);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                RadarManager.isRadarBlocked = true;
                mTitle = getString(R.string.title_section5);
                needToOpenRadar = false;
                bt.setVisibility(View.VISIBLE);
                if(currentSliderFragment != null)
                ((DrawerFragment)currentSliderFragment).terminateFragment();

                mapManager.clearMap();
                List<Place> dwarfs = ((App) getApplication()).getDwarfs();
                updateCameraForPlaces(dwarfs);
                mapManager.drawMarkers(getMap(), dwarfs);
                break;
            case 6:
                RadarManager.isRadarBlocked = true;
                mTitle = getString(R.string.title_section6);
                needToOpenRadar = false;
                bt.setVisibility(View.VISIBLE);
                if(currentSliderFragment != null)
                    ((DrawerFragment)currentSliderFragment).terminateFragment();

                mapManager.clearMap();
                List<Place> legends = ((App) getApplication()).getLegends();
                updateCameraForPlaces(legends);
                mapManager.drawMarkers(getMap(), legends);
                break;
            case 7:
                RadarManager.isRadarBlocked = true;
                mTitle = getString(R.string.title_section7);
                bt.setVisibility(View.VISIBLE);
                mapManager.clearMap();
                transportManager.updateTransportFromServer();
                transportManager.drawTransportInCircle();

                break;
        }
    }

    private void updateCameraForPlaces(List<Place> places)
    {
        CameraUpdate cameraUpdate = GetCameraUpdateForPlaces(places);
        mapManager.getMap().animateCamera(cameraUpdate);
    }

    public CameraUpdate GetCameraUpdateForPlaces(List<Place> places)
    {
        List<LatLng> positions = getPositions(places);

        double minLat = Double.MAX_VALUE, minLon = Double.MAX_VALUE, maxLat = Double.MIN_VALUE, maxLon = Double.MIN_VALUE;

        for (LatLng pos : positions)
        {
            if (pos.longitude < minLon) minLon = pos.longitude;
            if (pos.longitude > maxLon) maxLon = pos.longitude;
            if (pos.latitude < minLat) minLat = pos.latitude;
            if (pos.latitude > maxLat) maxLat = pos.latitude;
        }
        LatLngBounds bounds = new LatLngBounds(new LatLng(minLat, minLon), new LatLng(maxLat, maxLon));
        return CameraUpdateFactory.newLatLngBounds(bounds, 200);
    }

    public List<LatLng> getPositions(List<Place> places)
    {
        List<LatLng> positions = new ArrayList<LatLng>();

        for(Place place : places)
        {
            LatLng latLng = new LatLng(place.latitude, place.longitude);
            positions.add(latLng);
        }

        return positions;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    // do action bara chyba
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showRoute(int currentRouteId, boolean walking)
    {

        FragmentManager fragMen = getSupportFragmentManager();
        Fragment drawerFragment = fragMen.findFragmentByTag(getResources().getString(R.string.PSF));
        if(drawerFragment!=null) {
            ImageButton bt = (ImageButton) findViewById(R.id.search_button);
            bt.setVisibility(View.VISIBLE);
            ((DrawerFragment)drawerFragment).terminateFragment();
            //fragMen.beginTransaction().remove(drawerFragment).commit();
        }

        if(slidingDrawer!=null){
            slidingDrawer.animateClose();
        }

        Log.d("Routes", ((App) getApplicationContext()).getLocalRepository().getAllRoutes().size() + "");

        if(currentRouteId == -1)
        {
            currentRouteId = sharedPref.getInt(getResources().getString(R.string.current_route), -1);
        }

        if(currentRouteId != -1)
        {
            if(isNetworkAvailable()) {
                mapManager.clearMap();
                mapManager.showRoute(currentRouteId, walking);
            }
            else {
                Toast.makeText(getApplicationContext(), "Brak połączenia z internetem", Toast.LENGTH_SHORT);
            }
        }
    }

    public void showPlaceInPopup(final int placeId, boolean opened)
    {
        if(currentSliderFragment!=null) getSupportFragmentManager().beginTransaction().remove(currentSliderFragment).commit();

        editor.putInt(getResources().getString(R.string.current_marker), placeId);
        editor.commit();
        View C = findViewById(R.id.search_button);
        C.setVisibility(View.INVISIBLE);
        Place place = App.LOCALREP.getPlaceDetails(placeId);
        PlaceFragment toLoad = new PlaceFragment(this);
        //if (currentSliderFragment != null)
        //getSupportFragmentManager().beginTransaction().remove(currentSliderFragment).commit();
        DrawerFragment drawerFrag = new DrawerFragment();
        drawerFrag.setOpened(opened);
        currentSliderFragment = drawerFrag;
        FragmentManager fragmentManager = getSupportFragmentManager();
        drawerFrag.setFragemntstoLoad(toLoad, place.name);
        fragmentManager.beginTransaction()
                .replace(R.id.container, drawerFrag, getResources().getString(R.string.PSF))
                .commit();


        /*fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, getResources().getString(R.string.PSF))
                .commit();
                */
    }

    @Override
    public void onMyLocationChange(Location location) {
        if(sharedPref.getBoolean("needToResume", false)) {
            onResume();
            editor.putBoolean("needToResume", false);
            editor.commit();
        }
        currentLocation = location;
        Place.setCurrentLocation(location);
        if(needToOpenRadar) {
            Log.d("Radar", "Need to open");
            RadarManager.isRadarBlocked = false;

//            if (chosenTypes.size() == Place.AllPlacesTypes.size()) {
            if (chosenTypes.size() == Place.AllPlacesTypes.size() || (chosenTypes == null || chosenTypes.size() == 0) && (choosenCategories == null || choosenCategories.size() == 0)) {
                    sharedPref.edit().putBoolean("filterPlaces", false).commit();
                    mapManager.onMyLocationChange(location);
            } else {
                    sharedPref.edit().putBoolean("filterPlaces", true).commit();
                    mapManager.onMyLocationChange(location);
//                    mapManager.changeLocation(location, chosenTypes, choosenCategories);
                    Log.d("Radar", "get Places with filters");
            }
        } else {
            Log.d("Radar", "Do not open");
            RadarManager.isRadarBlocked = true;
        }
    }


            @Override
            public void onFragmentInteraction(String id) {

            }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void showEvent(int id)
    {
        Place placeToOpen = ((App)getApplicationContext()).getLocalRepository().getPlaceDetails(id);
        mapManager.drawMarker(map, placeToOpen, true, false);


        showPlaceInPopup(placeToOpen.id, false);
    }

    public void getChosenTypes() {
        String chosenTypesIdsString = sharedPref.getString("chosenTypes", null);
        chosenTypes = new ArrayList<>();
        if(chosenTypesIdsString != null) {
            String[] chosenTypesIdsStringArray = chosenTypesIdsString.split(",");
            for (int i = 0; i < chosenTypesIdsStringArray.length; i++) {
                if (isInteger(chosenTypesIdsStringArray[i])) {
                    for (Type type : Place.AllPlacesTypes) {
                        if (type.id == Integer.parseInt(chosenTypesIdsStringArray[i])) {
                            chosenTypes.add(type);
                        }
                    }
                }
            }
        }
    }

    public void saveCheckedTypes() {
        String checkedTypesIdsString = "";
        for(Type type : chosenTypes) {
            checkedTypesIdsString += type.id + ",";
        }
        sharedPref.edit().putString("chosenTypes", checkedTypesIdsString).commit();
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



    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
