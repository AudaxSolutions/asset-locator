package com.audax.dev.forte;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.audax.dev.forte.web.ApplicationRegistry;
import com.audax.dev.forte.web.WebApplication;

public class WebViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		
		Intent itt = this.getIntent();
		if (itt != null && itt.getExtras().containsKey("application")) {
			Integer appCode = itt.getExtras().getInt("application");
			try {
				WebApplication wap = ApplicationRegistry
					.getInstance().get(appCode);
				
				if (wap.getTitleResource() != -1) {
					this.setTitle(wap.getTitleResource());
				}else if (wap.getTitle() != null) {
					this.setTitle(wap.getTitle());
				}
				
				wap.initializeWebView((WebView)this.findViewById(R.id.web_view));
			} catch (InstantiationException e) {
				//Log.e("WebView", e.getMessage());
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_view, menu);
		return true;
	}*/

}
