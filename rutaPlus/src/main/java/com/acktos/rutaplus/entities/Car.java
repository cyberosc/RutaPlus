package com.acktos.rutaplus.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Car {

	public String id=null;
	public String alias=null;
	public String plate=null;
	public String brand=null;
	public String line=null;
	public String color=null;

	@Override 
	public String toString(){
		return brand+" "+line+" ("+plate+")";
	}

	public String toJson(){
		return "{\"id\":\""+id+"\",\"alias\":\""+alias+"\",\"plate\":\""+
				plate+"\",\"brand\":\""+brand+"\",\"line\":\""+line+"\",\"color\":\""+color+"\"}";
	}

	public void ToObject(String jsonString) throws JSONException{

		JSONArray jsonArray=new JSONArray(jsonString);
		JSONObject jsonObject=jsonArray.getJSONObject(0);
		jsonToObject(jsonObject);	

	}

	public void jsonToObject(JSONObject jsonObject) throws JSONException{
		
		id=jsonObject.getString("id");
		alias=jsonObject.getString("alias");
		plate=jsonObject.getString("placa");
		brand=jsonObject.getString("marca");
		line=jsonObject.getString("linea");
		color=jsonObject.getString("color");
	}

}
