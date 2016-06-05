package com.guide.przewo.Models;

import com.guide.przewo.R;

public class SearchResult
{
	public ObjectType objectType;
	public ObjectBase object;
	public String tag;
	public String hint;
	public String time;
	private int picture;

	public SearchResult(String text, ObjectType objectType, int picture)
	{
		tag = text;
		this.objectType = objectType;
		this.picture = picture;
	}

	public SearchResult(ObjectBase object, ObjectType objectType)
	{
		this(object, objectType, "", "");
	}

	public SearchResult(ObjectBase object, ObjectType objectType, String time)
	{
		this(object, objectType, time, "");
	}

	public SearchResult(ObjectBase object, ObjectType objectType, String time, String hint)
	{
		this.object = object;
		this.objectType = objectType;
		this.hint = hint;
		this.time = time;
	}

	public int getImage()
	{
		switch (objectType)
		{
			case EVENT:
				return R.drawable.events;
			case PLACE:
				return R.drawable.places;
			case ROUTE:
				return R.drawable.routes;
			case TAG:
				return picture;
			default:
				return -1;
		}
	}

	public String getName()
	{
		if (tag == null || tag.isEmpty())
		{
			return object.name;
		}
		else
		{
			return tag;
		}
	}
}
