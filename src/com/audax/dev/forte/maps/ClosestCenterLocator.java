package com.audax.dev.forte.maps;

import com.audax.dev.forte.data.Center;
import com.audax.dev.forte.data.Repository;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

public class ClosestCenterLocator extends AsyncTask<Void, Void, Center> {
	private Context context;
	private float maximumDistance = -1;
	private Location fromLocation;
	
	public interface ClosestCenterListener {
		void onCloseCenterFound(Center center, ClosestCenterLocator locator);
	}
	
	private ClosestCenterListener listener;
	

	public ClosestCenterListener getListener() {
		return listener;
	}

	public void setListener(ClosestCenterListener listener) {
		this.listener = listener;
	}

	public ClosestCenterLocator(Context context, float maximumDistance,
			Location fromLocation) {
		super();
		this.context = context;
		this.maximumDistance = maximumDistance;
		this.fromLocation = fromLocation;
	}

	public ClosestCenterLocator(Context context, Location fromLocation) {
		super();
		this.context = context;
		this.fromLocation = fromLocation;
	}
	
	

	@Override
	protected void onPostExecute(Center result) {
		super.onPostExecute(result);
		if (this.listener != null) {
			this.listener.onCloseCenterFound(result, this);
		}
	}

	public ClosestCenterLocator(Context context) {
		super();
		this.context = context;
	}

	public float getMaximumDistance() {
		return maximumDistance;
	}

	public void setMaximumDistance(float maximumDistance) {
		this.maximumDistance = maximumDistance;
	}

	public Location getFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(Location fromLocation) {
		this.fromLocation = fromLocation;
	}

	@Override
	protected Center doInBackground(Void... arg0) {
		return LocationUtils.getClosestCenter(this.fromLocation,
				(new Repository()).getAvailableCenters(context).iterator(),
				this.maximumDistance);
	}

}
