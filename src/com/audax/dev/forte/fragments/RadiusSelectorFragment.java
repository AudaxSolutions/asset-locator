package com.audax.dev.forte.fragments;

import android.app.Fragment;

public abstract class RadiusSelectorFragment extends Fragment {
	public abstract float getRadius();
	
	
	public interface OnRadiusChangedListener {
		void onRadiusChanged(RadiusSelectorFragment selector);
	}
	
	private OnRadiusChangedListener radiusChangedListener;

	public OnRadiusChangedListener getRadiusChangedListener() {
		return radiusChangedListener;
	}

	public void setRadiusChangedListener(
			OnRadiusChangedListener radiusChangedListener) {
		this.radiusChangedListener = radiusChangedListener;
	}
	
	public abstract String getSelectedUnit() ;
	
}
