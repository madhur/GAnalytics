package in.co.madhur.ganalyticsdashclock;

import in.co.madhur.ganalyticsdashclock.Consts.API_STATUS;

import com.google.api.services.analytics.model.GaData;


public class AnalyticsAPIResult extends APIResult
{
	private GaData result;
	
	public AnalyticsAPIResult(GaData result)
	{
			this.result=result;
			this.status=API_STATUS.SUCCESS;
	}
	
	public GaData getResult()
	{
		return result;
	}
	
	public void setResult(GaData result)
	{
		this.result = result;
	}

	

}
