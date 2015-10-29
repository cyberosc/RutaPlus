package com.acktos.rutaplus;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acktos.rutaplus.adapters.CreditCardsAdapter;
import com.acktos.rutaplus.controllers.PaymentController;


import com.acktos.rutaplus.controllers.UserController;
import com.acktos.rutaplus.entities.CreditCard;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity that shows a list of credit cards
 * through the connection to the payment gateway.
 */
public class CardListActivity extends Activity {


	//UI references
	private ListView listCreditCardsView;
	private TextView txtEmptyCards;
	private Button btnRefreshRegister;
	private Button btnLogout;
	private ProgressDialog progress;
	private boolean isFromMenu;
	private ActionBar actionBar;

	//APP Components
	private PaymentController payment;

	//CONSTANTS
	public static final String FROM_MENU_INTENT="com.acktos.rutaplus_FROM_MENU_INTENT";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card_list);
		
		actionBar=getActionBar();

		
		//initialize UI references
		btnRefreshRegister = (Button) findViewById(R.id.btn_refresh_register);
		listCreditCardsView=(ListView)findViewById(R.id.list_credit_cards);
		txtEmptyCards=(TextView)findViewById(R.id.lbl_empty_cards);
		btnLogout=(Button)findViewById(R.id.btn_no_add_payment);


		// Initialize APP Components
		payment=new PaymentController(this);
		paymentIsRegistered();

		if(getIntent().hasExtra(FROM_MENU_INTENT)){
			isFromMenu=true;
		}else{
			isFromMenu=false;;
		}
		
		if(isFromMenu){
			actionBar.setDisplayUseLogoEnabled(false);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		
		//set click handler to card list
		listCreditCardsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

				Object item=parent.getItemAtPosition(position);
				CreditCard card=(CreditCard)item;

				payment.setPaymentCard(card);
				
				Toast.makeText(CardListActivity.this, getString(R.string.set_payment_success),Toast.LENGTH_SHORT).show();
				Intent i=new Intent(CardListActivity.this,SelectServiceTypeActivity.class);
				startActivity(i);

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		
		Intent i;
		// Handle action bar buttons
		switch(item.getItemId()) {
		case R.id.action_add:

			Log.i("onOptionItemSeleted","add button");

			i=new Intent(this,AddCardActivity.class);
			startActivity(i);
			return true;
		
		case android.R.id.home:
			i=new Intent(this,SelectServiceTypeActivity.class);
			startActivity(i);
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public void logout(View view) {
		deleteFile(UserController.FILE_NAME_PROFILE);
		Intent i=new Intent(this,GoogleSignInActivity.class);
		startActivity(i);
		finish();
	}

	private void showTryAgainOptions(){

		btnRefreshRegister.setVisibility(View.VISIBLE);
		listCreditCardsView.setVisibility(View.GONE);
	}
	/**
	 * call login asyncTask
	 */
	private void paymentIsRegistered(){
		(new IsRegisteredTask()).execute();
	}

	/**
	 * call login asyncTask
	 */
	private void paymentLogin(){
		(new LoginTask()).execute();
	}

	/**
	 * call register asyncTask
	 */
	private void paymentRegister(){
		(new RegisterTask()).execute();
	}

	/**
	 * call get card list asyncTask
	 */
	private void paymentGetCardList(){
		(new getCardListTask()).execute();
	}

	/**
	 * invoked by refresh payment connect button
	 */
	public void refreshPaymentConnection(View view){

		//Log.i(this.getClass().getSimpleName()+"RefreshPaymentConnection","entre");
		paymentIsRegistered();
	}

	private void showProgress(){
		if (progress == null || !progress.isShowing()){
			progress = new ProgressDialog(this);
			progress.setCancelable(false);
			progress.setCanceledOnTouchOutside(false);
			progress.setMessage(getString(R.string.wait_payment_conection));				
			progress.show();
		}
	}

	private void dismissProgress(){

		if(progress!=null){
			progress.dismiss();
			progress = null;
		}

	}

	private class IsRegisteredTask extends AsyncTask<Void,Void,String>{

		@Override
		protected String doInBackground(Void... arg0) {

			return payment.isRegistered();
		}

		@Override
		protected void onPostExecute(String response) {
			super.onPostExecute(response);

			Log.i("is register response",response);
			if(!response.equals(PaymentController.RESULT_SERVER_ERROR)){

				if(response.equals(PaymentController.RESULT_FALSE)){
					paymentRegister();
				}else if(response.equals(PaymentController.RESULT_TRUE)){
					paymentLogin();
				}
			}else{
				showTryAgainOptions();
				dismissProgress();
			}

		}

		@Override
		protected void onPreExecute() {
			showProgress();
			super.onPreExecute();
		}

	}

	private class RegisterTask extends AsyncTask<Void,Void,String>{

		@Override
		protected String doInBackground(Void... arg0) {

			return payment.register();
		}

		@Override
		protected void onPostExecute(String response) {
			super.onPostExecute(response);

			Log.i("register response", response);

			if(!response.equals(PaymentController.RESULT_SERVER_ERROR) 
					&& !response.equals(PaymentController.RESULT_FALSE)){

				paymentLogin();

			}else{
				showTryAgainOptions();
			}


		}

	}

	private class LoginTask extends AsyncTask<Void,Void,String>{

		@Override
		protected String doInBackground(Void... arg0) {

			return payment.login();
		}

		@Override
		protected void onPostExecute(String response) {
			super.onPostExecute(response);

			Log.i("login response", response);

			if(!response.equals(PaymentController.RESULT_SERVER_ERROR) 
					&& !response.equals(PaymentController.RESULT_FALSE)){

				paymentGetCardList();

			}else{
				showTryAgainOptions();

			}

			dismissProgress();

		}

	}

	private class getCardListTask extends AsyncTask<Void, Void, ArrayList<CreditCard>>{


		@Override
		protected ArrayList<CreditCard> doInBackground(Void...params) {

			return payment.getCardList();
		}

		@Override
		protected void onPostExecute(ArrayList<CreditCard> cards) {

			super.onPostExecute(cards);

			if(cards!=null){

				if(cards.size()>=1){


					txtEmptyCards.setVisibility(View.GONE);
					btnRefreshRegister.setVisibility(View.GONE);

					CreditCardsAdapter creditCardAdapter = new CreditCardsAdapter(CardListActivity.this, cards);
					listCreditCardsView.setAdapter(creditCardAdapter); 

				}else{
					txtEmptyCards.setVisibility(View.VISIBLE);
					btnLogout.setVisibility(View.VISIBLE);
				}

			}



		}



	}

}
