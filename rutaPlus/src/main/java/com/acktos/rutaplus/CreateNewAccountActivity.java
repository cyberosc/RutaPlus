package com.acktos.rutaplus;

import com.acktos.rutaplus.controllers.UserController;
import com.acktos.rutaplus.entities.User;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class CreateNewAccountActivity extends Activity {

	private UserController userController;
	
	
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private RegisterUserTask mAuthTask = null;

	// Values for EditText.
	private String nameValue;
	private String emailValue;
	private String ccValue;
	private String phoneValue;
	private String pswrdValue;
	private boolean termsCheck;

	// UI references.
	private EditText txtName;
	private EditText txtEmail;
	private EditText txtCC;
	private EditText txtPhone;
	private EditText txtPswrd;
	private ToggleButton tglTerms;

	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_create_new_account);

		userController=new UserController(this);
		txtName = (EditText) findViewById(R.id.txt_name);
		txtEmail = (EditText) findViewById(R.id.txt_email);
		txtCC = (EditText) findViewById(R.id.txt_cc);
		txtPhone = (EditText) findViewById(R.id.txt_phone);
		txtPswrd = (EditText) findViewById(R.id.txt_pswrd);
		tglTerms=(ToggleButton) findViewById(R.id.tgl_terms);

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);


		txtPswrd = (EditText) findViewById(R.id.txt_pswrd);
		txtPswrd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});


		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});

	}
	
	public void showTerms(View view){
		
		Intent i=new Intent(this,TermsActivity.class);
		startActivity(i);
		
	}
	
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		txtName.setError(null);
		txtEmail.setError(null);
		txtCC.setError(null);
		txtPhone.setError(null);
		txtPswrd.setError(null);

		// Store values at the time of the login attempt.
		nameValue = txtName.getText().toString();
		emailValue = txtEmail.getText().toString();
		ccValue = txtCC.getText().toString();
		phoneValue = txtPhone.getText().toString();
		pswrdValue = txtPswrd.getText().toString();
		

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(pswrdValue)) {
			txtPswrd.setError(getString(R.string.error_field_required));
			focusView = txtPswrd;
			cancel = true;
		} else if (pswrdValue.length() < 4) {
			txtPswrd.setError(getString(R.string.error_invalid_password));
			focusView = txtPswrd;
			cancel = true;
		}
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

		if (TextUtils.isEmpty(emailValue)) {
			txtEmail.setError(getString(R.string.error_field_required));
			focusView = txtEmail;
			cancel = true;
		} else if (!emailValue.contains("@")) {
			txtEmail.setError(getString(R.string.error_invalid_email));
			focusView = txtEmail;
			cancel = true;
		}
		if (TextUtils.isEmpty(nameValue)) {
			txtName.setError(getString(R.string.error_field_required));
			focusView = txtName;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			
			//verify if the terms was checked
			termsCheck=tglTerms.isChecked();
			if(termsCheck){
				// Show a progress spinner, and kick off a background task to
				// perform the user login attempt.
				mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
				showProgress(true);
				mAuthTask = new RegisterUserTask();
				mAuthTask.execute(nameValue,emailValue,ccValue,phoneValue,pswrdValue);
			}else{
				Toast.makeText(this,getString(R.string.msg_check_terms),Toast.LENGTH_LONG).show();
			}
			
			
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
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
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class RegisterUserTask extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {

			User user=new User();
			user.setName(params[0]);
			user.setEmail(params[1]);
			user.setCc(params[2]);
			user.setPhone(params[3]);
			user.setPswrd(params[4]);

			if(userController.addUserService(user)){
				return true;
			}

			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;

			if (success) {
				//Toast.makeText(CreateNewAccountActivity.this, "El registro se agrego con exito", Toast.LENGTH_LONG).show();
				Intent i=new Intent(CreateNewAccountActivity.this,CardListActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			} else {
				Toast.makeText(CreateNewAccountActivity.this, "El registro NO se agrego", Toast.LENGTH_LONG).show();
				showProgress(false);
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
