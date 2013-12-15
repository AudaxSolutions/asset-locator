package com.audax.dev.forte.maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.audax.dev.forte.R;
import com.audax.dev.forte.RunnableTrain;
import com.audax.dev.forte.data.Center;
import com.audax.dev.forte.data.CentersLoaderTask;
import com.audax.dev.forte.data.ListUtils;
import com.audax.dev.forte.data.Repository;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

public class ForteCenterMapsInterractions {
	private Activity context;
	private int mapsFragmentId = 0;
	private GoogleMap googleMap;
	private Location currentLocation;
	public static final String TAG = "mapsItx";
	
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
	
	//private SearchView searchView;
	public void configureSearch(Menu menu, int searchViewId) {
//		 MenuItem searchItem = menu.findItem(searchViewId);
//		 searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//		 
//		 searchView.setOnSearchClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				CharSequence qry = searchView.getQuery();
//				doSearchForCenter(qry);
//			}
//		 });
	}

	protected void doSearchForCenter(CharSequence qry) {
		switchFocusTo(qry.toString(), false);
	}
	
	private boolean setupCompleted = false;
	
	public interface OnMapReadyListener {
		void onMapReady(ForteCenterMapsInterractions itx);
	}
	
	private OnMapReadyListener onMapReadyListener;
	

	public OnMapReadyListener getOnMapReadyListener() {
		return onMapReadyListener;
	}


	public void setOnMapReadyListener(OnMapReadyListener onMapReadyListener) {
		this.onMapReadyListener = onMapReadyListener;
	}


	private Marker myLocationMarker;

	public void prepareMap() {
		if (!setupCompleted) {
			setupCompleted = true;
		}else {
			return;
		}
		if (googleMap == null) {
			
			if (mapsClient == null) {
				mapsClient = new MapsClient(context);
			}
			
			if (this.context instanceof FragmentActivity) {
				FragmentActivity fAct = (FragmentActivity)context;
				
				if (mapsFragmentId != 0) {
					SupportMapFragment mf = (SupportMapFragment) fAct.getSupportFragmentManager()
							.findFragmentById(mapsFragmentId);
					googleMap = mf.getMap();
				}else {
					SupportMapFragment mf = (SupportMapFragment) fAct.getSupportFragmentManager()
							.findFragmentByTag("maps");
					googleMap = mf.getMap();
				}
				
			}else {
				if (mapsFragmentId != 0) {
					MapFragment mf = (MapFragment)context.getFragmentManager()
							.findFragmentById(mapsFragmentId);
			
					googleMap = mf.getMap();
				}else {
					MapFragment mf = (MapFragment)context.getFragmentManager()
							.findFragmentByTag("maps");
			
					googleMap = mf.getMap();
				}
				
			}
			
		    /*if (mapFragment != null) {
				Log.i("Fragment", "Google Maps fragment found");
			}else {
				Log.e("Fragment", "Google Maps fragment not found");
			}
			*/
		}
		
		final MapsClient.ClientListener oldL = mapsClient.getClientListener();
		mapsClient.setClientListener(new MapsClient.ClientListenerAdapter() {
			
			@Override
			public void onLocationChanged(MapsClient client, Location location) {
				currentLocation = location;
				client.stop();
				client.setClientListener(oldL);
			}
		});
		this.mapsClient.start();
		
		
		
		if (googleMap != null) {
	    	
	    	googleMap.setLocationSource(mapsClient);
	    	
	    	googleMap.setMyLocationEnabled(true);
	    	
	    	googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
	    	
	    	googleMap.setOnInfoWindowClickListener(new  GoogleMap.OnInfoWindowClickListener() {

				@Override
				public void onInfoWindowClick(final Marker arg0) {
					if (arg0.equals(myLocationMarker)) {
						ensureNearestCenterLoaded(false, new Runnable() {

							@Override
							public void run() {
								requestDrivingDirections(arg0, null);
							}
						});
						
					}else {
						Center center = getCenter(arg0);
						requestDrivingDirections(arg0, center);
					}
				}
	    		
	    	});
	    	
	    	
	    	googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
				
				@Override
				public void onCameraChange(CameraPosition arg0) {
					//Location l = client.getCurrentLocation();
					
					
					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Repository.PT_LAGOS, context.getResources().getInteger(R.integer.default_zoom_level)));
					
					googleMap.setOnCameraChangeListener(null);
					
					if (captureMarkerForCurrentLocation(true)) {
						ensureNearestCenterLoaded(true, new Runnable() {
							
							@Override
							public void run() {
								if (myLocationMarker != null) {
									myLocationMarker.showInfoWindow();
								}
								loadMarkers(new Runnable() {
									
									@Override
									public void run() {
										if (onMapReadyListener != null) {
								    		onMapReadyListener.onMapReady(ForteCenterMapsInterractions.this);
								    	}
									}
								});
								
							}
						});
					}
				}
			});
	    }
	}
	
	protected void loadMarkers(final Runnable completeCallback) {
		CentersLoaderTask task = new CentersLoaderTask(context);
		task.setLoaderListener(new CentersLoaderTask.LoaderListener() {
			
			@Override
			public void onCenterFound(Center center, CentersLoaderTask task) {
				addMarker(center);
			}

			@Override
			public void onCompleted(CentersLoaderTask loader) {
				if (completeCallback != null) {
					completeCallback.run();
				}
			}

			@Override
			public void onLoadStarted(CentersLoaderTask loader) {
				//clear();
			}
		});
		task.execute();
	}

	protected boolean captureMarkerForCurrentLocation(boolean b) {
		if (myLocationMarker == null || b) {
			if (myLocationMarker != null) {
				myLocationMarker.remove();
			}
			Location location = getCurrentLocation();
			if (location != null) {
				String title = null;
//				if (Geocoder.isPresent()) {
//					Geocoder g = new Geocoder(getContext(), Locale.getDefault());
//					if (g != null) {
//						try {
//							List<Address> addresses = g.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//							if (!addresses.isEmpty()) {
//								Address addr = addresses.get(0);
//								if (title == null) {
//									title = addr.getAddressLine(0);
//								}
//								if (title == null) {
//									title = addr.getSubLocality();
//								}
//							}
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				}
				
				if (title == null) {
					title = context.getString(R.string.my_location);
				}
				//Log.w("Location", "Could not find current");
				myLocationMarker = googleMap.addMarker(new MarkerOptions()
										.position(LocationUtils.toLatLng(getCurrentLocation()))
										.title(title)
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_forte_original_marker)));
				return true;
			}else {
				Toast.makeText(getContext(), R.string.error_google_services_not_found, Toast.LENGTH_LONG);
			}
		}
		return false;
	}


	protected void ensureNearestCenterLoaded(boolean force, final Runnable runnable) {
		if (this.closestCenter == null || force) {
			ClosestCenterLocator locator = new ClosestCenterLocator(getContext(), getCurrentLocation());
			locator.setListener(new ClosestCenterLocator.ClosestCenterListener() {
				@Override
				public void onCloseCenterFound(Center center, ClosestCenterLocator locator) {
					closestCenter = center;
					if (myLocationMarker != null) {
						myLocationMarker.setSnippet(getContext().getString(R.string.center_info_format,
								center.getName(), center.getLocation(), center.getDistanceInKilometers(getContext()), context.getString(R.string.kilometers)));
					}
					if (runnable != null) {
						runnable.run();
					}	
				}
			});
			locator.execute();
		}else {
			if (runnable != null) {
				runnable.run();
			}
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
			
			if (arg0.equals(myLocationMarker)) {
				//tag the center to use for location directions
				//infoWindow.findViewById(R.id.btn_info_directions).setTag(center);
				((TextView) infoWindow.findViewById(R.id.lbl_info_center_name)).setText(myLocationMarker.getTitle());
				((TextView) infoWindow.findViewById(R.id.lbl_info_location)).setText(myLocationMarker.getSnippet());
				if (closestCenter != null) {
					((TextView) infoWindow.findViewById(R.id.lbl_info_availability)).setText(String.format("%s, %s %s",
							closestCenter.getCategory(), context.getString(R.string.available),
							closestCenter.getAvailability()));
				}else {
					((TextView) infoWindow.findViewById(R.id.lbl_info_availability)).setText(null);
				}
				((TextView)infoWindow.findViewById(R.id.lbl_drive_direction))
				.setText(R.string.drive_there);
				
//				if (closestCenter != null) {
//					((TextView) infoWindow.findViewById(R.id.lbl_info_location)).setText(closestCenter.getName());
//					((TextView) infoWindow.findViewById(R.id.lbl_info_availability)).setText(String.format("%s, %.2f%s: %s", closestCenter.getCategory(),
//																						closestCenter.getDistanceInKilometers(getContext()),
//																						getContext().getResources().getString(R.string.kilometers),
//																						closestCenter.getAvailability()));
//				}else {
//					((TextView) infoWindow.findViewById(R.id.lbl_info_location)).setText(myLocationMarker.getSnippet());
//					((TextView) infoWindow.findViewById(R.id.lbl_info_availability)).setText(null);
//				}
				
			}else {
				Center center = getCenter(arg0);
				if (center == null) {
					return null;
				}
				((TextView)infoWindow.findViewById(R.id.lbl_drive_direction))
				.setText(R.string.drive_here);
				//tag the center to use for location directions
				//infoWindow.findViewById(R.id.btn_info_directions).setTag(center);
				((TextView) infoWindow.findViewById(R.id.lbl_info_center_name)).setText(center.getName());
				((TextView) infoWindow.findViewById(R.id.lbl_info_location)).setText(center.getLocation());
				((TextView) infoWindow.findViewById(R.id.lbl_info_availability)).setText(String.format("%s: %s",
						center.getCategory(), center.getAvailability()));
				
			}
			
			
			return infoWindow;
		}
		
	}
	
	private static class CenterMarkerMap {
		public Marker marker;
		public Center center;
		public CenterMarkerMap(Marker marker, Center center) {
			this.marker = marker;
			this.center = center;
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
			return map.center;
		}
		return null;
	}
	
	public void clear() {
		if (googleMap != null && centerMarkers != null) {
			if (!centerMarkers.isEmpty()) {
				Iterator<CenterMarkerMap> itr = this.centerMarkers.iterator();
				while (itr.hasNext()) {
					itr.next().marker.remove();
				}
				centerMarkers.clear();
			}
		}
	}
	
	public void addMarker(Center center) {
		MarkerOptions mo = new MarkerOptions();
		
		mo.position(center.getPosition());
		
		mo.title(center.getName());
		
		mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_forter_map_marker));
		//mo.icon(BitmapDescriptorFactory.fromResource(center.resolveIconResource()));
		
		mo.snippet(center.getLocation());
		
		Marker marker = googleMap.addMarker(mo);
		
		centerMarkers.add(new CenterMarkerMap(marker, center));
		
	}

	public void loadMarkersForCenters(Collection<Center> centers, boolean clearFirst, LatLng center) {
		if (googleMap == null) {
			throw new NullPointerException("googleMap is not set");
		}
		
		if (clearFirst) {
			this.clear();
		}
		
		for (Center loc : centers) {
			this.addMarker(loc);
		}
		
		int zoom = context.getResources().getInteger(R.integer.default_zoom_level);
		if (center != null) {
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoom));
		}
	}

	public void loadMarkersForCenters(Collection<Center> centers, boolean clearFirst) {
		Location l = this.getCurrentLocation();
		
		this.loadMarkersForCenters(centers, clearFirst, l != null ? LocationUtils.toLatLng(l) : null);
	}
	
	
	public Location getCurrentLocation() {
		if (currentLocation == null && this.mapsClient != null) {
			currentLocation = this.mapsClient.getCurrentLocation();
		}
		if (currentLocation == null) {
			Toast.makeText(getContext(), R.string.error_google_services_not_found, Toast.LENGTH_LONG);
		}
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}
	
	private float distanceRadius = -1;
	
	
	public float getDistanceRadius() {
		if (distanceRadius < 0) {
			distanceRadius = R.integer.default_circle_radius;
		}
		return distanceRadius;
	}


	public void setDistanceRadius(float distanceRadius) {
		float f = this.getDistanceRadius();
		float diff = f - distanceRadius;
		if (this.googleMap.getMaxZoomLevel() < diff) {
			this.googleMap.animateCamera(CameraUpdateFactory.zoomBy(diff));
			this.distanceRadius = distanceRadius;
			this.refreshCircle();
		}else if (this.googleMap.getMinZoomLevel() > diff) {
			this.googleMap.animateCamera(CameraUpdateFactory.zoomBy(diff));
			this.distanceRadius = distanceRadius;
			this.refreshCircle();
		}
		
	}
	
	private ValueAnimator currentAnimator;
	
	private Polyline centerLine;
	
	private static class MapRunnableCallback implements GoogleMap.CancelableCallback {
		private RunnableTrain train;
		
		
		public MapRunnableCallback(RunnableTrain train) {
			super();
			this.train = train;
		}

		@Override
		public void onCancel() {
			
		}

		@Override
		public void onFinish() {
			train.notifyCompleted();
		}
	}
	
	private void circleRound(Location from, Center center) {
		
		if (centerLine != null) {
			centerLine.remove();
		}
		LatLng fromPt = LocationUtils.toLatLng(from);
		
		LatLng toPt = center.getPosition();
		
		//LatLngBounds bounds = LatLngBounds.builder().include(fromPt).include(toPt).build();
		
		CircleOptions circleOpt = new CircleOptions();
		
		float[] results = {0, 0, 0};
		
		Location.distanceBetween(fromPt.latitude, fromPt.longitude, toPt.latitude, toPt.longitude, results);
		
		this.distanceRadius = results[0] + 100;
		
		circleOpt.center(fromPt);
		
		circleOpt.strokeColor(context.getResources().getColor(R.color.forte_foreground_dark));
		
		circleOpt.strokeWidth(context.getResources().getInteger(R.integer.center_circle_thickness));
		
		circleOpt.fillColor(Color.TRANSPARENT);
		
		circleOpt.visible(true);
		
		//Compensate by 1000 meters
		circleOpt.radius(this.distanceRadius);
		
		//circle.zIndex(4);
		
		circle = this.googleMap.addCircle(circleOpt);
		
		final int timeout = context.getResources().getInteger(R.integer.first_launch_pan_timeout);
		
		final int zoom = context.getResources().getInteger(R.integer.zoom_general);
		
		//Move to current location
		RunnableTrain.RunnableFunction fxn1 = new RunnableTrain.RunnableFunction() {
			
			@Override
			public void run(Object argument, RunnableTrain train) {
				LatLng p = (LatLng)argument;
				//googleMap.moveCamera(CameraUpdateFactory.newLatLng(p));
				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p, zoom));
				train.notifyCompleted();
				showInfoWindow(p, true);
			}
		};
		
		//Pan to nearest
		RunnableTrain.RunnableFunction fxn2 = new RunnableTrain.RunnableFunction() {
			
			@Override
			public void run(Object argument, RunnableTrain train) {
				LatLng p = (LatLng)argument;
				googleMap.animateCamera(CameraUpdateFactory.newLatLng(p),
						timeout, new MapRunnableCallback(train));
				showInfoWindow(p, false);
			}
		};
		
		//Move back to current location
		RunnableTrain.RunnableFunction fxn3 = new RunnableTrain.RunnableFunction() {
			
			@Override
			public void run(Object argument, RunnableTrain train) {
				LatLng p = (LatLng)argument;
				googleMap.animateCamera(CameraUpdateFactory.newLatLng(p),
						timeout, new MapRunnableCallback(train));
				showInfoWindow(p, true);
			}
		};
		
		RunnableTrain t1 = RunnableTrain.startFrom(fxn1);
		t1.setArgument(fromPt);
		RunnableTrain t2 = t1.before(fxn2, 1000);
		t2.setArgument(toPt);
		t2.before(fxn3, 2000).setArgument(fromPt);
		Handler h = new Handler();
		h.postAtTime(t1, 1000);
		
//		
//		this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
//		
//		this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(fromPt));
		
//		centerLine = this.googleMap.addPolyline(new PolylineOptions()
//			.add(fromPt, toPt)
//			.geodesic(true)
//			.color(getContext().getResources().getColor(R.color.link))
//		);
//		
		//this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fromPt, context.getResources().getInteger(R.integer.default_zoom_level)));
		
		//this.googleMap.moveCamera(CameraUpdateFactory.zoomOut());
		
		
		
//		//Set animation
		ValueAnimator anim = ValueAnimator.ofFloat(this.distanceRadius, 0);
		anim.setInterpolator(new AccelerateDecelerateInterpolator());
		anim.setDuration(context.getResources().getInteger(R.integer.circle_animation_duration));
		anim.setRepeatCount(ValueAnimator.INFINITE);
		anim.setRepeatMode(ValueAnimator.REVERSE);
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Float f = (Float)animation.getAnimatedValue();
				circle.setRadius(f);
			}
		});
		currentAnimator = anim;
		anim.start();
		
	}
	
	

	protected void showInfoWindow(final LatLng position, boolean current) {
		if (current) {
			if (myLocationMarker != null) {
				myLocationMarker.showInfoWindow();
			}
		}else {
			CenterMarkerMap map = ListUtils.getFirst(centerMarkers, new ListUtils.Matcher<CenterMarkerMap>() {

				@Override
				public boolean matches(CenterMarkerMap p0) {
					return p0.center.getPosition().equals(position);
				}
			});
			if (map != null) {
				map.marker.showInfoWindow();
			}
		}
	}
	
	private boolean showingCircle = false;
	private Circle circle;
	private Center closestCenter;
	public void showClosestCenters() {
		if (getCurrentLocation() == null) {
			Toast.makeText(getContext(), R.string.error_google_services_not_found, Toast.LENGTH_LONG);
			Log.d(TAG, "currentLocation is not set");
			return;
		}
		if (!this.showingCircle) {
			this.showingCircle = true;
			this.ensureNearestCenterLoaded(true, new Runnable() {
				
				@Override
				public void run() {
					circleRound(getCurrentLocation(), closestCenter);
				}
			});
		}else {
			this.refreshCircle();
		}
	}

	private void refreshCircle() {
		currentAnimator.end();
		float r = this.getDistanceRadius();
		currentAnimator.setFloatValues(r, 0);
		currentAnimator.start();
	}

	
	private void requestDrivingDirections(Marker marker, Center center) {
		//Construct intent for driving directions
		if (center == null) {
			center = closestCenter;
		}
		Location loc = currentLocation;
		if (loc == null && mapsClient != null) {
			loc = mapsClient.getCurrentLocation();
			currentLocation = loc;
		}
		if (loc != null && center != null) {
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
	
	protected void switchFocusTo(final String centerName, boolean animate) {
		CenterMarkerMap map = ListUtils.getFirst(centerMarkers, new ListUtils.Matcher<CenterMarkerMap>() {

			@Override
			public boolean matches(CenterMarkerMap p0) {
				return p0.center.getName().regionMatches(true, 0, centerName, 0, centerName.length());
			}
		});
		if (map != null) {
			int zoom = context.getResources().getInteger(R.integer.zoom_general);
			if (animate) {
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(map.marker.getPosition(), zoom));
			}else {
				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(map.marker.getPosition(), zoom));
			}
			map.marker.showInfoWindow();
		}
	}
	
	
	public void switchFocusTo(final UUID centerId, boolean animate) {
		
		CenterMarkerMap map = ListUtils.getFirst(centerMarkers, new ListUtils.Matcher<CenterMarkerMap>() {

			@Override
			public boolean matches(CenterMarkerMap p0) {
				return p0.center.getId().equals(centerId);
			}
		});
		
		int zoom = context.getResources().getInteger(R.integer.zoom_general);
		if (animate) {
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(map.marker.getPosition(), zoom));
		}else {
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(map.marker.getPosition(), zoom));
		}
		map.marker.showInfoWindow();
	}
	
}
