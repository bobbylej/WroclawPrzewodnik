package Models;

import java.io.Serializable;
import java.util.List;


public class Route implements Serializable
{
    private int id;
    private String name;
    private String description;
    private List<Place> places;
    private int version;

    public Route(int id, String name, String description, List<Place> locations, int version)
    {
        this.id = id;
        this.name = name;
        this.places = locations;
        this.version = version;
        this.description = description;
    }

    public int GetID()
    {
        return id;
    }

    public String GetName()
    {
        return name;
    }

    public String GetDescription()
    {
        return description;
    }

    public int GetVersion() { return version;}

    public Place GetPlace(int index)
    {
        return places.get(index);
    }

    public  List<Place> GetAllPlaces()
    {
        return places;
    }

    @Override
    public String toString() {
        return name;
    }
}
