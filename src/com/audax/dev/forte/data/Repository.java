package com.audax.dev.forte.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.audax.dev.forte.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Repository {
	public static final String TAG = "Repo";
	
	public static LatLng toLatLng(String position) {
		String[] parts = position.split("[\\,x]");
		return new LatLng(Double.parseDouble(parts[0].trim()),
				Double.parseDouble(parts[1].trim()));
	}
	
	
	
	//Use static so that it will be available static-wide
	private static List<Center> demoCenters;
	
	public Collection<Center> getAvailableCenters(Context context) {
		if (demoCenters == null) {
			demoCenters = new ArrayList<Center>();
			XmlResourceParser parser = context.getResources().getXml(R.xml.centers);
			String[] avails = {"6:00AM - 10:00PM"};
			int availabilityCount = avails.length;
			int code;
			try {
				Random rand = new Random();
				code = parser.next();
				String currentStage = "";
				while (code != XmlResourceParser.END_DOCUMENT) {
					if (code == XmlResourceParser.START_TAG) {
						String name = parser.getName();
						if (name.equalsIgnoreCase("state")) {
							currentStage = parser.getAttributeValue(0);
						}else if (name.equalsIgnoreCase("item")) {
							Center c = new Center(UUID.randomUUID());
							c.setAvailability(avails[rand.nextInt(availabilityCount)]);
							c.setState(currentStage);
							
							for (int i = 0, count = parser.getAttributeCount(); i < count; i++) {
								String attr = parser.getAttributeName(i);
								if ("name".equalsIgnoreCase(attr)) {
									String centerName = parser.getAttributeValue(i);
									int index = centerName.indexOf('-');
									if (index != -1) {
										c.setName(centerName.substring(0, index).trim());
										c.setLocation(centerName.substring(index + 1).trim());
									}else {
										c.setName(centerName);
									}
								}else if ("type".equalsIgnoreCase(attr)) {
//									int typeId = parser.getAttributeNameResource(i);
//									if (typeId == 0) {
//										typeId = R.string.category_service_station;
//									}
									c.setCategory(parser.getAttributeValue(i));
								}else if ("position".equalsIgnoreCase(attr)) {
									c.setPosition(toLatLng(parser.getAttributeValue(i)));
								}
								
							}
							demoCenters.add(c);
						}
					}
					code = parser.next();
				}
			} catch (XmlPullParserException e) {
				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}finally {
				parser.close();
			}
				
			/*
			for (String[] l : CENTERS) {
				Center c = new Center(UUID.randomUUID());
				c.setName(l[0]);
				c.setCategory(l[1]);
				c.setDistance(l[2]);
				c.setAvailability(l[3]);
				c.setPosition(toLatLng(l[4]));
				c.setLocation(l[5]);
				demoCenters.add(c);	
			}*/
		}
		return demoCenters;
		//return new ArrayList<Center>(2);
	}
	
	
	
	

	public static final LatLngBounds NIGERIA
		= new LatLngBounds(toLatLng("4.28068,6.654282"), toLatLng("13.902076,7.752914"));
	
	public static final LatLngBounds LAGOS
	= new LatLngBounds(toLatLng("6.380812,3.885727"), toLatLng("6.446318,4.544907"));
	
	public static final LatLng PT_LAGOS = toLatLng("6.47088,3.41104");
	
	private static final ArrayList<Product> PRODUCTS = new ArrayList<Product>();
	
	static {
		PRODUCTS.add(new Product(UUID.randomUUID(), "Aviation Fuel & Lubricants", "file:///android_asset/products/aviation_and_fuel_lubricants.html"));
	}
	
	public List<Product> getProducts() {
		return PRODUCTS;
	}
	
	public Product getProduct(final UUID id) {
		return ListUtils.getFirst(getProducts(), new ListUtils.Matcher<Product>() {

			@Override
			public boolean matches(Product p0) {
				return p0.getId().equals(id);
			}
		});
	}
	
	public Center getCenter(final UUID id, Activity context) {
		return ListUtils.getFirst(getAvailableCenters(context), new ListUtils.Matcher<Center>() {

			@Override
			public boolean matches(Center p0) {
				return p0.getId().equals(id);
			}
		});
	}
}
