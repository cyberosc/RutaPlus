package com.acktos.rutaplus.android;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class CheckGooglePlayServices {
	
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private final static String DEBUG_TAG="debug check play services";
	
	public static boolean checkPlayServices(Activity activity) {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, activity,PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(DEBUG_TAG, "This device is not supported.");
	            activity.finish();
	        }
	        return false;
	    }
	    return true;
	}
}
