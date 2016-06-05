package com.guide.przewo.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Category implements Serializable
{
	@SerializedName("id_category")
	public int id;
	public String name;
	public String description;

	public Category(int id, String name, String description)
	{
		this.id = id;
		this.name = name;
		this.description = description;
	}
}