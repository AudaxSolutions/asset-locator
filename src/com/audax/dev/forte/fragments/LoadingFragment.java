package com.audax.dev.forte.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.ProgressBar;

import com.audax.dev.forte.R;

public class LoadingFragment extends Fragment {
	private View loadingLabel;
	private ProgressBar progressBar;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.start_loading_layout, container);
		loadingLabel = v.findViewById(R.id.load_screen_wait_label);
		
		progressBar = (ProgressBar)v.findViewById(R.id.load_screen_progress_bar);
		
		startWaitAnim();
		return v;
	}
	
	public void startLoading() {
		startProgressAnim();
	}
	
	private Runnable completeTask;
	
	
	public Runnable getCompleteTask() {
		return completeTask;
	}
	public void setCompleteTask(Runnable completeTask) {
		this.completeTask = completeTask;
	}
	
	private void startProgressAnim() {
		ValueAnimator anim = ValueAnimator.ofInt(0, progressBar.getMax());
		
		anim.setDuration(2000);
		
		anim.setInterpolator(new AnticipateInterpolator());
		
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				onLoadComplete();
			}
		});
		
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				progressBar.setProgress((Integer)animation.getAnimatedValue());
			}});
		
		anim.start();
		
	}
	protected void onLoadComplete() {
		if (currentAnimator != null) {
			currentAnimator.end();
			currentAnimator = null;
		}
		
		if (completeTask != null) {
			completeTask.run();
		}
	}
	private Animator currentAnimator;
	private void startWaitAnim() {
		ObjectAnimator anim = 
					ObjectAnimator.ofFloat(loadingLabel, "alpha", 0f);
		
		anim.setDuration(1000);
		anim.setRepeatCount(ValueAnimator.INFINITE);
		currentAnimator = anim;
		anim.start();
		
	}
}
