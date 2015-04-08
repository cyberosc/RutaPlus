package com.acktos.rutaplus.controllers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.acktos.rutaplus.R;
import com.acktos.rutaplus.android.DateTimeUtils;
import com.acktos.rutaplus.android.Encrypt;
import com.acktos.rutaplus.android.HttpRequest;
import com.acktos.rutaplus.entities.Rate;
import com.acktos.rutaplus.entities.Service;
import com.acktos.rutaplus.entities.ServiceVIP;

public class ServiceVIPController {

	private Context context;
	private static final String TOKEN="ee8099de39d5167fe135baf92fa0df1c";
	private static final String KEY_USER_ID="id";
	private static final String KEY_ID="id";
	private static final String KEY_SERVICE_TYPE="tipo";
	private static final String KEY_ENCRYPT="encrypt";
	private static final String RESPONSE_TAG="response";
	private static final String MESSAGE_TAG="message";
	private static final String FIELDS_TAG="fields";
	private static final String RESPONSE_SUCCESS_CODE="200";

	
	
	private UserController userController;

	public ServiceVIPController(Context context){

		this.context=context;
		userController=new UserController(context);
	}

	public ArrayList<String> getRates(int serviceType){
		

		ArrayList<String> rates=null;
		String userId=userController.getUserId();

		HttpRequest httpPost=new HttpRequest(context.getString(R.string.url_get_rates));

		httpPost.setParam(KEY_USER_ID, userId);
		httpPost.setParam(KEY_SERVICE_TYPE, Integer.toString(serviceType));
		String encrypt=Encrypt.md5(userId+serviceType+TOKEN);
		httpPost.setParam(KEY_ENCRYPT, encrypt);

		String responseData=httpPost.postRequest();
		if(responseData!=null){
			Log.i("response cancel service",responseData);
			try {
				JSONObject jsonObject=new JSONObject(responseData);
				String responseCode=jsonObject.getString(RESPONSE_TAG);

				if(responseCode.equals(RESPONSE_SUCCESS_CODE)){
					rates=new ArrayList<String>();
					JSONObject ratesJson=jsonObject.getJSONObject(FIELDS_TAG);

					rates.add(ratesJson.getString(Rate.KEY_BASE_RATE));
					rates.add(ratesJson.getString(Rate.KEY_MIN_RATE));
					rates.add(ratesJson.getString(Rate.KEY_KM_RATE));
					rates.add(ratesJson.getString(Rate.KEY_DISCOUNT));
					rates.add(ratesJson.getString(Rate.KEY_INCREMENT));
				}
			} catch (JSONException e) {
				e.getMessage();
			}	
		}

		return rates;

	}
	
	public ArrayList<Rate> getAllRates(){
		
		ArrayList<Rate> rates=null;
		String userId=userController.getUserId();

		HttpRequest httpPost=new HttpRequest(context.getString(R.string.url_get_all_rates));

		httpPost.setParam(KEY_USER_ID, userId);
		String encrypt=Encrypt.md5(userId+TOKEN);
		httpPost.setParam(KEY_ENCRYPT, encrypt);

		String responseData=httpPost.postRequest();
		
		//String responseData="[{\"nombre\":\"VIP CAR\",\"tarifa\":\"5000\",\"minuto\":\"900\",\"km\":\"800\",\"descuento\":\"5000\",\"incremento\":\"1x\"},{\"nombre\":\"VIP VAN\",\"tarifa\":\"4000\",\"minuto\":\"901\",\"km\":\"801\",\"descuento\":\"5001\",\"incremento\":\"2x\"},{\"nombre\":\"VIP U\",\"tarifa\":\"3000\",\"minuto\":\"902\",\"km\":\"802\",\"descuento\":\"5002\",\"incremento\":\"3x\"}]";
		if(responseData!=null){
			Log.i("response getAllRates()",responseData);
			try {
				
				JSONObject jsonObject=new JSONObject(responseData);
				String responseCode=jsonObject.getString(RESPONSE_TAG);

				if(responseCode.equals(RESPONSE_SUCCESS_CODE)){
					
					rates=new ArrayList<Rate>();
					JSONArray jsonArray=jsonObject.getJSONArray(FIELDS_TAG);
					
					for(int i=0;i<=jsonArray.length();i++){
						
						JSONObject jsonObjectItem=jsonArray.getJSONObject(i);
						rates.add(new Rate(jsonObjectItem));
					}
				}
				
			} catch (JSONException e) {
				e.getMessage();
			}	
		}

		return rates;
	}
	
	public boolean addService(ServiceVIP service){

		boolean success=false;
		String cardReference;
		String cardType;
		String userEmail;
		String userId;
		String pswrd;
		String userCC;
		String DBDate;
		
		HttpRequest httpPost=new HttpRequest(context.getString(R.string.url_add_service_vip));
		
		UserController userController=new UserController(context);
		userId=userController.getUserId();
		userEmail=userController.getUserEmail();
		userCC=userController.getCC();
		
		if(TextUtils.isEmpty(service.cardReference)){
			cardReference="null";
		}else{
			cardReference=service.cardReference;
		}
		
		if(TextUtils.isEmpty(service.cardType)){
			cardType="null";
		}else{
			cardType=service.cardType;
		}
		
		pswrd=Encrypt.md5(userCC+TOKEN);
		DBDate=DateTimeUtils.convertDate(service.dateTime, DateTimeUtils.DIALOG_FORMAT,DateTimeUtils.DB_FORMAT);
		
		if(TextUtils.isEmpty(DBDate)){
			DBDate="null";
		}
		
		httpPost.setParam(ServiceVIP.KEY_NAME, "null");
		httpPost.setParam(ServiceVIP.KEY_ADDRESS, service.address);
		httpPost.setParam(ServiceVIP.KEY_COORDINATES, service.coordinates);
		httpPost.setParam(ServiceVIP.KEY_USER_ID, userId);
		httpPost.setParam(ServiceVIP.KEY_DESTINATION, "null");
		httpPost.setParam(ServiceVIP.KEY_DATE_TIME, DBDate);
		httpPost.setParam(ServiceVIP.KEY_PLACE_ID, "null");
		httpPost.setParam(ServiceVIP.KEY_SERVICE_TYPE, service.serviceType);
		httpPost.setParam(ServiceVIP.KEY_PAYMENT_USERNAME, userEmail);
		httpPost.setParam(ServiceVIP.KEY_PAYMENT_PSWRD, pswrd);
		httpPost.setParam(ServiceVIP.KEY_CARD_REFERENCE, cardReference);
		httpPost.setParam(ServiceVIP.KEY_CARD_TYPE, cardType);
		
		
		String encrypt=Encrypt.md5("null"
				+service.address
				+service.coordinates
				+userId
				+"null"
				+DBDate
				+"null"
				+service.serviceType
				+userEmail
				+pswrd
				+cardReference
				+cardType
				+TOKEN);
		
		Log.i("debug add service params","null"+" "
				+service.address+" "
				+service.coordinates+" "
				+userId+" "
				+"null"+" "
				+DBDate+" "
				+"null"+" "
				+service.serviceType+" "
				+userEmail+" "
				+pswrd+" "
				+cardReference+" "
				+cardType+" "
				+TOKEN);
		
		httpPost.setParam(KEY_ENCRYPT, encrypt);

		String responseData=httpPost.postRequest();
		if(responseData!=null){
			Log.i("response add service VIP",responseData);
			try {
				JSONObject jsonObject=new JSONObject(responseData);
				String responseCode=jsonObject.getString(RESPONSE_TAG);

				if(responseCode.equals(RESPONSE_SUCCESS_CODE)){
					success=true;
				}
			} catch (JSONException e) {
				e.getMessage();
			}	
		}

		return success;

	}
	
	public String cancelService(String serviceId){

		String message="";
		
		HttpRequest httpPost=new HttpRequest(context.getString(R.string.url_cancel_service));
		String encrypt=Encrypt.md5(serviceId+TOKEN);

		httpPost.setParam(KEY_ID, serviceId);
		httpPost.setParam(KEY_ENCRYPT, encrypt);

		String responseData=httpPost.postRequest();
		if(responseData!=null){
			Log.i("response cancel service",responseData);
			try {
				JSONObject jsonObject=new JSONObject(responseData);
				String responseCode=jsonObject.getString(RESPONSE_TAG);

				if(responseCode.equals(RESPONSE_SUCCESS_CODE)){
					message=jsonObject.getString(MESSAGE_TAG);
				}
			} catch (JSONException e) {
				e.getMessage();
			}	
		}


		return message;

	}
}
