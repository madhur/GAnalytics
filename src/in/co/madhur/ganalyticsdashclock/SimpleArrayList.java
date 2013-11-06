package in.co.madhur.ganalyticsdashclock;

import java.util.ArrayList;

public class SimpleArrayList extends ArrayList<GType>
{

	public GType GetById(String Id)
	{
		
		for(int i=0;i<this.size();++i)
		{
			GType gType=(GType) this.get(i);
			if(gType.getId().equals(Id))
				return gType;
			
		}
		
		return null;
	}
	

}
