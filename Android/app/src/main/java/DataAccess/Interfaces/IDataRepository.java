package DataAccess.Interfaces;

import android.content.Context;
import android.graphics.PointF;

import Models.Event;
import Models.ObjectBase;
import Models.Place;
import Models.Route;
import Models.SearchCriteria;

public interface IDataRepository
{
	public Place GetPlaceDetails(int id);

	public Event GetEventDetails(int id);

	public Route GetRouteDetails(Route route);

	public Iterable<Event> GetNearbyObjects(PointF location, float distanceInMetres, SearchCriteria criteria);

	public Iterable<ObjectBase> SearchFor(SearchCriteria criteria);

	public boolean IsAvailable();

	public void Update(ObjectBase obj);

	public void Init(Context context);
}
