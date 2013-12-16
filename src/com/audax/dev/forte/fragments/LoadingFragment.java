package com.audax.dev.forte.fragments;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.widget.ProgressBar;

import com.audax.dev.forte.R;

public class LoadingFragment extends Fragment {
	private View rootView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.rootView = inflater.inflate(R.layout.start_loading_layout, container);
		
//		loadingLabel = this.rootView.findViewById(R.id.load_screen_wait_label);
//		
//		progressBar = (ProgressBar)this.rootView.findViewById(R.id.load_screen_progress_bar);
//		
		startSplashAnimation();
		return this.rootView;
	}
	
	private void startSplashAnimation() {
		Animation anim1 = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
		this.rootView.findViewById(R.id.load_screen_wait_label).startAnimation(anim1);
		anim1 = AnimationUtils.loadAnimation(getActivity(), R.anim.banner_image);
		this.rootView.findViewById(R.id.banner_image).startAnimation(anim1);
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
	private final ArrayList<Animator> animations = new ArrayList<Animator>(3);
	private void startProgressAnim() {
		final ProgressBar bar = (ProgressBar)rootView.findViewById(R.id.load_screen_progress_bar);
		ValueAnimator anim1 = ValueAnimator.ofInt(0, bar.getMax());
		anim1.setInterpolator(new AnticipateInterpolator());
		anim1.setDuration(getResources().getInteger(R.integer.animation_loading));
		
		Animator.AnimatorListener completeL = new Animator.AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				onLoadComplete();
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
		
		};
		anim1.addListener(completeL);
		anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				bar.setProgress((Integer)animation.getAnimatedValue());
			}
		});
		anim1.start();
	}
	protected void onLoadComplete() {
		animations.clear();
		if (completeTask != null) {
			completeTask.run();
		}
	}
}
