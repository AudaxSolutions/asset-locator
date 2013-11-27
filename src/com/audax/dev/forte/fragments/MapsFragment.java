package com.audax.dev.forte.fragments;

import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.audax.dev.forte.R;
import com.audax.dev.forte.data.Repository;
import com.audax.dev.forte.maps.ForteCenterMapsInterractions;
import com.audax.dev.forte.maps.MapsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

public class MapsFragment extends SupportMapFragment implements View.OnClickListener,
		GoogleMap.OnInfoWindowClickListener {

	private MapsClient client;
	private ForteCenterMapsInterractions mapsItx;

	

	@Override
	public void onPause() {
		super.onPause();
		this.client.stop();
	}

	@Override
	public void onStop() {
		super.onStop();
		this.client.stop();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (this.client == null) {
			client = new MapsClient(this.getActivity());
		}
		this.client.start();

		if (this.mapsItx == null) {
			mapsItx = new ForteCenterMapsInterractions(this.getActivity(),
					this.getMap(), client);
			mapsItx.prepareMap();
		}

		if (!markersShown) {
			final Handler handler = new Handler();
			markersShown = true;
			handler.postAtTime(new Runnable() {

				@Override
				public void run() {
					Repository repo = new Repository();
					mapsItx.loadMarkersForCenters(repo.getAvailableCenters(),
							false);

					handler.postAtTime(new Runnable() {

						@Override
						public void run() {
							captureSentCenter();
						}
					}, 400);
				}
			}, 500);
		} else {
			captureSentCenter();
		}
	}

	private boolean markersShown = false;

	public void captureSentCenter() {

		boolean animate = Boolean
				.parseBoolean(getString(R.string.animate_center_focus));

		Intent itt = this.getActivity().getIntent();
		if (itt != null) {
			Bundle b = itt.getExtras();
			if (b != null && b.containsKey("centerId")) {
				UUID id = UUID.fromString(b.getString("centerId"));
				
				//Remove when done
				
				b.remove("centerId");

				this.mapsItx.switchFocusTo(id, animate);
				
			}
			
		}
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {

	}

	@Override
	public void onClick(View arg0) {

	}

}
