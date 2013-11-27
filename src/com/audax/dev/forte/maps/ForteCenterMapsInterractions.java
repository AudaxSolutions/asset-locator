package com.audax.dev.forte.maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.audax.dev.forte.R;
import com.audax.dev.forte.data.Center;
import com.audax.dev.forte.data.ListUtils;
import com.audax.dev.forte.data.Repository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ForteCenterMapsInterractions {
	private Activity context;
	private int mapsFragmentId;
	private GoogleMap googleMap;
	private Location currentLocation;
	
	private com.audax.dev.forte.maps.MapsClient mapsClient;
	
	public ForteCenterMapsInterractions(Activity context, int mapsFragmentId,
			MapsClient mapsClient) {
		super();
		this.context = context;
		this.mapsFragmentId = mapsFragmentId;
		this.mapsClient = mapsClient;
	}
	
	
	

	public ForteCenterMapsInterractions(Activity context, GoogleMap googleMap,
			MapsClient mapsClient) {
		super();
		this.context = context;
		this.googleMap = googleMap;
		this.mapsClient = mapsClient;
	}


	private boolean setupCompleted = false;

	public void prepareMap() {
		if (!setupCompleted) {
			setupCompleted = true;
		}
		if (googleMap == null) {
			
			if (mapsClient == null) {
				mapsClient = new MapsClient(context);
			}
			
			if (this.context instanceof FragmentActivity) {
				FragmentActivity fAct = (FragmentActivity)context;
				SupportMapFragment mf = (SupportMapFragment) fAct.getSupportFragmentManager()
											.findFragmentById(this.mapsFragmentId);
				googleMap = mf.getMap();
			}else {
				MapFragment mf = (MapFragment)context.getFragmentManager()
								.findFragmentById(this.mapsFragmentId);
				
				googleMap = mf.getMap();
			}
			
		    /*if (mapFragment != null) {
				Log.i("Fragment", "Google Maps fragment found");
			}else {
				Log.e("Fragment", "Google Maps fragment not found");
			}
			*/
			
		    
		}
		
		if (googleMap != null) {
	    	
	    	googleMap.setLocationSource(mapsClient);
	    	
	    	googleMap.setMyLocationEnabled(true);
	    	
	    	googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
	    	
	    	googleMap.setOnInfoWindowClickListener(new  GoogleMap.OnInfoWindowClickListener() {

				@Override
				public void onInfoWindowClick(Marker arg0) {
					Center center = getCenter(arg0);
					requestDrivingDirections(center);
				}
	    		
	    	});
	    	
	    	googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
				
				@Override
				public void onCameraChange(CameraPosition arg0) {
					//Location l = client.getCurrentLocation();
					//Log.w("Location", "Could not find current");
					
					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Repository.PT_LAGOS, 10));
					
					googleMap.setOnCameraChangeListener(null);
				}
			});
	    }
	}

	public void stopListening() {
		if (mapsClient != null) {
			mapsClient.stop();
		}
	}
	
	private class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
		@Override
		public View getInfoContents(Marker arg0) {
			return null;
		}
		
		private View infoWindow;

		@Override
		public View getInfoWindow(Marker arg0) {
			if (infoWindow == null) {
				infoWindow = context.getLayoutInflater().inflate(R.layout.center_marker_info, null);
				//infoWindow.findViewById(R.id.btn_info_directions).setOnClickListener(MapsActivity.this);
				infoWindow.setClickable(false);
			}
			
			Center center = getCenter(arg0);
			//tag the center to use for location directions
			//infoWindow.findViewById(R.id.btn_info_directions).setTag(center);
			((TextView) infoWindow.findViewById(R.id.lbl_info_center_name)).setText(center.getName());
			((TextView) infoWindow.findViewById(R.id.lbl_info_location)).setText(center.getLocation());
			((TextView) infoWindow.findViewById(R.id.lbl_info_availability)).setText(String.format("%s: %s",
					center.getCategory(), center.getAvailability()));
			return infoWindow;
		}
		
	}
	
	private static class CenterMarkerMap {
		public Marker marker;
		public UUID centerId;
		public CenterMarkerMap(Marker marker, UUID centerId) {
			this.marker = marker;
			this.centerId = centerId;
		}
		
	}
	
	private final ArrayList<CenterMarkerMap> centerMarkers = new ArrayList<CenterMarkerMap>();
	
	private Center getCenter(final Marker marker) {
		CenterMarkerMap map = ListUtils.getFirst(centerMarkers, new ListUtils.Matcher<CenterMarkerMap>() {

			@Override
			public boolean matches(CenterMarkerMap p0) {
				return p0.marker.equals(marker);
			}
		});
		
		if (map != null) {
			Repository repo = new Repository();
			return repo.getCenter(map.centerId);
		}
		return null;
	}

	public void loadMarkersForCenters(Collection<Center> centers, boolean clearFirst, LatLng center) {
		if (googleMap == null) {
			throw new NullPointerException("googleMap is not set");
		}
		
		if (clearFirst) {
			googleMap.clear();
			centerMarkers.clear();
		}
		
		for (Center loc : centers) {
			MarkerOptions mo = new MarkerOptions();
			
			mo.position(loc.getPosition());
			
			mo.title(loc.getName());
			
			mo.icon(BitmapDescriptorFactory.fromResource(loc.resolveIconResource()));
			
			mo.snippet(loc.getLocation());
			
			Marker marker = googleMap.addMarker(mo);
			
			centerMarkers.add(new CenterMarkerMap(marker, loc.getId()));
		}
	}

	public void loadMarkersForCenters(Collection<Center> centers, boolean clearFirst) {
		this.loadMarkersForCenters(centers, clearFirst, null);
	}
	
	
	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	private void requestDrivingDirections(Center center) {
		//Construct intent for driving directions
		Location loc = currentLocation;
		if (loc == null && mapsClient != null) {
			loc = mapsClient.getCurrentLocation();
			currentLocation = loc;
		}
		if (loc != null) {
			String apiPath = context.getString(R.string.google_maps_uri);
			//The start char for parameters
			//If '?' is already present the use '&'
			char parametersChar = apiPath.indexOf('?') == -1 ?  '?' : '&';
			
			StringBuilder builder = new StringBuilder();
			
			builder.append(apiPath);
			
			builder.append(parametersChar);
			
			builder.append("saddr=").append(LocationUtils.escapeLocation(loc));
			
			builder.append("&daddr=").append(LocationUtils.escapeLocation(center.getPosition()));
			
			builder.append("&mode=d");
			
			//Log.i("Requesting driving directions using %s", builder.toString());
			
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(builder.toString()));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            
            context.startActivity(intent);

		}else {
			Toast.makeText(context, R.string.no_location, Toast.LENGTH_SHORT).show();
		}
	}
	
	public Activity getContext() {
		return context;
	}

	public GoogleMap getGoogleMap() {
		return googleMap;
	}

	public void switchFocusTo(final UUID centerId) {
		this.switchFocusTo(centerId, false);
		
	}
	public void switchFocusTo(final UUID centerId, boolean animate) {

		CenterMarkerMap map = ListUtils.getFirst(centerMarkers, new ListUtils.Matcher<CenterMarkerMap>() {

			@Override
			public boolean matches(CenterMarkerMap p0) {
				int co = p0.centerId.compareTo(centerId);
				
				return co == 0;
			}
		});
		
		if (animate) {
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(map.marker.getPosition(), 10));
		}else {
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(map.marker.getPosition(), 10));
		}
		map.marker.showInfoWindow();
	}
	
}
