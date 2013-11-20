package com.audax.dev.forte;

import com.audax.dev.forte.maps.LocationUtils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;

public class SliderRadiusSelector extends RadiusSelectorFragment {
	private float currentRadius;
	@Override
	public float getRadius() {
		return currentRadius;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	private View parentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		parentView = inflater.inflate(R.layout.radius_selector_slider, container);
		((SeekBar) parentView.findViewById(R.id.seekBar1)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(
				) {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				captureRadius(progress);
			}
		});
		
		CompoundButton.OnCheckedChangeListener checkedListener = new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				captureRadius();
			}
		};
		
		((RadioButton)parentView.findViewById(R.id.rdo_select_kilometers)).setOnCheckedChangeListener(checkedListener);
		((RadioButton)parentView.findViewById(R.id.rdo_select_miles)).setOnCheckedChangeListener(checkedListener);
		
		return parentView;
	}
	
	protected void captureRadius() {
		
		currentRadius = ((SeekBar)this.parentView.findViewById(R.id.seekBar1)).getProgress();
		
		if (((RadioButton)parentView.findViewById(R.id.rdo_select_kilometers)).isChecked()) {
			currentRadius = currentRadius * (1 / LocationUtils.MILES_TO_KILO);
		}
		
		if (this.getRadiusChangedListener() != null) {
			this.getRadiusChangedListener().onRadiusChanged(this);
		}
	}

	protected void captureRadius(int progress) {
		currentRadius = progress;
		if (((RadioButton)parentView.findViewById(R.id.rdo_select_kilometers)).isChecked()) {
			currentRadius = progress * (1 / LocationUtils.MILES_TO_KILO);
		}
		
		if (this.getRadiusChangedListener() != null) {
			this.getRadiusChangedListener().onRadiusChanged(this);
		}
	}

	@Override
	public String getSelectedUnit() {
		if (((RadioButton)parentView.findViewById(R.id.rdo_select_kilometers)).isChecked()) {
			return this.getActivity().getString(R.string.kilometers);
		}
		return this.getActivity().getString(R.string.miles);
	}
	
	

}
