package in.co.madhur.ganalyticsdashclock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.Profiles;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class AnalyticsDataService extends Service
{
	public Analytics analytics_service;
	public ExtensionActivity extensionActivity;
	private String ProfileId;
	static final int REQUEST_AUTHORIZATION = 2;
	private IBinder binder = new LocalBinder();

	// public AnalyticsDataService(Analytics analytics_service)
	// {
	// this.analytics_service=analytics_service;
	//
	// }

	public void showAccounts()
	{

		new APIManagementTask().execute(APIOperation.SELECT_ACCOUNT);
	}

	public void showToast(final String toast)
	{
		extensionActivity.runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();

			}
		});

	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return binder;
	}

	public class LocalBinder extends Binder
	{
		AnalyticsDataService getService(ExtensionActivity extensionActivity)
		{
			AnalyticsDataService.this.extensionActivity = extensionActivity;
			return AnalyticsDataService.this;
		}

	}

	private class APIResultTask extends AsyncTask<String, Integer, GaData>
	{

		@Override
		protected GaData doInBackground(String... params)
		{
			try
			{
				return analytics_service.data().ga().get("ga:" + params[0], // Table Id. ga:
				// + profile id.
				"today", // Start date.
				"today", // End date.
				"ga:visits") // Metrics.
				.execute();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

	private class APIManagementTask extends
			AsyncTask<APIOperation, Integer, List<GAccount>>
	{

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			App app = (App) getApplication();

		}

		@Override
		protected void onPostExecute(List<GAccount> result)
		{
			super.onPostExecute(result);
			
			App.getEventBus().post(result);
		}

		@Override
		protected List<GAccount> doInBackground(APIOperation... params)
		{
			Accounts accounts;
			int account_num;
			Profiles profiles;
			com.google.api.services.analytics.model.Webproperties webproperties;
			String WebpropertyId;
			String Id;

			List<String> list_accounts = new ArrayList<String>();
			List<GAccount> gAccounts = new ArrayList<GAccount>();

			try
			{
				if (analytics_service == null)
				{

					Log.e("Tag", "Analytics service object isn null");
					return null;
				}

				accounts = analytics_service.management().accounts().list().execute();
				account_num = accounts.getItems().size();

				for (int i = 0; i < account_num; i++)
				{

					Id = accounts.getItems().get(i).getId();
					Log.d("Analytics_requests", "account_id: " + Id);
					Log.d("Analytics_requests", "account_name: "
							+ accounts.getItems().get(i).getName());

					gAccounts.add(new GAccount(Id, accounts.getItems().get(i).getName()));

					webproperties = analytics_service.management().webproperties().list(Id).execute();

					for (int j = 0; j < webproperties.getItems().size(); ++j)
					{

						WebpropertyId = webproperties.getItems().get(j).getId();
						Log.d("Analytics_requests", "property_id: "
								+ WebpropertyId);
						Log.d("Analytics_requests", "property_name: "
								+ webproperties.getItems().get(j).getName());

						GProperty gProperty = new GProperty(WebpropertyId, webproperties.getItems().get(j).getName());
						gAccounts.get(i).getProperties().add(gProperty);

						profiles = analytics_service.management().profiles().list(Id, WebpropertyId).execute();
						if (profiles != null && profiles.getItems() != null)
						{

							for (int k = 0; k < profiles.getItems().size(); ++k)
							{
								String Profile_Id = profiles.getItems().get(k).getId();
								Log.d("Analytics_requests", "profile_id: "
										+ Profile_Id);
								Log.d("Analytics_requests", "profile_id: "
										+ profiles.getItems().get(k).getName());

								GProfile gProfile = new GProfile(Profile_Id, profiles.getItems().get(k).getName());
								gAccounts.get(i).getProperties().get(j).getProfiles().add(gProfile);

								// ProfileId = Profile_Id;
							}
						}

					}

					list_accounts.add(accounts.getItems().get(i).getName());
				}

			}
			catch (UserRecoverableAuthIOException e)
			{
				AnalyticsDataService.this.extensionActivity.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			return gAccounts;

		}

		private GaData getResults(Analytics analytics, String profileId)
				throws IOException
		{

			return analytics.data().ga().get("ga:" + profileId, // Table Id. ga:
																// + profile id.
			"today", // Start date.
			"today", // End date.
			"ga:visits") // Metrics.
			.execute();
		}

		private void printResults(GaData results)
		{
			if (results != null && !results.getRows().isEmpty())
			{
				Log.v("TAG", "View (Profile) Name: "
						+ results.getProfileInfo().getProfileName());

				String result = results.getRows().get(0).get(0);
				Log.v("TAG", "Total Visits: " + result);
			}
			else
			{
				Log.v("Tag", "No results found");
			}
		}

	}

}
