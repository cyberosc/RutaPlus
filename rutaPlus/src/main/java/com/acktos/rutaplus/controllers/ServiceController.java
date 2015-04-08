package com.acktos.rutaplus.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.acktos.rutaplus.AddServiceVIPActivity;
import com.acktos.rutaplus.R;
import com.acktos.rutaplus.android.Encrypt;
import com.acktos.rutaplus.android.HttpRequest;
import com.acktos.rutaplus.entities.Service;

public class ServiceController {

	private Context context;
	private UserController userController;

	private static final String KEY_ID="id";
	private static final String KEY_ADDRESS="address";
	private static final String KEY_ADDRESS_DESTINATION="destino";
	private static final String KEY_PLACE="nombre";
	private static final String KEY_PLACE_ID="id_lugar";
	private static final String KEY_DESTINATION_ID="id_destino";
	private static final String KEY_CARD_REFERENCE="referencia_tarjeta";
	private static final String KEY_LATLNG="geolocation";
	private static final String KEY_DATE_TIME="fecha_recogida";
	private static final String KEY_CAR_ID="vehiculo";
	private static final String KEY_USER_ID="cliente";
	private static final String KEY_STATUS="estado";
	private static final String KEY_DEFER_DATE="fecha_aplazado";
	private static final String KEY_RATING="calificacion";
	private static final String KEY_COMMENT="comentario";

	private static final String KEY_ENCRYPT="encrypt";
	private static final String TOKEN="ee8099de39d5167fe135baf92fa0df1c";
	private static final String RESPONSE_SUCCESS_CODE="200";
	private static final String RESPONSE_TAG="response";

	public static final String STATUS_KEY_PENDING="Pendiente";
	public static final String STATUS_KEY_APPROVED="Aprobado";
	public static final String STATUS_KEY_ONTRACK="En camino";
	public static final String STATUS_KEY_COMPLETED="Completado";
	public static final String STATUS_KEY_CANCELED="Cancelado";
	public static final String STATUS_KEY_DEFER="Aplazado";
	public static final String STATUS_KEY_IN_PLACE="Llego el conductor";


	public ServiceController(Context context){
		this.context=context;
		userController=new UserController(this.context);
	}

	public boolean addService(Service service){

		boolean success=false;
		HttpRequest httpPost=new HttpRequest(context.getString(R.string.url_add_service));

		String userId=userController.getUserId();

		String name="null";
		String placeId="null";
		String destinationId="null";
		String pickupAddress="null";
		String destinationAddress="null";
		String carId="null";
		String electedDateTime="null";
		String electedCoordinates="null";
		String cardReference="null";

		if(!TextUtils.isEmpty(service.placeId)){
			placeId=service.placeId;
		}
		if(!TextUtils.isEmpty(service.destinationId)){
			destinationId=service.destinationId;
		}
		if(!TextUtils.isEmpty(service.pickupAddress)){
			pickupAddress=service.pickupAddress;
		}
		if(!TextUtils.isEmpty(service.destinationAddress)){
			destinationAddress=service.destinationAddress;
		}
		if(!TextUtils.isEmpty(service.carId)){
			carId=service.carId;
		}
		if(!TextUtils.isEmpty(service.dateTime)){
			electedDateTime=service.dateTime;
		}
		if(!TextUtils.isEmpty(service.latlng)){
			electedCoordinates=service.latlng;
		}
		if(!TextUtils.isEmpty(service.cardReference)){
			cardReference=service.cardReference;
		}



		httpPost.setParam(KEY_PLACE, name);
		httpPost.setParam(KEY_ADDRESS, pickupAddress);
		httpPost.setParam(KEY_LATLNG, electedCoordinates);
		httpPost.setParam(KEY_USER_ID, userId);
		httpPost.setParam(KEY_ADDRESS_DESTINATION, destinationAddress);
		httpPost.setParam(KEY_DATE_TIME,electedDateTime);
		httpPost.setParam(KEY_CAR_ID,carId);
		httpPost.setParam(KEY_PLACE_ID,placeId);
		httpPost.setParam(KEY_DESTINATION_ID, destinationId);
		httpPost.setParam(KEY_CARD_REFERENCE,cardReference);

		String encrypt=Encrypt.md5(
				name+
				pickupAddress+
				electedCoordinates+
				userId+
				destinationAddress+
				electedDateTime+
				carId+
				placeId+
				destinationId+
				cardReference+
				TOKEN);
		Log.i("debug encrypt service",name+
				pickupAddress+" "+
				electedCoordinates+" "+
				userId+" "+
				destinationAddress+" "+
				electedDateTime+" "+
				carId+" "+
				placeId+" "+
				destinationId+" "+
				cardReference+" "+
				TOKEN);
		httpPost.setParam(KEY_ENCRYPT, encrypt);


		String responseData=httpPost.postRequest();
		if(responseData!=null){
			Log.i("response data service",responseData);
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

	public ArrayList<Service> getAllServices(){

		ArrayList<Service> services=new ArrayList<Service>();
		String userId=userController.getUserId();

		HttpRequest httpRequest=new HttpRequest(context.getString(R.string.url_list_service));
		httpRequest.setParam(KEY_ID,"null");
		httpRequest.setParam(KEY_USER_ID, userId);
		String encrypt=Encrypt.md5(null+userId+TOKEN);
		httpRequest.setParam(KEY_ENCRYPT, encrypt);
		String responseData=httpRequest.postRequest();
		if(responseData!=null){
			Log.i(this.toString()+"getAllervices()",responseData);
			try {
				JSONObject jsonObject=new JSONObject(responseData);
				String responseCode=jsonObject.getString(RESPONSE_TAG);
				if(responseCode.equals(RESPONSE_SUCCESS_CODE)){

					JSONArray jsonArray=jsonObject.getJSONArray("fields");
					for(int i=0;i<jsonArray.length();i++){
						JSONObject itemObject=jsonArray.getJSONObject(i);
						services.add(addItemService(itemObject));

					}

				}
			} catch (JSONException e) {
				e.getMessage();
			}
		}

		return services;
	}

	/**
	 * add a service object to ArrayList
	 */
	private Service addItemService(JSONObject jsonObject){


		Service service=new Service();
		try {
			service.jsonToObject(jsonObject);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return service;
	}

	public boolean deferService(String serviceId,String oldDate,int minutes){

		boolean success=false;

		try {

			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
			Date date = sdf.parse(oldDate);

			DateTime dateTime=new DateTime(date);
			DateTime newTime=dateTime.plusMinutes(minutes);

			DateTimeFormatter fmt=DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
			String newDate=newTime.toString(fmt);

			HttpRequest httpPost=new HttpRequest(context.getString(R.string.url_defer_service));

			String encrypt=Encrypt.md5(serviceId+"null"+newDate+TOKEN);

			httpPost.setParam(KEY_ID, serviceId);
			httpPost.setParam(KEY_STATUS, "null");
			httpPost.setParam(KEY_DEFER_DATE, newDate);
			httpPost.setParam(KEY_ENCRYPT, encrypt);


			String responseData=httpPost.postRequest();
			if(responseData!=null){
				Log.i("response defer service",responseData);
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


		} catch (ParseException e) {

			e.printStackTrace();
		}

		return success;
	}

	public boolean ratingService(String serviceId,String rating ,String comment){

		boolean success=false;

		try {
			
			if(TextUtils.isEmpty(comment)){
				comment="null";
			}
			
			HttpRequest httpPost=new HttpRequest(context.getString(R.string.url_service_rating));

			String encrypt=Encrypt.md5(serviceId+rating+comment+TOKEN);
			
			Log.i(this.toString()+"ratingService",serviceId+" "+rating+" "+comment+" "+TOKEN);

			httpPost.setParam(KEY_ID, serviceId);
			httpPost.setParam(KEY_RATING, rating);
			httpPost.setParam(KEY_COMMENT, comment);
			httpPost.setParam(KEY_ENCRYPT, encrypt);


			String responseData=httpPost.postRequest();
			if(responseData!=null){
				Log.i(this.toString()+"response ratig service",responseData);
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


		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return success;
	}
	

}
