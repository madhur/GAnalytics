package in.co.madhur.ganalyticsdashclock;

import in.co.madhur.ganalyticsdashclock.AppPreferences.Keys;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;

public class DashAnalyticsPreferenceActivity extends PreferenceActivity
{
	AppPreferences appPreferences;

	private OnPreferenceChangeListener listPreferenceChangeListerner = new OnPreferenceChangeListener()
	{

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue)
		{
			UpdateLabel((ListPreference) preference, newValue.toString());
			return true;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		appPreferences = new AppPreferences(this);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		addPreferencesFromResource(R.xml.preference);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		SetListeners();

		UpdateLabel((ListPreference) findPreference(Keys.METRIC_ID.key), null);
		UpdateLabel((ListPreference) findPreference(Keys.PERIOD_ID.key), null);
	}

	private void UpdateLabel(ListPreference listPreference, String newValue)
	{

		if (newValue == null)
		{
			newValue = listPreference.getValue();
		}

		int index = listPreference.findIndexOfValue(newValue);
		if (index != -1)
		{
			newValue = (String) listPreference.getEntries()[index];
			listPreference.setTitle(newValue);
		}

	}

	private void SetListeners()
	{
		findPreference(Keys.METRIC_ID.key).setOnPreferenceChangeListener(listPreferenceChangeListerner);

		findPreference(Keys.PERIOD_ID.key).setOnPreferenceChangeListener(listPreferenceChangeListerner);

	}

}
