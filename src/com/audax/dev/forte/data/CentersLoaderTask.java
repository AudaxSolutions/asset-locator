package com.audax.dev.forte.data;

import android.app.Activity;
import android.os.AsyncTask;

public class CentersLoaderTask extends AsyncTask<Void, Center, Integer> {
	private Activity context;
	
	public CentersLoaderTask(Activity context) {
		super();
		this.context = context;
	}
	
	public interface LoaderListener {
		void onCenterFound(Center center, CentersLoaderTask loader);
		void onCompleted(CentersLoaderTask loader);
		void onLoadStarted(CentersLoaderTask loader);
	}
	
	private LoaderListener loaderListener;
	


	public LoaderListener getLoaderListener() {
		return loaderListener;
	}

	public void setLoaderListener(LoaderListener loaderListener) {
		this.loaderListener = loaderListener;
	}
	
	@Override
	protected void onProgressUpdate(Center... values) {
		if (loaderListener != null) {
			for (Center c : values) {
				loaderListener.onCenterFound(c, this);
			}
		}
	}
	

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (this.loaderListener != null) {
			this.loaderListener.onCompleted(this);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (this.loaderListener != null) {
			this.loaderListener.onLoadStarted(this);
		}
	}

	@Override
	protected Integer doInBackground(Void... params) {
		int total = 0;
		Repository repo = new Repository();
		for (Center c : repo.getAvailableCenters(context)) {
			this.publishProgress(c);
			total++;
		}
		return total;
	}

}
