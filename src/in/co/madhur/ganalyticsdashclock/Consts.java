package in.co.madhur.ganalyticsdashclock;


public class Consts
{
	
public enum APIMetrics
{
	TOTAL_VISITS_TODAY("today"), 
	TOTA_VISITS_YESTERDAY("yesterday");
	
	private String key;
	
	APIMetrics(String Key)
	{
		this.key=Key;
		
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
