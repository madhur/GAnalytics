package in.co.madhur.ganalyticsdashclock;

import in.co.madhur.ganalyticsdashclock.AnalyticsDataService.LocalBinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ExtensionActivity extends Activity implements OnItemSelectedListener
{
	static final int REQUEST_ACCOUNT_PICKER = 1;
	static final int REQUEST_AUTHORIZATION = 2;
	AnalyticsDataService mService;
	boolean mBound = false;

	private GoogleAccountCredential credential;
	private static Analytics analytics_service;
	AppPreferences appPreferences;
	
	Spinner properties_spinner, profile_spinner , metrics_spinner;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		List<String> scopes = new ArrayList<String>();
		appPreferences = new AppPreferences(this);
		
		setContentView(R.layout.activity_main);
		
		
		properties_spinner=(Spinner) findViewById(R.id.properties_spinner);
		profile_spinner=(Spinner) findViewById(R.id.profiles_spinner);
		metrics_spinner=(Spinner) findViewById(R.id.metrics_spinner);
		
		properties_spinner.setOnItemSelectedListener(this);
		profile_spinner.setOnItemSelectedListener(this);
		metrics_spinner.setOnItemSelectedListener(this);
		

		scopes.add(AnalyticsScopes.ANALYTICS_READONLY);
		
		Intent i=getIntent();
		

		credential = GoogleAccountCredential.usingOAuth2(this, scopes);
		
		if(i!=null && i.getAction().equals("AUTHORIZE"))
		{
			
			startActivityForResult(credential.newChooseAccountIntent(), REQUEST_AUTHORIZATION);
			return;
		}

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
		
		if(gAccounts!=null)
		{
			UpdateAccounts(gAccounts);
			
		}
	}
	
	
	private void UpdateAccounts(List<GAccount> gAccounts)
	{
		
		ArrayAdapter propertiesAdapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item, gAccounts);
		
		properties_spinner.setAdapter(propertiesAdapter);
		
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
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			Log.v("Tag", "onServiceConnected");
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService(ExtensionActivity.this);
			mService.analytics_service=analytics_service;
			mBound = true;
			Log.v("Tag", "starting service");
			mService.showAccounts();
			
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0)
		{
			mBound = false;
		}
	};
	
	@Subscribe public void Notify(Intent reason)
	{
		Log.v("Tag", "reason");
		
		startActivityForResult(reason, REQUEST_AUTHORIZATION);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
		// TODO Auto-generated method stub
		
	}

}
