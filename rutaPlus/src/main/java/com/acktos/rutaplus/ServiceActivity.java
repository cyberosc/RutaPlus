package com.acktos.rutaplus;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import com.acktos.rutaplus.adapters.PlaceAdapter;
import com.acktos.rutaplus.android.CheckGooglePlayServices;
import com.acktos.rutaplus.android.GCMFunctions;
import com.acktos.rutaplus.controllers.PaymentController;
import com.acktos.rutaplus.controllers.PlaceController;
import com.acktos.rutaplus.controllers.UserController;
import com.acktos.rutaplus.entities.Place;
import com.acktos.rutaplus.entities.User;
import com.acktos.rutaplus.interfaces.MyPlacesListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ServiceActivity extends Activity implements 
MapServiceFragment.OnChangeAddressListener,MyPlacesListener{

	private FragmentManager fragmentManager;
	private MapServiceFragment mapServiceFragment;
	private String address=null;

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	String SENDER_ID = "291721187802";
	static final String DEBUG_TAG = "ServiceActivity debug";

	//Registration ID from GCM
	GoogleCloudMessaging gcm;
	String regid;

	//extras
	private int serviceType;

	//my places list
	private static ArrayList<Place> mPlaces=new ArrayList<Place>();
	private static PlaceAdapter placeAdapter;
	private static PlaceController placeController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service);

		//check if registration ID exists
		checkGCMRegisterId();

		//get FragmentManager
		fragmentManager = getFragmentManager();


		// enable ActionBar APP icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);


		mapServiceFragment = new MapServiceFragment();
		fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, mapServiceFragment).commit();

		//get extras from intent
		Bundle extras=getIntent().getExtras();
		serviceType=extras.getInt(SelectServiceTypeActivity.TYPE_SERVICE);
		
		Log.i(this.getClass().getSimpleName()+"onCreated","type:"+serviceType);
		
		//initialize adapter
		placeAdapter=new PlaceAdapter(this, mPlaces);

		//initialize controller
		placeController =new PlaceController(this);

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.service, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle action bar buttons
		switch(item.getItemId()) {

		case R.id.send_address:

			Intent i;
			Log.i("onOptionItemSeleted","type:"+serviceType);

			if(serviceType!=SelectServiceTypeActivity.TYPE_ELECTED){
				i=new Intent(this,AddServiceVIPActivity.class);
				i.putExtra(SelectServiceTypeActivity.TYPE_SERVICE,serviceType);
				i.putExtra(MapServiceFragment.ARG_ADDRESS,address);
				startActivity(i);

			} else {	
				i=new Intent(this,AddServiceElectedActivity.class);
				i.putExtra(MapServiceFragment.ARG_ADDRESS,address);
			}

			startActivity(i);
			return true;

		case R.id.my_places_item:

			DialogFragment myPlacesDialog=new MyPlacesDialogFragment();
			myPlacesDialog.show(getFragmentManager(),"myPlacesDialog");

			return true;

		case android.R.id.home:
			i=new Intent(this,SelectServiceTypeActivity.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onPlaceItemSelected(String placeId,String address){


		if(serviceType==SelectServiceTypeActivity.TYPE_VIP){
			Intent i=new Intent(this,AddServiceVIPActivity.class);
			i.putExtra(PlaceController.TAG_PLACE_ID, placeId);
			i.putExtra(PlaceController.TAG_PLACE_ADDRESS,address);
			startActivity(i);
		}else if (serviceType==SelectServiceTypeActivity.TYPE_ELECTED){

			Intent i=new Intent(this,AddServiceElectedActivity.class);
			i.putExtra(PlaceController.TAG_PLACE_ID, placeId);
			i.putExtra(MapServiceFragment.ARG_ADDRESS,address);
			startActivity(i);

		}
	}

	@Override
	public void OnChangeAddress(String address) {
		this.address=address;
	}

	private void checkGCMRegisterId(){
		if(CheckGooglePlayServices.checkPlayServices(this)){
			gcm=GoogleCloudMessaging.getInstance(this);
			regid=GCMFunctions.getRegistrationId(this,UserController.FILE_NAME_PROFILE);

			if(regid.isEmpty()){
				RegisterGCMIdTask registerIdTask=new RegisterGCMIdTask();
				registerIdTask.execute();
			}
		}else{
			Log.i(DEBUG_TAG,"No valid Google Play Services APK found.");
		}
	}

	private class RegisterGCMIdTask extends AsyncTask<Void,Void,String>{

		@Override
		protected String doInBackground(Void... arg0) {

			String regid=null;
			try {
				regid=GCMFunctions.registerId(gcm, ServiceActivity.this, SENDER_ID);
			} catch (IOException e) {
				Log.i(DEBUG_TAG,"GCM IO exception");
				e.printStackTrace();
				return null;
			}

			if(regid!=null){
				try {
					GCMFunctions.storeRegistrationId(ServiceActivity.this, regid, UserController.FILE_NAME_PROFILE);
				} catch (JSONException e) {
					Log.i(DEBUG_TAG,"GCM JSON exception");
					e.printStackTrace();
				}

				UserController userController=new UserController(ServiceActivity.this);
				String userId=userController.getUserId();

				User user = new User();
				user.setMobileId(regid);
				user.setId(userId);

				userController.addUserService(user);
			}
			return regid;	
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if(result!=null){
				Log.i("DEBUG_TAG","success register GCM id");
			}
		}

	}

	private static void updateAdapterPlaceList(ArrayList<Place> places){

		mPlaces.clear();
		mPlaces.addAll(places);
		placeAdapter.notifyDataSetChanged();

	}

	// My places Dialog Fragment
	public static class MyPlacesDialogFragment extends DialogFragment{

		private View rootView;
		private ProgressBar progressBar;
		private TextView messageView;
		private ListView mPlacesList;
		private MyPlacesListener mPlacesListener;



		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);

			try{
				mPlacesListener=(MyPlacesListener) activity;
			}catch(ClassCastException e){
				throw new ClassCastException(activity.toString()+ " must implement MyPlacesListener");
			}
		}


		@Override
		public Dialog onCreateDialog(Bundle SaveInstanceState){


			LayoutInflater inflater=getActivity().getLayoutInflater();
			rootView=inflater.inflate(R.layout.dialog_my_places, null);

			mPlacesList=(ListView)rootView.findViewById(R.id.my_places_list);
			messageView=(TextView)rootView.findViewById(R.id.my_places_message);
			progressBar=(ProgressBar)rootView.findViewById(R.id.my_places_progress);

			mPlacesList.setAdapter(placeAdapter);
			mPlacesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {


					Object item=parent.getItemAtPosition(position);
					Place place=(Place)item;

					mPlacesListener.onPlaceItemSelected(place.id,place.address);
					getDialog().cancel();
				}
			});

			AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
			builder.setTitle(getString(R.string.my_places));
			builder.setView(rootView); 

			builder.setNegativeButton(getString(R.string.lbl_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					MyPlacesDialogFragment.this.getDialog().cancel();
				}
			});  


			MyPlacesTask myPlacesTask=new MyPlacesTask(progressBar,messageView,getActivity());
			myPlacesTask.execute();


			final AlertDialog alertDialog=builder.create();

			return alertDialog;
		}

	} 

	// Asynchronous task for find my places
	public static class MyPlacesTask extends AsyncTask<Void,Void,ArrayList<Place>>{


		private ProgressBar mLoader;
		private TextView mMessage;
		private Context context;

		public MyPlacesTask(ProgressBar loader,TextView message,Context context){
			mLoader=loader;
			mMessage=message;
			this.context=context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoader.setVisibility(View.VISIBLE);
			mMessage.setVisibility(View.VISIBLE);
		}

		@Override
		protected ArrayList<Place> doInBackground(Void... arg0) {

			return placeController.getAllPlaces();
		}

		@Override
		protected void onPostExecute(ArrayList<Place> places) {
			super.onPostExecute(places);
			mLoader.setVisibility(View.GONE);
			mMessage.setVisibility(View.GONE);

			if(places!=null){

				Log.i("debug arrayList",places.toString());
				updateAdapterPlaceList(places);
				if(places.size()<=0){
					mMessage.setText(context.getString(R.string.empty_my_places));
					mMessage.setVisibility(View.VISIBLE);
					mMessage.setPadding(0, 20, 0, 0);
				}

			}
			else{
				mMessage.setText(context.getString(R.string.failed_find_my_places));
			}

		}

	}
}

