package in.co.madhur.ganalyticsdashclock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.co.madhur.ganalyticsdashclock.AppPreferences.Keys;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.GaData;

public class DashAnalytics extends DashClockExtension implements OnSharedPreferenceChangeListener
{
	AppPreferences appPreferences;
	String ProfileId, metricKey, periodKey;
	private GoogleAccountCredential credential;
	private Analytics analytics_service;

	List<String> scopes = new ArrayList<String>();

	@Override
	protected void onUpdateData(int arg0)
	{
		Log.v(App.TAG, "Firing update:"+String.valueOf(arg0));

		new APIResultTask().execute(ProfileId);
	}

	@Override
	protected void onInitialize(boolean isReconnect)
	{
		super.onInitialize(isReconnect);
		appPreferences = new AppPreferences(this);
		appPreferences.sharedPreferences.registerOnSharedPreferenceChangeListener(this);

		scopes.add(AnalyticsScopes.ANALYTICS_READONLY);

		ProfileId = appPreferences.getMetadata(Keys.PROFILE_ID);

		try
		{
			credential = GoogleAccountCredential.usingOAuth2(this, scopes);
			credential.setSelectedAccountName(appPreferences.getUserName());
			analytics_service = getAnalyticsService(credential);
		}
		catch (Exception e)
		{

			Log.e(App.TAG, e.getMessage());
		}
	}

	private Analytics getAnalyticsService(GoogleAccountCredential credential)
	{
		return new Analytics.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName(getString(R.string.app_name)).build();

	}

	private class APIResultTask extends AsyncTask<String, Integer, GaData>
	{

		@Override
		protected GaData doInBackground(String... params)
		{
			try
			{
				return analytics_service.data().ga().get("ga:" + params[0], // Table
																			// Id.
																			// ga:
				"today", // Start date.
				"today", // End date.
				"ga:visits") // Metrics.
				.execute();
			}
			catch (IOException e)
			{
				Log.e(App.TAG, e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(GaData results)
		{
			Log.d(App.TAG, "onPostExecute");
			if (results != null && results.getRows()!=null)
			{
				
				if (!results.getRows().isEmpty())
				{
					Log.d(App.TAG, "Processing result");
					
					String profileName = results.getProfileInfo().getProfileName();
					String result = results.getRows().get(0).get(0);

					String selectedProperty = appPreferences.getMetadata(Keys.PROPERTY_NAME);

					String metricKey = appPreferences.getMetadata(Keys.METRIC_ID);

					int metricIdentifier = getResources().getIdentifier(metricKey, "string", DashAnalytics.this.getPackageName());
					if (metricIdentifier != 0)
					{

						try
						{
							publishUpdate(new ExtensionData().visible(true).status(result).icon(R.drawable.ic_dashclock).expandedTitle(String.format(getString(R.string.title_display_format), getString(metricIdentifier), result)).expandedBody(String.format(getString(R.string.body_display_format), profileName, selectedProperty)));
						}
						catch (Exception e)
						{

							Log.e(App.TAG, "Exception while published:"
									+ e.getMessage());
						}

					}
				}
				else
					Log.d(App.TAG, "empty result");
			}
			else
				Log.d(App.TAG, "empty result");
		}

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
			if(analytics_service==null)
				onInitialize(false);
			
			onUpdateData(DashClockExtension.UPDATE_REASON_MANUAL);
		
	}

}
