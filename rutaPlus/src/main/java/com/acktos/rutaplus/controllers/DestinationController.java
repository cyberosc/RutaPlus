package com.acktos.rutaplus.controllers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.acktos.rutaplus.R;
import com.acktos.rutaplus.android.Encrypt;
import com.acktos.rutaplus.android.HttpRequest;
import com.acktos.rutaplus.entities.Destination;
import com.acktos.rutaplus.entities.User;

/**
 * @deprecated deprecated controller class for chosen driver
 */
public class DestinationController {

	private Context context;
	private UserController userController;
	private static final String TOKEN="ee8099de39d5167fe135baf92fa0df1c";
	private static final String RESPONSE_SUCCESS_CODE="200";
	private static final String RESPONSE_TAG="response";
	private static final String KEY_ENCRYPT="encrypt";

	public DestinationController(Context context){
		this.context=context;
		userController=new UserController(context);
	}


	public ArrayList<Destination> getAllDestination(){

		ArrayList<Destination> destinations=null;
		String userId=userController.getUserId();


		HttpRequest httpRequest=new HttpRequest(context.getString(R.string.url_list_destination));
		
		httpRequest.setParam(Destination.KEY_ID,"null");
		httpRequest.setParam(User.KEY_USER, userId);
		httpRequest.setParam(Destination.KEY_TYPE, "1");
		
		String encrypt=Encrypt.md5(null+userId+"1"+TOKEN);
		httpRequest.setParam(KEY_ENCRYPT, encrypt);
		
		String responseData=httpRequest.postRequest();
		if(responseData!=null){
			Log.i("debug getAllDestination",responseData);
			try {
				JSONObject jsonObject=new JSONObject(responseData);
				String responseCode=jsonObject.getString(RESPONSE_TAG);
				if(responseCode.equals(RESPONSE_SUCCESS_CODE)){
					
					destinations=new ArrayList<Destination>();
					JSONArray jsonArray=jsonObject.getJSONArray("fields");
					for(int i=0;i<jsonArray.length();i++){
						JSONObject itemObject=jsonArray.getJSONObject(i);
						destinations.add(addItemCar(itemObject));
						//Log.i("debug item object",itemObject.toString(1));
					}
					//Log.i("debug arrayList",cars.toString());
				}
			} catch (JSONException e) {
				e.getMessage();
			}
		}

		return destinations;
	}
	
	/**
	 * add a destination object to ArrayList
	 */
	private Destination addItemCar(JSONObject jsonObject){
		
		Destination destination=new Destination();
		try {
			destination.jsonToObject(jsonObject);
		} catch (JSONException e) {
			e.getMessage();
		}
		return destination;
	}
}
