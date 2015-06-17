package com.acktos.rutaplus;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acktos.rutaplus.controllers.ServiceVIPController;
import com.acktos.rutaplus.entities.ServiceVIP;
import com.acktos.rutaplus.services.CancelServiceIntentService;
import com.acktos.rutaplus.util.HoloCircularProgressBar;
import com.squareup.picasso.Picasso;


public class WaitingDriverProgressActivity extends Activity {

    private static final String TAG = "WaitingDriverProgress";

    private HoloCircularProgressBar mHoloCircularProgressBar;
    private ObjectAnimator mProgressBarAnimator;

    // UI references
    private RelativeLayout driverLayout;
    private TextView driverNameView;
    private TextView driverPlateView;
    private TextView countingView;
    private ImageView driverPhotoView;
    private TextView serviceTypeView;

    //Constants
    private static final Integer FINISH_DURATION = 3000;

    //attributes
    private long ttl=290000;
    private String serviceId;
    private String driverName;
    private String driverPlate;
    private String driverPhoto;
    private String serviceType;
    private boolean driverIsAssigned=false;
    private long initialProgressTime;

    //State vars
    public String STATE_DRIVER_IS_ASSIGNED="DRIVER_IS_ASSIGNED";
    public String STATE_INITIAL_PROGRESS_TIME="INITIAL_PROGRESS_TIME";
    public String STATE_DRIVER_NAME="DRIVER_NAME";
    public String STATE_DRIVER_PLATE="DRIVER_PLATE";
    public String STATE_DRIVER_PHOTO="DRIVER_PHOTO";



    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_driver_progress);

        if (getIntent() != null) {
            final Bundle extras = getIntent().getExtras();
            if (extras != null) {

                //get ttl time
                ttl=Long.parseLong(extras.getString(ServiceVIP.KEY_TTL))*1000;
                serviceId=extras.getString(ServiceVIPController.KEY_ID);

                Log.i(TAG,"ttl:"+ttl);
                Log.i(TAG,"service id:"+serviceId);

                final int theme = extras.getInt("theme");
                if (theme != 0) {
                    setTheme(theme);
                }
            }
        }

        //Initialize assign driver values by default
        driverName=getString(R.string.not_drivers_available);
        driverPlate=null;
        driverPhoto=null;
        serviceType=getString(R.string.try_again_later);


        //Initialize UI
        countingView = (TextView) findViewById(R.id.counting);
        driverNameView = (TextView) findViewById(R.id.box_driver_name);
        driverPlateView = (TextView) findViewById(R.id.box_driver_plate);
        driverPhotoView= (ImageView) findViewById(R.id.box_driver_photo);
        serviceTypeView= (TextView) findViewById(R.id.box_service_type);
        driverLayout = (RelativeLayout) findViewById(R.id.driver_layout);
        driverLayout.setVisibility(View.INVISIBLE);

        //set click listener to driver layout
        driverLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent i=new Intent(WaitingDriverProgressActivity.this, ServiceListActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });


        //setup circular progressBar
        mHoloCircularProgressBar = (HoloCircularProgressBar) findViewById(R.id.holoCircularProgressBar);
        mHoloCircularProgressBar.setProgressColor(Color.rgb(255, 255, 255));
        mHoloCircularProgressBar.setProgressBackgroundColor(R.color.gray_ligth);
        mHoloCircularProgressBar.setMarkerProgress(1f);
        setUpProgressBar(mHoloCircularProgressBar,1f, ttl);


    }

    @Override
    protected void onResume() {

        Log.i(TAG,"entry to onResume()");
        registerReceiver(receiverAssignDriver, new IntentFilter(GCMIntentService.BROADCAST_ASSIGN_DRIVER));
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "entry to onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"entry to onDestroy()");
        unregisterReceiver(receiverAssignDriver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.i(TAG,"entry to onPause()");
        super.onPause();
    }

    public void finishProgressBarCycle()
    {
        if (mProgressBarAnimator != null) {
            mProgressBarAnimator.cancel();
            setUpProgressBar(mHoloCircularProgressBar,1f,FINISH_DURATION);
        }
    }

    private void setUpProgressBar(final HoloCircularProgressBar progressBar, final float progress, final long duration) {

        mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
        mProgressBarAnimator.setDuration(duration);
        mProgressBarAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationCancel(final Animator animation) {}

            @Override
            public void onAnimationEnd(final Animator animation) {

                float progressBarstatus=progressBar.getProgress();

                if (progressBarstatus == 1) {

                    showDriverInfoBox();

                    if(!driverIsAssigned){
                        automaticCancelService(serviceId);
                    }
                }
            }

            @Override
            public void onAnimationRepeat(final Animator animation) {}

            @Override
            public void onAnimationStart(final Animator animation) {

                initialProgressTime=System.currentTimeMillis();

            }
        });

        mProgressBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                progressBar.setProgress((Float) animation.getAnimatedValue());
                Float contadorfloat = (Float) animation.getAnimatedValue();

                contadorfloat = contadorfloat * 100;
                String tiempo = String.format("%.0f", contadorfloat);
                tiempo = tiempo + "%";
                countingView.setText(tiempo);

            }
        });
        progressBar.setMarkerProgress(progress);
        mProgressBarAnimator.start();

    }

    private void showDriverInfoBox(){

        driverNameView.setText(driverName.toUpperCase());
        serviceTypeView.setText(serviceType);

        if(driverPlate!=null){
            driverPlateView.setText((getString(R.string.plate) + " : " + driverPlate).toUpperCase());
        }else{
            driverPlateView.setVisibility(View.GONE);
        }

        if(driverPhoto!=null){
            Picasso.with(this)
                    .load(driverPhoto)
                    .placeholder(R.drawable.placeholder_driver)
                    .into(driverPhotoView);
        }else{
            Picasso.with(this)
                    .load(R.drawable.placeholder_driver)
                    .into(driverPhotoView);
        }

        serviceTypeView.setText(serviceType);
        driverLayout.setVisibility(View.VISIBLE);



    }

    //register a broadcast for new data from webservice
    private BroadcastReceiver receiverAssignDriver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i(TAG,"entry to onReceive Assign driver");
            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                driverName = bundle.getString(GCMIntentService.KEY_DRIVER_NAME);
                driverPlate = bundle.getString(GCMIntentService.KEY_DRIVER_PLATE);
                driverPhoto= bundle.getString(GCMIntentService.KEY_DRIVER_PHOTO);
                serviceType=getString(R.string.driver_assign);
                //Log.i("result code download data",resultCode+"");
                driverIsAssigned=true;
                finishProgressBarCycle();

            }
        }
    };

    private void restoreStateProgressbar(){

        Long finishProgressTime=initialProgressTime+ttl;
        Long currentTime=System.currentTimeMillis();

        if(driverIsAssigned){
            mHoloCircularProgressBar.setProgress(1);
            mProgressBarAnimator.cancel();
            countingView.setText("100%");

        }else if(currentTime>=finishProgressTime){
            mHoloCircularProgressBar.setProgress(1);
            mProgressBarAnimator.cancel();
            countingView.setText("100%");
        }else{
            Long animatedMillis=currentTime-initialProgressTime;
            mProgressBarAnimator.setCurrentPlayTime(animatedMillis);
        }

    }

    private void automaticCancelService(String serviceId){

        Intent i=new Intent(this, CancelServiceIntentService.class);
        i.putExtra(ServiceVIPController.KEY_ID,serviceId);
        startService(i);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        driverIsAssigned=savedInstanceState.getBoolean(STATE_DRIVER_IS_ASSIGNED);
        initialProgressTime=savedInstanceState.getLong(STATE_INITIAL_PROGRESS_TIME);

        driverName=savedInstanceState.getString(STATE_DRIVER_NAME);
        driverPlate=savedInstanceState.getString(STATE_DRIVER_PLATE);
        driverPhoto=savedInstanceState.getString(STATE_DRIVER_PHOTO);
        restoreStateProgressbar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_DRIVER_IS_ASSIGNED, driverIsAssigned);
        outState.putLong(STATE_INITIAL_PROGRESS_TIME, initialProgressTime);
        outState.putString(STATE_DRIVER_NAME, driverName);
        outState.putString(STATE_DRIVER_PLATE,driverPlate);
        outState.putString(STATE_DRIVER_PHOTO,driverPhoto);
        Log.i(TAG,"Entry to on saveInstanceState");
    }
}
