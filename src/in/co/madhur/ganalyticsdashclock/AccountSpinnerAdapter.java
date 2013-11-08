package in.co.madhur.ganalyticsdashclock;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;

public class AccountSpinnerAdapter implements SpinnerAdapter
{
	Context context;
	ArrayList<String> items=new ArrayList<String>();
	
	public AccountSpinnerAdapter(Context context, String accountName)
	{
		this.context=context;
		items.add("More");
		items.add(accountName);
		
	}

	@Override
	public int getCount()
	{
		return items.size();
	}

	@Override
	public Object getItem(int position)
	{
		return items.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public int getItemViewType(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getViewTypeCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
