package com.audax.dev.forte.maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import android.location.Location;
import android.net.Uri;

import com.audax.dev.forte.data.Center;
import com.google.android.gms.maps.model.LatLng;

public class LocationUtils {
	
	
	
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
			
			c.setDistanceInMeters(results[0]);
		}
	}
	
	public static LatLng toLatLng(Location loc) {
		return new LatLng(loc.getLatitude(), loc.getLongitude());
	}
	
	public static void updateDistance(LatLng fromPoint, Center toCenter) {
		float[] results = {0, 0, 0};
		Location.distanceBetween(fromPoint.latitude, fromPoint.longitude,
				toCenter.getPosition().latitude, toCenter.getPosition().longitude, results);
	
		toCenter.setDistanceInMeters(results[0]);
	}
	
	public static Center getClosestCenter(Location fromPoint, Iterator<Center> centers) {
		return getClosestCenter(fromPoint, centers, -1);
	}
	public static Center getClosestCenter(Location fromPoint, Iterator<Center> centers, float maxDistance) {
		Center closest = null;
		float last = maxDistance;
		float[] results = {0, 0, 0};
		while (centers.hasNext()) {
			Center c = centers.next();
			Location.distanceBetween(fromPoint.getLatitude(), fromPoint.getLongitude(),
					c.getPosition().latitude, c.getPosition().longitude, results);
			if (last > results[0] || last < 0) {
				last = results[0];
				closest = c;
			}
		}
		closest.setDistanceInMeters(last);
		return closest;
	}
	
	public static Center getClosestCenter(LatLng fromPoint, Iterator<Center> centers, float maxDistance) {
		Center closest = null;
		float last = maxDistance;
		float[] results = {0, 0, 0};
		while (centers.hasNext()) {
			Center c = centers.next();
			Location.distanceBetween(fromPoint.latitude, fromPoint.longitude,
					c.getPosition().latitude, c.getPosition().longitude, results);
			if (last > results[0] || last < 0) {
				last = results[0];
				closest = c;
			}
		}
		closest.setDistanceInMeters(last);
		return closest;
	}
	
	public static void updateDistance(Location fromPoint, Center toCenter) {
		float[] results = {0, 0, 0};
		Location.distanceBetween(fromPoint.getLatitude(), fromPoint.getLongitude(),
				toCenter.getPosition().latitude, toCenter.getPosition().longitude, results);
	
		toCenter.setDistanceInMeters(results[0]);
	}
	
	
	
	
	
	public static Collection<Center> collectCentersCloseTo(Location location, float distance, Collection<Center> allCenters) {
		ArrayList<Center> list = new ArrayList<Center>();
		float[] results = {0, 0, 0};
		for (Center c : allCenters) {
			Location.distanceBetween(location.getLatitude(), location.getLongitude(),
						c.getPosition().latitude, c.getPosition().longitude, results);
			
			if (results[0] <= distance) {
				
				c.setDistanceInMeters(results[0]);
				
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
		return getClosestCenter(location, centers.iterator(), -1);
	}
}
