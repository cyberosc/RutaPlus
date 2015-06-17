package com.acktos.rutaplus.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;

import com.acktos.rutaplus.controllers.ServiceVIPController;
import com.acktos.rutaplus.entities.ServiceVIP;


public class CancelServiceIntentService extends IntentService {

    private String serviceId;

    public CancelServiceIntentService() {
        super("CancelServiceIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final Bundle extras = intent.getExtras();
            if (extras != null) {

                serviceId=extras.getString(ServiceVIPController.KEY_ID);
                ServiceVIPController serviceVIPController=new ServiceVIPController(this);
                serviceVIPController.cancelService(serviceId);
            }
        }

    }

}
