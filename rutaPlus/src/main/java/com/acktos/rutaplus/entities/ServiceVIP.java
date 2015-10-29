package com.acktos.rutaplus.entities;


/**
 * A simple DAO class for encapsulating an entity and send service VIP data through the REST API.
 * Representing a user's request for a VIP service.
 */
public class ServiceVIP {

    /**Address of the user's location*/
	public String address;

    /**Latitude and longitude of the user's location*/
	public String coordinates;

    /**Date on which the driver must pickup the user*/
	public String dateTime;

    /**Card id from {@link CreditCard}*/
	public String cardReference;

    /**Card type from {@link CreditCard}*/
	public String cardType;

    /**Type of vehicle requested*/
	public String serviceType;
	
	public static final String KEY_NAME="nombre";
	public static final String KEY_ADDRESS="address";
	public static final String KEY_COORDINATES="geolocation";
	public static final String KEY_USER_ID="cliente";
	public static final String KEY_DESTINATION="destino";
	public static final String KEY_DATE_TIME="fecha_recogida";
	public static final String KEY_PLACE_ID="id_lugar";
	public static final String KEY_SERVICE_TYPE="tipo";
	public static final String KEY_PAYMENT_USERNAME="nombreUsuario";
	public static final String KEY_PAYMENT_PSWRD="claveUsuario";
	public static final String KEY_CARD_REFERENCE="medioPagoId";
	public static final String KEY_CARD_TYPE="tipoMedioPagoId";
	public static final String KEY_TTL="ttl";
	
	
	
}
