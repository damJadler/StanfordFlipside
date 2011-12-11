package com.stanfordflipside.adam;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class ViewArticle extends Activity {

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		WebView webView=new WebView(this);
		setContentView(webView);

		//Code start--code taken from Android documentation for WebView 
		final Activity activity = this;
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				// Activities and WebViews measure progress with different scales.
				// The progress meter will automatically disappear when we reach 100%
				activity.setProgress(progress * 1000);
			}
		});
		webView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
			}
		});
		//end code borrowing from Android documentation

		Bundle extras=getIntent().getExtras();
		String url=extras.getString(ArticleActivity.ARTICLE_SELECTED);
		
		webView.getSettings().setBuiltInZoomControls(true);		
		webView.setInitialScale(50);
		
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);

		webView.loadUrl(url);


	}

	/*public boolean onTouchEvent(MotionEvent ev)
	{
		if (ev.getAction() == MotionEvent.ACTION_DOWN ||
				ev.getAction() == MotionEvent.ACTION_POINTER_DOWN ||
				ev.getAction() == MotionEvent.ACTION_POINTER_1_DOWN ||
				ev.getAction() == MotionEvent.ACTION_POINTER_2_DOWN ||
				ev.getAction() == MotionEvent.ACTION_POINTER_3_DOWN) 
		{
			
					
			if (multiTouchZoom && !buttonsZoom) {
				if (getPointerCount(ev) > 1) {
					getSettings().setBuiltInZoomControls(true);
					getSettings().setSupportZoom(true);
				} else {
					getSettings().setBuiltInZoomControls(false);
					getSettings().setSupportZoom(false);
				}
			}
		}

		if (!multiTouchZoom && buttonsZoom) {
			if (getPointerCount(ev) > 1) {
				return true;
			}
		}
	}*/

}
