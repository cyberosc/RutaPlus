package com.acktos.rutaplus.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkAndroid {
	
	
	
	public static boolean isNetworkAvalible(Context context){
		
		ConnectivityManager cm=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo=cm.getActiveNetworkInfo();
		if(networkInfo!=null && networkInfo.isConnected()){
			return true;
		}
		
		return false;
	}
	
	public static boolean isWifiConnection(Context context){
		ConnectivityManager cm=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo=cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
		boolean isWifi=networkInfo.isConnected();
		return isWifi;
	}
	
	public static boolean isMobileConnection(Context context){
		ConnectivityManager cm=(ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo=cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobile=networkInfo.isConnected();
		return isMobile;
	}
}
