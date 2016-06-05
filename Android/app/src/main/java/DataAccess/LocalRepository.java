package DataAccess;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import DataAccess.Interfaces.IDataRepository;
import Models.Event;
import Models.ObjectBase;
import Models.Place;
import Models.Route;
import Models.SearchCriteria;

public class LocalRepository implements IDataRepository
{
	SQLiteHelper sqLiteHelper;
	Context context;

    public LocalRepository(Context context) {
        Init(context);
        sqLiteHelper = new SQLiteHelper(context);
        sqLiteHelper.onUpgrade(sqLiteHelper.getDB(), 1, 1);
        sqLiteHelper.fillTypes(sqLiteHelper.getDB());
    }

	public void Init(Context context)
	{
		this.context = context;
	}

	public Place GetPlaceDetails(int id)
	{
		Place place;
		OpenConnection();

		place = sqLiteHelper.GetPlaceWithID(id);

		CloseConnection();
		return place;
	}

	public Event GetEventDetails(int id)
	{
        Event event;
        OpenConnection();

        event = sqLiteHelper.GetEventWithID(id);

        CloseConnection();
        return event;
	}

	public Route GetRouteDetails(Route route)
	{
		// TODO implement sqlite getRouteDetails
		return null;
	}

	public Iterable<Event> GetNearbyObjects(PointF location, float distanceInMetres, SearchCriteria criteria)
	{
		// TODO implement sqlite getNearbyObjects
		return null;
	}

    public List<Place> GetNearbyPlaces(LatLng location, float distanceInMetres, SearchCriteria criteria) {
        List<Place> places;
        double distanceInDeg = distanceInMetres / 110574;
        float latMax = (float) (location.latitude + distanceInDeg);
        float latMin = (float) (location.latitude - distanceInDeg);
        float longMax = (float) (location.longitude + distanceInDeg);
        float longMin = (float) (location.longitude - distanceInDeg);

        OpenConnection();

        places = sqLiteHelper.GetPlacesWithLocation(latMax,latMin,longMax,longMin);

        CloseConnection();
        return places;
    }

    public List<Event> GetNearbyEvents(LatLng location, float distanceInMetres, SearchCriteria criteria) {
        List<Event> events;
        double distanceInDeg = distanceInMetres / 110574;
        float latMax = (float) (location.latitude + distanceInDeg);
        float latMin = (float) (location.latitude - distanceInDeg);
        float longMax = (float) (location.longitude + distanceInDeg);
        float longMin = (float) (location.longitude - distanceInDeg);

        OpenConnection();

        events = sqLiteHelper.GetEventsWithLocation(latMax, latMin, longMax, longMin);

        CloseConnection();
        return events;
    }

    public List<String> getPlacesTypes() {
        List<String> temp = sqLiteHelper.GetPlacesTypes();
        return temp;
    }

	public Iterable<ObjectBase> SearchFor(SearchCriteria criteria)
	{
		// TODO implement sqlite searchFor
		return null;
	}

    public void insertEvent(Event event) {
        sqLiteHelper.insertEvent(event);
    }

    public void insertPlace(Place place) {
        sqLiteHelper.insertPlace(place);
    }

    public void insertRoute(Route route) {
        sqLiteHelper.insertRoute(route );
    }

    public Route getRouteById(int id){
        return sqLiteHelper.GetRouteWithID(id);
    }

    public List<Route> getAllRoutes(){
        return sqLiteHelper.getAllRoutes();
    }

	public boolean IsAvailable()
	{
		return sqLiteHelper != null;
	}

	public void Update(ObjectBase obj)
	{
		// TODO update sqlitedata
	}

	private void OpenConnection()
	{
		sqLiteHelper = new SQLiteHelper(context);
	}

	private void CloseConnection()
	{
		sqLiteHelper.close();
	}
}
