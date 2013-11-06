package in.co.madhur.ganalyticsdashclock;

import java.util.ArrayList;
import java.util.List;

public class GProperty extends GType
{
	
	private ArrayList<GProfile> profiles=new ArrayList<GProfile>();
	
	public GProperty()
	{
		
	}
	
	public GProperty(String Id, String Name)
	{
		super(Id, Name);
	}


	public ArrayList<GProfile> getProfiles()
	{
		return profiles;
	}

	public void setProfiles(ArrayList<GProfile> profiles)
	{
		this.profiles = profiles;
	}
	
	public static GProperty GetById(ArrayList<GProperty> items, String Id)
	{
		for(int i=0;i<items.size();++i)
		{
			if(items.get(i).getId().equals(Id))
				return items.get(i);
			
		}
		
		return null;
	}

	
}
