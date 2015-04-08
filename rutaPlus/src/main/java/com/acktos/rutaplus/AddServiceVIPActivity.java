package com.acktos.rutaplus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.acktos.rutaplus.CreateNewServiceFragment.DatePickerFragment;
import com.acktos.rutaplus.CreateNewServiceFragment.TimePickerFragment;
import com.acktos.rutaplus.ServiceActivity.MyPlacesDialogFragment;
import com.acktos.rutaplus.controllers.PaymentController;
import com.acktos.rutaplus.controllers.PlaceController;
import com.acktos.rutaplus.controllers.ServiceController;
import com.acktos.rutaplus.controllers.ServiceVIPController;
import com.acktos.rutaplus.controllers.UserController;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AddServiceVIPActivity extends Activity {

	private boolean inmediateIsOn=false;
	private boolean ratesExists=false;
	private String address;
	private String coordinates;
	private String location;
	private String placeId;
	private String placeAddress;
	private int serviceType;
	private String cardType;
	private String cardReference;
	
	public static final String TAG_ADDRESS_VIP="com.acktos.rutaplus.ADDRESS_VIP";
	public static final String TAG_IMMEDIATE_VIP="com.acktos.rutaplus.IMMEDIATE_VIP";
	public static final String TAG_ADDRESS_ID_VIP="com.acktos.rutaplus.ADDRESS_ID_VIP";
	public static final String TAG_DATE_TIME_VIP="com.acktos.rutaplus.DATE_TIME_VIP";
	public static final String TAG_COORDINATES_VIP="com.acktos.rutaplus.COORDINATES_VIP";
	public static final String TAG_CARD_REFERENCE_VIP="com.acktos.rutaplus.CARD_REFERENCE_VIP";
	public static final String TAG_CARD_TYPE="com.acktos.rutaplus.CARD_TYPE";


	//UI references
	private Switch tgleInmediate;
	private static EditText txtDate;
	private static EditText txtTime;
	private EditText txtAddress;
	private TextView txtBaseRate;
	private TextView txtMinRate;
	private TextView txtKmRate;
	private TextView txtDiscount;
	private TextView txtIncrement;
	//private View ratesContent;
	
	//private boolean isEnterprise=false;
	private UserController userController;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(false);
		setContentView(R.layout.activity_add_service_vip);


		//UI references
		txtAddress=(EditText) findViewById(R.id.txt_pickup_address);
		tgleInmediate=(Switch) findViewById(R.id.tgle_inmediate);
		txtDate = (EditText) findViewById(R.id.txt_date_inmediate);
		txtTime = (EditText) findViewById(R.id.txt_time_inmediate);

		txtBaseRate= (TextView) findViewById(R.id.txt_base_rate);
		txtMinRate= (TextView) findViewById(R.id.txt_min_rate);
		txtKmRate= (TextView) findViewById(R.id.txt_km_rate);
		txtDiscount= (TextView) findViewById(R.id.txt_discount);
		txtIncrement= (TextView) findViewById(R.id.txt_increment);
		//ratesContent=(View) findViewById(R.id.content_rates);
		
		
		//check if user is enterprise
		userController=new UserController(this);
		//isEnterprise=userController.isEnterprise();
		
		
		//get extras
		Bundle extras=getIntent().getExtras();
		location=extras.getString(MapServiceFragment.ARG_ADDRESS);
		placeId=extras.getString(PlaceController.TAG_PLACE_ID);
		placeAddress=extras.getString(PlaceController.TAG_PLACE_ADDRESS);
		serviceType=extras.getInt(SelectServiceTypeActivity.TYPE_SERVICE);
		

		//set values for address
		if(!TextUtils.isEmpty(placeId)){
			address=placeAddress;
			txtAddress.setEnabled(false);
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		}else{
			try{
				address=location.split(";")[1].split("-")[0]+"-";;
				coordinates=location.split(";")[0];	
			}catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
			}
			catch(NullPointerException e){
				e.getMessage();
			}

		}

		txtAddress.setText(address);

		changeStateInmediate();
		
		//if(!isEnterprise){
			getRates();
		//}else{
			//ratesContent.setVisibility(View.GONE);
		//}
		

	}

	@Override
	protected void onResume() {
		super.onResume();
		changeStateInmediate();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_service_vip, menu);
		
		//if(isEnterprise){
			//MenuItem getRatesAction=menu.findItem(R.id.action_get_rates);
			//getRatesAction.setVisible(false);
		//}
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle action bar buttons
		switch(item.getItemId()) {
		case R.id.action_add_service_vip:

			Log.i("onOptionItemSeleted","add new service vip");
			addServiceVipAttempt();

			return true;
		case R.id.action_get_rates:
			getRates();
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public void showDatePicker(View v){
		DialogFragment datePickerDialog=new DatePickerFragment();
		datePickerDialog.show(getFragmentManager(),"datePickerDialog");
	}

	public void showTimePicker(View v){
		DialogFragment timePickerDialog=new TimePickerFragment();
		timePickerDialog.show(getFragmentManager(),"timePickerDialog");
	}

	public void onToggleCliked(View view){

		inmediateIsOn=((Switch)view).isChecked();
		changeStateInmediate();
	}

	private void changeStateInmediate(){

		if(inmediateIsOn){
			txtDate.setVisibility(View.GONE);
			txtTime.setVisibility(View.GONE);

		}else{

			txtDate.setVisibility(View.VISIBLE);
			txtTime.setVisibility(View.VISIBLE);

		}
	}

	private void getRates(){

		(new GetRatesTask(this)).execute();
	}

	private void updateRateViews(ArrayList<String> rates){

		if(rates!=null){
			ratesExists=true;

			txtBaseRate.setText("$"+rates.get(0));
			txtMinRate.setText("$"+rates.get(1));
			txtKmRate.setText("$"+rates.get(2));
			txtDiscount.setText("%"+rates.get(3));
			txtIncrement.setText(rates.get(4));
		}
	}

	private void addServiceVipAttempt(){

		boolean validate=true;
		View focusView=null;

		//reset errors
		txtAddress.setError(null);
		txtTime.setError(null);

		String addressValue=txtAddress.getText().toString();
		String dateValue=txtDate.getText().toString();
		String timeValue=txtTime.getText().toString();

		if(TextUtils.isEmpty(addressValue)){
			txtAddress.setError(getString(R.string.error_field_required));
			focusView = txtAddress;
			validate = false;

		} else if(!tgleInmediate.isChecked() &&  (TextUtils.isEmpty(dateValue) || TextUtils.isEmpty(timeValue))){

			Log.i("debug validate","entre a segunda validación:"+Boolean.toString(tgleInmediate.isChecked()));
			Toast.makeText(this, getString(R.string.msg_empty_datetime), Toast.LENGTH_LONG).show();
			validate = false;

		}else if(!tgleInmediate.isChecked() && !CreateNewServiceFragment.validateDatetimeService(dateValue +" "+timeValue, 0)){
			Toast.makeText(this,getString(R.string.msg_date_vip_failed), Toast.LENGTH_LONG).show();
			validate = false;
		}



		if(validate){

			//if(ratesExists || isEnterprise){
			if(ratesExists){
				String datetime=txtDate.getText().toString()+" "+txtTime.getText().toString();

				//Without payment
				//Intent i=new Intent(this,ListCreditCardsActivity.class);
				
				Intent i;
				//if(isEnterprise){
					// i=new Intent(this,ServiceVIPConfirmActivity.class);
				//}else{
					 i=new Intent(this,ServiceVIPConfirmActivity.class);
				//}
				

				if(!TextUtils.isEmpty(placeId)){
					i.putExtra(PlaceController.TAG_PLACE_ID,placeId);
				}else{
					i.putExtra(TAG_ADDRESS_VIP,addressValue);
					i.putExtra(TAG_COORDINATES_VIP,coordinates);
				}

				i.putExtra(TAG_IMMEDIATE_VIP, tgleInmediate.isChecked());

				if(tgleInmediate.isChecked()){
					datetime="null";
				}

				
				i.putExtra(TAG_DATE_TIME_VIP,datetime);
				i.putExtra(SelectServiceTypeActivity.TYPE_SERVICE,serviceType);
				startActivity(i);
				
			}else{
				Toast.makeText(this, getString(R.string.msg_empty_rates), Toast.LENGTH_LONG).show();
			}


		}else{
			if(focusView!=null){
				focusView.requestFocus();
			}
			return;
		}

	}

	public static class DatePickerFragment extends DialogFragment implements OnDateSetListener{

		final Calendar c=Calendar.getInstance();

		@Override
		public Dialog onCreateDialog(Bundle saveInstanceState){

			int year=c.get(Calendar.YEAR);
			int month=c.get(Calendar.MONTH);
			int day=c.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(),this,year,month,day);
		}


		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {

			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
			c.set(Calendar.YEAR,year);
			c.set(Calendar.MONTH,monthOfYear);
			c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
			txtDate.setText(sdf.format(c.getTime()));
		}

	}

	public static class TimePickerFragment extends DialogFragment implements OnTimeSetListener{

		final Calendar c=Calendar.getInstance();

		@Override
		public Dialog onCreateDialog(Bundle saveInstanceState){

			int hour=c.get(Calendar.HOUR_OF_DAY);
			int minute=c.get(Calendar.MINUTE);

			return new TimePickerDialog(getActivity(), this, hour, minute,false);
		}


		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

			SimpleDateFormat sdf=new SimpleDateFormat("hh:mm a",Locale.getDefault());
			c.set(Calendar.HOUR_OF_DAY,hourOfDay);
			c.set(Calendar.MINUTE,minute);

			//get date from picker
			String datePicker=txtDate.getText().toString();
			String timePicker=sdf.format(c.getTime());

			if(!TextUtils.isEmpty(datePicker)){
				if(CreateNewServiceFragment.validateDatetimeService(datePicker +" "+timePicker, 0)){

					txtTime.setText(timePicker);
				}else{
					Toast.makeText(getActivity(),getActivity().getString(R.string.msg_date_vip_failed), Toast.LENGTH_LONG).show();
					txtTime.setText(null);

				}
			}else{
				Toast.makeText(getActivity(),getActivity().getString(R.string.msg_no_date_selected), Toast.LENGTH_LONG).show();
				txtTime.setText(null);
			}

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
			setProgressBarIndeterminateVisibility(false);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected ArrayList<String> doInBackground(Void... params) {

			ServiceVIPController serviceVipController=new ServiceVIPController(context);
			rates=serviceVipController.getRates(serviceType);
			return rates;
		}
	}

}