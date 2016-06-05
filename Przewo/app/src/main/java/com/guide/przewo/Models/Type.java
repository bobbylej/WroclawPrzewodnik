package com.guide.przewo.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Type implements Serializable
{
    @SerializedName("id_type")
    public int id;
    public String name;
    public String description;

    public Type(int id, String name, String description)
    {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString()
    {
        return name;
    }
}