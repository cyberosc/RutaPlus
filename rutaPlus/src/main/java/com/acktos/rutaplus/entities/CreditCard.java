package com.acktos.rutaplus.entities;

public class CreditCard {
	
	public String name=null;
	public String reference=null;
	public String termination=null;
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
