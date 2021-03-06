package com.audax.dev.forte.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.audax.dev.forte.CenterListActivity;
import com.audax.dev.forte.ProductListActivity;
import com.audax.dev.forte.R;
import com.audax.dev.forte.WebViewActivity;
import com.audax.dev.forte.maps.ForteCenterMapsInterractions;
import com.audax.dev.forte.maps.MapsClient;
import com.audax.dev.forte.web.ApplicationRegistry;
import com.google.android.gms.maps.SupportMapFragment;

public class ForteMapsFragment extends SupportMapFragment {


	private ForteCenterMapsInterractions mapsItx;
	
	
	private MapsClient mapsClient;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (mapsItx == null) {
			if (mapsClient == null) {
				mapsClient = new MapsClient(this.getActivity());
			}
			
			mapsItx = new ForteCenterMapsInterractions(this.getActivity(), this.getMap(), mapsClient);
			mapsItx.setOnMapReadyListener(new ForteCenterMapsInterractions.OnMapReadyListener() {
				
				@Override
				public void onMapReady(ForteCenterMapsInterractions itx) {
					mapsItx.showClosestCenters();
				}
			});
		}
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(FLAG_PREPARE)) {
				this.isPrepared = savedInstanceState.getBoolean(FLAG_PREPARE);
			}
			if (savedInstanceState.containsKey(FLAG_MENU_SET)) {
				this.menuInited = savedInstanceState.getBoolean(FLAG_MENU_SET);
			}
		}
		this.setHasOptionsMenu(true);
		
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_item_list:
			Intent itt = new Intent(this.getActivity(), CenterListActivity.class);
			this.startActivity(itt);
			return true;
		case R.id.main_item_news:
			itt = new Intent(this.getActivity(), WebViewActivity.class);
			itt.putExtra("application", ApplicationRegistry.APP_NEWS);
			this.startActivity(itt);
			return true;
		case R.id.main_item_products:
			itt = new Intent(this.getActivity(), ProductListActivity.class);
			this.startActivity(itt);
			return true;
		case R.id.action_search_in_home_map:
			return false;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean menuInited = false;
	@Override
	public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
		if (!menuInited) {
			menuInited = true;
			inflater.inflate(R.menu.maps_menu, menu);
			
			if (this.mapsItx != null) {
				this.mapsItx.configureSearch(menu, R.id.action_search_in_home_map);
			}else {
				Handler h = new Handler();
				h.postAtTime(new Runnable() {
					
					@Override
					public void run() {
						if (mapsItx != null) {
							mapsItx.configureSearch(menu, R.id.action_search_in_home_map);
						}
					}
				}, 2000);
			}
		}
		
	}


	

	protected void doSearchForCenter(CharSequence qry) {
		
	}

	private boolean isPrepared;

	@Override
	public void onResume() {
		super.onResume();
		if (this.mapsClient != null) {
			this.mapsClient.start();
		}
		if (this.isPrepared != true) {
			this.isPrepared = true;
			mapsItx.prepareMap();
		}
	}
	
	
	private static final String FLAG_PREPARE = "forte-maps:isPrepared",
								FLAG_MENU_SET = "forte-maps:menuSet";


	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBoolean(FLAG_PREPARE, this.isPrepared);
		outState.putBoolean(FLAG_MENU_SET, menuInited);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (this.mapsClient != null) {
			this.mapsClient.stop();
		}
	}
	
	
	
}
