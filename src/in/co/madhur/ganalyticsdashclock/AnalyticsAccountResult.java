package in.co.madhur.ganalyticsdashclock;

import in.co.madhur.ganalyticsdashclock.API.GNewProfile;
import in.co.madhur.ganalyticsdashclock.Consts.API_STATUS;

import java.util.ArrayList;

public class AnalyticsAccountResult
{
	
	private ArrayList<GNewProfile> items;
	private String errorMessage;
	private API_STATUS status;
	private boolean persist;
	
	public AnalyticsAccountResult(API_STATUS status)
	{
		this.status=status;
	}
	
	public AnalyticsAccountResult(String errorMessage)
	{
		this.errorMessage=errorMessage;
		this.setStatus(API_STATUS.FAILURE);
	}
	
	public AnalyticsAccountResult (ArrayList<GNewProfile>  items, boolean persist)
	{
		this.items=items;
		this.setStatus(API_STATUS.SUCCESS);
		this.persist=persist;
	}

	
	public  ArrayList<GNewProfile>  getItems()
	{
		return items;
	}
	public void setItems( ArrayList<GNewProfile>  items)
	{
		this.items = items;
	}
	
	
	public String getErrorMessage()
	{
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public API_STATUS getStatus()
	{
		return status;
	}

	public void setStatus(API_STATUS status)
	{
		this.status = status;
	}

	public boolean isPersist()
	{
		return persist;
	}

	public void setPersist(boolean persist)
	{
		this.persist = persist;
	}
	


}
