package com.guide.przewo.Models;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class ObjectFactory<T>
{
	public <T> List<T> getObjectFromJson(JSONObject json, Type resultType)
	{
		List<T> list = null;

		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>()
		{
			@Override
			public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				return new DateTime(json.getAsString());
			}
		}).create();

		String rows = null;
		try
		{
			rows = json.getJSONArray("rows").toString();
		    list = gson.fromJson(rows, resultType);
			Log.d("Object parsed:", "");
		} catch (JSONException e)
		{
			Log.d("Object parsing failed:", e.getMessage());
		}

		return list;
	}
}
