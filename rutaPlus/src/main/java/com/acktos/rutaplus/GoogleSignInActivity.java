package com.acktos.rutaplus;

import org.json.JSONException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import co.com.payok.lib.seguridad.constastes.CifradoConstantes;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.acktos.rutaplus.android.InternalStorage;
import com.acktos.rutaplus.controllers.UserController;
import com.acktos.rutaplus.entities.User;
import com.acktos.rutaplus.netcom.Payment;


public class GoogleSignInActivity extends Activity implements ConnectionCallbacks,OnConnectionFailedListener, OnClickListener{


	private static final int RC_SIGN_IN = 0;
	//private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	private boolean mSignInClicked;
	private ConnectionResult mConnectionResult;

	//private SignInButton gSignInButtom;
	private ProgressBar progressBarGplus;

	private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;
	private static final String DIALOG_TAG_GOOGLE_PLAY_SERVICES="gPlayServiceNoAvailable";

	private InternalStorage storage;
	private User user;
	private ActionBar actionBar;


	//private UserController userController;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google);
		
		actionBar=getActionBar();
		actionBar.hide();
		
		//gSignInButtom=(SignInButton) findViewById(R.id.sign_in_button_google);
		//gSignInButtom.setSize(SignInButton.SIZE_WIDE);
		//gSignInButtom.setOnClickListener(this);

		progressBarGplus=(ProgressBar) findViewById(R.id.progress_bar_gplus);

		user=new User();

		
	}

	public void onStart(){
		super.onStart();
		//mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();

		/*if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.google, menu);
		//return true;
		return false;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,0).show();
			return;
		}

		if (!mIntentInProgress) {
			mConnectionResult = result;
			if (mSignInClicked) {
				resolveSignInError();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			/*if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}*/
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		
		
		Log.i(this.getLocalClassName(),"OnConnected");
		mSignInClicked = false;
		
		//revokeGplusAccess();
		
		storage=new InternalStorage(this);
		if(!storage.isFileExists(UserController.FILE_NAME_PROFILE)&& mSignInClicked==true){// Get user's information

			
			User user;
			user=getProfileInformation();
			if(user!=null){
				
				Log.i(this.getLocalClassName(),"OnConnected:getProfileInformation()");
				HttpPostRequest postRequest=new HttpPostRequest();
				postRequest.execute(user);
			}else{
				Toast.makeText(this,getString(R.string.plus_generic_error), Toast.LENGTH_LONG).show();
				updateGsignInButton(true);
			}
			
		}


	}

	private void logoutFromGplus(){

		/*if(mGoogleApiClient.isConnected()){
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
			Log.i("debug gplus","success logout from gplus");
		}*/

	}	

	private void revokeGplusAccess() {

		/*if (mGoogleApiClient.isConnected()) {

			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
				@Override
				public void onResult(Status arg0) {
					Log.i("debug gplus", "User access revoked");
					mGoogleApiClient.connect();

				}

			});
		}*/
	}

	@Override
	public void onConnectionSuspended(int arg0) {

		//mGoogleApiClient.connect();
		//updateUI(false);
	}

	@Override
	public void onClick(View view) {

		switch(view.getId()) {
		case R.id.sign_in_button_google:

			int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
			if (available != ConnectionResult.SUCCESS) {
				GooglePlayServicesDialog gPlayServicesDialog=new GooglePlayServicesDialog();
				gPlayServicesDialog.show(getFragmentManager(),DIALOG_TAG_GOOGLE_PLAY_SERVICES);
				return;
			}
			//updateGsignInButton(false);
			signInWithGplus();
			break;

		}

	}

	private void signInWithGplus() {
		
		
		/*if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}*/
	}

	private void resolveSignInError() {
		/*if (mConnectionResult.hasResolution()) {

			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);

			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}*/
	}

	private User getProfileInformation() {
		/*try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
				String personId=currentPerson.getId();
				String personName = currentPerson.getDisplayName();
				String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

				user.setName(personName);
				user.setEmail(email);
				user.setgId(personId);


				return user;
			} else {
				//Toast.makeText(getApplicationContext(),"La información de tu perfil de google no esta disponible", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return null;

	}

	private void updateGsignInButton(boolean visible){
		
		
		if(visible){
			Log.i("debug button","active el boton");
			//gSignInButtom.setVisibility(View.VISIBLE);
			//gSignInButtom.setEnabled(true);
		}else{
			Log.i("debug button","desactive el boton");
			//gSignInButtom.setEnabled(false);
		}
	}
	
	public void launchLoginAtivity(View view){
		Intent i=new Intent(GoogleSignInActivity.this,LoginActivity.class);
		startActivity(i);
	}

    public void launchRememberPassword(View view){
        Intent i=new Intent(GoogleSignInActivity.this,RememberPasswordActivity.class);
        startActivity(i);
    }


	public void createNewAccount(View view){
		Intent newAccountIntent = new Intent(this, CreateNewAccountActivity.class);
		startActivity(newAccountIntent);
	}

	public static class GooglePlayServicesDialog extends DialogFragment{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			int available=GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
			if(available==ConnectionResult.SUCCESS){
				return null;
			}
			if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
				return GooglePlayServicesUtil.getErrorDialog(available,getActivity(), REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES);
			}


			AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.plus_generic_error);
			builder.setCancelable(true);
			return builder.create();

		}


	}

	private class HttpPostRequest extends AsyncTask<User,Void,Boolean>{
		
		private UserController userController;
		@Override
		protected void onPreExecute(){
			progressBarGplus.setVisibility(View.VISIBLE);
			updateGsignInButton(false);
		}
		
		@Override
		protected Boolean doInBackground(User... params) {

			
			//Log.i("debug async task","entre");
			userController=new UserController(GoogleSignInActivity.this);
			boolean result=userController.addUserService(params[0]);

			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			revokeGplusAccess();
			
			if(result){
				
				
				try {
					
					Intent i;
					
					if(userController.isProfileCompleted()){
						 i=new Intent(GoogleSignInActivity.this,CardListActivity.class);
						
					}else{
						i=new Intent(GoogleSignInActivity.this,ProfileUserActivity.class);
						i.putExtra("name", user.getName());
						
					}
					
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					finish();
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				finish();
			}else{
				Toast.makeText(GoogleSignInActivity.this,getString(R.string.failed_register_user_gplus),Toast.LENGTH_LONG).show();
				
			}
			
			mSignInClicked=false;
			updateGsignInButton(true);
			progressBarGplus.setVisibility(View.GONE);
			
		}



	}

}