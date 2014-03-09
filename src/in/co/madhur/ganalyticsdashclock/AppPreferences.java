package in.co.madhur.ganalyticsdashclock;

import in.co.madhur.ganalyticsdashclock.Consts.ANALYTICS_METRICS;
import in.co.madhur.ganalyticsdashclock.API.GNewProfile;

import java.io.IOException;
import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public  class AppPreferences
{

	public SharedPreferences sharedPreferences;
	protected Context context;
	
	public AppPreferences(Context context)
	{
		this.context = context;
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		
	}
	

	public enum Keys
	{

		USER_NAME("pref_user_name"),
		CONFIGURATION("pref_config"),
		ACCOUNT_ID("account_id"),
		ACCOUNT_NAME("account_name"),
		PROPERTY_ID("property_id"),
		PROPERTY_NAME("property_name"),
		PROFILE_ID("profile_id"),
		PROFILE_NAME("profile_name"),
		METRIC_ID("metric_id"),
		PERIOD_ID("period_id"),
		ADSENSE_PERIOD_ID("adsense_period_id"),
		USE_LOCAL_TIME("pref_adsense_usetimezonee"),
		SHOW_CURRENCY("pref_showcurrency"),
		ADSENSE_CLICK_INTENT("adsense_click_intent"),
		ANALYTICS_CLICK_INTENT("analytics_click_intent"),
		SHOW_PROFILE("pref_analytics_showprofile"),
		SHOW_ADSENSE_LASTUPDATE("pref_adsense_showlastupdate"),
		SHOW_ANALYTICS_LASTUPDATE("pref_analytics_showlastupdate"),
		AUTH_TOKEN("pref_auth_token");

		public final String key;

		private Keys(String key)
		{
			this.key = key;

		}

	};
	

	
	public enum ANALYTICS_KEYS 
	{
		SHOW_VISITORS("pref_showvisitors", ANALYTICS_METRICS.VISITORS),
		SHOW_NEW_VISITS("pref_shownewVisits", ANALYTICS_METRICS.NEW_VISITS),
		SHOW_PERCENT_NEWVISITS("pref_showpercentNewVisits", ANALYTICS_METRICS.PERCENT_NEW_VISITS),
		SHOW_VISITS("pref_showvisits", ANALYTICS_METRICS.VISITS),
		SHOW_BOUNCERATE("pref_showvisitBounceRate", ANALYTICS_METRICS.BOUNCE_RATE),
		SHOW_PAGEVIEWS("pref_showpageviews", ANALYTICS_METRICS.PAGE_VIEWS),
		SHOW_PAGEVIEWS_PERVISIT("pref_showpageviewsPerVisit", ANALYTICS_METRICS.PAGE_VIEWS_PER_VISIT),
		SHOW_SCREEN_VIEWS("pref_showscreenviews", ANALYTICS_METRICS.SCREEN_VIEWS),
		SHOW_UNIQUE_SCREENVIEWS("pref_showuniqueScreenviews", ANALYTICS_METRICS.UNIQUE_SCREEN_VIEWS),
		SHOW_SCREENVIEWS_PERSESSION("pref_showscreenviewsPerSession", ANALYTICS_METRICS.SCREEN_VIEWS_PER_SESSION),
		SHOW_GOAL_COMPLETIONS("pref_goalcompletionsall", ANALYTICS_METRICS.GOAL_COMPLETIONS_ALL),
		SHOW_GOAL_VALUE("pref_goalvalueall",ANALYTICS_METRICS.GOAL_VALUE),
		SHOW_GOAL_CONVERSIONRATE("pref_goalconversionrateall", ANALYTICS_METRICS.GOAL_CONVERSIONRATE_ALL);
		
		public final String key;
		public final String metric;

		private ANALYTICS_KEYS(String key, ANALYTICS_METRICS metric)
		{
			this.key = key;
			this.metric=metric.toString();

		}
		
		public String getMetric()
		{
			
			return metric;
		}
	
	}

	public void setAuthToken(String token)
	{

		Editor editor = sharedPreferences.edit();
		editor.putString(Keys.AUTH_TOKEN.key, token);
		editor.commit();
	}

	public String getAuthToken()
	{

		return sharedPreferences.getString(Keys.AUTH_TOKEN.key, "");

	}

	public void setUserName(String username)
	{

		Editor editor = sharedPreferences.edit();
		editor.putString(Keys.USER_NAME.key, username);
		editor.commit();
	}

	public void saveConfigData(ArrayList<GNewProfile> gAccounts, String accountName)
			throws JsonProcessingException
	{
		String accountsjson;
		ObjectMapper mapper = new ObjectMapper();
		accountsjson = mapper.writeValueAsString(gAccounts);
		SharedPreferences.Editor editor = this.sharedPreferences.edit();
		editor.putString(Keys.CONFIGURATION.key + accountName, accountsjson);
		editor.commit();
	}

	public ArrayList<GNewProfile> getConfigData(String accountName)
			throws JsonParseException, JsonMappingException, IOException
	{
		String json = sharedPreferences.getString(Keys.CONFIGURATION.key
				+ accountName, "");
		ArrayList<GNewProfile> gAccounts = null;
		if (!TextUtils.isEmpty(json))
		{
			ObjectMapper mapper = new ObjectMapper();
			gAccounts = mapper.readValue(json, new TypeReference<ArrayList<GNewProfile>>()
			{
			});
		}

		return gAccounts;

	}
	
	public boolean getboolMetaData(Keys key)
	{
		boolean defValue=false;
		
		return sharedPreferences.getBoolean(key.key, defValue);
	}
	
	public boolean getboolMetaDataStr(String key)
	{
		boolean defValue=false;
		
		return sharedPreferences.getBoolean(key, defValue);
	}
	
	
	public boolean getAnalyticProperty(ANALYTICS_KEYS key)
	{
		boolean defValue=false;
		
		return sharedPreferences.getBoolean(key.key, defValue);
	}
	
	
	
	
	
	public boolean isLocalTime()
	{
		return sharedPreferences.getBoolean(Keys.USE_LOCAL_TIME.key, true);
	}
	
	public boolean isShowcurrency()
	{
		return sharedPreferences.getBoolean(Keys.SHOW_CURRENCY.key, true);
		
	}
	
	

	public void setMetadata(Keys key, String value)
	{
		Editor editor = sharedPreferences.edit();
		editor.putString(key.key, value);
		editor.commit();
	}

	public void setMetadataMultiple(String accountId, String accountName, String propertyId, String propertyName, String profileId, String profileName)
	{
		Editor editor = sharedPreferences.edit();
		editor.putString(Keys.ACCOUNT_ID.key, accountId);
		editor.putString(Keys.ACCOUNT_NAME.key, accountName);
		editor.putString(Keys.PROPERTY_ID.key, propertyId);
		editor.putString(Keys.PROPERTY_NAME.key, propertyName);
		editor.putString(Keys.PROFILE_ID.key, profileId);
		editor.putString(Keys.PROFILE_NAME.key, profileName);
		editor.commit();
	}
	
	public void setMetadataMultiple(String accountId, String accountName, String propertyId, String propertyName, String profileId, String profileName, String accountEmail)
	{
		Editor editor = sharedPreferences.edit();
		editor.putString(Keys.ACCOUNT_ID.key, accountId);
		editor.putString(Keys.ACCOUNT_NAME.key, accountName);
		editor.putString(Keys.PROPERTY_ID.key, propertyId);
		editor.putString(Keys.PROPERTY_NAME.key, propertyName);
		editor.putString(Keys.PROFILE_ID.key, profileId);
		editor.putString(Keys.PROFILE_NAME.key, profileName);
		
		editor.putString(Keys.USER_NAME.key, accountEmail);
		editor.commit();
	}

	public String getMetadata(Keys key)
	{
		String defValue = "";

		if (key == Keys.METRIC_ID)
			defValue = Defaults.METRIC_ID;
		else if (key == Keys.PERIOD_ID)
			defValue = Defaults.PERIOD_ID;
		else if (key==Keys.ADSENSE_PERIOD_ID)
			defValue=Defaults.PERIOD_ID;

		return sharedPreferences.getString(key.key, defValue);
	}

	public String getUserName()
	{

		return sharedPreferences.getString(Keys.USER_NAME.key, "");

	}

	public void setMetadataMultiple(String accountId, String accountName, String accountEmail)
	{
		Editor editor = sharedPreferences.edit();
		editor.putString(Keys.ACCOUNT_ID.key, accountId);
		editor.putString(Keys.ACCOUNT_NAME.key, accountName);
		editor.putString(Keys.USER_NAME.key, accountEmail);
		editor.commit();
		
	}

}