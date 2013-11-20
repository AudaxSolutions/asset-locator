package com.audax.dev.forte;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.audax.dev.forte.data.Center;
import com.audax.dev.forte.maps.LocationUtils;

public class CentersAdapter extends BaseAdapter {
	private List<Center> centerList;
	private Context context;
	private Location currentLocation;

	public CentersAdapter(Context context) {
		super();
		this.centerList = new ArrayList<Center>();
		this.context = context;
	}
	
	public Context getContext() {
		return context;
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
		this.notifyDataSetChanged();
	}

	public void addAll(Collection<Center> centers) {
		this.centerList.addAll(centers);
		this.notifyDataSetChanged();
	}
	
	public void addAll(Center[] centers) {
		java.util.Collections.addAll(this.centerList, centers);
		this.notifyDataSetChanged();
	}
	
	
	
	@Override
	public boolean isEmpty() {
		return this.centerList.isEmpty();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	public void add(Center center) {
		this.centerList.add(center);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return centerList.size();
	}

	@Override
	public Object getItem(int arg0) {
		Center c= centerList.get(arg0);
		//Determine if the distance should be updated then do so
		if (c.shouldUpdateDistance() && this.currentLocation != null) {
			LocationUtils.updateDistance(currentLocation, c);
		}
		return c;
	}

	@Override
	public long getItemId(int arg0) {
		Center c = this.centerList.get(arg0);
		if (c != null) {
			c.getId().getMostSignificantBits();
		}
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			arg1 =  ((Activity)this.context).getLayoutInflater()
					.inflate(R.layout.service_center_item, arg2, false);
		}
		Center center = this.centerList.get(arg0);
		((TextView)arg1.findViewById(R.id.lbl_center_name))
			.setText(center.getName());
		((TextView)arg1.findViewById(R.id.lbl_center_category))
		.setText(center.getCategory());
		
		((TextView)arg1.findViewById(R.id.lbl_center_location))
		.setText(center.getLocation());
		
		((TextView)arg1.findViewById(R.id.lbl_center_distance))
		.setText(center.getDistance());
		
		((TextView)arg1.findViewById(R.id.lbl_center_availability))
		.setText(center.getAvailability());
		return arg1;
	}
					
}