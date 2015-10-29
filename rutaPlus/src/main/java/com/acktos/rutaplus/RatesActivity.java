package com.acktos.rutaplus;

import java.util.ArrayList;

import com.acktos.rutaplus.controllers.ServiceVIPController;
import com.acktos.rutaplus.entities.Rate;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * Show a list of all rates for only informational purposes.
 */
public class RatesActivity extends Activity {

	private ProgressDialog progress;
	
	LinearLayout ratesContent;
	private ActionBar actionBar;

	private static final int ITEMS_PER_RATE=5;
	private static final String PREFIX_PESO="$";
	private static final String PREFIX_DISCOUNT="-";
	private static final String SUFIX_DISCOUNT="%";
	private static final String SUFIX_INCREMENT="x";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rates);
		
		ratesContent=(LinearLayout) findViewById(R.id.rates_content);

		actionBar=getActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		getRates(0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if(id==android.R.id.home){
			Intent i=new Intent(this,SelectServiceTypeActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {

		Intent i=new Intent(this,SelectServiceTypeActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();

	}

	private void getRates(int serviceType){


		(new GetAllRatesTask(this)).execute(serviceType);
	}

	private void updateRateViews(ArrayList<Rate> rates){

		if(rates!=null){
			
			
			//ratesContent.removeAllViews();
			for(int i=0; i<rates.size();i++){
				
				TextView title=(TextView)getLayoutInflater().inflate(R.layout.rate_title_item, null);
				title.setText(rates.get(i).name);
				ratesContent.addView(title);
				
				for(int j=1;j<=ITEMS_PER_RATE;j++){
					
					int icResource=0;
					String prefix="";
					String sufix="";
					String subtitle="";
					String value="";
					
					switch(j){
						
					case Rate.ORDER_FOR_BASE_RATE:
						icResource=R.drawable.ic_base_rate;
						prefix=PREFIX_PESO;
						sufix="";
						subtitle=Rate.TITLE_FOR_BASE_RATE;
						value=rates.get(i).baseRate;
						break;
					
					case Rate.ORDER_FOR_MIN_RATE:
						icResource=R.drawable.ic_min;
						prefix=PREFIX_PESO;
						sufix="";
						subtitle=Rate.TITLE_FOR_MIN_RATE;
						value=rates.get(i).minRate;
						break;
					case Rate.ORDER_FOR_KM_RATE:
						icResource=R.drawable.ic_km;
						prefix=PREFIX_PESO;
						sufix="";
						subtitle=Rate.TITLE_FOR_KM_RATE;
						value=rates.get(i).kmRate;
						break;
					case Rate.ORDER_FOR_DISCOUNT:
						icResource=R.drawable.ic_dis;
						prefix=PREFIX_DISCOUNT;
						sufix=SUFIX_DISCOUNT;
						subtitle=Rate.TITLE_FOR_DISCOUNT;
						value=rates.get(i).discount;
						break;
					case Rate.ORDER_FOR_INCREMENT:
						icResource=R.drawable.ic_base_rate;
						prefix="";
						sufix=SUFIX_INCREMENT;
						subtitle=Rate.TITLE_FOR_INCREMENT;
						value=rates.get(i).increase;
						break;
					
					}
					
					//groupView for content items
					LinearLayout contentItem=(LinearLayout)getLayoutInflater().inflate(R.layout.content_rate_item, null);
					
					
					ImageView icView=(ImageView)getLayoutInflater().inflate(R.layout.rate_image, null);
					icView.setImageResource(icResource);
					
					TextView subtitleView=(TextView)getLayoutInflater().inflate(R.layout.rate_subtitle_item, null);
					subtitleView.setText(subtitle);
					
					TextView valueView=(TextView)getLayoutInflater().inflate(R.layout.rate_value, null);
					valueView.setText(prefix+value+sufix);
					
					LinearLayout.LayoutParams weightImg = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
					LinearLayout.LayoutParams weightText = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 2);
					
					contentItem.addView(icView,weightImg);
					contentItem.addView(subtitleView,weightText);
					contentItem.addView(valueView,weightText);
					
					
					//LinearLayout.LayoutParams content_params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 5);
					ratesContent.addView(contentItem);
					
				}
				

			}
		}
	}
	
	private void showProgress(){
		if (progress == null || !progress.isShowing()){
			progress = new ProgressDialog(this);
			progress.setCancelable(false);
			progress.setCanceledOnTouchOutside(false);
			progress.setMessage(getString(R.string.waiting_for_rates));				
			progress.show();
		}
	}

	private void dismissProgress(){

		if(progress!=null){
			progress.dismiss();
			progress = null;
		}

	}

    /**
     * Async task to get all rates.
     */
 	private class GetAllRatesTask extends AsyncTask<Integer,Void,ArrayList<Rate>>{

		private Context context;
		private ArrayList<Rate> rates;

		public GetAllRatesTask(Context context){
			this.context=context;
		}

		@Override
		protected void onPostExecute(ArrayList<Rate> rates) {
			super.onPostExecute(rates);
			updateRateViews(rates);
			dismissProgress();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress();
		}

		@Override
		protected ArrayList<Rate> doInBackground(Integer... params) {

			ServiceVIPController serviceVipController=new ServiceVIPController(context);
			rates=serviceVipController.getAllRates();
			return rates;
		}
	}
}
