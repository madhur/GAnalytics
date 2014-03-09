package in.co.madhur.ganalyticsdashclock.API;

import android.util.Log;
import in.co.madhur.ganalyticsdashclock.Consts;
import in.co.madhur.ganalyticsdashclock.Consts.ANALYTICS_METRICS;

public class GMetric extends GType
{
	
	public GMetric()
	{
		
	}

	public GMetric(String Id, String Name)
	{
		super(Id, Name);
	}
	
	public GMetric(ANALYTICS_METRICS Id, String Name)
	{
		super(Id.toString(), Name);
	}
	
	@Override
	public String toString()
	{
		return Name;
	}
}
