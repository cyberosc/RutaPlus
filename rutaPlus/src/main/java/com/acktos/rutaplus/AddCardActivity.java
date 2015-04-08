package com.acktos.rutaplus;


import co.com.payok.lib.impl.PayOkClienteApi;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AddCardActivity extends Activity {
	
	//UI references
	WebView webView = null;
	private ProgressDialog progress;
	private ActionBar actionBar;
	
	@SuppressLint("SetJavaScriptEnabled") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_card);
		
		actionBar=getActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		String url = "";

		try {
			url = PayOkClienteApi.getInstance().agregarTarjeta();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		showProgress();

		webView = (WebView)findViewById(R.id.web_add_card);

		webView.getSettings().setJavaScriptEnabled(true);

		webView.setWebViewClient(new PayOkWebViewCliente());

		webView.loadUrl(url);
	}
	
	private void showProgress(){
		if (progress == null || !progress.isShowing()){
			progress = new ProgressDialog(this);
			progress.setCancelable(false);
			progress.setCanceledOnTouchOutside(false);
			progress.setMessage(getString(R.string.wait_payment_conection));				
			progress.show();
		}
	}

	private void dismissProgress(){

		if(progress!=null){
			progress.dismiss();
			progress = null;
		}

	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		if(id==android.R.id.home){
			Intent intent = new Intent(this, CardListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();

		}
		return super.onOptionsItemSelected(item);
	}

	public class PayOkWebViewCliente extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, String url) {
			dismissProgress();
		}
	}
}
