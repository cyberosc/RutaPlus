package com.acktos.rutaplus.android;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

public class GCMFunctions {

	public static final String KEY_REGISTRATION_ID="mobile_id";
	public static final String KEY_APP_VERSION="app_version";
	private static final String DEBUG_TAG="debug GCM functions";

	public static String getRegistrationId(Context context, String filename) {

		String registrationId=null;
		InternalStorage storage=new InternalStorage(context);
		String dataFile=storage.readFile(filename);

		if(dataFile!=null){
			try {

				JSONObject jsonObject=new JSONObject(dataFile);
				registrationId=jsonObject.getString(KEY_REGISTRATION_ID);
				int registeredVersion = jsonObject.getInt(KEY_APP_VERSION);
				int currentVersion = getAppVersion(context);
				if (registeredVersion != currentVersion) {
					Log.i(DEBUG_TAG, "App version changed.");
					return "";
				}

			} catch (JSONException e) {
				//e.printStackTrace();
				Log.i(DEBUG_TAG, "JSON Exception,not found registration_id key");
				return "";
			}

		}else{
			Log.i(DEBUG_TAG, "Registration id not found.");
			return "";
		}

		return registrationId;
	}

	public static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public static String registerId(GoogleCloudMessaging gcm,Context context,String senderId) throws IOException{
		
		String regid=null;
		if (gcm == null) {
			gcm = GoogleCloudMessaging.getInstance(context);
		}
		regid = gcm.register(senderId);
		Log.i(DEBUG_TAG,"Device registered, registration ID=" + regid);
		
		return regid;
	}
	
	public static void storeRegistrationId(Context context,String regid,String filename) throws JSONException{
		
		InternalStorage storage=new InternalStorage(context);
	    int appVersion = getAppVersion(context);
	    
	    String dataFile=storage.readFile(filename);
	    JSONObject jsonObject=new JSONObject(dataFile);
	    jsonObject.put(KEY_REGISTRATION_ID,regid);
	    jsonObject.put(KEY_APP_VERSION,appVersion);
	    
	    storage.saveFile(filename, jsonObject.toString());
	    Log.i(DEBUG_TAG, "Saving regId "+regid+" on app version " + appVersion);
	}
}

