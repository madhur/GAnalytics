package in.co.madhur.ganalyticsdashclock;

import in.co.madhur.ganalyticsdashclock.AnalyticsDataService.LocalBinder;
import in.co.madhur.ganalyticsdashclock.AppPreferences.Keys;
import in.co.madhur.ganalyticsdashclock.Consts.API_STATUS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.common.collect.ListMultimap;
import com.squareup.otto.Subscribe;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity

{
	static final int REQUEST_ACCOUNT_PICKER = 1;
	static final int REQUEST_AUTHORIZATION = 2;
	AnalyticsDataService mService;
	boolean mBound = false;

	private GoogleAccountCredential credential;
	private Analytics analytics_service;
	AppPreferences appPreferences;
	ListView listView;

	ArrayList<GNewProfile> acProfiles;
	ListMultimap<GProperty, GProfile> propertiesMap;

	OnNavigationListener listNavigator;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		List<String> scopes = new ArrayList<String>();
		appPreferences = new AppPreferences(this);

		setContentView(R.layout.activity_main2);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		listView = (ListView) findViewById(R.id.listview);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		listView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{

				MyAdapter myAdapter = (MyAdapter) listView.getAdapter();
				GNewProfile newProfile = (GNewProfile) myAdapter.getItem(position);

				if (newProfile != null)
				{
					PersistPreferences(newProfile);
				}

			}
		});

		// listNavigator = new OnNavigationListener()
		// {
		//
		// @Override
		// public boolean onNavigationItemSelected(int itemPosition, long
		// itemId)
		// {
		// // TODO Auto-generated method stub
		// return false;
		// }
		// };

		scopes.add(AnalyticsScopes.ANALYTICS_READONLY);

		credential = GoogleAccountCredential.usingOAuth2(this, scopes);
		if (TextUtils.isEmpty(appPreferences.getUserName()))
		{
			try
			{

				startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
			}
			catch (ActivityNotFoundException e)
			{

				Toast.makeText(this, getString(R.string.gps_missing), Toast.LENGTH_LONG).show();

				return;
			}

		}
		else
		{
			setNavigationList(appPreferences.getUserName());
			// credential.setSelectedAccountName(appPreferences.getUserName());
			// analytics_service = getAnalyticsService(credential);
			// getAnalyticsAccounts();
		}

		App.getEventBus().register(this);
	}

	private ArrayList<String> getAccountsList()
	{
		ArrayList<String> accountList = new ArrayList<String>();

		AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
		Account[] list = manager.getAccounts();

		for (Account account : list)
		{
			if (account.type.equalsIgnoreCase("com.google"))
			{
				accountList.add(account.name);
			}
		}

		return accountList;
	}

	private void setNavigationList(String accountName)
	{
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getActionBar().setDisplayShowTitleEnabled(false);

		ArrayList<String> navItems = getAccountsList();
		navItems.add("Add Account");

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActionBar().getThemedContext(), android.R.layout.simple_spinner_item, android.R.id.text1, navItems);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		getActionBar().setListNavigationCallbacks(adapter, new OnNavigationListener()
		{

			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId)
			{
				String selItem = adapter.getItem(itemPosition);

				if (selItem.equals("Add Account"))
				{
					startAddGoogleAccountIntent();
					return true;
				}

				if (getAccountsList().size() >= itemPosition)
				{
					Log.v(App.TAG, "Fetching accounts for:"
							+ getAccountsList().get(itemPosition));
					credential.setSelectedAccountName(getAccountsList().get(itemPosition));

					analytics_service = getAnalyticsService(credential);
					if (analytics_service == null)
						Log.e(App.TAG, "analytics service is null");

					if (mBound)
						unbindService(mConnection);

					getAnalyticsAccounts();
				}

				return true;
			}
		});

		if (accountName != null)
		{
			int index = navItems.indexOf(accountName);
			if (index != -1)
				getActionBar().setSelectedNavigationItem(index);
			else
				Log.e(App.TAG, "acount not found");
		}
	}

	private void startAddGoogleAccountIntent()
	{
		Intent addAccountIntent = new Intent(android.provider.Settings.ACTION_ADD_ACCOUNT).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		addAccountIntent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[] { "com.google" });
		startActivity(addAccountIntent);
	}

	@Subscribe
	public void UpdateUI(AnalyticsAccountResult result)
	{
		ProgressBar progressbar = (ProgressBar) findViewById(R.id.pbHeaderProgress);
		LinearLayout spinnerLayout = (LinearLayout) findViewById(R.id.spinnerslayout);
		TextView statusMessage = (TextView) findViewById(R.id.statusMessage);

		switch (result.getStatus())
		{
			case STARTING:
				statusMessage.setVisibility(View.GONE);
				progressbar.setVisibility(View.VISIBLE);
				spinnerLayout.setVisibility(View.GONE);

				break;

			case FAILURE:
				statusMessage.setVisibility(View.VISIBLE);
				progressbar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.GONE);
				statusMessage.setText(result.getErrorMessage());

				break;

			case SUCCESS:

				statusMessage.setVisibility(View.GONE);
				progressbar.setVisibility(View.GONE);
				spinnerLayout.setVisibility(View.VISIBLE);

				if (result.getItems() != null)
				{
					this.acProfiles = result.getItems();

					MyAdapter myAdapter = new MyAdapter(acProfiles, this);
					listView.setAdapter(myAdapter);

					UpdateSelectionPreferences();

					if (result.isPersist())
					{
						Log.d(App.TAG, "saving configdata");

						try
						{
							appPreferences.saveConfigData(acProfiles);
						}
						catch (JsonProcessingException e)
						{
							Log.e(App.TAG, e.getMessage());
						}
					}

					appPreferences.setUserName(credential.getSelectedAccountName());
				}

				break;
		}

	}

	@Override
	protected void onStart()
	{
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, (android.view.Menu) menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_refresh:
				if (!mBound || mService == null)
				{
					Toast.makeText(this, getString(R.string.gps_missing), Toast.LENGTH_LONG).show();

					return true;

				}

				if (Connection.isConnected(this))
				{
					mService.showAccountsAsync();
				}
				else
					Toast.makeText(this, getString(R.string.network_not_connected), Toast.LENGTH_SHORT).show();

				break;

			case R.id.action_settings:
				Intent i = new Intent();
				i.setClass(this, DashAnalyticsPreferenceActivity.class);
				startActivity(i);
				break;

			default:
				return super.onOptionsItemSelected(item);

		}

		return true;
	}

	// @SuppressWarnings({ "unchecked", "rawtypes" })
	// private void UpdateAccounts(ArrayList<GNewProfile> acProfiles)
	// {
	//
	// MyAdapter myAdapter = new MyAdapter(acProfiles, this);
	// listView.setAdapter(myAdapter);
	//
	// }

	private void PersistPreferences(GNewProfile newProfile)
	{
		if (newProfile != null)
			appPreferences.setMetadataMultiple(newProfile.getAccountId(), newProfile.getAccountName(), newProfile.getPropertyId(), newProfile.getPropertyName(), newProfile.getProfileId(), newProfile.getProfileName());

	}

	private void UpdateSelectionPreferences()
	{
		String accountId = appPreferences.getMetadata(Keys.ACCOUNT_ID);
		String propertyId = appPreferences.getMetadata(Keys.PROPERTY_ID);
		String profileId = appPreferences.getMetadata(Keys.PROFILE_ID);
		String metricId = appPreferences.getMetadata(Keys.METRIC_ID);
		String periodId = appPreferences.getMetadata(Keys.PERIOD_ID);

		if (!TextUtils.isEmpty(accountId) && !TextUtils.isEmpty(propertyId)
				&& !TextUtils.isEmpty(profileId)
				&& !TextUtils.isEmpty(metricId) && !TextUtils.isEmpty(periodId))
		{
			int position = GProfile.getItemPositionByProfileId(acProfiles, profileId);
			if (position != -1)
			{

				listView.setItemChecked(position, true);
			}

		}
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{
		switch (requestCode)
		{
			case REQUEST_ACCOUNT_PICKER:
				if (resultCode == RESULT_OK && data != null
						&& data.getExtras() != null)
				{
					String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
					if (accountName != null)
					{
						appPreferences.setUserName(accountName);

						setNavigationList(accountName);

						credential.setSelectedAccountName(accountName);
						analytics_service = getAnalyticsService(credential);

						getAnalyticsAccounts();
					}
				}

				break;
			case REQUEST_AUTHORIZATION:
				if (resultCode == Activity.RESULT_OK)
				{
					getAnalyticsAccounts();
				}
				else
				{
					startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
				}
		}
	}

	private void getAnalyticsAccounts()
	{
		Intent i = new Intent();
		i.setClass(this, AnalyticsDataService.class);
		bindService(i, mConnection, Context.BIND_AUTO_CREATE);

	}

	private Analytics getAnalyticsService(GoogleAccountCredential credential)
	{
		return new Analytics.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName(getString(R.string.app_name)).build();

	}

	private ServiceConnection mConnection = new ServiceConnection()
	{

		@Override
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService(MainActivity.this);
			mService.analytics_service = analytics_service;
			mBound = true;

			InitAccount();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0)
		{
			mBound = false;
		}
	};

	private void InitAccount()
	{
		int selectedIndex = getActionBar().getSelectedNavigationIndex();
		String selectedAccount = getAccountsList().get(selectedIndex);
		Log.v(App.TAG, "Initing accounts for " + selectedAccount);

		if (selectedAccount.equals(appPreferences.getUserName()))
		{
			try
			{
				acProfiles = (ArrayList<GNewProfile>) appPreferences.getConfigData();

			}
			catch (JsonParseException e)
			{
				Log.e(App.TAG, e.getMessage());
			}
			catch (JsonMappingException e)
			{
				Log.e(App.TAG, e.getMessage());
			}
			catch (IOException e)
			{
				Log.e(App.TAG, e.getMessage());
			}

			if (acProfiles == null)
			{
				mService.showAccountsAsync();
			}
			else
			{
				UpdateUI(new AnalyticsAccountResult(acProfiles, false));
				UpdateSelectionPreferences();
			}
		}
		else
		{
			mService.showAccountsAsync();

		}

	}

	@Subscribe
	public void Notify(Intent reason)
	{

		startActivityForResult(reason, REQUEST_AUTHORIZATION);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if (mBound)
		{
			unbindService(mConnection);
			mBound = false;
		}
	}

}
