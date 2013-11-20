package com.audax.dev.forte;

import java.util.Collection;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.audax.dev.forte.data.Center;
import com.audax.dev.forte.data.Repository;
import com.audax.dev.forte.maps.ForteCenterMapsInterractions;
import com.audax.dev.forte.maps.LocationUtils;

public class StationLocatorActivity extends Activity {
	//private ArrayAdapter<String> standardRadiusList;
	private ForteCenterMapsInterractions mapItx;
	private RadiusSelectorFragment radiusSelector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_locator);
		// Show the Up button in the action bar.
//		
//		standardRadiusList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
//		
//		standardRadiusList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		
//		
//		Spinner cboRadius = ((Spinner)this.findViewById(R.id.cbo_list_radius));
//		
//		cboRadius.setAdapter(standardRadiusList);
//		
//		cboRadius.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int pos, long arg3) {
//				
//				final String distance = (String)arg0.getItemAtPosition(pos);
//				Handler handler = new Handler();
//				handler.postAtTime(new Runnable() {
//					
//					@Override
//					public void run() {
//						showPointsWithin(distance);
//					}
//				}, 300);
//				
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});
//		
		radiusSelector = (RadiusSelectorFragment)this.getFragmentManager()
							.findFragmentById(R.id.radius_selector_fragment);
		
		radiusSelector.setRadiusChangedListener(new RadiusSelectorFragment.OnRadiusChangedListener() {
			
			@Override
			public void onRadiusChanged(final RadiusSelectorFragment selector) {
				Handler handler = new Handler();
				handler.postAtTime(new Runnable() {
					
					@Override
					public void run() {
						showCentersWithin(selector);
					}
				}, 500);
			}
		});
		
		this.mapItx = new ForteCenterMapsInterractions(this, R.id.nearest_center_map, null);
		
		this.mapItx.prepareMap();
		
		setupActionBar();
		
		fillAppropriateItems();
		
//		CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				fillAppropriateItems();
//			};
//		};
//		
//		((RadioButton)findViewById(R.id.rdo_kilometers)).setOnCheckedChangeListener(listener);
//		
//		((RadioButton)findViewById(R.id.rdo_miles)).setOnCheckedChangeListener(listener);
//		
		this.mapItx.setCurrentLocation(this.getCurrentLocation());
	}
	
	private Location getCurrentLocation() {
		return ((NearestCenterFragmentX)this.getFragmentManager()
				.findFragmentById(R.id.nearest_location_fragment)
				).getCurrentLocation();
	}
	
	protected void showCentersWithin(RadiusSelectorFragment selector) {
		
		float radius = selector.getRadius();
		
		NearestCenterFragmentX nFr = (NearestCenterFragmentX) getFragmentManager()
				.findFragmentById(R.id.nearest_location_fragment);
		
		Repository repo = new Repository();
		
		Collection<Center> centers = LocationUtils.collectionCentersCloseTo(nFr.getCurrentLocation(),
				radius, repo.getAvailableCenters());
		
		if (centers.isEmpty()) {
			
			TextView tv = (TextView)this.findViewById(R.id.lbl_no_centers_found);
			
			tv.setText(String.format(getString(R.string.no_centers_within),
					radius, selector.getSelectedUnit()));
			
			tv.setVisibility(View.VISIBLE);
			
			this.findViewById(R.id.map_frame).setVisibility(View.INVISIBLE);
		}else {
			findViewById(R.id.lbl_no_centers_found).setVisibility(View.GONE);
			
			this.mapItx.loadMarkersForCenters(centers, true);
			
			this.findViewById(R.id.map_frame).setVisibility(View.VISIBLE);
		}

	}
	

	private void fillAppropriateItems() {
//		String unit = ((RadioButton)this.findViewById(R.id.rdo_kilometers)).isChecked()
//				? getString(R.string.kilometers) : getString(R.string.miles);
//		String[] units = getResources().getStringArray(R.array.radius_list);
//		
//		standardRadiusList.clear();
//		
//		for (String u : units) {
//			standardRadiusList.add(String.format(u, unit));
//		}
	}
	
//	private float getUnitMultiplier() {
//		if (((RadioButton)this.findViewById(R.id.rdo_kilometers)).isChecked()) {
//			return (1 / LocationUtils.MILES_TO_KILO);
//		}
//		return 1.0f;
//	}
//

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.station_locator, menu);
		return true;
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
