package in.co.madhur.ganalyticsdashclock;

import java.util.ArrayList;

public class GProfile extends GType
{
	
	public GProfile()
	{
		
	}
	

	public GProfile(String Id, String Name)
	{
		super(Id, Name);
	}
	
	
	public static GProfile GetById(ArrayList<GProfile> items, String Id)
	{
		for(int i=0;i<items.size();++i)
		{
			if(items.get(i).getId().equals(Id))
				return items.get(i);
			
		}
		
		return null;
	}

}
