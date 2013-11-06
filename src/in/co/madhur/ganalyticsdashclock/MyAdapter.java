package in.co.madhur.ganalyticsdashclock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter
{
	ArrayList<GNewProfile> items;
	Context context;
	
	public MyAdapter(ArrayList<GNewProfile> items, Context context)
	{
		this.items=items;
		this.context=context;
		
		
		
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
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View vi=convertView;
		if(vi==null)
			vi=GetInflater(context).inflate(R.layout.list_row, null);
		
		GNewProfile newProfile=(GNewProfile) this.getItem(position);
		
		TextView propertyText=(TextView) vi.findViewById(R.id.property_name);
		
		TextView profileText=(TextView) vi.findViewById(R.id.profile_name);
		
		RadioButton radioSelect=(RadioButton) vi.findViewById(R.id.radioselect);
		
		
		propertyText.setText(newProfile.getPropertyName());
		profileText.setText(newProfile.getProfileName());
		
		return vi;
	}
	
	private static LayoutInflater GetInflater(Context context)
	{

		return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	
}
