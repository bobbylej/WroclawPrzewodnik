package com.guide.przewo.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Śbyzby on 2015-06-28.
 */
public class Transport implements Serializable
{
    @SerializedName("name")
    public String name;
    @SerializedName("type")
    public String type;
    @SerializedName("x")
    public float x;
    @SerializedName("y")
    public float y;
    @SerializedName("k")
    public int k;

    public Transport(String name, String type, float x, float y, int k)
    {
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.k = k;
    }
}
