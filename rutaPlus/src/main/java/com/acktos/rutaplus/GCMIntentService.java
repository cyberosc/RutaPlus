package com.acktos.rutaplus;

import com.acktos.rutaplus.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GCMIntentService extends IntentService {
	
	public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    
    private static final String DEBUG_TAG="debug GCMIntentService";
    public static final String KEY_DRIVER_NAME="Nombre";
    public static final String KEY_DRIVER_PLATE="Placa";
    public static final String KEY_DRIVER_PHOTO="Imagen";
    public static final String KEY_MESSAGE_TYPE="Type";
    public static final String TYPE_ASSIGN_DRIVER="conductor_asignado";

    public static final String BROADCAST_ASSIGN_DRIVER= "com.acktos.rutaplus";

    private String messageType;
    private String driverName;
    private String driverPlate;
    private String driverPhoto;

    public GCMIntentService() {
        super("291721187802");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    	Log.w(DEBUG_TAG,"entre a onhandle intent");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                messageType=extras.getString(KEY_MESSAGE_TYPE);
                Log.i(DEBUG_TAG, "message type django:" + messageType);

                if(messageType!=null){

                    if(messageType.equals(TYPE_ASSIGN_DRIVER)){
                        driverName=extras.getString(KEY_DRIVER_NAME);
                        driverPlate=extras.getString(KEY_DRIVER_PLATE);
                        driverPhoto=extras.getString(KEY_DRIVER_PHOTO);

                        sendBroadcastDriverAssign(driverName, driverPlate, driverPhoto);
                    }else{
                        // Post notification of received message.
                        sendNotification("" + extras.getString("Category"));
                        Log.i(DEBUG_TAG, "Received: " + extras.toString());
                    }

                }


            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
    	Log.w(DEBUG_TAG,"entre a enviar notificacion");
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, ServiceListActivity.class), 0);

        NotificationCompat.Builder mBuilder =new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("Ruta Plus")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendBroadcastDriverAssign(String driverName, String driverPlate,String driverPhoto){

        Log.i(DEBUG_TAG,"Entry to sendBroadcastDriverAssign");
        Intent intent = new Intent(BROADCAST_ASSIGN_DRIVER);
        intent.putExtra(KEY_DRIVER_NAME, driverName);
        intent.putExtra(KEY_DRIVER_PLATE, driverPlate);
        intent.putExtra(KEY_DRIVER_PHOTO, driverPhoto);
        sendBroadcast(intent);

    }
}
