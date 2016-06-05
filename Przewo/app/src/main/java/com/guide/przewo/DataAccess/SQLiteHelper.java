package com.guide.przewo.DataAccess;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.guide.przewo.Models.Category;
import com.guide.przewo.Models.Event;
import com.guide.przewo.Models.ObjectType;
import com.guide.przewo.Models.Place;
import com.guide.przewo.Models.Route;
import com.guide.przewo.Models.SearchCriteria;
import com.guide.przewo.Models.SearchResult;
import com.guide.przewo.Models.Transport;
import com.guide.przewo.Models.Type;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper
{
	private static final String LOG = "SQLiteHelper";

	private static final int DATABASE_VERSION = 2;

	private static final String DATABASE_NAME = "piwko";

    private static final String TABLE_PLACES = "places";
    private static final String TABLE_PHOTOS = "photos";
	private static final String TABLE_EVENTS = "events";
    private static final String TABLE_TYPES = "types";
    private static final String TABLE_ROUTES = "routes";
    private static final String TABLE_ROUTE_PLACES = "routePlaces";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_PLACES_TYPES = "placesTypes";
    private static final String TABLE_EVENTS_CATEGORIES = "eventsCategories";
    private static final String TABLE_TRANSPORT = "transport";

    // COMMON COLUMNS
	private static final String KEY_NAME = "name";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_URL = "url";
    private static final String KEY_STATE = "state";

    // PLACES TABLE NAMES
    private static final String KEY_ID_PLACE = "id_place";
	private static final String KEY_CITY = "city";
	private static final String KEY_STREET = "street";
	private static final String KEY_TYPES = "types";
    private static final String KEY_EVENTS_COUNT = "events_count";
	private static final String CREATE_TABLE_PLACES = "CREATE TABLE IF NOT EXISTS " + TABLE_PLACES + "(" + KEY_ID_PLACE + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_LATITUDE + " REAL," + KEY_LONGITUDE + " REAL," + KEY_CITY + " TEXT," + KEY_STREET + " TEXT," + KEY_STATE + " INTEGER," + KEY_URL + " TEXT," + KEY_EVENTS_COUNT + " INTEGER" + ")";

    // PHOTOS TABLE NAMES
    private static final String KEY_ID_OBJECT = "id_object";
    private static final String KEY_OBJECT_TYPE = "object_type";
    private static final String CREATE_TABLE_PHOTOS = "CREATE TABLE IF NOT EXISTS " + TABLE_PHOTOS + "(" + KEY_ID_OBJECT + " INTEGER," + KEY_OBJECT_TYPE + " TEXT," + KEY_URL + " TEXT)";

    // EVENTS TABLE NAMES
    private static final String KEY_ID_EVENT = "id_event";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_END_TIME = "end_time";
    private static final String CREATE_TABLE_EVENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS + "(" + KEY_ID_EVENT + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_STATE + " INTEGER," + KEY_URL + " TEXT," + KEY_ID_PLACE + " INTEGER," + KEY_START_TIME + " TEXT," + KEY_END_TIME + " TEXT" + ")";

    // ROUTE_PLACES TABLE NAMES
    private static final String KEY_ID_ROUTE = "id_route";
    private static final String KEY_PLACE_ORDER = "place_order";
    private static final String KEY_BEFORE = "before";
    private static final String KEY_AFTER = "after";
    private static final String CREATE_TABLE_ROUTE_PLACES = "CREATE TABLE IF NOT EXISTS " + TABLE_ROUTE_PLACES + "(" + KEY_ID_ROUTE + " INTEGER," + KEY_ID_PLACE + " INTEGER," + KEY_PLACE_ORDER + " INTEGER," + KEY_BEFORE + " TEXT," + KEY_AFTER + " TEXT," + " PRIMARY KEY ( "+KEY_ID_ROUTE +" , "+ KEY_ID_PLACE +" ))";

    // ROUTES TABLE NAMES
    private static final String KEY_TOUR_TIME = "tour_time";
    private static final String CREATE_TABLE_ROUTES = "CREATE TABLE IF NOT EXISTS " + TABLE_ROUTES + "(" + KEY_ID_ROUTE + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DESCRIPTION + " TEXT,"  + KEY_URL + " TEXT, " + KEY_TOUR_TIME + " REAL)";

    // PLACE TYPES TABLE NAMES
    private static final String KEY_ID_TYPE = "id_type";
    private static final String CREATE_TABLE_PLACES_TYPES = "CREATE TABLE IF NOT EXISTS " + TABLE_PLACES_TYPES + "(" + KEY_ID_PLACE + " INTEGER," + KEY_ID_TYPE + " INTEGER," + " PRIMARY KEY ( "+KEY_ID_PLACE +" , "+ KEY_ID_TYPE +" ))";

    // TYPES TABLE NAMES
    private static final String CREATE_TABLE_TYPES = "CREATE TABLE IF NOT EXISTS " + TABLE_TYPES + "(" + KEY_ID_TYPE + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DESCRIPTION + " TEXT,"  + KEY_URL + " TEXT" + ")";

    // EVENTS CATEGORIES TABLE NAMES
    private static final String KEY_ID_CATEGORY = "id_category";
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORIES + " (" + KEY_ID_CATEGORY + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DESCRIPTION + " TEXT" + ")";

    private static final String CREATE_TABLE_EVENTS_CATEGORIES = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS_CATEGORIES + " (" + KEY_ID_EVENT + " INTEGER," + KEY_ID_CATEGORY + " INTEGER," + " PRIMARY KEY ( "+KEY_ID_EVENT +" , "+ KEY_ID_CATEGORY +" ))";

    // TRANSPORT
    private static final String CREATE_TABLE_TRANSPORT = "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSPORT + " ( name TEXT, type TEXT, x REAL, y REAL, k INTEGER)";


    private static final String PLACE_PHOTO_TOKEN = "P";
    private static final String EVENT_PHOTO_TOKEN = "E";

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
        db.execSQL(CREATE_TABLE_PHOTOS);
		db.execSQL(CREATE_TABLE_EVENTS);
        db.execSQL(CREATE_TABLE_TYPES);
        db.execSQL(CREATE_TABLE_PLACES_TYPES);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_EVENTS_CATEGORIES);
        db.execSQL(CREATE_TABLE_ROUTES);
        db.execSQL(CREATE_TABLE_ROUTE_PLACES);
        db.execSQL(CREATE_TABLE_TRANSPORT);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
        if (newVersion > oldVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TYPES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES_TYPES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS_CATEGORIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTE_PLACES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSPORT);

            onCreate(db);
        }
	}

    // #########################################################################
    // PLACE ###################################################################

    public void addOrUpdatePlace(Place place)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String existQuery =  getExistenceQuery(TABLE_PLACES, KEY_ID_PLACE, Integer.toString(place.id));
        String placeQuery = "";

        if(db.rawQuery(existQuery, null).getCount() == 0)
        {
            placeQuery = getInsertPlaceQuery(place);
        }
        else
        {
            placeQuery = getUpdatePlaceQuery(place);
        }

        Log.e("IOrUPlace", placeQuery);
        db.execSQL(placeQuery);

        String typeQuery = getClearPlaceTypesQuery(place);
        db.execSQL(typeQuery);

        if (place.typesIds != null)
        {
            for (int typeId : place.typesIds)
            {
                typeQuery = getAddPlaceTypeConnectionQuery(place, typeId);
                Log.e("InsertPlaceTypes", typeQuery);
                db.execSQL(typeQuery);
            }
        }

        String photosQuery = getClearPlacePhotosQuery(place);
        db.execSQL(photosQuery);

        if (place.photos != null)
        {
            for (String photo : place.photos)
            {
                photosQuery = getAddPlacePhotoQuery(place, photo);
                Log.e("InsertPlacePhoto", photosQuery);
                db.execSQL(photosQuery);
            }
        }

        db.close();
    }

    private String getInsertPlaceQuery(Place place)
    {
        String insertQuery = "INSERT INTO " + TABLE_PLACES + " (" + KEY_ID_PLACE + ", " + KEY_NAME + ", "
                + KEY_DESCRIPTION + ", " + KEY_LATITUDE + ", " + KEY_LONGITUDE + ", " + KEY_EVENTS_COUNT + ", "
                + KEY_CITY + ", " + KEY_STREET + ", " + KEY_STATE + ", "
                + KEY_URL +") VALUES ("
                + place.id + ", '" + place.name + "', '" + place.description + "', "
                + place.latitude + ", " + place.longitude + ", " + place.eventCount + ", '"
                + place.city + "', '" + place.street + "', '" + place.state + "', '"
                + place.url +"')";

        return insertQuery;
    }

    private String getUpdatePlaceQuery(Place place)
    {
        String updateQuery = "UPDATE "+ TABLE_PLACES + " SET " +
                KEY_NAME + " = '" + place.name + "', " +
                KEY_DESCRIPTION + " = '" + place.description + "', " +
                KEY_LATITUDE +  " = " + place.latitude + ", " +
                KEY_LONGITUDE +  " = " + place.longitude + ", " +
                KEY_EVENTS_COUNT +  " = " + place.eventCount + ", " +
                KEY_CITY +  " = '" + place.city + "', " +
                KEY_STREET +  " = '" + place.street + "', " +
                KEY_STATE +  " = '" + place.street + "', " +
                KEY_URL +  " = '" + place.url + "' " +
                " WHERE " + KEY_ID_PLACE + " = " + place.id;

        return updateQuery;
    }

    public Place getPlaceWithId(int id)
    {
        Place place = null;

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PLACES + " WHERE " + KEY_ID_PLACE + " = " + id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            String name = c.getString(c.getColumnIndex(KEY_NAME));
            String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
            String url = c.getString(c.getColumnIndex(KEY_URL));
            float longitude = c.getFloat(c.getColumnIndex(KEY_LONGITUDE));
            float latitude = c.getFloat(c.getColumnIndex(KEY_LATITUDE));
            String city = c.getString(c.getColumnIndex(KEY_CITY));
            String street = c.getString(c.getColumnIndex(KEY_STREET));
            int eventsCount = getUpcomingEventsForPlace(id);

            List<Integer> types = getTypesIdsOfPlace(id);
            List<String> photos = getPhotosOfPlace(id);

            place = new Place(id, name, description, url, types,photos, latitude, longitude, city, street, eventsCount);
        }

        c.close();
        db.close();
        return place;
    }

    public int getUpcomingEventsForPlace(int idPlace)
    {
        int eventCount = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS + " WHERE " + KEY_ID_PLACE + " = " + idPlace;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            while(c.moveToNext())
            {
                DateTime endTime = new DateTime(Long.parseLong(c.getString(c.getColumnIndex(KEY_END_TIME))));
                ///if (endTime.isAfter(new DateTime()))
                //{
                    eventCount++;
                //}
            }
            c.close();
        }
        db.close();

        return eventCount;
    }

    public List<Place> getAllPlaces()
    {
        List<Place> places = new ArrayList<Place>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PLACES;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            int i = 0;
            while(c.moveToNext()) {
                int id = c.getInt(c.getColumnIndex(KEY_ID_PLACE));
                String name = c.getString(c.getColumnIndex(KEY_NAME));
                String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
                String url = c.getString(c.getColumnIndex(KEY_URL));
                List<Integer> types = getTypesIdsOfPlace(id);
                List<String> photos = getPhotosOfPlace(id);

                float longitude = c.getFloat(c.getColumnIndex(KEY_LONGITUDE));
                float latitude = c.getFloat(c.getColumnIndex(KEY_LATITUDE));
                String city = c.getString(c.getColumnIndex(KEY_CITY));
                String street = c.getString(c.getColumnIndex(KEY_STREET));
                int eventsCount = getUpcomingEventsForPlace(id);

                Place place = new Place(id, name, description, url, types, photos, latitude, longitude, city, street, eventsCount);
                places.add(place);
            }
        }
        c.close();
        db.close();

        return places;
    }


    public List<Place> getPlacesContaining(String query)
    {
        List<Place> places = new ArrayList<Place>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PLACES + " WHERE " + KEY_NAME + " LIKE '%" + query +"%'";

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            int i = 0;
            while(c.moveToNext()) {
                int id = c.getInt(c.getColumnIndex(KEY_ID_PLACE));
                String name = c.getString(c.getColumnIndex(KEY_NAME));
                String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
                String url = c.getString(c.getColumnIndex(KEY_URL));
                List<Integer> types = getTypesIdsOfPlace(id);
                List<String> photos = getPhotosOfPlace(id);

                float longitude = c.getFloat(c.getColumnIndex(KEY_LONGITUDE));
                float latitude = c.getFloat(c.getColumnIndex(KEY_LATITUDE));
                String city = c.getString(c.getColumnIndex(KEY_CITY));
                String street = c.getString(c.getColumnIndex(KEY_STREET));
                int eventsCount = getUpcomingEventsForPlace(id);

                Place place = new Place(id, name, description, url, types, photos, latitude, longitude, city, street, eventsCount);
                places.add(place);
            }
        }
        c.close();
        db.close();

        return places;
    }

    public List<Place> getPlacesOfTypes(List<Integer> ids)
    {
        List<Place> places = new ArrayList<Place>();
        String idsWithComas = "";
        for(Integer id : ids)
        {
            idsWithComas += id + ",";
        }
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT p.* FROM " + TABLE_PLACES_TYPES + " t JOIN " + TABLE_PLACES + " p ON t." + KEY_ID_PLACE + " = p." + KEY_ID_PLACE + "  WHERE t." + KEY_ID_TYPE + " IN (" + idsWithComas.substring(0, idsWithComas.length() - 1) +")";

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            int i = 0;
            while(c.moveToNext()) {
                int id = c.getInt(c.getColumnIndex(KEY_ID_PLACE));
                String name = c.getString(c.getColumnIndex(KEY_NAME));
                String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
                String url = c.getString(c.getColumnIndex(KEY_URL));
                List<Integer> types = getTypesIdsOfPlace(id);
                List<String> photos = getPhotosOfPlace(id);

                float longitude = c.getFloat(c.getColumnIndex(KEY_LONGITUDE));
                float latitude = c.getFloat(c.getColumnIndex(KEY_LATITUDE));
                String city = c.getString(c.getColumnIndex(KEY_CITY));
                String street = c.getString(c.getColumnIndex(KEY_STREET));
                int eventsCount = getUpcomingEventsForPlace(id);

                Place place = new Place(id, name, description, url, types, photos, latitude, longitude, city, street, eventsCount);
                places.add(place);
            }
        }
        c.close();
        db.close();

        return places;
    }

    public List<Place> getPlacesWithFromRange(float latMax, float latMin, float longMax, float longMin)
    {
        List<Place> places = new ArrayList<Place>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PLACES + " WHERE " + KEY_LATITUDE + " > " + latMin
                + " and " + KEY_LATITUDE + " < " + latMax + " and " +  KEY_LONGITUDE+ " > " + longMin
                + " and " +  KEY_LONGITUDE+ " < " + longMax;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            int i = 0;
            while(c.moveToNext()) {
                int id = c.getInt(c.getColumnIndex(KEY_ID_PLACE));
                String name = c.getString(c.getColumnIndex(KEY_NAME));
                String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
                String url = c.getString(c.getColumnIndex(KEY_URL));
                List<Integer> types = getTypesIdsOfPlace(id);
                List<String> photos = getPhotosOfPlace(id);

                float longitude = c.getFloat(c.getColumnIndex(KEY_LONGITUDE));
                float latitude = c.getFloat(c.getColumnIndex(KEY_LATITUDE));
                String city = c.getString(c.getColumnIndex(KEY_CITY));
                String street = c.getString(c.getColumnIndex(KEY_STREET));
                int eventsCount = getUpcomingEventsForPlace(id);

                Place place = new Place(id, name, description, url, types, photos, latitude, longitude, city, street, eventsCount);
                places.add(place);
            }
        }
        c.close();
        db.close();

        return places;
    }

    public List<Place> getPlacesWithFromRange(float latMax, float latMin, float longMax, float longMin, List<Type> choosenTypes, List<Category> choosenCategories)
    {
        List<Place> places = new ArrayList<Place>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PLACES + " WHERE " + KEY_LATITUDE + " > " + latMin
                + " and " + KEY_LATITUDE + " < " + latMax + " and " +  KEY_LONGITUDE+ " > " + longMin
                + " and " +  KEY_LONGITUDE+ " < " + longMax;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            int i = 0;
            while(c.moveToNext()) {
                int id = c.getInt(c.getColumnIndex(KEY_ID_PLACE));
                String name = c.getString(c.getColumnIndex(KEY_NAME));
                String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
                String url = c.getString(c.getColumnIndex(KEY_URL));
                List<Integer> types = getTypesIdsOfPlace(id);
                List<String> photos = getPhotosOfPlace(id);

                float longitude = c.getFloat(c.getColumnIndex(KEY_LONGITUDE));
                float latitude = c.getFloat(c.getColumnIndex(KEY_LATITUDE));
                String city = c.getString(c.getColumnIndex(KEY_CITY));
                String street = c.getString(c.getColumnIndex(KEY_STREET));
                int eventsCount = getUpcomingEventsForPlace(id);

                Place place = new Place(id, name, description, url, types, photos, latitude, longitude, city, street, eventsCount);

                if(choosenTypes != null && choosenTypes.size() > 0) {
                    boolean include = false;
                    for (Type type : choosenTypes) {
                        for (Type placeType : place.types) {
                            if (type.id == placeType.id) {
                                include = true;
                                break;
                            }
                        }
                        if (include) {
                            break;
                        }
                    }
                    if (include) {
                        places.add(place);
                    }
                }
            }
        }
        c.close();
        db.close();

        return places;
    }

    public Place getRandomPlace()
    {
        Place place = null;

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PLACES + " ORDER BY RANDOM() LIMIT 1";

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            int id = c.getInt(c.getColumnIndex(KEY_ID_PLACE));
            String name = c.getString(c.getColumnIndex(KEY_NAME));
            String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
            String url = c.getString(c.getColumnIndex(KEY_URL));
            float longitude = c.getFloat(c.getColumnIndex(KEY_LONGITUDE));
            float latitude = c.getFloat(c.getColumnIndex(KEY_LATITUDE));
            String city = c.getString(c.getColumnIndex(KEY_CITY));
            String street = c.getString(c.getColumnIndex(KEY_STREET));
            int eventsCount = getUpcomingEventsForPlace(id);

            List<Integer> types = getTypesIdsOfPlace(id);
            List<String> photos = getPhotosOfPlace(id);

            place = new Place(id, name, description, url, types, photos, latitude, longitude, city, street, eventsCount);
        }

        c.close();
        db.close();
        return place;
    }

    // #########################################################################
    // EVENT ###################################################################

    public void addOrUpdateEvent(Event event)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String existQuery =  getExistenceQuery(TABLE_EVENTS, KEY_ID_EVENT, Integer.toString(event.id));
        String eventQuery = "";

        if (db.rawQuery(existQuery, null).getCount() == 0)
        {
            eventQuery = getInsertEventQuery(event);
        }
        else
        {
            eventQuery = getUpdateEventQuery(event);
        }


        db.execSQL(eventQuery);

        String typeQuery = getClearEventCategoriesQuery(event);
        db.execSQL(typeQuery);

        if (event.categoriesIds != null)
        {
            for(int typeId : event.categoriesIds)
            {
                typeQuery = getAddEventCategoryConnectionQuery(event, typeId);
                db.execSQL(typeQuery);
            }

        }

        String photosQuery = getClearEventPhotosQuery(event);
        db.execSQL(photosQuery);

        if (event.photos != null)
        {
            for(String url : event.photos)
            {
                photosQuery = getAddEventPhotoQuery(event, url);
                Log.e("InsertEventPhoto", photosQuery);
                db.execSQL(photosQuery);
            }

            db.close();
        }
    }

    public String getInsertEventQuery(Event event) {

        String insertQuery = "INSERT INTO " + TABLE_EVENTS + " (" + KEY_ID_EVENT + ", " + KEY_NAME + ", "
                + KEY_DESCRIPTION + ", " + KEY_URL + ", "
                + KEY_START_TIME + ", " + KEY_END_TIME + ", " + KEY_ID_PLACE + ") VALUES ("
                + event.id + ", '" + event.name + "', '" + event.description + "', '"
                + event.url + "', '"
                + event.startTime.getMillis() + "','" + event.endTime.getMillis() + "', " + event.idPlace + ");";

        return insertQuery;
    }

    public String getUpdateEventQuery(Event event)
    {
        String updateQuery = "UPDATE "+ TABLE_EVENTS + " SET " +
                KEY_NAME + " = '" + event.name + "', " +
                KEY_DESCRIPTION + " = '" + event.description + "', " +
                KEY_URL + " = '" + event.url + "', " +
                KEY_START_TIME + " = '" + event.startTime.getMillis() + "', " +
                KEY_END_TIME + " = '" + event.endTime.getMillis() + "', " +
                KEY_ID_PLACE + " = " + event.idPlace + ", " +
                KEY_STATE + " = '" + event.state + "'" +
                " WHERE " + KEY_ID_EVENT + " = " + event.id;

        return updateQuery;
    }

    public Event getEventWithId(int id)
    {
        Event event = null;

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS + " WHERE " + KEY_ID_EVENT + " = " + id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst())
        {
            String name = c.getString(c.getColumnIndex(KEY_NAME));
            String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
            String url = c.getString(c.getColumnIndex(KEY_URL));
            DateTime startTime = new DateTime(Long.parseLong(c.getString(c.getColumnIndex(KEY_START_TIME))));
            DateTime endTime = new DateTime(Long.parseLong(c.getString(c.getColumnIndex(KEY_END_TIME))));
            int placeId = c.getInt(c.getColumnIndex(KEY_ID_PLACE));

            List<Integer> categories = getCategoriesIdsOfEvent(id);
            List<String> photos = getPhotosOfEvent(id);

            event = new Event(id, name, description, url, categories, photos, startTime, endTime, placeId);
        }

        c.close();
        db.close();

        return event;
    }

    public List<Event> getEventsOfCategories(List<Integer> ids)
    {
        List<Event> events = new ArrayList<Event>();
        String idsWithComas = "";
        for (Integer id : ids)
        {
            idsWithComas += id + ",";
        }
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT e.* FROM " + TABLE_EVENTS_CATEGORIES + " c JOIN " + TABLE_EVENTS + " e ON c." + KEY_ID_EVENT + " = e." + KEY_ID_EVENT + "  WHERE c." + KEY_ID_CATEGORY + " IN (" + idsWithComas.substring(0, idsWithComas.length() - 1) + ")";

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            while(c.moveToNext()) {
                int eventId = c.getInt(c.getColumnIndex(KEY_ID_EVENT));
                int placeId = c.getInt(c.getColumnIndex(KEY_ID_PLACE));
                String name = c.getString(c.getColumnIndex(KEY_NAME));
                String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
                String url = c.getString(c.getColumnIndex(KEY_URL));
                DateTime startTime = new DateTime(Long.parseLong(c.getString(c.getColumnIndex(KEY_START_TIME))));
                DateTime endTime = new DateTime(Long.parseLong(c.getString(c.getColumnIndex(KEY_END_TIME))));

                List<Integer> categories = getCategoriesIdsOfEvent(eventId);
                List<String> photos = getPhotosOfEvent(eventId);

                events.add(new Event(eventId, name, description, url, categories, photos, startTime, endTime, placeId));
            }
            c.close();
        }
        db.close();

        return events;
    }

    public List<Event> getEventsInPlace(int placeId)
    {
        List<Event> events = new ArrayList<Event>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS + " WHERE " + KEY_ID_PLACE + " = " + placeId;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            while(c.moveToNext()) {
                int eventId = c.getInt(c.getColumnIndex(KEY_ID_EVENT));
                String name = c.getString(c.getColumnIndex(KEY_NAME));
                String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
                String url = c.getString(c.getColumnIndex(KEY_URL));
                DateTime startTime = new DateTime(Long.parseLong(c.getString(c.getColumnIndex(KEY_START_TIME))));
                DateTime endTime = new DateTime(Long.parseLong(c.getString(c.getColumnIndex(KEY_END_TIME))));

                List<Integer> categories = getCategoriesIdsOfEvent(eventId);
                List<String> photos = getPhotosOfEvent(eventId);

                events.add(new Event(eventId, name, description, url, categories, photos, startTime, endTime, placeId));
            }
            c.close();
        }
        db.close();

        return events;
    }

    public List<Event> getEventsContaining(String query)
    {
        List<Event> events = new ArrayList<Event>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS + " WHERE " + KEY_NAME + " LIKE '%" + query +"%'";

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            while(c.moveToNext()) {
                int eventId = c.getInt(c.getColumnIndex(KEY_ID_EVENT));
                int placeId = c.getInt(c.getColumnIndex(KEY_ID_PLACE));
                String name = c.getString(c.getColumnIndex(KEY_NAME));
                String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
                String url = c.getString(c.getColumnIndex(KEY_URL));
                DateTime startTime = new DateTime(Long.parseLong(c.getString(c.getColumnIndex(KEY_START_TIME))));
                DateTime endTime = new DateTime(Long.parseLong(c.getString(c.getColumnIndex(KEY_END_TIME))));

                List<Integer> categories = getCategoriesIdsOfEvent(eventId);
                List<String> photos = getPhotosOfEvent(eventId);

                events.add(new Event(eventId, name, description, url, categories, photos, startTime, endTime, placeId));
            }
            c.close();
        }
        db.close();

        return events;
    }


    // #########################################################################
    // ROUTE ###################################################################

    public void addOrUpdateRoute(Route route)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String existQuery =  getExistenceQuery(TABLE_ROUTES, KEY_ID_ROUTE, Integer.toString(route.id));
        String routeQuery = "";

        if (db.rawQuery(existQuery, null).getCount() == 0)
        {
            routeQuery = getInsertRouteQuery(route);
        }
        else
        {
            routeQuery = getUpdateRouteQuery(route);
        }

        db.execSQL(routeQuery);

        routeQuery = getClearRoutePlacesQuery(route);
        db.execSQL(routeQuery);

        if (route.placesIds != null)
        {
            int i = 0;
            for(int placeId : route.placesIds)
            {
                routeQuery = getAddRoutePlaceConnectionQuery(route, placeId, i++, "", "");
                db.execSQL(routeQuery);
            }
        }

        db.close();
    }

    public String getInsertRouteQuery(Route route)
    {
        String insertQuery = "INSERT INTO " + TABLE_ROUTES + " (" + KEY_ID_ROUTE + ", " + KEY_NAME + ", "
                + KEY_DESCRIPTION +", " + KEY_TOUR_TIME + ") VALUES ("
                + route.id + ", '" + route.name + "', '" + route.description + "'," + route.tour_time + ");";

        return insertQuery;
    }

    public String getUpdateRouteQuery(Route route)
    {
        String updateQuery = "UPDATE "+ TABLE_ROUTES + " SET " +
                KEY_NAME + " = '" + route.name + "', " +
                KEY_DESCRIPTION + " = '" + route.description + "', " +
                KEY_TOUR_TIME + " = " + route.tour_time + "" +
                " WHERE " + KEY_ID_ROUTE + " = " + route.id;

        return updateQuery;
    }

    private String getAddRoutePlaceConnectionQuery(Route route, int placeId, int order, String before, String after)
    {
        String addPlaceType = "INSERT INTO " + TABLE_ROUTE_PLACES + "(" + KEY_ID_ROUTE + ", " + KEY_ID_PLACE  + ", " + KEY_PLACE_ORDER + ", " + KEY_BEFORE + ", " + KEY_AFTER + ")"
                + " VALUES( " + route.id + ", " + placeId +  "," + order + ", '" + before + "','" + after +"')";

        return addPlaceType;
    }

    private String getClearRoutePlacesQuery(Route route)
    {
        String clearQuery = "DELETE FROM " + TABLE_ROUTE_PLACES + " WHERE " + KEY_ID_ROUTE + " = " + route.id;

        return clearQuery;
    }

    public List<Route> getAllRoutes()
    {

        SQLiteDatabase db = this.getReadableDatabase();
        List<Route> routes = new ArrayList<Route>();
        String selectQuery = "SELECT  * FROM " + TABLE_ROUTES;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        c.moveToFirst();
        while (c.isAfterLast() == false)
        {
            int routeId = c.getInt(c.getColumnIndex(KEY_ID_ROUTE));
            String routeName = c.getString(c.getColumnIndex(KEY_NAME));
            String routeUrl = c.getString(c.getColumnIndex(KEY_URL));
            String routeDesc = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
            double tourTime = c.getDouble(c.getColumnIndex(KEY_TOUR_TIME));
            Route route = new Route(routeId, routeName, routeDesc, routeUrl, getPlacesInRoute(routeId), tourTime);

            routes.add(route);
            c.moveToNext();
        }

        c.close();

        return routes;
    }

    public List<Route> getRoutesContaining(String query)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Route> routes = new ArrayList<Route>();
        String selectQuery = "SELECT  * FROM " + TABLE_ROUTES + " WHERE " + KEY_NAME + " LIKE '%" + query + "%'";

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        c.moveToFirst();
        while (c.isAfterLast() == false)
        {
            int routeId = c.getInt(c.getColumnIndex(KEY_ID_ROUTE));
            String routeName = c.getString(c.getColumnIndex(KEY_NAME));
            String routeUrl = c.getString(c.getColumnIndex(KEY_URL));
            String routeDesc = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
            double tourTime = c.getDouble(c.getColumnIndex(KEY_TOUR_TIME));
            Route route = new Route(routeId, routeName, routeDesc, routeUrl, tourTime);

            routes.add(route);
            c.moveToNext();
        }

        c.close();

        return routes;
    }

    public Route getRouteWithID(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ROUTES + " WHERE " + KEY_ID_ROUTE + " = " + id;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            String name = c.getString(c.getColumnIndex(KEY_NAME));
            String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
            String url = c.getString(c.getColumnIndex(KEY_URL));
            double tourTime = c.getDouble(c.getColumnIndex(KEY_TOUR_TIME));

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
                place = this.getPlaceWithId(placeId);
                if(place != null) {
//                    places.add(placeOrder, place);
                    places.add(place);
                }
                c.moveToNext();
            }

            Route route = new Route(id,name,description,url,places, tourTime);
            c.close();
            return route;
        }
        c.close();

        return null;
    }

    public List<Place> getPlacesInRoute(int routeId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Place> places = new ArrayList<Place>();
        String selectQuery = "SELECT  * FROM " + TABLE_PLACES + " WHERE " + KEY_ID_PLACE + " IN (SELECT "
                + KEY_ID_PLACE + " FROM " + TABLE_ROUTE_PLACES + " WHERE " + KEY_ID_ROUTE + " = " + routeId + ")";

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        c.moveToFirst();
        while (c.isAfterLast() == false)
        {
            int id = c.getInt(c.getColumnIndex(KEY_ID_PLACE));
            String name = c.getString(c.getColumnIndex(KEY_NAME));
            String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
            String url = c.getString(c.getColumnIndex(KEY_URL));
            List<Integer> types = getTypesIdsOfPlace(id);
            List<String> photos = getPhotosOfPlace(id);

            float longitude = c.getFloat(c.getColumnIndex(KEY_LONGITUDE));
            float latitude = c.getFloat(c.getColumnIndex(KEY_LATITUDE));
            String city = c.getString(c.getColumnIndex(KEY_CITY));
            String street = c.getString(c.getColumnIndex(KEY_STREET));
            int eventsCount = getUpcomingEventsForPlace(id);

            Place place = new Place(id, name, description, url, types, photos, latitude, longitude, city, street, eventsCount);
            places.add(place);
            c.moveToNext();
        }
        c.close();

        return places;
    }

    // ##########################################################################
    // PLACE TYPES ##############################################################

    public void addOrUpdatePlaceType(Type type)
    {
        SQLiteDatabase db = this.getReadableDatabase();

            String existQuery =  getExistenceQuery(TABLE_TYPES, KEY_ID_TYPE, Integer.toString(type.id));
            String typeQuery;
            if (db.rawQuery(existQuery, null).getCount() == 0)
            {
                typeQuery = getAddTypeQuery(type);
            }
            else
            {
                typeQuery = getUpdateTypeQuery(type);
            }

            db.execSQL(typeQuery);

        db.close();
    }

    private String getAddTypeQuery(Type type)
    {
        String addTypeQuery = "INSERT INTO " + TABLE_TYPES + " ( " + KEY_ID_TYPE + ", " + KEY_NAME + ", " + KEY_DESCRIPTION + " )" +
                            " VALUES (" +type.id + ", '" + type.name + "', '" + type.description + "' )";

        return addTypeQuery;
    }

    private String getUpdateTypeQuery(Type type)
    {
        String updateTypeQuery = "UPDATE " + TABLE_TYPES + " SET " +  KEY_NAME + " = '" + type.name + "', " +  KEY_DESCRIPTION + " = '" + type.description + "' WHERE " + KEY_ID_TYPE + " = " + type.id;

        return updateTypeQuery;
    }

    public List<Integer> getTypesIdsOfPlace(int placeId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PLACES_TYPES + " WHERE " + KEY_ID_PLACE + " = " + placeId;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        List<Integer> types = new ArrayList<Integer>();
        if (c != null)
        {
            while(c.moveToNext())
            {
                int typeId = c.getInt(c.getColumnIndex(KEY_ID_TYPE));

                types.add(typeId);
            }
        }
        c.close();

        return types;
    }

    public List<Type> getAllPlacesTypes()
    {
        List<Type> types = new ArrayList<Type>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_TYPES;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            while(c.moveToNext())
            {
                int id = c.getInt(c.getColumnIndex(KEY_ID_TYPE));
                String name = c.getString(c.getColumnIndex(KEY_NAME));
                String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));

                types.add(new Type(id, name, description));
            }
        }
        c.close();
        db.close();

        return types;
    }

    private String getAddPlaceTypeConnectionQuery(Place place, int typeId)
    {
        String addPlaceType = "INSERT INTO " + TABLE_PLACES_TYPES + " VALUES( " + place.id + ", " + typeId +  ")";

        return addPlaceType;
    }

    private String getClearPlaceTypesQuery(Place place)
    {
        String clearQuery = "DELETE FROM " + TABLE_PLACES_TYPES + " WHERE " + KEY_ID_PLACE + " = " + place.id;

        return clearQuery;
    }

    // ##########################################################################
    // PLACE PHOTOS #########################################################

    private String getAddPlacePhotoQuery(Place place, String url)
    {
        return getAddObjectPhotoQuery(place.id, PLACE_PHOTO_TOKEN, url);
    }

    private String getClearPlacePhotosQuery(Place place)
    {
        return getClearObjectPhotosQuery(place.id, PLACE_PHOTO_TOKEN);
    }

    public List<String> getPhotosOfPlace(int placeId)
    {
        return getPhotosOfObject(placeId, PLACE_PHOTO_TOKEN);
    }

    // ##########################################################################
    // EVENT PHOTOS #########################################################

    private String getAddEventPhotoQuery(Event event, String url)
    {
        return getAddObjectPhotoQuery(event.id, EVENT_PHOTO_TOKEN, url);
    }

    private String getClearEventPhotosQuery(Event event)
    {
        return getClearObjectPhotosQuery(event.id, EVENT_PHOTO_TOKEN);
    }

    public List<String> getPhotosOfEvent(int eventId)
    {
        return getPhotosOfObject(eventId, EVENT_PHOTO_TOKEN);
    }

    // ##########################################################################
    // OBJECT PHOTOS #########################################################

    private String getClearObjectPhotosQuery(int id, String token)
    {
        String clearPhotosQuery = "DELETE FROM " + TABLE_PHOTOS + " WHERE " + KEY_ID_OBJECT + " = " + id + " AND " + KEY_OBJECT_TYPE +" = '" + token + "'";

        return clearPhotosQuery;
    }

    private String getAddObjectPhotoQuery(int id, String token, String url)
    {
        String addPhoto = "INSERT INTO " + TABLE_PHOTOS + " VALUES( " + id + ", '" + token + "','" + url + "')";

        return addPhoto;
    }

    public List<String> getPhotosOfObject(int objectId, String token)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PHOTOS + " WHERE " + KEY_ID_OBJECT + " = " + objectId +" AND " + KEY_OBJECT_TYPE + " = '" + token +"'";

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        List<String> photos = new ArrayList<String>();
        if (c != null)
        {
            while(c.moveToNext())
            {
                String photoUrl = c.getString(c.getColumnIndex(KEY_URL));

                photos.add(photoUrl);
            }
        }
        c.close();

        return photos;
    }

    // ##########################################################################
    // EVENT CATEGORIES #########################################################

    public void addOrUpdateEventCategory(Category category)
    {
        SQLiteDatabase db = this.getReadableDatabase();

            String existQuery =  getExistenceQuery(TABLE_CATEGORIES, KEY_ID_CATEGORY, Integer.toString(category.id));
            String categoryQuery;
            Log.d("test", CREATE_TABLE_CATEGORIES);
            if (db.rawQuery(existQuery, null).getCount() == 0)
            {
                categoryQuery = getAddCategoryQuery(category);
            } else
            {
                categoryQuery = getUpdateCategoryQuery(category);
            }

            db.execSQL(categoryQuery);

        db.close();
    }

    private String getAddCategoryQuery(Category category)
    {
        String addCategoryQuery = "INSERT INTO " + TABLE_CATEGORIES + " ( " + KEY_ID_CATEGORY + ", " + KEY_NAME + ", " + KEY_DESCRIPTION + " )" +
                " VALUES (" +category.id + ", '" + category.name + "', '" + category.description + "' )";

        return addCategoryQuery;
    }

    private String getUpdateCategoryQuery(Category category)
    {
        String updateCategoryQuery = "UPDATE " + TABLE_CATEGORIES + " SET " +  KEY_NAME + " = '" + category.name + "', " +  KEY_DESCRIPTION + " = '" + category.description + "' WHERE " + KEY_ID_CATEGORY + " = " + category.id;

        return updateCategoryQuery;
    }

    public List<Integer> getCategoriesIdsOfEvent(int eventId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS_CATEGORIES+ " WHERE " + KEY_ID_EVENT+ " = " + eventId;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        List<Integer> categories = new ArrayList<Integer>();
        if (c != null)
        {
            while(c.moveToNext())
            {
                int categoryId = c.getInt(c.getColumnIndex(KEY_ID_CATEGORY));

                categories.add(categoryId);
            }
        }
        c.close();

        return categories;
    }

    public List<Category> getAllEventsCategories()
    {
        List<Category> categories = new ArrayList<Category>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            while(c.moveToNext())
            {
                int id = c.getInt(c.getColumnIndex(KEY_ID_CATEGORY));
                String name = c.getString(c.getColumnIndex(KEY_NAME));
                String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));

                categories.add(new Category(id, name, description));
            }
        }
        c.close();
        db.close();

        return categories;
    }

    private String getAddEventCategoryConnectionQuery(Event event, int categoryId)
    {
        String addPlaceType = "INSERT INTO " + TABLE_EVENTS_CATEGORIES + " VALUES( " + event.id + ", " + categoryId +  ")";

        return addPlaceType;
    }

    private String getClearEventCategoriesQuery(Event event)
    {
        String clearQuery = "DELETE FROM " + TABLE_EVENTS_CATEGORIES + " WHERE " + KEY_ID_EVENT + " = " + event.id;

        return clearQuery;
    }

    // ##########################################################################
    // OTHER AWESOME STUFF ######################################################

    public Collection<SearchResult> getSearchResult(SearchCriteria criteria)
    {
        List<SearchResult> results = new ArrayList<SearchResult>();

        List<Place> places = getPlacesContaining(criteria.query);
        for(Place place : places)
        {
            String typeName = (place.types != null && place.types.size() > 0) ? place.types.get(0).name : "";
            results.add(new SearchResult(place, ObjectType.PLACE, place.getDistanceInHumarFormat(), typeName));
        }

        List<Event> events = getEventsContaining(criteria.query);
        for(Event event : events)
        {
            String categoryName = (event.categories != null && event.categories.size() > 0) ? event.categories.get(0).name : "";
            results.add(new SearchResult(event, ObjectType.EVENT, event.getDateInHumanFormat(), categoryName));
        }

        List<Route> routes = getRoutesContaining(criteria.query);
        for(Route route : routes)
        {
            results.add(new SearchResult(route, ObjectType.ROUTE, "Trasa"));
        }

        List<Integer> typesIds = new ArrayList<>();
        for(Type type : Place.AllPlacesTypes)
        {
            if (type.name.toLowerCase().contains(criteria.query))
            {
                typesIds.add(type.id);
            }
        }

        List<Integer> categoriesIds = new ArrayList<>();
        for(Category category : Event.AllEventCategories)
        {
            if (category.name.toLowerCase().contains(criteria.query))
            {
                categoriesIds.add(category.id);
            }
        }

        if (typesIds.size() > 0)
        {
            List<Place> placesOfTypes = getPlacesOfTypes(typesIds);
            for (Place place : placesOfTypes)
            {
                String hint = "";

                for (int i = 0; i < place.types.size() && hint.equals(""); i++)
                {
                    if (typesIds.contains(place.types.get(i).id))
                    {
                        hint = place.types.get(i).name;
                    }
                }

                results.add(new SearchResult(place, ObjectType.PLACE, place.getDistanceInHumarFormat(), hint));
            }
        }

        if (categoriesIds.size() > 0)
        {
            List<Event> eventsOfCategories = getEventsOfCategories(categoriesIds);
            for (Event event : eventsOfCategories)
            {
                String hint = "";

                for (int i = 0; i < event.categories.size() && hint.equals(""); i++)
                {
                    if (categoriesIds.contains(event.categories.get(i).id))
                    {
                        hint = event.categories.get(i).name;
                    }
                }

                results.add(new SearchResult(event, ObjectType.EVENT, event.getDateInHumanFormat(), hint));
            }
        }

        HashMap<String, SearchResult> uniqueResults = new HashMap<>();
        for(SearchResult result : results)
        {
            uniqueResults.put(result.getName(), result);
        }

        return uniqueResults.values();
    }

    public String getExistenceQuery(String TableName, String dbfield, String fieldValue)
    {
        String query;
        if(dbfield == null || dbfield.equals("")) {
            query = "SELECT 1 FROM " + TableName;
        }
        else {
            if(fieldValue == null || fieldValue.equals("")) {
                query = "SELECT " + dbfield + " FROM " + TableName;
            }
            else {
                query = "SELECT * FROM " + TableName + " WHERE " + dbfield + " = " + fieldValue + "";
            }
        }

        return query;
    }

    public void addTransport(Transport transport)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL("INSERT INTO " + TABLE_TRANSPORT + " VALUES( '" + transport.name + "', '" + transport.type + "', " + transport.x + ", " + transport.y + ", " + transport.k + " )");
        db.close();
    }

    public List<Transport> getTransport()
    {
        List<Transport> transports = new ArrayList<Transport>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_TRANSPORT;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
        {
            int i = 0;
            while(c.moveToNext()) {
                String name = c.getString(c.getColumnIndex("name"));
                String type = c.getString(c.getColumnIndex("type"));
                float x = c.getFloat(c.getColumnIndex("x"));
                float y = c.getFloat(c.getColumnIndex("y"));
                int k = c.getInt(c.getColumnIndex("k"));

                Transport transport = new Transport(name, type, x, y, k);
                transports.add(transport);
            }
        }
        c.close();
        db.close();

        return transports;
    }

    public void clearTransport()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL("DELETE FROM " + TABLE_TRANSPORT);
        db.close();
    }
}
