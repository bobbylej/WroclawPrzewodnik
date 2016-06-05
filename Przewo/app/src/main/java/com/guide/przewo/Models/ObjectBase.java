package com.guide.przewo.Models;


public abstract class ObjectBase
{
	public String name;
	public String description;
	public State state;
	public String url;
	public ObjectType type;

	public ObjectBase(String name, String description, String url, ObjectType type)
	{
		this.name = name;
		this.description = description;
		this.url = url;
		this.type = type;
	}

	public String toString()
	{
		return  "\nname=" + name +
				"\ndesc=" + description +
				"\nurl=" + url +
				"\nstate=" + state;
	}
}
