package Models;

import com.google.android.gms.maps.model.LatLng;

public class Location
{
	private LatLng point;
	private String city;
	private String street;

	public Location(float latitude, float longitude, String city, String street)
	{
		point = new LatLng(longitude, latitude);
		this.city = city;
		this.street = street;
	}


	public LatLng GetPoint()
	{
		return point;
	}


	public String GetCity()
	{
		return city;
	}


	public String GetStreet()
	{
		return street;
	}
}