package in.co.madhur.ganalyticsdashclock;

import in.co.madhur.ganalyticsdashclock.API.GAccount;
import in.co.madhur.ganalyticsdashclock.API.GNewProfile;
import in.co.madhur.ganalyticsdashclock.API.GProfile;
import in.co.madhur.ganalyticsdashclock.API.GProperty;
import in.co.madhur.ganalyticsdashclock.Consts.APIOperation;
import in.co.madhur.ganalyticsdashclock.Consts.API_STATUS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.Profiles;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

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
	public MainActivity extensionActivity;
	static final int REQUEST_AUTHORIZATION = 2;
	private IBinder binder = new LocalBinder();

	public void showAccountsAsync()
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
		AnalyticsDataService getService(MainActivity extensionActivity)
		{
			AnalyticsDataService.this.extensionActivity = extensionActivity;
			return AnalyticsDataService.this;
		}

	}

	private class APIManagementTask extends
			AsyncTask<APIOperation, Integer, AnalyticsAccountResult>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			
			
			App.getEventBus().post(new AnalyticsAccountResult(API_STATUS.STARTING));

		}

		@Override
		protected void onPostExecute(AnalyticsAccountResult result)
		{
			super.onPostExecute(result);

			App.getEventBus().post(result);

		}

		@Override
		protected AnalyticsAccountResult doInBackground(APIOperation... params)
		{
			Accounts accounts;
			int account_num;
			Profiles profiles;
			com.google.api.services.analytics.model.Webproperties webproperties;
			String WebpropertyId;
			String Id, accountName, propertyName, profileName;

			ListMultimap<GProperty, GProfile> propertiesMap = ArrayListMultimap.create();
			List<GAccount> gAccounts = new ArrayList<GAccount>();

			boolean isApp;
			ArrayList<GNewProfile> acProfiles = new ArrayList<GNewProfile>();

			try
			{
				if (analytics_service == null)
				{

					Log.e(App.TAG, "Analytics service object isn null");
					return null;
				}

				int size = analytics_service.management().accounts().list().size();
				if (size < 1)
				{
					getString(R.string.no_analytics_account);
					Log.v(App.TAG, String.valueOf(size));
					// return new AnalyticsAccountResult(errorMessage);
				}

				try
				{
					accounts = analytics_service.management().accounts().list().execute();
				}
				catch (GoogleJsonResponseException e)
				{

					Log.e(App.TAG, e.getMessage());
					Log.e(App.TAG, e.getStatusMessage());
					Log.e(App.TAG, String.valueOf(e.getStatusCode()));

					return new AnalyticsAccountResult(e.getStatusMessage());
				}

				account_num = accounts.getItems().size();

				for (int i = 0; i < account_num; i++)
				{

					Id = accounts.getItems().get(i).getId();
					accountName = accounts.getItems().get(i).getName();

					Log.d(App.TAG, "account_id: " + Id);
					Log.d(App.TAG, "account_name: "
							+ accounts.getItems().get(i).getName());

					gAccounts.add(new GAccount(Id, accounts.getItems().get(i).getName()));

					webproperties = analytics_service.management().webproperties().list(Id).execute();

					for (int j = 0; j < webproperties.getItems().size(); ++j)
					{

						WebpropertyId = webproperties.getItems().get(j).getId();
						propertyName = webproperties.getItems().get(j).getName();
						Log.d(App.TAG, "property_id: " + WebpropertyId);
						Log.d(App.TAG, "property_name: "
								+ webproperties.getItems().get(j).getName());

						String kind = webproperties.getItems().get(j).getWebsiteUrl();
						if (kind == null)
							isApp = true;
						else
							isApp = false;

						GProperty gProperty = new GProperty(WebpropertyId, webproperties.getItems().get(j).getName());
						gAccounts.get(i).getProperties().add(gProperty);

						profiles = analytics_service.management().profiles().list(Id, WebpropertyId).execute();
						if (profiles != null && profiles.getItems() != null)
						{

							for (int k = 0; k < profiles.getItems().size(); ++k)
							{
								String Profile_Id = profiles.getItems().get(k).getId();
								profileName = profiles.getItems().get(k).getName();

								Log.d(App.TAG, "profile_id: " + Profile_Id);
								Log.d(App.TAG, "profile_id: "
										+ profiles.getItems().get(k).getName());

								GProfile gProfile = new GProfile(Profile_Id, profiles.getItems().get(k).getName());

								gAccounts.get(i).getProperties().get(j).getProfiles().add(gProfile);

								acProfiles.add(new GNewProfile(Id, accountName, WebpropertyId, propertyName, Profile_Id, profileName, isApp));
								propertiesMap.put(gProperty, gProfile);

							}
						}

					}

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

			return new AnalyticsAccountResult(acProfiles, true);

		}

	}

}
