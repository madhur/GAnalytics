package in.co.madhur.ganalyticsdashclock;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class AppPreferences
{
	
	private SharedPreferences sharedPreferences;
	Context context;

	public enum Keys
	{

		USER_NAME("pref_user_name"),
		CONFIGURATION("pref_config"),
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
		
		Editor editor=sharedPreferences.edit();
		editor.putString(Keys.AUTH_TOKEN.key, token);
		editor.commit();
	}
	
	public String getAuthToken()
	{
		
		return sharedPreferences.getString(Keys.AUTH_TOKEN.key, "");
		
	}
	
	public void setUserName(String username)
	{
		
		Editor editor=sharedPreferences.edit();
		editor.putString(Keys.USER_NAME.key, username);
		editor.commit();
	}
	
	public void saveConfigData(List<GAccount> gAccounts) throws JsonProcessingException
	{
		String accountsjson, profilesjson, propertiesjson = null;
		ObjectMapper mapper=new ObjectMapper();
		accountsjson=mapper.writeValueAsString(gAccounts);
		SharedPreferences.Editor editor=this.sharedPreferences.edit();
		editor.putString(Keys.CONFIGURATION.key, accountsjson);
		editor.commit();
	}
	
	public List<GAccount> getConfigData() throws JsonParseException, JsonMappingException, IOException
	{
		String json=sharedPreferences.getString(Keys.CONFIGURATION.key, "");
		ObjectMapper mapper = new ObjectMapper();
		List<GAccount> gAccounts = mapper.readValue(json, new TypeReference<List<GAccount>>()
		{
		});
				
		return gAccounts;
		
	}
	
	public String getUserName()
	{
		
		return sharedPreferences.getString(Keys.USER_NAME.key, "");
		
	}



}
