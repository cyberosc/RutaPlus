package com.acktos.rutaplus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.acktos.rutaplus.controllers.UserController;


public class RememberPasswordActivity extends Activity {

    private EditText edtRememberPass;

    private View rememberForm;
    private View rememberStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remember_password);
        edtRememberPass=(EditText) findViewById(R.id.edt_remember_pass);
        rememberForm= findViewById(R.id.remember_pass_form);
        rememberStatus= findViewById(R.id.remember_pass_status);

    }

    public void rememberPassAttempt(View view){

        edtRememberPass.setError(null);

        String email=edtRememberPass.getText().toString();

        if (isValidEmail(email)) {
            showProgress(true);
            (new RememberPasswordTask()).execute(email);
        }else{
            edtRememberPass.setError(getString(R.string.error_invalid_email));
        }


    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        rememberStatus.setVisibility(View.VISIBLE);
        rememberStatus.animate().setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        rememberStatus.setVisibility(show ? View.VISIBLE
                                : View.GONE);
                    }
                });

        rememberForm.setVisibility(View.VISIBLE);
        rememberForm.animate().setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        rememberForm.setVisibility(show ? View.GONE
                                : View.VISIBLE);
                    }
                });

    }

    public class RememberPasswordTask extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... strings) {

            String result=(new UserController(RememberPasswordActivity.this)).rememberPassword(strings[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String msg;

            if(result.equals(UserController.RESPONSE_OK)){
                msg=getString(R.string.success_remember_password);
            }else if(result.equals(UserController.SERVER_ERROR)){
                msg=getString(R.string.failed_remember_password);
            }else{
                msg=getString(R.string.msg_user_not_exists);
            }

            Toast.makeText(RememberPasswordActivity.this,msg,Toast.LENGTH_SHORT).show();
            showProgress(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }
}