package in.co.madhur.ganalyticsdashclock;

import in.co.madhur.ganalyticsdashclock.AnalyticsDataService.LocalBinder;


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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class ExtensionActivity extends Activity implements
		OnItemSelectedListener
{
	static final int REQUEST_ACCOUNT_PICKER = 1;
	static final int REQUEST_AUTHORIZATION = 2;
	AnalyticsDataService mService;
	boolean mBound = false, dirty=false;

	private GoogleAccountCredential credential;
	private Analytics analytics_service;
	AppPreferences appPreferences;

	Spinner properties_spinner, profile_spinner, metrics_spinner,
			accounts_spinner;
	
	ArrayList<GAccount> gAccounts;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		List<String> scopes = new ArrayList<String>();
		appPreferences = new AppPreferences(this);

		setContentView(R.layout.activity_main);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		accounts_spinner = (Spinner) findViewById(R.id.accounts_spinner);
		properties_spinner = (Spinner) findViewById(R.id.properties_spinner);
		profile_spinner = (Spinner) findViewById(R.id.profiles_spinner);
		metrics_spinner = (Spinner) findViewById(R.id.metrics_spinner);

		accounts_spinner.setOnItemSelectedListener(this);
		properties_spinner.setOnItemSelectedListener(this);
		profile_spinner.setOnItemSelectedListener(this);
		metrics_spinner.setOnItemSelectedListener(this);

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
	public void UpdateUI(ArrayList<GAccount> gAccounts)
	{

		if (gAccounts != null)
		{
			this.gAccounts=gAccounts;
			UpdateAccounts(gAccounts);

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
		if(item.getItemId()!= R.id.action_refresh)
			return super.onOptionsItemSelected(item);
		
		
		if(mBound && mService!=null && Connection.isConnected(this))
		{
			mService.showAccounts();
			dirty=true;
		}
		else
			Toast.makeText(this, getString(R.string.network_not_connected), Toast.LENGTH_SHORT).show();
			
		return true;
	}
	
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void UpdateAccounts(List<GAccount> gAccounts)
	{

		ArrayAdapter accountsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, gAccounts);
		ArrayAdapter propertiesAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, gAccounts.get(0).getProperties());
		ArrayAdapter profilesAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, gAccounts.get(0).getProperties().get(0).getProfiles());
		ArrayAdapter metricsAdapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item, new GMetrics[]{ new GMetrics("today", this.getString(R.string.visits_today)), new GMetrics("yesterday", this.getString(R.string.visits_yesterday))});
		
		accounts_spinner.setAdapter(accountsAdapter);
		properties_spinner.setAdapter(propertiesAdapter);
		profile_spinner.setAdapter(profilesAdapter);
		metrics_spinner.setAdapter(metricsAdapter);
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
			mService = binder.getService(ExtensionActivity.this);
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
			gAccounts=(ArrayList<GAccount>) appPreferences.getConfigData();
			
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
		
		if(gAccounts==null)
			mService.showAccounts();
		else
			UpdateAccounts(gAccounts);
	}

	@Subscribe
	public void Notify(Intent reason)
	{
		Log.v("Tag", "reason");

		startActivityForResult(reason, REQUEST_AUTHORIZATION);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
	{
		switch (parent.getId())
		{
			case R.id.accounts_spinner:
				GAccount selAccount = (GAccount) parent.getSelectedItem();
				ArrayAdapter propertiesAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, selAccount.getProperties());
				properties_spinner.setAdapter(propertiesAdapter);

				break;

			case R.id.properties_spinner:
				GProperty selProperty = (GProperty) parent.getSelectedItem();
				ArrayAdapter profilesAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, selProperty.getProfiles());
				profile_spinner.setAdapter(profilesAdapter);
				break;

			case R.id.profiles_spinner:
				// save it to preferences
				break;
				
			case R.id.metrics_spinner:
				
				break;

		}

	}
	
	public void onClickSave(View v)
	{
		if(gAccounts!=null && dirty)
		{
			Log.d("TAG", "saving configdata");
			
			try
			{
				appPreferences.saveConfigData(gAccounts);
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

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
		Log.v("TAG", "nothing selected");

	}

}
