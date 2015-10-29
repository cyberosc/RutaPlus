package com.acktos.rutaplus.entities;


/**
 * A simple DAO class for encapsulating an entity  through the REST API.
 * Representing a user's credit card
 */
public class CreditCard {

	/**Name franchise credit card*/
	public String name=null;

	/**Unique id for a credit car to perform transactions*/
	public String reference=null;

	/** Last 4 digits of a credit card*/
	public String termination=null;

	/**Integer that represent a franchise type of a credit card ex: 2= VISA */
	public String type=null;
	
	
	public static final String CARD_TYPE_VISA="2";
	public static final String CARD_TYPE_MASTERCARD="1";
	public static final String CARD_TYPE_AMEX="4";
	public static final String CARD_TYPE_DINERS="3";
	
	public static final String CARD_NAME_VISA="Visa";
	public static final String CARD_NAME_MASTERCARD="MasterdCard";
	public static final String CARD_NAME_AMEX="American Express";
	public static final String CARD_NAME_DINERS="Diners";


	public CreditCard(String name,String reference,String termination,String type){
		
		this.name=name;
		this.reference=reference;
		this.termination=termination;
		this.type=type;
	}
	
}
