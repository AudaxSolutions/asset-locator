package com.audax.dev.forte.fragments;

import java.util.Collection;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.audax.dev.forte.Constants;
import com.audax.dev.forte.MapsActivity;
import com.audax.dev.forte.R;
import com.audax.dev.forte.data.Center;
import com.audax.dev.forte.data.Repository;
import com.audax.dev.forte.maps.LocationUtils;
import com.audax.dev.forte.maps.MapsClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;
public class NearestCenterFragment extends Fragment implements MapsClient.ClientListener, View.OnClickListener {

	private MapsClient mapsClient;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mapsClient = new MapsClient(this.getActivity());
		
		mapsClient.setClientListener(this);
	}
	
	private View rootView;
	

	public Location getCurrentLocation() {
		return mapsClient.getCurrentLocation();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.nearest_center_view, container);
		this.rootView = view;
		view.findViewById(R.id.lnk_view_in_map).setOnClickListener(this);
		return view;
	}
	
	

	public MapsClient getMapsClient() {
		return mapsClient;
	}

	@Override
	public void onPause() {
		
		super.onPause();
		this.mapsClient.stop();
	}
	
	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}


	@Override
	public void onResume() {
		
		super.onResume();
		
		int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getActivity());
		if (code == ConnectionResult.SUCCESS) {
			Log.i("Google Play", "Service is available");
			
			this.mapsClient.start();
		} else {
			 
			 Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
	                    code,
	                    this.getActivity(),
	                    Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST);

	            // If Google Play services can provide an error dialog
	            if (errorDialog != null) {
	                // Create a new DialogFragment for the error dialog
	                ErrorDialogFragment errorFragment =
	                        new ErrorDialogFragment();
	                // Set the dialog in the DialogFragment
	                errorFragment.setDialog(errorDialog);
	                // Show the error dialog in the DialogFragment
	                FragmentActivity fa = (FragmentActivity)this.getActivity();
	                errorFragment.show(fa.getSupportFragmentManager(),
	                        "Location Updates");
	            }
		}
	}

	@Override
	public void onStop() {
		
		super.onStop();
		this.mapsClient.stop();
	}
	
	private Center closestCenter;
	
	public void refreshLocation() {
		this.mapsClient.start();
	}
	
	
	@Override
	public void onLocationChanged(MapsClient client) {
		Repository repo = new Repository();
		Collection<Center> centers = repo.getAvailableCenters();
		Location loc = client.getCurrentLocation();
		LatLng ll = new LatLng(loc.getLatitude(), loc.getLongitude());
		client.stop();
		Center closest = LocationUtils.getClosest(ll, centers);
		
		String distanceText = String.format("%1$.2f%2$s/%3$.2f%4$s", closest.getDistanceInMiles(),
					this.getString(R.string.miles),
					closest.getDistanceInMiles() * LocationUtils.MILES_TO_KILO,
					this.getActivity().getString(R.string.kilometers));
		
		closest.setDistance(distanceText);
		
		this.closestCenter = closest;
		((TextView)this.rootView
					.findViewById(R.id.lbl_nearest_center_name))
					.setText(closest.getName());
		((TextView)this.rootView
				.findViewById(R.id.lbl_distance_away))
				.setText(distanceText);
		
		this.rootView.findViewById(R.id.lbl_loading_nearest).setVisibility(View.GONE);
		this.rootView.findViewById(R.id.lbl_nearest_center_name).setVisibility(View.VISIBLE);
		this.rootView.findViewById(R.id.lbl_distance_away).setVisibility(View.VISIBLE);
		this.rootView.findViewById(R.id.lnk_view_in_map).setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.lnk_view_in_map:
			if (this.closestCenter != null) {
				
				Intent itt = new Intent(this.getActivity(), MapsActivity.class);
				
				itt.putExtra("centerId", closestCenter.getId().toString());
				
				//itt.putExtra("marker", mo);
				
				this.startActivity(itt);
			}
			break;
		}
	}
	
}
