package in.co.madhur.ganalyticsdashclock;

import java.util.ArrayList;
import java.util.List;

public class GAccount
{
	private String Id;
	private String Name;
	private List<GProperty> properties=new ArrayList<GProperty>();
	
	public GAccount(String Id, String Name)
	{
		this.Id=Id;
		this.Name=Name;
	}
	
	public GAccount()
	{
		
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

	public List<GProperty> getProperties()
	{
		return properties;
	}

	public void setProperties(List<GProperty> properties)
	{
		this.properties = properties;
	}
	
	@Override
	public String toString()
	{
		return Name;
	}
	
}
