package in.co.madhur.ganalyticsdashclock;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class DashAnalytics extends DashClockExtension
{

	@Override
	protected void onUpdateData(int arg0)
	{
		 publishUpdate(new ExtensionData()
         .visible(true)
         .status("Hello")
         .expandedTitle("Hello, world!")
         .expandedBody("This is an example."));
	}
	
	@Override
	protected void onInitialize(boolean isReconnect)
	{
		super.onInitialize(isReconnect);
	}

}
