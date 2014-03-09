package in.co.madhur.ganalyticsdashclock;


public class Consts
{
	
	public static final String HARDWARE_PHONE="hardware_phone";
	public static final String LOCATION_WEB_SITE="location_web_site";
	public static final String PRO_APP="in.co.madhur.dashclock";
	
	public enum ATTRIBUTE_TYPE
	{
		INTEGER, PERCENT, METRIC_RATIO, METRIC_CURRENCY, METRIC_TALLY, FLOAT, CURRENCY

	};
	
	public enum API_STATUS
	{
		STARTING, FAILURE, SUCCESS;

	}
	
	public enum APIPeriod
	{
		TODAY("today"),
		YESTERDAY("yesterday"),
		THISMONTH("thismonth"),
		LASTMONTH("lastmonth"),
		LASTWEEK("lastweek"),
		LAST30DAYS("last30days");

		private String key;

		APIPeriod(String Key)
		{
			this.key = Key;

		}

		@Override
		public String toString()
		{
			return key;
		}

	};



	
	
	
	
	
	public enum ANALYTICS_METRICS
	{
		VISITS("ga_visits"),
		VISITORS("ga_visitors"),
		NEW_VISITS("ga_newVisits"),
		PERCENT_NEW_VISITS("ga_percentNewVisits"),
		PAGE_VIEWS("ga_pageviews"),
		BOUNCE_RATE("ga_visitBounceRate"),
		PAGE_VIEWS_PER_VISIT("ga_pageviewsPerVisit"),
		SCREEN_VIEWS("ga_screenviews"),
		UNIQUE_SCREEN_VIEWS("ga_uniqueScreenviews"),
		SCREEN_VIEWS_PER_SESSION("ga_screenviewsPerSession"),
		GOAL_COMPLETIONS_ALL("ga_goalCompletionsAll"),
		GOAL_VALUE("ga_goalValueAll"),
		GOAL_CONVERSIONRATE_ALL("ga_goalConversionRateAll");

		private String key;

		ANALYTICS_METRICS(String Key)
		{
			this.key = Key;

		}

		@Override
		public String toString()
		{
			return key;
		}
		
		public static ANALYTICS_METRICS getByMetric(String metricKey)
		{
			for(ANALYTICS_METRICS value: ANALYTICS_METRICS.values())
			{
				
				if(value.key.equalsIgnoreCase(metricKey))
				{
					return value;
				}
			}
			
			return null;
			
		}
	};

	public enum APIMetrics
	{
		VISITS("visits"),
		VISITORS("visitors"),
		NEW_VISITS("newVisits"),
		VISIT_COUNT("visitCount");

		private String key;

		APIMetrics(String Key)
		{
			this.key = Key;

		}

		@Override
		public String toString()
		{
			return key;
		}

	};

	public enum APIOperation
	{
		SELECT_ACCOUNT
	}

}
