package in.co.madhur.ganalyticsdashclock;

public class Consts
{
	
	public static final String HARDWARE_PHONE="hardware_phone";
	public static final String LOCATION_WEB_SITE="location_web_site";

	public enum APIPeriod
	{
		TODAY("today"), YESTERDAY("yesterday");

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
	
	public enum API_STATUS
	{
		STARTING, FAILURE, SUCCESS;
		
	}

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
