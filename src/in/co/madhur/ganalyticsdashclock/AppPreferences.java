package in.co.madhur.ganalyticsdashclock;

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

public class AppPreferences
{

	public SharedPreferences sharedPreferences;
	Context context;

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
		AUTH_TOKEN("pref_auth_token");

		public final String key;

		private Keys(String key)
		{
			this.key = key;

		}

	};

	public AppPreferences(Context context)
	{
		this.context = context;
		this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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

	public String getMetadata(Keys key)
	{
		String defValue = "";

		if (key == Keys.METRIC_ID)
			defValue = Defaults.METRIC_ID;
		else if (key == Keys.PERIOD_ID)
			defValue = Defaults.PERIOD_ID;

		return sharedPreferences.getString(key.key, defValue);
	}

	public String getUserName()
	{

		return sharedPreferences.getString(Keys.USER_NAME.key, "");

	}

}
