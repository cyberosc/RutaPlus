package com.acktos.rutaplus.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple DAO class for encapsulating an entity through the REST API.
 * This class represents the items that compose a fee for a service.
 */
public class Rate {

	/** Name for a rate ex: VIP CAR*/
	public String name;

	/** Minimum price of a service*/
	public String baseRate;

	/** Price por minute*/
	public String minRate;

	/** Price per kilometer*/
	public String kmRate;

	/** Discount percentage*/
	public String discount;

	/** Price increases at peak hours, expressed as an integer that indicates the number of times increases */
	public String increase;
	
	
	public static final String KEY_NAME_RATE="nombre";
	public static final String KEY_BASE_RATE="tarifa";
	public static final String KEY_MIN_RATE="minuto";
	public static final String KEY_KM_RATE="km";
	public static final String KEY_DISCOUNT="descuento";
	public static final String KEY_INCREMENT="incremento";
	
	public static final int ORDER_FOR_BASE_RATE=1;
	public static final int ORDER_FOR_MIN_RATE=2;
	public static final int ORDER_FOR_KM_RATE=3;
	public static final int ORDER_FOR_DISCOUNT=4;
	public static final int ORDER_FOR_INCREMENT=5;
	
	public static final String TITLE_FOR_BASE_RATE="Tarifa base";
	public static final String TITLE_FOR_MIN_RATE="Tarifa por minuto";
	public static final String TITLE_FOR_KM_RATE="Tarifa por Km";
	public static final String TITLE_FOR_DISCOUNT="Descuento";
	public static final String TITLE_FOR_INCREMENT="Incremento actual";
	
	
	public Rate(JSONObject jsonObject){
		
		try {
			
			this.name=jsonObject.getString(KEY_NAME_RATE);
			this.baseRate=jsonObject.getString(KEY_BASE_RATE);
			this.minRate=jsonObject.getString(KEY_MIN_RATE);
			this.kmRate=jsonObject.getString(KEY_KM_RATE);
			this.discount=jsonObject.getString(KEY_DISCOUNT);
			this.increase=jsonObject.getString(KEY_INCREMENT);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
}
