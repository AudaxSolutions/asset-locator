package com.audax.dev.forte.data;

import java.util.UUID;

import android.content.Context;
import android.net.Uri;

import com.audax.dev.forte.R;
import com.google.android.gms.maps.model.LatLng;

public class Center implements Cloneable {
	private String name, category, distance, location, availability, state;
	private UUID id;
	private float distanceInMeters;
	public Center(UUID id, String name, String category, String distance, String location) {
		super();
		this.id = id;
		this.name = name;
		this.category = category;
		this.distance = distance;
		this.location = location;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public static int getIconResourceId(String type) {
		return R.drawable.service_point;
	}



	private long lastUpdated = 0L;
	
	/**
	 * The number of milliseconds between the distance my be updated
	 */
	private static final long UPDATE_AGE = 20 * 60 * 1000;

	@Override
	public Center clone() throws CloneNotSupportedException {
		
		return (Center)super.clone();
	}
	
	public boolean shouldUpdateDistance() {
		return System.currentTimeMillis() - lastUpdated > UPDATE_AGE;
	}

	private int iconResource = -1;
	
	private LatLng position;

	public LatLng getPosition() {
		return position;
	}

	public void setPosition(LatLng position) {
		this.position = position;
	}

	public int getIconResource() {
		return iconResource;
	}

	public float getDistanceInMiles(Context context) {
		return (Float.parseFloat(context.getString(R.string.conversation_meter_to_mile)) * distanceInMeters);
	}
	
	public float getDistanceInKilometers(Context context) {
		return (this.distanceInMeters / 1000);
	}
	
	public float getDistanceInMeters() {
		return distanceInMeters;
	}

	public void setDistanceInMeters(float distanceInMeters) {
		this.lastUpdated = System.currentTimeMillis();
		this.distanceInMeters = distanceInMeters;
	}

	public int getItemIconResource(Context context) {
		if (this.category.equals(context.getString(R.string.category_service_station))) {
			return R.drawable.ic_motor_oil;
		}
		if (this.category.equals(context.getString(R.string.category_car_wash))) {
			return R.drawable.ic_carwash_icon;
		}
		return R.drawable.ic_gas_station_icon_gray;
		
	}
	
	public void setIconResource(int iconResource) {
		this.iconResource = iconResource;
	}

	public Center(UUID id) {
		this.id = id;
	}	

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}

	public UUID getId() {
		return id;
	}
	
	public int resolveIconResource() {
		if (this.getIconResource() == -1) {
			return this.getDefaultIconResource();
		}
		return this.getIconResource();
	}
	
	public int getDefaultIconResource() {
		//TODO: Use the category to determine icon resource
		return com.audax.dev.forte.R.drawable.service_point;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.iconResource = getIconResourceId(category);
		this.category = category;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	private Uri pageUrl, imageUri;
	public Uri getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(Uri pageUrl) {
		this.pageUrl = pageUrl;
	}

	public Uri getImageUri() {
		return imageUri;
	}

	public void setImageUri(Uri imageUri) {
		this.imageUri = imageUri;
	}
	
	
}
