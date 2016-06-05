package DataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import Models.Event;
import Models.Location;
import Models.Place;
import Models.Route;
import Models.Type;

public class SQLiteHelper extends SQLiteOpenHelper
{
	private static final String LOG = "SQLiteHelper";

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "piwko";

	private static final String TABLE_PLACES = "places";
	private static final String TABLE_EVENTS = "events";
    private static final String TABLE_TYPES = "types";
    private static final String TABLE_ROUTES = "routes";
    private static final String TABLE_ROUTE_PLACES = "routePlaces";
    private static final String TABLE_CATEGORIES = "categories";

	// COMMON TABLE NAMES
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";
	private static final String KEY_CITY = "city";
	private static final String KEY_STREET = "street";
	private static final String KEY_STATE = "state";
	private static final String KEY_URL = "url";
	private static final String KEY_VERSION = "version";
	private static final String KEY_CHANGE_DATE = "change_date";
	private static final String KEY_TYPES = "types";
    private static final String KEY_EVENTS_COUNT = "events_count";
	private static final String CREATE_TABLE_PLACES = "CREATE TABLE IF NOT EXISTS " + TABLE_PLACES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_LATITUDE + " REAL," + KEY_LONGITUDE + " REAL," + KEY_EVENTS_COUNT + " INTEGER," + KEY_CITY + " TEXT," + KEY_STREET + " TEXT," + KEY_STATE + " INTEGER," + KEY_URL + " TEXT," + KEY_VERSION + " INTEGER," + KEY_CHANGE_DATE + " INTEGER," + KEY_TYPES + " TEXT" + ")";
	// EVENTS TABLE NAMES
	private static final String KEY_PLACE = "place";
	private static final String KEY_START_DATE = "start_date";
	private static final String KEY_END_DATE = "end_date";
	private static final String KEY_START_TIME = "start_time";
	private static final String KEY_END_TIME = "end_time";
	private static final String CREATE_TABLE_EVENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_LATITUDE + " REAL," + KEY_LONGITUDE + " REAL," + KEY_STATE + " INTEGER," + KEY_URL + " TEXT," + KEY_VERSION + " INTEGER," + KEY_CHANGE_DATE + " INTEGER," + KEY_TYPES + " TEXT" + KEY_PLACE + " INTEGER," + KEY_START_DATE + " INTEGER," + KEY_END_DATE + " INTEGER," + KEY_START_TIME + " INTEGER," + KEY_END_TIME + " INTEGER," + KEY_PLACE + " INTEGER" + ")";
    // TYPES TABLE NAMES
    private static final String CREATE_TABLE_TYPES = "CREATE TABLE IF NOT EXISTS " + TABLE_TYPES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DESCRIPTION + " TEXT" + ")";

    // ROUTES TABLE NAMES
    private static final String CREATE_TABLE_ROUTES = "CREATE TABLE IF NOT EXISTS " + TABLE_ROUTES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_VERSION + " INTEGER" + ")";

    // ROUTE_PLACES TABLE NAMES
    private static final String KEY_ID_ROUTE = "id_route";
    private static final String KEY_PLACE_ORDER = "place_order";
    private static final String KEY_ID_PLACE = "id_place";
    private static final String KEY_BEFORE = "before";
    private static final String KEY_AFTER = "after";
    private static final String CREATE_TABLE_ROUTE_PLACES = "CREATE TABLE IF NOT EXISTS " + TABLE_ROUTE_PLACES + "(" + KEY_ID_ROUTE + " INTEGER," + KEY_PLACE_ORDER + " INTEGER," + KEY_ID_PLACE + " INTEGER," + KEY_BEFORE + " TEXT," + KEY_AFTER + " TEXT," + " PRIMARY KEY ( "+KEY_ID_ROUTE +" , "+ KEY_PLACE_ORDER +" ))";

    public SQLiteHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

    public SQLiteDatabase getDB() {
        return this.getReadableDatabase();
    }

	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(CREATE_TABLE_PLACES);
		db.execSQL(CREATE_TABLE_EVENTS);
        db.execSQL(CREATE_TABLE_TYPES);
        db.execSQL(CREATE_TABLE_ROUTES);
        db.execSQL(CREATE_TABLE_ROUTE_PLACES);
        Log.e(LOG, "CREATED TABLES ////////////////////////////");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TYPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTE_PLACES);
		onCreate(db);
	}
    
    public void fillTypes(SQLiteDatabase db) {
        List<String> stringTypes = Arrays.asList("Restauracja", "Muzeum", "Natura", "Centrum handlowe", "Teatr", "Kino", "_", "Obiekt sportowy", "Inne miejsca", "Kultura", "Obiekt sakralny");
        for(int i=0; i<stringTypes.size(); i++) {
            if(!exists(TABLE_TYPES, KEY_NAME, stringTypes.get(i))) {
                insertType(new Type(i+1, stringTypes.get(i), ""));
                //Log.d("TYPES EXISTS", exists(TABLE_TYPES, KEY_NAME, stringTypes.get(i)) + " checkcheckcheckcheckcheckcheckcheck");
            }
        }
    }

	public Place GetPlaceWithID(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_PLACES + " WHERE " + KEY_ID + " = " + id;

		Log.e(LOG, selectQuery);
		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null && c.moveToFirst())
		{

            String name = c.getString(c.getColumnIndex(KEY_NAME));
            String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
            String url = c.getString(c.getColumnIndex(KEY_URL));
            int version = c.getInt(c.getColumnIndex(KEY_VERSION));
            // TODO co to

//            String stringTypes = c.getString(c.getColumnIndex(KEY_TYPES));
//
//            String[] separateTypes = stringTypes.split(",");
//            List<Type> types = new ArrayList<Type>();
//            for(int i = 0; i < stringTypes.length; i++)
//            {
//                types.add(new Type(i+1, separateTypes[i], "asd"));
//                Log.e("Type:", separateTypes[i]);
//            }

            List<String> stringTypes = Arrays.asList(c.getString(c.getColumnIndex(KEY_TYPES)));

            List<Type> types = new ArrayList<Type>();

            for(int i = 0; i < stringTypes.size(); i++)
            {
                Type type = GetTypeWithName(stringTypes.get(i));
                types.add(type);
//                types.add(new Type(i, stringTypes.get(i), "desc"));
            }


            Date changeDate = new Date(c.getInt(c.getColumnIndex(KEY_CHANGE_DATE)));

            float longitude = c.getFloat(c.getColumnIndex(KEY_LONGITUDE));
            float latitude = c.getFloat(c.getColumnIndex(KEY_LATITUDE));
            String city = c.getString(c.getColumnIndex(KEY_CITY));
            String street = c.getString(c.getColumnIndex(KEY_STREET));

            Location location = new Location(latitude, longitude, city, street);

            Place place = new Place(id, name, description, url, version, types, changeDate, location);

            return place;
        }
        return null;
	}

    public Route GetRouteWithID(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ROUTES + " WHERE " + KEY_ID + " = " + id;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            String name = c.getString(c.getColumnIndex(KEY_NAME));
            String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
            int version = c.getInt(c.getColumnIndex(KEY_VERSION));


            selectQuery = "SELECT  * FROM " + TABLE_ROUTE_PLACES + " WHERE " + KEY_ID_ROUTE + " = " + id;

            Log.e(LOG, selectQuery);
            c = db.rawQuery(selectQuery, null);
            List<Place> places = new ArrayList<>();
            int placeOrder;
            int placeId;
            Place place;
            c.moveToFirst();
            while (c.isAfterLast() == false) {
                placeOrder = c.getInt(c.getColumnIndex(KEY_PLACE_ORDER));
                placeId = c.getInt(c.getColumnIndex(KEY_ID_PLACE));
                place = this.GetPlaceWithID(placeId);
                places.add(placeOrder,place);
                c.moveToNext();
            }

            Route route = new Route(id,name,description,places,version);
            return route;
        }

        return null;
    }

    public List<Route> getAllRoutes(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Route> routes = new ArrayList<Route>();
        String selectQuery = "SELECT  * FROM " + TABLE_ROUTES;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        int routeId;
        Route route;
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            routeId = c.getInt(c.getColumnIndex(KEY_ID));
            route = this.GetRouteWithID(routeId);
            routes.add(route);
            Log.e("Dodano", ""+routeId);
            c.moveToNext();
        }

        return routes;
    }

    public List<String> GetPlacesTypes()
    {
        List<String> types = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_TYPES;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
//            c.moveToFirst();
            while(c.moveToNext()) {
                String name = c.getString(c.getColumnIndex(KEY_NAME));

                types.add(name);
            }
        }

        return types;
    }

    public List<Place> GetPlacesWithLocation(float latMax, float latMin, float longMax, float longMin)
    {
        List<Place> places = new ArrayList<Place>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PLACES + " WHERE " + KEY_LATITUDE + " > " + latMin
                + " and " + KEY_LATITUDE + " < " + latMax + " and " +  KEY_LONGITUDE+ " > " + longMin
                + " and " +  KEY_LONGITUDE+ " < " + longMax;
//        String selectQuery = "SELECT  * FROM " + TABLE_PLACES;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            while(c.moveToNext()) {
                int id = c.getInt(c.getColumnIndex(KEY_ID));
                String name = c.getString(c.getColumnIndex(KEY_NAME));
                String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
                String url = c.getString(c.getColumnIndex(KEY_URL));
                int version = c.getInt(c.getColumnIndex(KEY_VERSION));

                // Lista typów jako lista stringów, później zmieniona w liste obiektów Type
                List<String> stringTypes = Arrays.asList(c.getString(c.getColumnIndex(KEY_TYPES)));

                Log.e(LOG, c.getString(c.getColumnIndex(KEY_TYPES)) + " =+++++AAAAAAAAAAAAAAAAAAAAAAAA  " + stringTypes.size());

                List<Type> types = new ArrayList<Type>();

                for(int i = 0; i < stringTypes.size(); i++)
                {
                    Type type = GetTypeWithName(stringTypes.get(i));
                    if(type != null) {
                        types.add(type);
                        Log.d("PLACETYPE", stringTypes.get(i) + "===================" + type.id);
                    }
//                    types.add(new Type(i, stringTypes.get(i), "desc"));
                }
                if(types.size() <= 0 ) {
                    types.add(new Type(9, "Inne miejsca", ""));
                }

                Date changeDate = new Date(c.getInt(c.getColumnIndex(KEY_CHANGE_DATE)));

                float longitude = c.getFloat(c.getColumnIndex(KEY_LONGITUDE));
                float latitude = c.getFloat(c.getColumnIndex(KEY_LATITUDE));
                String city = c.getString(c.getColumnIndex(KEY_CITY));
                String street = c.getString(c.getColumnIndex(KEY_STREET));

                Location location = new Location(longitude, latitude, city, street);
                Log.d("SQLITE", latitude + " " + longitude);

                Place place = new Place(id, name, description, url, version, types, changeDate, location);
                places.add(place);
            }
        }

        return places;
    }

    public int CountEventsInPlace(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  COUNT(*) FROM " + TABLE_EVENTS + " WHERE " + KEY_PLACE + " = " + id;

        int eventsAmount = (int) DatabaseUtils.longForQuery(db, selectQuery, null);
        return eventsAmount;
    }

    public Event GetEventWithID(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS + " WHERE " + KEY_ID + " = " + id;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst())
        {

            String name = c.getString(c.getColumnIndex(KEY_NAME));
            String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
            String url = c.getString(c.getColumnIndex(KEY_URL));
            int version = c.getInt(c.getColumnIndex(KEY_VERSION));

            // Lista typów jako lista stringów, później zmieniona w liste obiektów Type
            List<String> stringTypes = Arrays.asList(c.getString(c.getColumnIndex(KEY_TYPES)));

            List<Type> types = new ArrayList<Type>();

            for(int i = 0; i < stringTypes.size(); i++)
            {
                Type type = GetTypeWithName(stringTypes.get(i));
                types.add(type);
            }

            Date changeDate = new Date(c.getInt(c.getColumnIndex(KEY_CHANGE_DATE)));

            Date startDate = new Date(c.getInt(c.getColumnIndex(KEY_START_DATE)));
            Time startTime = new Time(c.getInt(c.getColumnIndex(KEY_START_TIME)));
            Date endDate = new Date(c.getInt(c.getColumnIndex(KEY_END_DATE)));
            Time endTime = new Time(c.getInt(c.getColumnIndex(KEY_END_TIME)));

            int placeId = c.getInt(c.getColumnIndex(KEY_PLACE));
            Place place = GetPlaceWithID(placeId);

            Event event = new Event(id, name, description, url, version, types, changeDate, startDate, endDate, startTime, endTime, place);

            return event;
        }
        return null;
    }

    public List<Event> GetEventsWithLocation(float latMax, float latMin, float longMax, float longMin)
    {
        List<Event> events = new ArrayList<Event>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS + " AS E WHERE " +
                "EXISTS (SELECT  * FROM " + TABLE_PLACES + " WHERE "
                + "E." + KEY_PLACE + " = " + TABLE_PLACES + "." + KEY_ID
                + " and " + TABLE_PLACES + "." + KEY_LATITUDE + " > " + latMin
                + " and " + TABLE_PLACES + "." + KEY_LATITUDE + " < " + latMax
                + " and " +  TABLE_PLACES + "." + KEY_LONGITUDE+ " > " + longMin
                + " and " +  TABLE_PLACES + "." + KEY_LONGITUDE+ " < " + longMax + ")";

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
//            c.moveToFirst();
            while(c.moveToNext()) {
                int id = c.getInt(c.getColumnIndex(KEY_ID));
                String name = c.getString(c.getColumnIndex(KEY_NAME));
                String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
                String url = c.getString(c.getColumnIndex(KEY_URL));
                int version = c.getInt(c.getColumnIndex(KEY_VERSION));

                // Lista typów jako lista stringów, później zmieniona w liste obiektów Type
                List<String> stringTypes = Arrays.asList(c.getString(c.getColumnIndex(KEY_TYPES)));

                List<Type> types = new ArrayList<Type>();

                for(int i = 0; i < stringTypes.size(); i++)
                {
                    types.add(new Type(i, stringTypes.get(i), "desc"));
                }

                Date changeDate = new Date(c.getInt(c.getColumnIndex(KEY_CHANGE_DATE)));

                Date startDate = new Date(c.getInt(c.getColumnIndex(KEY_START_DATE)));
                Time startTime = new Time(c.getInt(c.getColumnIndex(KEY_START_TIME)));
                Date endDate = new Date(c.getInt(c.getColumnIndex(KEY_END_DATE)));
                Time endTime = new Time(c.getInt(c.getColumnIndex(KEY_END_TIME)));

                int placeId = c.getInt(c.getColumnIndex(KEY_PLACE));
                Place place = GetPlaceWithID(placeId);

                Event event = new Event(id, name, description, url, version, types, changeDate, startDate, endDate, startTime, endTime, place);
                events.add(event);
            }
        }

        return events;
    }

    public Type GetTypeWithID(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_TYPES + " WHERE " + KEY_ID + " = " + id;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            String name = c.getString(c.getColumnIndex(KEY_NAME));
            String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));


            Type type = new Type(id,name,description);
            return type;
        }

        return null;
    }

    public Type GetTypeWithName(String name)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_TYPES + " WHERE " + KEY_NAME + " = '" + name + "'";

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            int id = c.getInt(c.getColumnIndex(KEY_ID));
            String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));


            Type type = new Type(id,name,description);
            return type;
        }

        return null;
    }

    public void insertEvent(Event event) {

        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        ContentValues initialValues = new ContentValues();

        // convert date to string
        String startDate = dateFormat.format(event.GetStartDate());
        String endDate = dateFormat.format(event.GetEndDate());

        String types = "";
        for(Type type : event.GetTypes()) {
            types += type.name +",";
        }
        types = types.substring(0, types.length()-1);
        Log.d("Types InsertEvent", types + " |asdbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");

        String insertQuery = "INSERT INTO " + TABLE_EVENTS + " (" + KEY_ID + ", " + KEY_NAME + ", "
                + KEY_DESCRIPTION + ", " + KEY_URL + ", " + KEY_VERSION + ", " + KEY_TYPES + ", "
                + KEY_CHANGE_DATE + ", " + KEY_START_DATE + ", " + KEY_END_DATE + ", "
                + KEY_START_TIME + ", " + KEY_END_TIME + ", " + KEY_PLACE + ") VALUES ("
                + event.GetID() + ", '" + event.GetName() + "', '" + event.GetDescription() + "', '"
                + event.GetLink() + "', " + event.GetVersion() + ", '" + types + "', "
                + dateFormat.format(new Date()) + ", " + startDate + ", " + endDate + ", "
                + event.GetStartTime().getTime() + ", " + event.GetEndTime().getTime() + ", "
                + event.GetPlace().GetID() + ");";

        Log.e(LOG, insertQuery);

        if(!exists(TABLE_EVENTS, KEY_ID, Integer.toString(event.GetID()))) {
            db.execSQL(insertQuery);
        }
    }

    public void insertPlace(Place place){

        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        ContentValues initialValues = new ContentValues();

        // convert date to string
        String changeDate = dateFormat.format(place.GetChangeDate());

        String types = "";
        for(Type type : place.GetTypes()) {
            types += type.name +",";
        }
        types = types.substring(0, types.length()-1);
        Log.d("Types InsertPlace", types + " |asdbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");

        String insertQuery = "INSERT INTO " + TABLE_PLACES + " (" + KEY_ID + ", " + KEY_NAME + ", "
                + KEY_DESCRIPTION + ", " + KEY_LATITUDE + ", " + KEY_LONGITUDE + ", " + KEY_EVENTS_COUNT + ", "
                + KEY_CITY + ", " + KEY_STREET + ", " + KEY_STATE + ", "
                + KEY_URL + ", " + KEY_VERSION + ", " + KEY_CHANGE_DATE + ", " + KEY_TYPES + ") VALUES ("
                + place.GetID() + ", '" + place.GetName() + "', '" + place.GetDescription() + "', "
                + place.GetLocation().GetPoint().latitude + ", " + place.GetLocation().GetPoint().longitude + ", " + place.getEventCount() + ", '"
                + place.GetLocation().GetCity() + "', '" + place.GetLocation().GetStreet() + "', " + place.GetState() + ", '"
                + place.GetLink() + "', " + place.GetVersion() + ", " + changeDate + ", '"
                + types + "');";

        Log.e(LOG, insertQuery);

//        String deleteQuery = "DELETE FROM " + TABLE_PLACES + " WHERE " + KEY_ID + " = " + place.id;
//        db.execSQL(deleteQuery);

        if(!exists(TABLE_PLACES, KEY_ID, Integer.toString(place.GetID()))) {
            db.execSQL(insertQuery);
        }
    }


    public void insertRoute(Route route){
        SQLiteDatabase db = this.getReadableDatabase();

        String insertQuery = "INSERT INTO " + TABLE_ROUTES + " (" + KEY_ID + ", " + KEY_NAME + ", "
                + KEY_DESCRIPTION + ", " + KEY_VERSION+ ") VALUES ("
                + route.GetID() + ", '" + route.GetName() + "', '" + route.GetDescription() + "', "
                + route.GetVersion() + ");";

        Log.e(LOG, insertQuery);

        if(!exists(TABLE_ROUTES, KEY_ID, Integer.toString(route.GetID()))) {
            db.execSQL(insertQuery);
        }

        int i = 0;
       for(Place place : route.GetAllPlaces()) {

            insertQuery = "INSERT INTO " + TABLE_ROUTE_PLACES+ " (" + KEY_ID_ROUTE + ", " + KEY_PLACE_ORDER + ", "
                    + KEY_ID_PLACE + ", " + KEY_BEFORE + ", " + KEY_AFTER+ ") VALUES ("
                    + route.GetID() + ", " + i + ", " + place.GetID() + ", '"
                    + "null" + "', '" + "null" + "');";
            db.execSQL(insertQuery);
            i++;
           Log.e(LOG, insertQuery);
        }

    }

    public void insertType(Type type){

        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("Types InsertType", type.name + " |asdbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");

        String insertQuery = "INSERT INTO " + TABLE_TYPES + " (" + KEY_ID + ", " + KEY_NAME + ") VALUES ("
                + type.id + ", '" + type.name + "');";

        Log.e(LOG, insertQuery);

        if(!exists(TABLE_PLACES, KEY_ID, type.name)) {
            db.execSQL(insertQuery);
        }
    }




    public boolean exists(String TableName, String dbfield, String fieldValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query;
        if(dbfield.equals("")) {
            Query = "SELECT 1 FROM " + TableName;
        }
        else {
            if(fieldValue.equals("")) {
                Query = "SELECT " + dbfield + " FROM " + TableName;
            }
            else {
                Query = "SELECT * FROM " + TableName + " WHERE " + dbfield + " = '" + fieldValue + "'";
            }
        }
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            //Log.d("EXISTS", "NOT " + TableName + " " + dbfield + " = " + fieldValue);
            return false;
        }
        //Log.d("EXISTS", "YES " + TableName + " " + dbfield + " = " + fieldValue);
        return true;
    }
}
