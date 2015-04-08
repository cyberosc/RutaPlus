package com.acktos.rutaplus;

import java.util.ArrayList;

import com.acktos.rutaplus.controllers.PaymentController;
import com.acktos.rutaplus.controllers.ServiceVIPController;
import com.acktos.rutaplus.entities.CreditCard;
import com.acktos.rutaplus.entities.Service;
import com.acktos.rutaplus.entities.ServiceVIP;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class ServiceVIPConfirmActivity extends Activity {

	private TextView txtBaseRate;
	private TextView txtMinRate;
	private TextView txtKmRate;
	private TextView txtDiscount;
	private TextView txtIncrement;
	private TextView txtAddress;
	private TextView txtDateTime;
	
	private String address;
	private String coordinates;
	private String dateTime;
	private boolean immediate;
	
	private int serviceType;
	private boolean addServiceAttempt=true;
	
	//APP
	PaymentController payment;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(false);
		setContentView(R.layout.activity_service_vipconfirm);
		
		//APP Initialize
		payment=new PaymentController(this);

		txtBaseRate= (TextView) findViewById(R.id.txt_conf_base_rate);
		txtMinRate= (TextView) findViewById(R.id.txt_conf_min_rate);
		txtKmRate= (TextView) findViewById(R.id.txt_conf_km_rate);
		txtDiscount= (TextView) findViewById(R.id.txt_conf_discount);
		txtIncrement= (TextView) findViewById(R.id.txt_conf_increment);
		txtAddress= (TextView) findViewById(R.id.txt_conf_address);
		txtDateTime= (TextView) findViewById(R.id.txt_conf_date_time);
		
		//get Extras
		Bundle extras=getIntent().getExtras();
		address=extras.getString(AddServiceVIPActivity.TAG_ADDRESS_VIP);
		coordinates=extras.getString(AddServiceVIPActivity.TAG_COORDINATES_VIP);
		immediate=extras.getBoolean(AddServiceVIPActivity.TAG_IMMEDIATE_VIP);
		dateTime=extras.getString(AddServiceVIPActivity.TAG_DATE_TIME_VIP);
		serviceType=extras.getInt(SelectServiceTypeActivity.TYPE_SERVICE);
		
		Log.i("debug toogle service attempt4",Boolean.toString(immediate));
		
		txtAddress.setText(address);
		
		if(immediate){
			txtDateTime.setText(getString(R.string.lbl_immediate_service));
		}else{
			txtDateTime.setText(dateTime);
		}
		
		
		getRates();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_service_vip, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle action bar buttons
		switch(item.getItemId()) {
		case R.id.action_add_service_vip:
			
			Log.i("onOptionItemSeleted","add new service vip task");
			addServiceTask();
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

	private void getRates(){

		(new GetRatesTask(this)).execute();
	}
	
	private void addServiceTask(){
		
		CreditCard card=payment.getPaymentCard();
		
		ServiceVIP service=new ServiceVIP();
		service.address=address;
		service.coordinates=coordinates;
		service.dateTime=dateTime;
		service.cardReference=card.reference;
		service.cardType=card.type;
		service.serviceType=Integer.toString(serviceType);
		
		if(addServiceAttempt){
			(new AddNewServiceTask(this)).execute(service);
		}
		
	}
	
	private void updateRateViews(ArrayList<String> rates){

		if(rates!=null){
			txtBaseRate.setText("$"+rates.get(0));
			txtMinRate.setText("$"+rates.get(1));
			txtKmRate.setText("$"+rates.get(2));
			txtDiscount.setText("%"+rates.get(3));
			txtIncrement.setText(rates.get(4));
		}
	}

	private class GetRatesTask extends AsyncTask<Void,Void,ArrayList<String>>{

		private Context context;
		private ArrayList<String> rates;

		public GetRatesTask(Context context){
			this.context=context;
		}

		@Override
		protected void onPostExecute(ArrayList<String> rates) {
			super.onPostExecute(rates);
			updateRateViews(rates);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected ArrayList<String> doInBackground(Void... params) {

			ServiceVIPController serviceVipController=new ServiceVIPController(context);
			rates=serviceVipController.getRates(serviceType);
			return rates;
		}
	}
	
	private class AddNewServiceTask extends AsyncTask<ServiceVIP,Void,Boolean>{

		private Context context;

		public AddNewServiceTask(Context context){
			this.context=context;
		}
			
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			String message="";
			if(result){
				
				message="El servicio fue enviado con éxito";
				
				Intent i=new Intent(ServiceVIPConfirmActivity.this,ServiceListActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
				
			}else{
				message="El servicio NO fue enviado con éxito";
			}
			
			setProgressBarIndeterminateVisibility(false);
			Toast.makeText(context,message,Toast.LENGTH_LONG).show();
			addServiceAttempt=true;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Boolean doInBackground(ServiceVIP... service) {
			
			boolean success=false;
			addServiceAttempt=false;
			ServiceVIPController serviceVipController=new ServiceVIPController(context);
			success=serviceVipController.addService(service[0]);
			return success;
		}
	}

}
