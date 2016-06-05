package com.guide.przewo.DataAccess;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.reflect.TypeToken;
import com.guide.przewo.DataAccess.Interfaces.IDataRepository;
import com.guide.przewo.MainActivity;
import com.guide.przewo.Models.Category;
import com.guide.przewo.Models.Event;
import com.guide.przewo.Models.ObjectFactory;
import com.guide.przewo.Models.Place;
import com.guide.przewo.Models.Route;
import com.guide.przewo.Models.SearchCriteria;
import com.guide.przewo.Models.SearchResult;
import com.guide.przewo.Models.Transport;
import com.guide.przewo.Models.Type;
import com.guide.przewo.R;
import com.guide.przewo.TransportManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

public class App extends Application implements IDataRepository
{
	private final static int outDateTimeInHours = 5;
    public static LocalRepository LOCALREP;
	private LocalRepository localRepository;
	SharedPreferences sharedPref;

	public App()
	{
	}

	public void onCreate()
	{
		localRepository = new LocalRepository(getApplicationContext());
        LOCALREP = localRepository;
		sharedPref = getSharedPreferences(getResources().getString(R.string.main_store), 0);

		updatePlacesFromServer();
		updateEventsFromServer();
		updateRoutesFromServer();
		updateTypesFromServer();
		updateCategoriesFromServer();
	}

	private void updatePlacesFromServer()
	{
		NodeAccess nodeAccess;
		nodeAccess = new NodeAccess("http://piwko.usermd.net/api");
		Log.d("Places update", "Start");

		nodeAccess.get("/places/update/" + getLastUpdateDate("places"), null, new JsonHttpResponseHandler()
		{
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				ObjectFactory objectFactory = new ObjectFactory();
				List<Place> updatedPlaces = (List<Place>) objectFactory.<Place>getObjectFromJson(response, new TypeToken<List<Place>>()
				{
				}.getType());
				localRepository.addOrUpdatePlaces(updatedPlaces);
				sharedPref.edit().putString("lastPlacesUpdate", getCurrentTimestamp());
				Log.d("Places updated", "Added " + updatedPlaces.size());
			}
		});
	}

	private void updateEventsFromServer()
	{
		NodeAccess nodeAccess;
		nodeAccess = new NodeAccess("http://piwko.usermd.net/api");
		Log.d("Events update", "Start");

		nodeAccess.get("/events/update/" + getLastUpdateDate("events"), null, new JsonHttpResponseHandler()
		{
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				ObjectFactory objectFactory = new ObjectFactory();
				List<Event> updatedEvents = (List<Event>) objectFactory.<Event>getObjectFromJson(response, new TypeToken<List<Event>>() {}.getType());
				localRepository.addOrUpdateEvents(updatedEvents);
				sharedPref.edit().putString("lastEventsUpdate", getCurrentTimestamp());
				Log.d("Events updated",  "Added " + updatedEvents.size());
			}
		});
	}

	private void updateRoutesFromServer()
	{
		NodeAccess nodeAccess;
		nodeAccess = new NodeAccess("http://piwko.usermd.net/api");
		Log.d("Routes update", "Start");

		nodeAccess.get("/routes/update/" + getLastUpdateDate("routes"), null, new JsonHttpResponseHandler()
		{
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				ObjectFactory objectFactory = new ObjectFactory();
				List<Route> updatedRoutes = (List<Route>) objectFactory.<Route>getObjectFromJson(response, new TypeToken<List<Route>>() {}.getType());
				localRepository.addOrUpdateRoutes(updatedRoutes);
				sharedPref.edit().putString("lastRoutesUpdate", getCurrentTimestamp());
				Log.d("Routes updated", "Added " + updatedRoutes.size());

				Route.AllRoutes = localRepository.getAllRoutes();
			}
		});
	}

	private void updateTypesFromServer()
	{
		NodeAccess nodeAccess;
		nodeAccess = new NodeAccess("http://piwko.usermd.net/api");
		Log.d("Types update", "Start");

		nodeAccess.get("/types/update/" + getLastUpdateDate("types"), null, new JsonHttpResponseHandler()
		{
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				ObjectFactory objectFactory = new ObjectFactory();
				List<Type> updatedTypes = (List<Type>) objectFactory.<Type>getObjectFromJson(response, new TypeToken<List<Type>>() {}.getType());
				localRepository.addOrUpdateTypes(updatedTypes);
				sharedPref.edit().putString("lastTypesUpdate", getCurrentTimestamp());
				Log.d("Types updated",  "Added " + updatedTypes.size());

				Place.AllPlacesTypes = localRepository.getAllPlacesTypes();
			}
		});
	}

	private void updateCategoriesFromServer()
	{
		NodeAccess nodeAccess;
		nodeAccess = new NodeAccess("http://piwko.usermd.net/api");
		Log.d("Categories update", "Start");

		nodeAccess.get("/categories/update/" + getLastUpdateDate("categories"), null, new JsonHttpResponseHandler()
		{
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				ObjectFactory objectFactory = new ObjectFactory();
				List<Category> updatedCategories = (List<Category>) objectFactory.<Category>getObjectFromJson(response, new TypeToken<List<Category>>() {}.getType());
				localRepository.addOrUpdateEventCategories(updatedCategories);
				sharedPref.edit().putString("lastCategoriesUpdate", getCurrentTimestamp());
				Log.d("Categories updated",  "Added " + updatedCategories.size());

				Event.AllEventCategories = localRepository.getAllEventCategories();
			}
		});
	}

	public Place getPlaceDetails(int id)
	{
		Place result = localRepository.getPlaceDetails(id);

		return result;
	}

	@Override
	public Event getEventDetails(int id)
	{
		return localRepository.getEventDetails(id);
	}

	@Override
	public Route getRouteDetails(int id)
	{
		return localRepository.getRouteDetails(id);
	}

	@Override
	public Iterable<Place> getNearbyPlaces(LatLng location, float distanceInMetres, SearchCriteria criteria)
	{
		return localRepository.getNearbyPlaces(location, distanceInMetres, criteria);
	}

	public Collection<SearchResult> searchFor(SearchCriteria criteria)
	{
		return localRepository.searchFor(criteria);
	}

	public List<Place> getDwarfs()
	{
		int dwarfPlaceType = 13;
		return localRepository.getPlacesOfType(dwarfPlaceType);
	}

	public List<Place> getLegends()
	{
		int legendPlaceType = 18;
		return localRepository.getPlacesOfType(legendPlaceType);
	}

	public class PlaceDetailsUIUpdate extends AsyncTask<String, Integer, HttpResponse>
	{
		private Context context;
		private IDataRepository dataRepository;

		public PlaceDetailsUIUpdate(Context context, IDataRepository dataRepository)
		{
			this.context = context;
			this.dataRepository = dataRepository;
		}

		protected HttpResponse doInBackground(String... params)
		{
			dataRepository.getPlaceDetails(Integer.parseInt(params[0]));

			return null;
		}

		protected void onPostExecute(Long result)
		{

		}
	}

	private String getLastUpdateDate(String dataType)
	{
		// update default value after each app version release
		String defaultDate = "2015-01-29%2004:00:44";

		String lastUpdateDate;
		switch (dataType)
		{
			case "places":
				lastUpdateDate = sharedPref.getString("lastPlacesUpdate", defaultDate);
				break;
			case "events":
				lastUpdateDate = sharedPref.getString("lastEventsUpdate", defaultDate);
				break;
			case "routes":
				lastUpdateDate = sharedPref.getString("lastRoutesUpdate", defaultDate);
				break;
			case "types":
				lastUpdateDate = sharedPref.getString("lastTypesUpdate", defaultDate);
				break;
			case "categories":
				lastUpdateDate = sharedPref.getString("lastCategoriesUpdate", defaultDate);
				break;
			default:
				lastUpdateDate = defaultDate;
		}

		return lastUpdateDate;
	}

	private String getCurrentTimestamp()
	{
		DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
		// TEST:
		String timestamp = "2015-01-29%2004:00:44";
		// CORRECT:
		//String timestamp = DateTime.now().toString(dtf);

		return timestamp;
	}

	public LocalRepository getLocalRepository()
	{
		return localRepository;
	}
}
