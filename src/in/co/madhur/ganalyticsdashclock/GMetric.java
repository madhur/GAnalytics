package in.co.madhur.ganalyticsdashclock;

import android.util.Log;
import in.co.madhur.ganalyticsdashclock.Consts.APIMetrics;

public class GMetric extends GType
{
	
	public GMetric()
	{
		
	}

	public GMetric(String Id, String Name)
	{
		super(Id, Name);
	}
	
	public GMetric(APIMetrics Id, String Name)
	{
		super(Id.toString(), Name);
		Log.v("TAG", Id.toString());
	}
	
	@Override
	public String toString()
	{
		return Name;
	}
}
