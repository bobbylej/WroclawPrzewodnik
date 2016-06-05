package DataAccess;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import DataAccess.Interfaces.IDataRepository;
import Models.Event;
import Models.ObjectBase;
import Models.Place;
import Models.Route;
import Models.SearchCriteria;
import Models.Type;

public class OnlineRepository implements IDataRepository
{
	private final int healthCheckTimeInMinutes = 3;
	private Date availablityCheckDate;
	private NodeAccess nodeAccess;
	private Context context;

	public OnlineRepository(String url)
	{
		nodeAccess = new NodeAccess(url);
	}

	public void Init(Context context)
	{
		this.context = context;
	}

	public Place GetPlaceDetails(int id)
	{
		final Place downloadedPlace = null;

		Log.d("Online", "id " + id);
		nodeAccess.get("/places/" + id, null, new JsonHttpResponseHandler()
		{
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				Log.d("Sukces", "status " + statusCode);
				Gson gson = new Gson();
				downloadedPlace.CopyFrom(gson.fromJson(response.toString(), Place.class));
			}
		});

		return downloadedPlace;
	}

	public Event GetEventDetails(int id)
	{
		// TODO implement online getEvent
		return null;
	}

	public Route GetRouteDetails(Route route)
	{
		// TODO implement online getRouteDetails
		return null;
	}

	public Iterable<Event> GetNearbyObjects(PointF location, float distanceInMetres, SearchCriteria criteria)
	{
		// TODO implement online getNearby
		return null;
	}

    public List<Type> GetPlacesTypes()
    {
        // TODO implement online GetPlacesTypes
        final List<Type> types = new ArrayList<Type>();

        nodeAccess.get("/types", null, new JsonHttpResponseHandler()
        {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                Gson gson = new Gson();
                Type t = (gson.fromJson(response.toString(), Type.class));
            }
        });

        return types;
        // Still TODO
    }

	public Iterable<ObjectBase> SearchFor(SearchCriteria criteria)
	{
		// TODO implement online searchFor
		return null;
	}

	public boolean IsAvailable()
	{
        Log.d("RESPONSE+++++++++++++++++++++++++++++++++++++++++++", "Available");
		if (availablityCheckDate != null)
		{
			Date now = new Date();
			long difference = now.getTime() - availablityCheckDate.getTime();
			if (difference < (1000 * 60 * healthCheckTimeInMinutes))
			{
				return true;
			}
		}

		nodeAccess.get("", null, new JsonHttpResponseHandler()
		{
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				Log.d("RESPONSE+++++++++++++++++++++++++++++++++++++++++++", response.toString());
			}
		});

		availablityCheckDate = new Date();
		return true;
	}

	public void Update(ObjectBase obj)
	{
		// nie uzywamy, ew. w przyszlosci do wrzucania czegos do bazy z poziomu telefonu(komentarze, edycja swojego miejsca)
	}
}
