package com.audax.dev.forte.maps;

import java.util.ArrayList;
import java.util.Collection;

import android.location.Location;
import android.net.Uri;

import com.audax.dev.forte.data.Center;
import com.google.android.gms.maps.model.LatLng;

public class LocationUtils {
	
	private static class LocationMap {
		public Center center;
		public float distance;
		public LocationMap(Center center, float distance) {
			super();
			this.center = center;
			this.distance = distance;
		}
		public Center getCenter() {
			center.setDistanceInMiles(distance);
			return center;
		}
		
		
		
	}
	
	public static final float MILES_TO_KILO = 1.60934f;
	
	public float milesToKilometers(float miles) {
		return miles * MILES_TO_KILO;
	}
	
	public static String escapeLocation(Location loc) {
		return Uri.encode(String.format("%s,%s", loc.getLatitude(), loc.getLongitude()));
	}
	
	public static String escapeLocation(LatLng loc) {
		return Uri.encode(String.format("%s,%s", loc.latitude, loc.longitude));
	}
	
	public static void updateDistances(LatLng fromPoint, Collection<Center> toCenters) {
		float[] results = {0, 0, 0};
		for (Center c : toCenters) {
			Location.distanceBetween(fromPoint.latitude, fromPoint.longitude,
						c.getPosition().latitude, c.getPosition().longitude, results);
			
			c.setDistanceInMiles(results[0]);
		}
	}
	
	public static void updateDistance(LatLng fromPoint, Center toCenter) {
		float[] results = {0, 0, 0};
		Location.distanceBetween(fromPoint.latitude, fromPoint.longitude,
				toCenter.getPosition().latitude, toCenter.getPosition().longitude, results);
	
		toCenter.setDistanceInMiles(results[0]);
	}
	
	public static void updateDistance(Location fromPoint, Center toCenter) {
		float[] results = {0, 0, 0};
		Location.distanceBetween(fromPoint.getLatitude(), fromPoint.getLongitude(),
				toCenter.getPosition().latitude, toCenter.getPosition().longitude, results);
	
		toCenter.setDistanceInMiles(results[0]);
	}
	
	public static Collection<Center> collectionCentersCloseTo(Location location, float distance, Collection<Center> allCenters) {
		ArrayList<Center> list = new ArrayList<Center>();
		float[] results = {0, 0, 0};
		for (Center c : allCenters) {
			Location.distanceBetween(location.getLatitude(), location.getLongitude(),
						c.getPosition().latitude, c.getPosition().longitude, results);
			
			if (results[0] <= distance) {
				
				c.setDistanceInMiles(results[0]);
				
				list.add(c);
			}
		}
		return list;
		
	}
	
	
	
	/**
	 * Get the center closest to a certain location/point
	 * @param location
	 * @param centers
	 * @return closest center
	 * @throws CloneNotSupportedException 
	 */
	public static Center getClosest(LatLng location, Collection<Center> centers) {
		int len = centers.size();
		if (len == 0) {
			return null;
		}
		LocationMap[] maps = new LocationMap[len];
		LocationMap closest = null;
		
		int idx = 0;
		float[] results = {0, 0, 0};
		for (Center c : centers) {
			Location.distanceBetween(location.latitude, location.longitude,
						c.getPosition().latitude, c.getPosition().longitude, results);
			maps[idx++] = new LocationMap(c, results[0]);
		}
		
		closest = maps[0];
		
		//Resolve closest
		for (LocationMap map : maps) {
			if (closest.distance > map.distance) {
				closest = map;
			}
		}
		
		return closest.getCenter();
	}
}
