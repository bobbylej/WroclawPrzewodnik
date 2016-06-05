package com.guide.przewo.Models;

public class SearchCriteria
{
	public String query = "";
	public boolean onlyPlaces = false;
	public boolean onlyEvents = false;

	public SearchCriteria(String query, boolean onlyPlaces, boolean onlyEvents)
	{
		this.query = query;
		this.onlyPlaces = onlyPlaces;
		this.onlyEvents = onlyEvents;
	}
}
