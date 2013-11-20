package com.audax.dev.forte;

import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.audax.dev.forte.data.Repository;
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
		
		client = new MapsClient(this);
		
		this.setupActionBar();
		
		mapsItx = new ForteCenterMapsInterractions(this, R.id.mapFragment, client);
		
		mapsItx.prepareMap();
		
		final Handler handler = new Handler();
		handler.postAtTime(new Runnable() {
			
			@Override
			public void run() {
				Repository repo = new Repository();
				mapsItx.loadMarkersForCenters(repo.getAvailableCenters(), false);
				
				handler.postAtTime(new Runnable() {
					
					@Override
					public void run() {
						captureSentCenter();
					}
				}, 400);
			}
		}, 500);
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
			
			//NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
//	private class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
//		@Override
//		public View getInfoContents(Marker arg0) {
//			return null;
//		}
//		
//		private View infoWindow;
//
//		@Override
//		public View getInfoWindow(Marker arg0) {
//			if (infoWindow == null) {
//				infoWindow = getLayoutInflater().inflate(R.layout.center_marker_info, null);
//				//infoWindow.findViewById(R.id.btn_info_directions).setOnClickListener(MapsActivity.this);
//				infoWindow.setClickable(false);
//			}
//			
//			Center center = getCenter(arg0);
//			//tag the center to use for location directions
//			//infoWindow.findViewById(R.id.btn_info_directions).setTag(center);
//			((TextView) infoWindow.findViewById(R.id.lbl_info_center_name)).setText(center.getName());
//			((TextView) infoWindow.findViewById(R.id.lbl_info_location)).setText(center.getLocation());
//			((TextView) infoWindow.findViewById(R.id.lbl_info_availability)).setText(String.format("%s: %s",
//					center.getCategory(), center.getAvailability()));
//			return infoWindow;
//		}
//		
//	}
//	
//	private Center getCenter(final Marker marker) {
//		CenterMarkerMap map = ListUtils.getFirst(centerMarkers, new ListUtils.Matcher<CenterMarkerMap>() {
//
//			@Override
//			public boolean matches(CenterMarkerMap p0) {
//				return p0.marker.equals(marker);
//			}
//		});
//		
//		if (map != null) {
//			Repository repo = new Repository();
//			return repo.getCenter(map.centerId);
//		}
//		return null;
//	}
//
//	private Repository store;
//	
//	private static class CenterMarkerMap {
//		public Marker marker;
//		public UUID centerId;
//		public CenterMarkerMap(Marker marker, UUID centerId) {
//			this.marker = marker;
//			this.centerId = centerId;
//		}
//		
//	}
//	
//	private ArrayList<CenterMarkerMap> centerMarkers = new ArrayList<MapsActivity.CenterMarkerMap>();
//	
//	private class LocationsLoadTask extends AsyncTask<String, Center, Void> {
//
//		@Override
//		protected Void doInBackground(String... providers) {
//			for (Center l : store.getAvailableCenters()) {
//				this.publishProgress(l);
//			}
//			return null;
//		}
//
//		@Override
//		protected void onProgressUpdate(Center... values) {
//			for (Center loc : values) {
//				MarkerOptions mo = new MarkerOptions();
//				
//				mo.position(loc.getPosition());
//				
//				mo.title(loc.getName());
//				
//				mo.icon(BitmapDescriptorFactory.fromResource(loc.resolveIconResource()));
//				
//				mo.snippet(loc.getLocation());
//				
//				Marker marker = googleMap.addMarker(mo);
//				
//				centerMarkers.add(new CenterMarkerMap(marker, loc.getId()));
//			}
//		}

//		@Override
//		protected void onPostExecute(Void result) {
//			super.onPostExecute(result);
//			captureSentCenter();
//		}
		
//		
//	}
//	
//	
//	private void initLocations() {	
//		if (store == null) {
//			store = new Repository();
//		}
//		if (googleMap == null) {
//			
//		    if (mapFragment == null) {
//		    	mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.mapFragment);
//		    }
//		    /*if (mapFragment != null) {
//				Log.i("Fragment", "Google Maps fragment found");
//			}else {
//				Log.e("Fragment", "Google Maps fragment not found");
//			}
//			*/
//		    
//			googleMap = mapFragment.getMap();
//			
//		    if (googleMap != null) {
//		    	
//		    	googleMap.setLocationSource(client);
//		    	
//		    	googleMap.setMyLocationEnabled(true);
//		    	
//		    	googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
//		    	
//		    	googleMap.setOnInfoWindowClickListener(this);
//		    	
//		    	googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
//					
//					@Override
//					public void onCameraChange(CameraPosition arg0) {
//						//Location l = client.getCurrentLocation();
//						//Log.w("Location", "Could not find current");
//						
//						googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Repository.PT_LAGOS, 10));
//						
//						googleMap.setOnCameraChangeListener(null);
//					}
//				});
//		    }
//		}
//		if (googleMap != null) {
//			
//			//Mark current location
//			LocationsLoadTask task = new LocationsLoadTask();
//			task.execute(LocationManager.NETWORK_PROVIDER);
//		}
//	}
//
//
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
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

//	private void requestDrivingDirections(Center center) {
//		//Construct intent for driving directions
//		Location loc = this.client.getCurrentLocation();
//		if (loc != null) {
//			String apiPath = getString(R.string.google_maps_uri);
//			//The start char for parameters
//			//If '?' is already present the use '&'
//			char parametersChar = apiPath.indexOf('?') == -1 ?  '?' : '&';
//			
//			StringBuilder builder = new StringBuilder();
//			
//			builder.append(apiPath);
//			
//			builder.append(parametersChar);
//			
//			builder.append("saddr=").append(LocationUtils.escapeLocation(loc));
//			
//			builder.append("&daddr=").append(LocationUtils.escapeLocation(center.getPosition()));
//			
//			builder.append("&mode=d");
//			
//			//Log.i("Requesting driving directions using %s", builder.toString());
//			
//			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                    Uri.parse(builder.toString()));
//            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
//            
//            this.startActivity(intent);
//
//		}else {
//			Toast.makeText(this, R.string.no_location, Toast.LENGTH_SHORT).show();
//		}
//	}

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
