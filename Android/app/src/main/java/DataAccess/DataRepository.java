package DataAccess;

import android.content.Context;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;

import java.util.Date;

import DataAccess.Interfaces.IDataRepository;
import Models.Event;
import Models.ObjectBase;
import Models.Place;
import Models.Route;
import Models.SearchCriteria;

public class DataRepository implements IDataRepository
{
	private final static int outDateTimeInHours = 5;
	private IDataRepository localRepository;
	private IDataRepository onlineRepository;

	public DataRepository(IDataRepository localRepository, IDataRepository onlineRepository, Context context)
	{
		this.localRepository = localRepository;
		this.onlineRepository = onlineRepository;

		Init(context);
	}

	private static boolean IsOutDated(ObjectBase obj)
	{
		Date now = new Date();
		long difference = now.getTime() - obj.GetChangeDate().getTime();

		return difference > (1000 * 60 * 60 * outDateTimeInHours);
	}

	public void Init(Context context)
	{
		localRepository.Init(context);
		onlineRepository.Init(context);
	}

	public Place GetPlaceDetails(int id)
	{
		Log.d("Get place details: ", "id " + id);
		Place result = localRepository.GetPlaceDetails(id);
		if ((result == null || IsOutDated(result)) && onlineRepository.IsAvailable())
		{
			Log.d("Update", "id " + id);
			localRepository.Update(onlineRepository.GetPlaceDetails(id));
		}

		Log.d("Update", "id " + id);
		return localRepository.GetPlaceDetails(id);
	}

	public Event GetEventDetails(int id)
	{
		return null;
	}

	public Route GetRouteDetails(Route route)
	{
		return null;
	}

	public Iterable<Event> GetNearbyObjects(PointF location, float distanceInMetres, SearchCriteria criteria)
	{
		return null;
	}

	public Iterable<ObjectBase> SearchFor(SearchCriteria criteria)
	{
		return null;
	}

	public boolean IsAvailable()
	{
		return onlineRepository.IsAvailable();
	}

	public void Update(ObjectBase obj)
	{
		localRepository.Update(obj);
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
			dataRepository.GetPlaceDetails(Integer.parseInt(params[0]));

			return null;
		}

		protected void onPostExecute(Long result)
		{

		}

	}
}
