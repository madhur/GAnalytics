package in.co.madhur.ganalyticsdashclock;

public class GMetrics
{
	private String metricKey;
	private String metricDisplay;
	
	public GMetrics(String metricKey, String  metricDisplay)
	{
		this.metricKey=metricKey;
		this.metricDisplay=metricDisplay;
		
	}
	
	public GMetrics()
	{
		
	}

//	public String getMetricKey()
//	{
//		return metricKey;
//	}
//
//	public void setMetricKey(String metricKey)
//	{
//		this.metricKey = metricKey;
//	}
	
	@Override
	public String toString()
	{
		return metricDisplay;
	}
}
