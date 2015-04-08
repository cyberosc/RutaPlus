package com.acktos.rutaplus.android;

import android.content.Context;
import android.widget.Toast;

public class ShowToast {
	
	
	public static void show(String message,Context context){
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
}
