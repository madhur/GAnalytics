package in.co.madhur.ganalyticsdashclock;

import in.co.madhur.ganalyticsdashclock.AppPreferences.Keys;

import java.util.ArrayList;

import com.google.android.apps.dashclock.configuration.AppChooserPreference;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public abstract class BasePreferenceActivity extends PreferenceActivity
{
	protected AppPreferences appPreferences;
	protected ArrayList<String> prefKeys=new ArrayList<String>();

	protected OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener()
	{

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
		{
			if(prefKeys.contains(key))
				EnableDisablePreferences(false);

		}
	};

	protected final OnPreferenceChangeListener listPreferenceChangeListerner = new OnPreferenceChangeListener()
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

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	protected abstract void EnableDisablePreferences(boolean loading);

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				finish();
				break;
		}
		return true;
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		SetListeners();
	}

	protected void UpdateLabel(ListPreference listPreference, String newValue)
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

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
	{
		super.onPreferenceTreeClick(preferenceScreen, preference);

		// If the user has clicked on a preference screen, set up the action bar
		if (preference instanceof PreferenceScreen)
		{
			initializeActionBar((PreferenceScreen) preference);
		}

		return false;
	}
	
	protected void EnableDisablePreferences(boolean loading, int maxCount)
	{
		
		int count = 0;
		
		for(String key: prefKeys)
		{
			if(appPreferences.getboolMetaDataStr(key))
				count++;
			
		}
		
		if (count > maxCount)
		{
			if(!loading)
				Toast.makeText(getBaseContext(), getString(R.string.max_attributes_message), Toast.LENGTH_LONG).show();
			
			for(String key: prefKeys)
			{
				if(!appPreferences.getboolMetaDataStr(key))
					findPreference(key).setEnabled(false);
				
			}
		}
		else
		{
			for(String key: prefKeys)
			{
					findPreference(key).setEnabled(true);
				
			}
			
		}
	}
		

	// ** Sets up the action bar for an {@link PreferenceScreen} */
	public  void initializeActionBar(PreferenceScreen preferenceScreen)
	{
		final Dialog dialog = preferenceScreen.getDialog();

		if (dialog != null)
		{
			// Inialize the action bar
			dialog.getActionBar().setDisplayHomeAsUpEnabled(true);
			
		//	dialog.getActionBar().setLogo(getIcon());

			// Apply custom home button area click listener to close the
			// PreferenceScreen because PreferenceScreens are dialogs which
			// swallow
			// events instead of passing to the activity
			// Related Issue:
			// https://code.google.com/p/android/issues/detail?id=4611
			View homeBtn = dialog.findViewById(android.R.id.home);

			if (homeBtn != null)
			{
				OnClickListener dismissDialogClickListener = new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						dialog.dismiss();
					}
				};

				// Prepare yourselves for some hacky programming
				ViewParent homeBtnContainer = homeBtn.getParent();

				// The home button is an ImageView inside a FrameLayout
				if (homeBtnContainer instanceof FrameLayout)
				{
					ViewGroup containerParent = (ViewGroup) homeBtnContainer.getParent();

					if (containerParent instanceof LinearLayout)
					{
						// This view also contains the title text, set the whole
						// view as clickable
						((LinearLayout) containerParent).setOnClickListener(dismissDialogClickListener);
					}
					else
					{
						// Just set it on the home button
						((FrameLayout) homeBtnContainer).setOnClickListener(dismissDialogClickListener);
					}
				}
				else
				{
					// The 'If all else fails' default case
					homeBtn.setOnClickListener(dismissDialogClickListener);
				}
			}
		}
	}


	

	protected void SetListeners()
	{

		final String intentKey;
		Keys clickIntentKey;
		if (this instanceof DashAnalyticsPreferenceActivity)
		{
			intentKey = Keys.ANALYTICS_CLICK_INTENT.key;
			clickIntentKey = Keys.ANALYTICS_CLICK_INTENT;
		}

		else
		{
			intentKey = Keys.ADSENSE_CLICK_INTENT.key;
			clickIntentKey = Keys.ADSENSE_CLICK_INTENT;
		}

		CharSequence intentSummary = AppChooserPreference.getDisplayValue(this, appPreferences.getMetadata(clickIntentKey));
		getPreferenceScreen().findPreference(intentKey).setSummary(TextUtils.isEmpty(intentSummary)
				|| intentSummary.equals(getString(R.string.pref_shortcut_default)) ? ""
				: intentSummary);

		getPreferenceScreen().findPreference(intentKey).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
		{

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				CharSequence intentSummary = AppChooserPreference.getDisplayValue(getBaseContext(), newValue.toString());
				getPreferenceScreen().findPreference(intentKey).setSummary(TextUtils.isEmpty(intentSummary)
						|| intentSummary.equals(getResources().getString(R.string.pref_shortcut_default)) ? ""
						: intentSummary);
				return true;
			}

		});
	}

}
