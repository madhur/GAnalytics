package in.co.madhur.ganalyticsdashclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class AppPreferences
{
	
	private SharedPreferences sharedPreferences;
	Context context;

	public enum Keys
	{

		USER_NAME("pref_user_name"),
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
	
	public String getUserName()
	{
		
		return sharedPreferences.getString(Keys.USER_NAME.key, "");
		
	}



}
