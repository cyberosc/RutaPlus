package com.acktos.rutaplus;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class TermsActivity extends Activity {
	
	private ActionBar actionBar; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_terms);
		WebView webview = new WebView(this);
		webview.loadUrl(getString(R.string.url_terms));
		setContentView(webview);
		
		actionBar=getActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	
}
