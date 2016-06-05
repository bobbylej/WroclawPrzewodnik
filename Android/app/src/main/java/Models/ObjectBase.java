package Models;

import java.util.Date;
import java.util.List;

public abstract class ObjectBase
{
    public int id;
    public String name;
    public String description;
    public State state;
    public String link;
    public int version;
    public List<Type> types;
    public Date changeDate;


	public ObjectBase(int id, String name, String description, String link, int version, List<Type> types, Date changeDate)
	{
		this.id = id;
		this.name = name;
		this.description = description;
		this.link = link;
		this.version = version;
		this.types = types;
		this.changeDate = changeDate;
	}

	public abstract Location GetLocation();

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

	public State GetState()
	{
		return state;
	}

	public String GetLink()
	{
		return link;
	}

	public int GetVersion()
	{
		return version;
	}

	public Date GetChangeDate()
	{
		return changeDate;
	}

	public List<Type> GetTypes()
	{
		return types;
	}

	public void ChangeState(State state)
	{
		this.state = state;
	}

	public String toString()
	{
		return GetName();
	}
}
