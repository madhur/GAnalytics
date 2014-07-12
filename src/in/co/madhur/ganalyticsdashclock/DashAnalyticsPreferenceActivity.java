package in.co.madhur.ganalyticsdashclock;

import in.co.madhur.ganalyticsdashclock.AppPreferences.ANALYTICS_KEYS;
import in.co.madhur.ganalyticsdashclock.AppPreferences.Keys;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;

public class DashAnalyticsPreferenceActivity extends BasePreferenceActivity
{

	@Override
	protected void EnableDisablePreferences(boolean loading)
	{

		EnableDisablePreferences(loading, 4);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		PreferenceManager prefMgr = getPreferenceManager();
		//prefMgr.setSharedPreferencesName(Consts.ANALYTICS_PREFERENCE_NAME);
		//prefMgr.setSharedPreferencesMode(Context.MODE_PRIVATE);

		appPreferences = new AppPreferences(this);
		addPreferencesFromResource(R.xml.preference);

		this.appPreferences.sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

	}

	@Override
	protected void onResume()
	{
		super.onResume();

		UpdateLabel((ListPreference) findPreference(Keys.METRIC_ID.key), null);
		UpdateLabel((ListPreference) findPreference(Keys.PERIOD_ID.key), null);

		prefKeys.clear();
		for (ANALYTICS_KEYS key : ANALYTICS_KEYS.values())
		{

			prefKeys.add(key.key);
		}

		prefKeys.add(Keys.SHOW_PROFILE.key);
		prefKeys.add(Keys.SHOW_ANALYTICS_LASTUPDATE.key);

		EnableDisablePreferences(true);

	}

	@Override
	protected void SetListeners()
	{
		super.SetListeners();

		findPreference(Keys.METRIC_ID.key).setOnPreferenceChangeListener(listPreferenceChangeListerner);

		findPreference(Keys.PERIOD_ID.key).setOnPreferenceChangeListener(listPreferenceChangeListerner);

	}


}
