package com.acktos.rutaplus.entities;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * A simple DAO class for encapsulating an entity through the REST API.
 * This class represents the API response to a service request.
 */

public class Service {

    /** request unique ID response */
	public String id;

    /** Address of pickup*/
	public String pickupAddress;

    /**Latitude and longitude of the user's location*/
	public String latlng;

    /** Unique ID for user's favorite pickup place*/
	public String placeId;

    /** Unique ID for user's favorite destination place*/
	public String destinationId;

    /**Date on which the driver must pickup the user*/
	public String dateTime;

    /** Unique id for a vehicle that providing the service*/
	public String carId;

    /** String that represents current service status ex: APPROVED */
	public String status;

    /** Driver name who provide the service*/
	public String driver;

    /** Time that user decided defer service*/
	public String dateTimeDefer;

    /** Address of destination to which the user is directed*/
	public String destinationAddress;

    /**Type of vehicle that user requested*/
	public String serviceType;

    /**Card id from {@link CreditCard}*/
	public String cardReference;

    /** URI indicating driver's photo*/
	public String driverPhoto;

    /**plate of the vehicle that will provide the service*/
	public String carPlate;

    /** Driver's telephone number*/
    public String driverPhone;
	
	//Constant keys
	public static final String KEY_PLACE_ID="com.acktos.rutaplus.PLACE_ID";
	public static final String KEY_DESTINATION_ID="com.acktos.rutaplus.DESTINATION_ID";
	public static final String KEY_PICKUP_ADDRESS="com.acktos.rutaplus.PICKUP_ADDRESS";
	public static final String KEY_DESTINATION_ADDRESS="com.acktos.rutaplus.DESTINATION_ADDRESS";
	public static final String KEY_CAR_ID="com.acktos.rutaplus.KEY_CAR_ID";
	public static final String KEY_ELECTED_DATETIME="com.acktos.rutaplus.ELECTED_DATETIME";
	public static final String KEY_COORDINATES="com.acktos.rutaplus.COORDINATES";
	
	public static final String STATUS_KEY_PENDING="Pendiente";
	public static final String STATUS_KEY_APPROVED="Aprobado";
	public static final String STATUS_KEY_ONTRACK="En camino";
	public static final String STATUS_KEY_DRIVER_ARRIVED="Llego el conductor";
	public static final String STATUS_KEY_COMPLETED="Completado";
	public static final String STATUS_KEY_CANCELED="Cancelado";
	public static final String STATUS_KEY_FAILED="Fallido";
	
	public static final String SERVICE_TYPE_VIP="Conductor vip";
	public static final String SERVICE_TYPE_ELECTED="Conductor elegido";
	
	public static final String STATUS_ID_CANCELED="5";
	public static final int DEFER_MINUTES=-45;
	
	@Override
	public String toString() {
		return placeId;
	};
	public String toJson(){
		return "{\"latlng\":\""+latlng+"\"," +
                "\"placeId\":\""+placeId+"\"," +
                "\"dateTime\":\""+dateTime+"\","+
				"\"carId\":\""+carId+"\"," +
                "\"id\":\""+id+"\"," +
                "\"pickupAddress\":\""+pickupAddress+"\"," +
                "\"status\":\""+status+"\"," +
                "\"driver\":\""+driver+"\"," +
                "\"dateTimeDefer\":\""+dateTimeDefer+"\"," +
                "\"destinationAddress\":\""+destinationAddress+"\"," +
                "\"serviceType\":\""+serviceType+"\"," +
                "\"destinationId\":\""+destinationId+"\"," +
                "\"driverPhoto\":\""+driverPhoto+"\"," +
                "\"driverPhone\":\""+driverPhone+"\"," +
                "\"carPlate\":\""+carPlate+"\"}";
	}

	public void ToObject(String jsonString) throws JSONException{

		JSONObject jsonObject=new JSONObject(jsonString);
		//Log.i("debug json object",jsonObject.toString(1));
		jsonToObject(jsonObject);	

	}
	public void jsonToObject(JSONObject jsonObject) throws JSONException{

		id=jsonObject.getString("id");
		placeId=jsonObject.getString("lugar");
		serviceType=jsonObject.getString("servicio");
		dateTime=jsonObject.getString("fecha_recogida");
		dateTimeDefer=jsonObject.getString("fecha_aplazado");
		driver=jsonObject.getString("conductor");
		pickupAddress=jsonObject.getString("direccion");
		status=jsonObject.getString("estado");
		driverPhoto=jsonObject.getString("imagen_conductor");
		carPlate=jsonObject.getString("placa");
        driverPhone=jsonObject.getString("celular");
	
	}
	
	
	public void serviceFromJson(JSONObject jsonObject) throws JSONException{
		id=jsonObject.getString("id");
		pickupAddress=jsonObject.getString("pickupAddress");
		dateTime=jsonObject.getString("dateTime");
		status=jsonObject.getString("status");
		placeId=jsonObject.getString("placeId");
		dateTimeDefer=jsonObject.getString("dateTimeDefer");
		driver=jsonObject.getString("driver");
		destinationAddress=jsonObject.getString("destinationAddress");
		serviceType=jsonObject.getString("serviceType");
		driverPhoto=jsonObject.getString("driverPhoto");
		carPlate=jsonObject.getString("carPlate");
        driverPhone=jsonObject.getString("driverPhone");
	}
}
