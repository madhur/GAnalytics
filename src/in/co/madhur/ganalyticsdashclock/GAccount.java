package in.co.madhur.ganalyticsdashclock;

import java.util.ArrayList;

public class GAccount extends GType
{
	private ArrayList<GProperty> properties=new ArrayList<GProperty>();
	
	public GAccount(String Id, String Name)
	{
		super(Id, Name);
	}
	
	public GAccount()
	{
		
	}
	

	public ArrayList<GProperty> getProperties()
	{
		return properties;
	}

	public void setProperties(ArrayList<GProperty> properties)
	{
		this.properties = properties;
	}
	
	public static GAccount GetById(ArrayList<GAccount> items, String Id)
	{
		for(int i=0;i<items.size();++i)
		{
			if(items.get(i).getId().equals(Id))
				return items.get(i);
			
		}
		
		return null;
	}

	
}
