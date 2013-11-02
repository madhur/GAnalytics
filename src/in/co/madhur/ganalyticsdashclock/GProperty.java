package in.co.madhur.ganalyticsdashclock;

import java.util.ArrayList;
import java.util.List;

public class GProperty
{
	
	private String Id;
	private String Name;
	private List<GProfile> profiles=new ArrayList<GProfile>();
	
	public GProperty(String Id, String Name)
	{
		this.Id=Id;
		this.Name=Name;
	}
	
	public String getName()
	{
		return Name;
	}
	public void setName(String name)
	{
		Name = name;
	}
	public String getId()
	{
		return Id;
	}
	public void setId(String id)
	{
		Id = id;
	}

	List<GProfile> getProfiles()
	{
		return profiles;
	}

	void setProfiles(List<GProfile> profiles)
	{
		this.profiles = profiles;
	}

}
