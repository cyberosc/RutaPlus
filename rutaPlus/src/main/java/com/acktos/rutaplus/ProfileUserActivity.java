package com.acktos.rutaplus;

import org.json.JSONException;
import org.json.JSONObject;

import com.acktos.rutaplus.android.InternalStorage;
import com.acktos.rutaplus.controllers.UserController;
import com.acktos.rutaplus.entities.User;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays the profile user form and send it to REST API.
 */
public class ProfileUserActivity extends Activity {
	private UserController userController;

	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";


	private CompleteUserProfileTask completeProfileTask = null;

	private String ccValue;
	private String phoneValue;

	// UI references.
	private EditText txtCC;
	private EditText txtPhone;
	private TextView txtMessage;
	
	
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_profile_user);
		
		userController=new UserController(this);
		txtCC = (EditText) findViewById(R.id.txt_cc);
		txtPhone = (EditText) findViewById(R.id.txt_phone);
		txtMessage = (TextView) findViewById(R.id.txt_message);
		
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		
		Bundle extras=getIntent().getExtras();
		String name=extras.getString("name");
		txtMessage.setText(String.format(getString(R.string.greeting_profile_complete), name));
		
		
		findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		//getMenuInflater().inflate(R.menu.create_new_account, menu);
		//return true;
		return false;
	}


	public void attemptLogin() {
		if (completeProfileTask != null) {
			return;
		}

		// Reset errors.
		txtCC.setError(null);
		txtPhone.setError(null);


		// Store values at the time of the login attempt.
		ccValue = txtCC.getText().toString();
		phoneValue = txtPhone.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		
		if (TextUtils.isEmpty(phoneValue)) {
			txtPhone.setError(getString(R.string.error_field_required));
			focusView = txtPhone;
			cancel = true;
		}
		if (TextUtils.isEmpty(ccValue)) {
			txtCC.setError(getString(R.string.error_field_required));
			focusView = txtCC;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			completeProfileTask = new CompleteUserProfileTask();
			completeProfileTask.execute(ccValue,phoneValue);
		}
	}


	private void showProgress(final boolean show) {


		int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

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

	}

	/**
	 * Async task to send the user profile to REST API.
	 */
	public class CompleteUserProfileTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			
			InternalStorage storage=new InternalStorage(ProfileUserActivity.this);
			String jsonString=storage.readFile(UserController.FILE_NAME_PROFILE);
			//Log.i("debug file before save",jsonString);
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(jsonString);
				String userId=jsonObject.getString(UserController.ID_TAG);
				User user=new User();
				user.setId(userId);
				user.setCc(params[0]);
				user.setPhone(params[1]);
				
				if(userController.addUserService(user)){
					return true;
				}
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			completeProfileTask = null;
			

			if (success) {
				Toast.makeText(ProfileUserActivity.this, "El registro se agrego con exito", Toast.LENGTH_LONG).show();
				Intent i=new Intent(ProfileUserActivity.this,CardListActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				showProgress(false);
				finish();
			} else {
				showProgress(false);
				Toast.makeText(ProfileUserActivity.this, "El registro NO se agrego", Toast.LENGTH_LONG).show();
				
			}
		}

		@Override
		protected void onCancelled() {
			completeProfileTask = null;
			showProgress(false);
		}
	}
}
