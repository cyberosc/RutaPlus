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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user
 */
public class LoginActivity extends Activity {
	
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String valueEmail;
	private String valuePswrd;

	// UI references.
	private EditText txtEmail;
	private EditText txtPswrd;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	
	//controller reference
	private UserController userController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		valueEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		txtEmail = (EditText) findViewById(R.id.txt_email);
		txtEmail.setText(valueEmail);

		txtPswrd = (EditText) findViewById(R.id.txt_pswrd);
		txtPswrd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					Toast.makeText(LoginActivity.this, "entre a esto que no se que es", Toast.LENGTH_LONG).show();
					attemptLogin();
					return true;
					
				}
				return false;
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}*/

	/**
	 * Attempts to sign in  the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		txtEmail.setError(null);
		txtPswrd.setError(null);

		// Store values at the time of the login attempt.
		valueEmail = txtEmail.getText().toString();
		valuePswrd = txtPswrd.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(valuePswrd)) {
			txtPswrd.setError(getString(R.string.error_field_required));
			focusView = txtPswrd;
			cancel = true;
		} else if (valuePswrd.length() < 4) {
			txtPswrd.setError(getString(R.string.error_invalid_password));
			focusView = txtPswrd;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(valueEmail)) {
			txtEmail.setError(getString(R.string.error_field_required));
			focusView = txtEmail;
			cancel = true;
		} else if (!valueEmail.contains("@")) {
			txtEmail.setError(getString(R.string.error_invalid_email));
			focusView = txtEmail;
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
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
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

	/**
	 * Asynchronous login task used to authenticate the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, String> {
		
		@Override
		protected String doInBackground(Void... params) {
			
			String response;
			userController=new UserController(LoginActivity.this);
			response=userController.loginUserService(valueEmail, valuePswrd);
			return response;
			
		}

		@Override
		protected void onPostExecute(final String response) {
			mAuthTask = null;
			String successMessage=null;
			showProgress(false);
			Log.i(this.getClass().getSimpleName(),"response:"+response);

			if (response.equals(UserController.LOGIN_SUCCESS)) {
				//successMessage="Inicio de sesión éxitoso";
				//Intent i=new Intent(LoginActivity.this,SelectServiceTypeActivity.class);
				Intent i=new Intent(LoginActivity.this,CardListActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			} else if(response.equals(UserController.LOGIN_FAILED)) {
				successMessage="Tu usuario o contraseña son incorrectos.";
				txtPswrd.setError(getString(R.string.error_incorrect_password));
				txtPswrd.requestFocus();
				Toast.makeText(LoginActivity.this, successMessage, Toast.LENGTH_LONG).show();
				
			}else if(response.equals(UserController.LOGIN_ENTERPRISE)){
				Intent i=new Intent(LoginActivity.this,SelectServiceTypeActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			}else {
				Toast.makeText(LoginActivity.this,getString(R.string.login_error),Toast.LENGTH_LONG).show();
			}
			
			
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
			Toast.makeText(LoginActivity.this,getString(R.string.login_error),Toast.LENGTH_LONG).show();
		}
	}
}
