package in.co.madhur.ganalyticsdashclock;

import in.co.madhur.ganalyticsdashclock.Consts.APIPeriod;

public class GPeriod extends GType
{
	public GPeriod()
	{
		
	}

	public GPeriod(String Id, String Name)
	{
		super(Id, Name);
	}
	
	public GPeriod(APIPeriod Id, String Name)
	{
		super(Id.toString(), Name);
	}
	
	@Override
	public String toString()
	{
		return Name;
	}
}
