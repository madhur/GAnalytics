package in.co.madhur.ganalyticsdashclock;

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
import com.google.api.services.analytics.Analytics.Data.Ga.Get;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.GaData;

public class DashAnalytics extends DashClockExtension implements
		OnSharedPreferenceChangeListener
{
	AppPreferences appPreferences;
	String ProfileId, metricKey, periodKey;
	private GoogleAccountCredential credential;
	private Analytics analytics_service;

	List<String> scopes = new ArrayList<String>();

	@Override
	protected void onUpdateData(int arg0)
	{
		
		ProfileId = appPreferences.getMetadata(Keys.PROFILE_ID);
		metricKey = appPreferences.getMetadata(Keys.METRIC_ID);
		periodKey = appPreferences.getMetadata(Keys.PERIOD_ID);
		
		Log.v(App.TAG, "Firing update:" + String.valueOf(arg0));

		if (Connection.isConnected(this))
		{
			new APIResultTask().execute(ProfileId, metricKey, periodKey);
		}
		else
			Log.d(App.TAG, "No network, postponing update");
	}

	@Override
	protected void onInitialize(boolean isReconnect)
	{
		super.onInitialize(isReconnect);
		appPreferences = new AppPreferences(this);
		appPreferences.sharedPreferences.registerOnSharedPreferenceChangeListener(this);

		scopes.add(AnalyticsScopes.ANALYTICS_READONLY);

		try
		{
			credential = GoogleAccountCredential.usingOAuth2(this, scopes);
			credential.setSelectedAccountName(appPreferences.getUserName());
			analytics_service = getAnalyticsService(credential);
		}
		catch (Exception e)
		{

			Log.e(App.TAG, "Exception in onInitialize" + e.getMessage());
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
				Get apiQuery = analytics_service.data().ga().get("ga:"
						+ params[0], params[2], params[2], "ga:" + params[1]);
				Log.d(App.TAG, apiQuery.toString());

				return apiQuery.execute();
			}
			catch (Exception e)
			{
				Log.e(App.TAG, "Exception in doInBackground" + e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(GaData results)
		{
			String profileName = appPreferences.getMetadata(Keys.PROFILE_NAME);
			String selectedProperty = appPreferences.getMetadata(Keys.PROPERTY_NAME);
			String metricKey = appPreferences.getMetadata(Keys.METRIC_ID);
			int metricIdentifier = getResources().getIdentifier(metricKey, "string", DashAnalytics.this.getPackageName());
			int periodIdentifier = getResources().getIdentifier(periodKey, "string", DashAnalytics.this.getPackageName());
			String result;
			
			Log.d(App.TAG, "Processing result for " + profileName);

			Log.d(App.TAG, "onPostExecute");
			if (results != null && results.getRows() != null)
			{

				if (!results.getRows().isEmpty())
				{

					result = results.getRows().get(0).get(0);
				}
				else
				{
					result="0";
					Log.d(App.TAG, "empty result");
					
				}
			}
			else
			{
				result="0";
				Log.d(App.TAG, "null result");
			}
			
			
			try
			{
				publishUpdate(new ExtensionData().visible(true).status(result).icon(R.drawable.ic_dashclock).expandedTitle(String.format(getString(R.string.title_display_format), getString(metricIdentifier), getString(periodIdentifier), result)).expandedBody(String.format(getString(R.string.body_display_format), profileName, selectedProperty)));
			}
			catch (Exception e)
			{

				Log.e(App.TAG, "Exception while published:"
						+ e.getMessage());
			}
			
		}

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		if (analytics_service == null)
			onInitialize(false);

		onUpdateData(DashClockExtension.UPDATE_REASON_MANUAL);

	}

}
