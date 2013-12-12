package com.audax.dev.forte;

import java.lang.reflect.Field;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.audax.dev.forte.fragments.ForteMapsFragment;
import com.audax.dev.forte.fragments.LoadingFragment;
import com.audax.dev.forte.maps.MapsClient;
import com.audax.dev.forte.util.SystemUiHider;
import com.audax.dev.forte.web.ApplicationRegistry;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends FragmentActivity implements MapsClient.ClientListener, View.OnClickListener {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
//	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	//private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	
	//private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	//private SystemUiHider mSystemUiHider;
	
	private ForteMapsFragment mapsFragment;
	
	private void setupNew() {
		
		//Hide initialy
		LoadingFragment lf = (LoadingFragment)
				this.getSupportFragmentManager().findFragmentById(R.id.loading_fragment);
		lf.setCompleteTask(new Runnable() {
			
			@Override
			public void run() {
				//ab.show();
				switchToMapsFragment();
			}
		});
		lf.startLoading();
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	    //Force overflow button to show
		try {
	        ViewConfiguration config = ViewConfiguration.get(this);
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception ex) {
	        // Ignore
	    }
		this.setupNew();
	}

	
	protected void switchToMapsFragment() {
		
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction trx = fm.beginTransaction();
		if (mapsFragment == null) {
			mapsFragment = new ForteMapsFragment ();
		}
		
		trx.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		
		LoadingFragment lf = (LoadingFragment)
				fm.findFragmentById(R.id.loading_fragment);
		
		trx.remove(lf);
		
		trx.add(R.id.fragment_container, mapsFragment, "maps");
		
		
		//I don't want user to switch back to 'loading'
		//trx.addToBackStack(null);
		
		trx.commit();
		
		trx.show(mapsFragment);
	}

	protected void showNearestCenter() {
		View v = this.findViewById(R.id.lay_nearest);
		v.setVisibility(View.VISIBLE);
	}

	protected void switchToMenuActivity() {
		Intent itt = new Intent(this, MainMenuActivity.class);
		this.startActivity(itt);
	}
	
	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			// mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_item_list:
			Intent itt = new Intent(this, CenterListActivity.class);
			this.startActivity(itt);
			return true;
		case R.id.main_item_news:
			itt = new Intent(this, WebViewActivity.class);
			itt.putExtra("application", ApplicationRegistry.APP_NEWS);
			this.startActivity(itt);
			return true;
		case R.id.main_item_products:
			itt = new Intent(this, ProductListActivity.class);
			this.startActivity(itt);
			return true;
		}
		return super.onOptionsItemSelected(item);
//		switch (item.getItemId()) {
//		case R.id.home:
//			Intent itt = new Intent(this, MainMenuActivity.class);
//			this.startActivity(itt);
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
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

	public void showMaps(View v) {
		Intent mapIntent = new Intent(this, MapsActivity.class);
		this.startActivity(mapIntent);
	}
	
	

	@Override
	public void onLocationChanged(MapsClient client, Location location) {
		
	}

	@Override
	protected void onStop() {
		
		super.onStop();
//		if (mapsClient != null) {
//			mapsClient.stop();
//		}
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_home:
			switchToMenuActivity();
			break;
		}
	}


	
	
}
