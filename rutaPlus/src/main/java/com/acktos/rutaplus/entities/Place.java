package com.acktos.rutaplus.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @deprecated deprecated DAO class for favorite places
 */
public class Place {

	public String id=null;
	public String address=null;
	public String place=null;
	public String favorite=null;

	@Override 
	public String toString(){
		return toJson();
	}

	public String toJson(){
		return "{\"id\":\""+id+"\",\"address\":\""+address+"\",\"place\":\""+place+"\",\"favorite\":\""+favorite+"\"}";
	}

	public void ToObject(String jsonString) throws JSONException{

		JSONArray jsonArray=new JSONArray(jsonString);
		JSONObject jsonObject=jsonArray.getJSONObject(0);
		jsonToObject(jsonObject);	

	}

	public void jsonToObject(JSONObject jsonObject) throws JSONException{
		
		id=jsonObject.getString("id");
		address=jsonObject.getString("direccion");
		place=jsonObject.getString("nombre");
		favorite=jsonObject.getString("favorito");
	}

}
