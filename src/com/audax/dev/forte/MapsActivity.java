package com.audax.dev.forte;

import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.audax.dev.forte.maps.ForteCenterMapsInterractions;
import com.audax.dev.forte.maps.MapsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MapsActivity extends FragmentActivity implements View.OnClickListener, GoogleMap.OnInfoWindowClickListener {

//	private SupportMapFragment mapFragment;
//	private GoogleMap googleMap;
	private MapsClient client;
	private ForteCenterMapsInterractions mapsItx;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.map_layout);
		
		this.setupActionBar();
		
		if (client == null) {
			client = new MapsClient(this);
			
			
			
			mapsItx = new ForteCenterMapsInterractions(this, R.id.mapFragment, client);
			
			mapsItx.setOnMapReadyListener(new ForteCenterMapsInterractions.OnMapReadyListener() {
				
				@Override
				public void onMapReady(ForteCenterMapsInterractions itx) {
					captureSentCenter();
				}
			});
			
			mapsItx.prepareMap();
		}
		//initLocations();
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			this.finish();
			return true;
			//NavUtils.navigateUpFromSameTask(this);
		case R.id.action_search_in_map:
			return false;
		}
		return super.onOptionsItemSelected(item);
	}
	
			
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	//Look for center ids that was passed to activity via intent
	public void captureSentCenter() {
		
		boolean animate = Boolean.parseBoolean(getString(R.string.animate_center_focus));
		
		Intent itt = this.getIntent();
		
		if (itt != null && itt.getExtras().containsKey("centerId")) {
			
			UUID id = UUID.fromString(itt.getExtras().getString("centerId"));
			
			this.mapsItx.switchFocusTo(id, animate);	
		}
	}

//

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		if (this.mapsItx != null) {
			this.mapsItx.configureSearch(menu, R.id.action_search_in_map);
		}else {
			Handler h = new Handler();
			h.postAtTime(new Runnable() {
				
				@Override
				public void run() {
					if (mapsItx != null) {
						mapsItx.configureSearch(menu, R.id.action_search_in_map);
					}
				}
			}, 2000);
		}
		return super.onCreateOptionsMenu(menu);
	}



	@Override
	protected void onPause() {
		super.onPause();
		this.client.stop();
	}



	@Override
	protected void onStop() {
		
		super.onStop();
		//this.mapsItx.stopListening();
		this.client.stop();
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		}
		
	}

	
	@Override
	public void onInfoWindowClick(Marker arg0) {
//		Center center = this.getCenter(arg0);
//		requestDrivingDirections(center);
	}
	
	

}
