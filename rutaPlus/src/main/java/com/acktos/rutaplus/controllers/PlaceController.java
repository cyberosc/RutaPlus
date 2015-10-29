package com.acktos.rutaplus.controllers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acktos.rutaplus.R;
import com.acktos.rutaplus.android.Encrypt;
import com.acktos.rutaplus.android.HttpRequest;
import com.acktos.rutaplus.entities.Place;

import android.content.Context;
import android.util.Log;


/**
 * @deprecated deprecated controller class for user's favorite places.
 */
public class PlaceController {
	
	private Context context;
	private static final String KEY_ID="id";
	private static final String KEY_USER_ID="cliente";
	private static final String KEY_FAVORITE="favorito";
	private static final String KEY_ENCRYPT="encrypt";
	private UserController userController;
	private static final String TOKEN="ee8099de39d5167fe135baf92fa0df1c";
	private static final String RESPONSE_SUCCESS_CODE="200";
	private static final String RESPONSE_TAG="response";
	
	public static final String TAG_PLACE_ID="com.acktos.rutaplus.PLACE_ID";
	public static final String TAG_PLACE_ADDRESS="com.acktos.rutaplus.PLACE_ADDRESS";
	
	
	public PlaceController(Context context){
		this.context=context;
		userController=new UserController(this.context);
	}
	
	
	public ArrayList<Place> getAllPlaces(){
		
		ArrayList<Place> places=null;
		String userId=userController.getUserId();
		

		HttpRequest httpRequest=new HttpRequest(context.getString(R.string.url_list_places));
		
		httpRequest.setParam(KEY_ID,"null");
		httpRequest.setParam(KEY_USER_ID, userId);
		httpRequest.setParam(KEY_FAVORITE,"null");
		
		String encrypt=Encrypt.md5("null"+userId+"null"+TOKEN);
		httpRequest.setParam(KEY_ENCRYPT, encrypt);
		String responseData=httpRequest.postRequest();
		if(responseData!=null){
			Log.i("debug response list places",responseData);
			try {
				JSONObject jsonObject=new JSONObject(responseData);
				String responseCode=jsonObject.getString(RESPONSE_TAG);
				if(responseCode.equals(RESPONSE_SUCCESS_CODE)){
					
					places=new ArrayList<Place>();
					JSONArray jsonArray=jsonObject.getJSONArray("fields");
					for(int i=0;i<jsonArray.length();i++){
						JSONObject itemObject=jsonArray.getJSONObject(i);
						places.add(addItemCar(itemObject));
						//Log.i("debug item object",itemObject.toString(1));
					}
					//Log.i("debug arrayList",places.toString());
				}
			} catch (JSONException e) {
				e.getMessage();
			}
		}
		
		return places;
	}
	
	/**
	 * add a car object to ArrayList
	 */
	private Place addItemCar(JSONObject jsonObject){
		
		Place place=new Place();
		try {
			place.jsonToObject(jsonObject);
		} catch (JSONException e) {
			e.getMessage();
		}
		return place;
	}
}

