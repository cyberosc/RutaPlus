package com.acktos.rutaplus;

import com.acktos.rutaplus.controllers.UserController;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePasswordActivity extends Activity {
	
	//App references
	UserController userController;
	
	//UI references
	private EditText txtChangePassword;
	private EditText txtChangePasswordConfirm;
	private Button btnChangePassword;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		
		txtChangePassword=(EditText)findViewById(R.id.txt_change_password);
		txtChangePasswordConfirm=(EditText)findViewById(R.id.txt_change_password_confirm);
		btnChangePassword=(Button)findViewById(R.id.btn_change_password);
		
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		
		userController=new UserController(this);
	}
	
	/**
	 * invoked by change password button
	 */
	public void changePassword(View view){
		
		String oldPassword=txtChangePassword.getText().toString();
		String newPassword=txtChangePasswordConfirm.getText().toString();
		
		if(oldPassword.equals(newPassword) && !TextUtils.isEmpty(newPassword)){
			(new ChangePasswordTask()).execute(oldPassword,newPassword);
		}else{
			Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_LONG).show();
		}
		
	}
	
	
	/**
	 * Asynchronous change password task.
	 */
	private class ChangePasswordTask extends AsyncTask<String, Void, String> {
		
		
		
		@Override
		protected void onPreExecute() {
			showProgress(true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			
			String response=null;
			response=userController.changePassword(txtChangePassword.getText().toString(), 
					txtChangePasswordConfirm.getText().toString());
			return response;
			
		}

		@Override
		protected void onPostExecute(final String response) {
			
			if(response.equals(UserController.RESPONSE_OK)){
				Toast.makeText(ChangePasswordActivity.this, getString(R.string.success_change_password),
						Toast.LENGTH_LONG).show();
				
				Intent i=new Intent(ChangePasswordActivity.this,SelectServiceTypeActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
				
			}else if(response.equals(UserController.RESPONSE_FAILED)){
				Toast.makeText(ChangePasswordActivity.this, getString(R.string.failed_change_password),
						Toast.LENGTH_LONG).show();
			}else if(response.equals(UserController.SERVER_ERROR)){
				Toast.makeText(ChangePasswordActivity.this,getString(R.string.login_error),Toast.LENGTH_LONG).show();
			}
			
			showProgress(false);
		}

		@Override
		protected void onCancelled() {
			
			showProgress(false);
			Toast.makeText(ChangePasswordActivity.this,getString(R.string.login_error),Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}
