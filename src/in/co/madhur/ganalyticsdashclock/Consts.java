package in.co.madhur.ganalyticsdashclock;

public class Consts
{

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
