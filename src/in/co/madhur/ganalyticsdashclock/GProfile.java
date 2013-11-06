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
	
	
	public static GNewProfile GetById(ArrayList<GNewProfile> items, String Id)
	{
		for(int i=0;i<items.size();++i)
		{
			if(items.get(i).getProfileId().equals(Id))
				return items.get(i);
			
		}
		
		return null;
	}
	
	public static int getItemPositionByProfileId(ArrayList<GNewProfile> items, final String id)
	{
	    for (int i = 0; i < items.size(); i++)
	    {
	        if (((GNewProfile)items.get(i)).getProfileId().equals(id))
	            return i;
	    }
	    return -1;
	}

}
