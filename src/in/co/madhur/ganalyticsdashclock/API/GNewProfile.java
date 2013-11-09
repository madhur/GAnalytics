package in.co.madhur.ganalyticsdashclock.API;

public class GNewProfile
{

	private String accountId;
	private String accountName;
	private String propertyId;
	private String propertyName;
	private String profileId;
	private String profileName;
	private boolean isApp;
	
	public GNewProfile()
	{
		
		
	}
	
	
	public GNewProfile(String accountId, String accountName, String propertyId, String propertyName, String profileId, String profileName)
	{
		this.accountId=accountId;
		this.accountName=accountName;
		this.propertyId=propertyId;
		this.propertyName=propertyName;
		this.profileId= profileId;
		this.profileName= profileName;
	}
	
	public GNewProfile(String accountId, String accountName, String propertyId, String propertyName, String profileId, String profileName, boolean isApp)
	{
		this.accountId=accountId;
		this.accountName=accountName;
		this.propertyId=propertyId;
		this.propertyName=propertyName;
		this.profileId= profileId;
		this.profileName= profileName;
		this.setApp(isApp);
	}


	public String getAccountId()
	{
		return accountId;
	}

	public void setAccountId(String accountId)
	{
		this.accountId = accountId;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public String getPropertyId()
	{
		return propertyId;
	}

	public void setPropertyId(String propertyId)
	{
		this.propertyId = propertyId;
	}

	public String getPropertyName()
	{
		return propertyName;
	}

	public void setPropertyName(String propertyName)
	{
		this.propertyName = propertyName;
	}

	public String getProfileId()
	{
		return profileId;
	}

	public void setProfileId(String profileId)
	{
		this.profileId = profileId;
	}

	public String getProfileName()
	{
		return profileName;
	}

	public void setProfileName(String profileName)
	{
		this.profileName = profileName;
	}


	public boolean isApp()
	{
		return isApp;
	}


	public void setApp(boolean isApp)
	{
		this.isApp = isApp;
	}

}
