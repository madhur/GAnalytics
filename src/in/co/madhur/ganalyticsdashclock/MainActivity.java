package in.co.madhur.ganalyticsdashclock;

import in.co.madhur.ganalyticsdashclock.AnalyticsDataService.LocalBinder;
import in.co.madhur.ganalyticsdashclock.AppPreferences.Keys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.common.collect.ListMultimap;
import com.squareup.otto.Subscribe;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class MainActivity extends Activity 
		
{
	static final int REQUEST_ACCOUNT_PICKER = 1;
	static final int REQUEST_AUTHORIZATION = 2;
	AnalyticsDataService mService;
	boolean mBound = false, dirty = false;

	private GoogleAccountCredential credential;
	private Analytics analytics_service;
	AppPreferences appPreferences;
	ListView listView;

	ArrayList<GNewProfile> acProfiles;
	ListMultimap<GProperty, GProfile> propertiesMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		List<String> scopes = new ArrayList<String>();
		appPreferences = new AppPreferences(this);

		setContentView(R.layout.activity_main2);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		listView=(ListView) findViewById(R.id.listview);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				
				MyAdapter myAdapter=(MyAdapter) listView.getAdapter();
				GNewProfile newProfile=(GNewProfile) myAdapter.getItem(position);
				
				Log.v("TAG", newProfile.getProfileName());
				
			}
		});

		scopes.add(AnalyticsScopes.ANALYTICS_READONLY);

		credential = GoogleAccountCredential.usingOAuth2(this, scopes);

		if (TextUtils.isEmpty(appPreferences.getUserName()))
			startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
		else
		{
			credential.setSelectedAccountName(appPreferences.getUserName());
			analytics_service = getAnalyticsService(credential);
			Log.v("Tag", "getAnalyticsAccounts");
			getAnalyticsAccounts();
		}

		App.getEventBus().register(this);
	}

	@Subscribe
	public void UpdateUI(ArrayList<GNewProfile> acProfiles)
	{

		if (acProfiles != null)
		{
			this.acProfiles=acProfiles;
			UpdateAccounts(acProfiles);
			UpdateSelectionPreferences();
		}
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
		if (item.getItemId() != R.id.action_refresh)
			return super.onOptionsItemSelected(item);

		if (mBound && mService != null && Connection.isConnected(this))
		{
			mService.showAccounts();
			dirty = true;
		}
		else
			Toast.makeText(this, getString(R.string.network_not_connected), Toast.LENGTH_SHORT).show();

		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void UpdateAccounts(ArrayList<GNewProfile> acProfiles)
	{

		MyAdapter myAdapter=new MyAdapter(acProfiles, this);
		//listView.setAdapter(myAdapter);
		listView.setAdapter(myAdapter);
		
		
	}

	private void UpdateSelectionPreferences()
	{
		String accountId = appPreferences.getMetadata(Keys.ACCOUNT_ID);
		String propertyId = appPreferences.getMetadata(Keys.PROPERTY_ID);
		String profileId = appPreferences.getMetadata(Keys.PROFILE_ID);
		String metricId = appPreferences.getMetadata(Keys.METRIC_ID);

		if (!TextUtils.isEmpty(accountId) && !TextUtils.isEmpty(propertyId) && !TextUtils.isEmpty(profileId) && !TextUtils.isEmpty(metricId))
		{

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

						credential.setSelectedAccountName(accountName);
						analytics_service = getAnalyticsService(credential);

						getAnalyticsAccounts();
					}
				}

				break;
			case REQUEST_AUTHORIZATION:
				if (resultCode == Activity.RESULT_OK)
				{
					Log.v("Tag", "Request Authorization");
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
		Log.v("Tag", "binding service");
		bindService(i, mConnection, Context.BIND_AUTO_CREATE);

	}

	private Analytics getAnalyticsService(GoogleAccountCredential credential)
	{
		return new Analytics.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName(getString(R.string.app_name)).build();

	}

	/** Defines callbacks for service binding, passed to bindService() */
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
		try
		{
			acProfiles = (ArrayList<GNewProfile>) appPreferences.getConfigData();

		}
		catch (JsonParseException e)
		{
			Log.e("TAG", e.getMessage());
		}
		catch (JsonMappingException e)
		{
			Log.e("TAG", e.getMessage());
		}
		catch (IOException e)
		{
			Log.e("TAG", e.getMessage());
		}

		if (acProfiles == null)
			mService.showAccounts();
		else
		{
			UpdateAccounts(acProfiles);
			UpdateSelectionPreferences();
		}

	}

	@Subscribe
	public void Notify(Intent reason)
	{
		Log.v("Tag", "reason");

		startActivityForResult(reason, REQUEST_AUTHORIZATION);
	}

	public void onClickSave(View v)
	{
		if (acProfiles != null && dirty)
		{
			Log.d("TAG", "saving configdata");

			try
			{
				appPreferences.saveConfigData(acProfiles);
			}
			catch (JsonProcessingException e)
			{
				Log.e("TAG", e.getMessage());
			}
		}
		else
			Log.d("TAG", "skipping save of configdata");


		finish();

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if (mConnection != null)
		{
			unbindService(mConnection);
			mService.extensionActivity = null;

		}
	}



}
