package com.acktos.rutaplus.controllers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.acktos.rutaplus.R;
import com.acktos.rutaplus.android.Encrypt;
import com.acktos.rutaplus.android.HttpRequest;
import com.acktos.rutaplus.entities.Car;

import android.content.Context;
import android.util.Log;


/**
 * @deprecated deprecated controller class for elected Driver
 */
public class CarController {
	
	private Context context;
	private static final String KEY_ID="id";
	private static final String KEY_ALIAS="alias";
	private static final String KEY_PLATE="placa";
	private static final String KEY_BRAND="marca";
	private static final String KEY_LINE="linea";
	private static final String KEY_COLOR="color";
	private static final String KEY_USER_ID="cliente";
	private static final String KEY_ENCRYPT="encrypt";
	private UserController userController;
	private static final String TOKEN="ee8099de39d5167fe135baf92fa0df1c";
	private static final String RESPONSE_SUCCESS_CODE="200";
	private static final String RESPONSE_TAG="response";
	
	public CarController(Context context){
		this.context=context;
		userController=new UserController(this.context);
	}
	
	public boolean addCarService(Car car){

		boolean success=false;
		HttpRequest httpPost=new HttpRequest(context.getString(R.string.url_add_car_service));

		String userId=userController.getUserId();
		
		
		
		String carId="null";
		String carAlias="null";
		String carColor="null";
		
		if(car.id!=null){
			carId=car.id;
		}
		
		if(!car.alias.isEmpty()){
			carAlias=car.alias;
		}
		if(!car.color.isEmpty()){
			carColor=car.color;
		}
		
		httpPost.setParam(KEY_ID, carId);
		httpPost.setParam(KEY_USER_ID, userId);
		httpPost.setParam(KEY_PLATE, car.plate);
		httpPost.setParam(KEY_BRAND, car.brand);
		httpPost.setParam(KEY_LINE, car.line);
		httpPost.setParam(KEY_COLOR, carColor);
		httpPost.setParam(KEY_ALIAS, carAlias);
		
		Log.i("debug encrypt car",car.id+userId+car.alias+car.plate+car.brand+car.color+car.line+TOKEN);
		String encrypt=Encrypt.md5(carId+userId+carAlias+car.plate+car.brand+carColor+car.line+TOKEN);
		httpPost.setParam(KEY_ENCRYPT, encrypt);

		String responseData=httpPost.postRequest();
		Log.i("debug response new car",responseData);
		if(responseData!=null){
			Log.i("response data",responseData);
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
	
	public ArrayList<Car> getAllCars(){
		
		ArrayList<Car> cars=new ArrayList<Car>();
		String userId=userController.getUserId();
		

		HttpRequest httpRequest=new HttpRequest(context.getString(R.string.url_list_cars));
		httpRequest.setParam(KEY_ID,"null");
		httpRequest.setParam(KEY_USER_ID, userId);
		String encrypt=Encrypt.md5(null+userId+TOKEN);
		httpRequest.setParam(KEY_ENCRYPT, encrypt);
		String responseData=httpRequest.postRequest();
		if(responseData!=null){
			//Log.i("debug response data",responseData);
			try {
				JSONObject jsonObject=new JSONObject(responseData);
				String responseCode=jsonObject.getString(RESPONSE_TAG);
				if(responseCode.equals(RESPONSE_SUCCESS_CODE)){
					
					JSONArray jsonArray=jsonObject.getJSONArray("fields");
					for(int i=0;i<jsonArray.length();i++){
						JSONObject itemObject=jsonArray.getJSONObject(i);
						cars.add(addItemCar(itemObject));
						//Log.i("debug item object",itemObject.toString(1));
					}
					//Log.i("debug arrayList",cars.toString());
				}
			} catch (JSONException e) {
				e.getMessage();
			}
		}
		
		return cars;
	}

	public boolean deleteCar(Car car){
		boolean success=false;
		HttpRequest httpPost=new HttpRequest(context.getString(R.string.url_delete_car));

		String userId=userController.getUserId();
		String encrypt=Encrypt.md5(car.id+userId+TOKEN);
		httpPost.setParam(KEY_ID, car.id);
		httpPost.setParam(KEY_USER_ID, userId);
		httpPost.setParam(KEY_ENCRYPT, encrypt);

		String responseData=httpPost.postRequest();
		if(responseData!=null){
			Log.i("response delete car",responseData);
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
	
	
	/**
	 * add a car object to ArrayList
	 */
	private Car addItemCar(JSONObject jsonObject){
		
		Car car=new Car();
		try {
			car.jsonToObject(jsonObject);
		} catch (JSONException e) {
			e.getMessage();
		}
		return car;
	}
}

