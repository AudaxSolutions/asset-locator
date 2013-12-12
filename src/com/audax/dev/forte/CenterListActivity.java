package com.audax.dev.forte;



import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.audax.dev.forte.data.Center;
import com.audax.dev.forte.data.CentersAdapter;
import com.audax.dev.forte.data.CentersLoaderTask;
import com.audax.dev.forte.maps.MapsClient;

public class CenterListActivity extends ListActivity implements MapsClient.ClientListener {
	
	private CentersAdapter centersAdapter;
	private MapsClient client;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.centers_list_view);
		
		client = new MapsClient(this);
		
		client.setClientListener(new MapsClient.ClientListenerAdapter() {
			
			@Override
			public void onLocationChanged(MapsClient client, Location location) {
				client.stop();
				loadCenters(location);
			}
		});
		client.start();
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.center_list, menu);
		return super.onCreateOptionsMenu(menu);
	}





	protected void loadCenters(final Location location) {
		centersAdapter = new CentersAdapter(this);
		centersAdapter.setCurrentLocation(location);
		setListAdapter(centersAdapter);
		
		CentersLoaderTask task = new CentersLoaderTask(this);
		
		task.setLoaderListener(new CentersLoaderTask.LoaderListener() {
			
			@Override
			public void onLoadStarted(CentersLoaderTask loader) {
			}
			
			@Override
			public void onCompleted(CentersLoaderTask loader) {
				
			}
			
			@Override
			public void onCenterFound(Center center, CentersLoaderTask loader) {
				//LocationUtils.updateDistance(location, center);
				centersAdapter.add(center);
			}
		});
		task.execute();
	}



	@Override
	protected void onResume() {
		super.onResume();
	}



	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.center_list, menu);
		
		return true;
	}*/

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

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Center center = (Center)l.getAdapter().getItem(position);
		
		/*MarkerOptions mo = new MarkerOptions();
		
		mo.position(center.getPosition());
		
		mo.title(center.getName());
		
		mo.snippet(center.getLocation());
		*/
		Intent itt = new Intent(this, MapsActivity.class);
		
		itt.putExtra("centerId", center.getId().toString());
		
		//itt.putExtra("marker", mo);
		
		this.startActivity(itt);
	}



	@Override
	public void onLocationChanged(MapsClient client, Location location) {
		if (this.centersAdapter != null) {
			this.centersAdapter.setCurrentLocation(location);
			client.stop();
		}
	}	
}
