package com.acktos.rutaplus.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @deprecated deprecated DAO class for destination place
 */
public class Destination {
	
	public String id;
	public String address;
	
	public static final String KEY_ID="id";
	public static final String KEY_TYPE="tipo";
	
	@Override 
	public String toString(){
		return id+" "+address;
	}

	public String toJson(){
		
		JSONObject jsonObject=new JSONObject();
		
		try {
			jsonObject.put("id", id);
			jsonObject.put("address", address);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonObject.toString();
	}

	public void ToObject(String jsonString) throws JSONException{

		JSONArray jsonArray=new JSONArray(jsonString);
		JSONObject jsonObject=jsonArray.getJSONObject(0);
		jsonToObject(jsonObject);	

	}

	public void jsonToObject(JSONObject jsonObject) throws JSONException{
		
		id=jsonObject.getString("id");
		address=jsonObject.getString("direccion");
		
	}
}
