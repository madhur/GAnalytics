package in.co.madhur.ganalyticsdashclock;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import android.app.Application;

public class App extends Application
{
	private static Bus bus;

	@Override
	public void onCreate()
	{
		super.onCreate();

		bus = new Bus(ThreadEnforcer.ANY);
	}

	public static Bus getEventBus()
	{
		return bus;
	}

}
