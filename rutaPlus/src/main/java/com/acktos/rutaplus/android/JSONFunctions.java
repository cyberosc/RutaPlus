package com.acktos.rutaplus.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONFunctions {


	public static JSONArray getJsonArrayUrl(String url){

		HttpRequest request=new HttpRequest(url); 
		String data=request.request();

		if(data!=null){
			try {
				JSONArray jsonArray=new JSONArray(data);
				return jsonArray;
			} catch (JSONException e) {
				Log.e("JsonFuncionError","error json");
				e.printStackTrace();
			}
		}
		return null;

	}


	public static String getValueString(JSONArray jsonArray,String key){

		String value=null;
		try {
			JSONObject jsonObject=new JSONObject();
			jsonObject=JSONFunctions.findJSONObject(key, jsonArray);
			value=jsonObject.getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;

	}

	public static JSONArray setValueString(String key,String value, JSONArray jsonArray){

		int position=JSONFunctions.findPosition(key, jsonArray);
		if(position>=0){
			try {
				JSONObject jsonObject=jsonArray.getJSONObject(position);
				jsonObject.put(key, value);
			} catch (JSONException e) {
				Log.e("error setValueString","error json");
				e.printStackTrace();
			}
			
		}
		return jsonArray;
	}

	/**
	 * @param key
	 * @return
	 */
	public static JSONObject findJSONObject(String key,JSONArray haystack){

		JSONObject needle=null;
		int position=JSONFunctions.findPosition(key, haystack);
		if(position>=0){
			try {
				needle=haystack.getJSONObject(position);
			} catch (JSONException e) {
				Log.e("JSONFunctions.findJSONObject", "error json");
				e.printStackTrace();
			}
		}
		return needle;
	}
	
	public static int findPosition(String key,JSONArray haystack){
		
		int position=-1;
		JSONObject jsonObject=new JSONObject();
		if(haystack!=null){
			for(int i=0;i<=haystack.length();i++){

				try {
					jsonObject=haystack.getJSONObject(i);
					if(!jsonObject.isNull(key)){
						return i;
					}

				} catch (JSONException e) {
					Log.e("JSONFunctions.findJSONObject", "error json");
					e.printStackTrace();
				}
			}
		}
		return position;
	}

}
