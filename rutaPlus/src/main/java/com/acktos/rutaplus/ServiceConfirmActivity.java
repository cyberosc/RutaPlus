package com.acktos.rutaplus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.acktos.rutaplus.controllers.ServiceController;
import com.acktos.rutaplus.entities.Service;

public class ServiceConfirmActivity extends Activity {


	private TextView txtPickUpAddress;
	private TextView txtDestinationAddress;
	private TextView txtPickupDate;

	// if attempt in course
	private boolean attemptAddService;

	//Elected service attributes
	private String placeId;
	private String destinationId;
	private String pickupAddress;
	private String destinationAddress;
	private String carId;
	private String electedDateTime;
	private String electedCoordinates;
	private String cardReference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(false);
		setContentView(R.layout.activity_service_confirm);
		
		txtPickUpAddress=(TextView)findViewById(R.id.txt_pickup_address);
		txtDestinationAddress=(TextView)findViewById(R.id.txt_destination_address);
		txtPickupDate=(TextView)findViewById(R.id.txt_pickup_date);
		
		attemptAddService=false;

		//get extras
		Bundle extras=getIntent().getExtras();
		getElectedServiceAttributes(extras);
		

		txtPickUpAddress.setText(pickupAddress);
		txtDestinationAddress.setText(destinationAddress);
		txtPickupDate.setText(electedDateTime);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.next, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle action bar buttons
		switch(item.getItemId()) {
		case R.id.btn_action_next:
			
			Log.i(this.toString()+"onOptionItemSeleted","next button");
			
			if(!attemptAddService){
				
				Service electedService=new Service();
				
				electedService.placeId=placeId;
				electedService.destinationId=destinationId;
				electedService.pickupAddress=pickupAddress;
				electedService.latlng=electedCoordinates;
				electedService.destinationAddress=destinationAddress;
				electedService.carId=carId;
				electedService.dateTime=electedDateTime;
				electedService.cardReference=cardReference;
				
				(new AddNewServiceTask(this)).execute(electedService);
			}
			
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

	private void getElectedServiceAttributes(Bundle extras){

		if(extras!=null){
			placeId=extras.getString(Service.KEY_PLACE_ID);
			destinationId=extras.getString(Service.KEY_DESTINATION_ID);
			pickupAddress=extras.getString(Service.KEY_PICKUP_ADDRESS);
			destinationAddress=extras.getString(Service.KEY_DESTINATION_ADDRESS);
			carId=extras.getString(Service.KEY_CAR_ID);
			electedDateTime=extras.getString(Service.KEY_ELECTED_DATETIME);
			electedCoordinates=extras.getString(Service.KEY_COORDINATES);
			cardReference=extras.getString(AddServiceVIPActivity.TAG_CARD_REFERENCE_VIP);
			
			//debug values
			Log.i(this.toString()+" getElectedService","placeID:"+placeId);
			Log.i(this.toString()+" getElectedService","destinationID:"+destinationId);
			Log.i(this.toString()+" getElectedService","pickup:"+pickupAddress);
			Log.i(this.toString()+" getElectedService","destinationAdress:"+destinationAddress);
			Log.i(this.toString()+" getElectedService","carID:"+carId);
			Log.i(this.toString()+" getElectedService","dateTime elected:"+electedDateTime);
			Log.i(this.toString()+" getElectedService","coordinates elected:"+electedCoordinates);
			Log.i(this.toString()+" getElectedService","cardReference:"+cardReference);
		}

	}
	
	private class AddNewServiceTask extends AsyncTask<Service,Void,Boolean>{


		private Context context;

		public AddNewServiceTask(Context context){
			this.context=context;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//ProgressStatus.show(getActivity(),progressView,serviceForm,true);
			setProgressBarIndeterminateVisibility(true);
			attemptAddService=true;
		}

		@Override
		protected Boolean doInBackground(Service... params) {

			ServiceController serviceController=new ServiceController(context);
			boolean success=false;
			success=serviceController.addService(params[0]);
			return success;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);
			if(success){
				Intent i=new Intent(context,ServiceListActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(i);
				finish();
				Toast.makeText(context, context.getString(R.string.success_add_service),Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(context, context.getString(R.string.failed_add_service),Toast.LENGTH_LONG).show();
			}
			//ProgressStatus.show(getActivity(),progressView,serviceForm,false);
			setProgressBarIndeterminateVisibility(false);
			attemptAddService=false;
		}

	}
}
