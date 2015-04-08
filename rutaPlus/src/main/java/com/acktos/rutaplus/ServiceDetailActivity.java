package com.acktos.rutaplus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import com.acktos.rutaplus.android.DateTimeUtils;
import com.acktos.rutaplus.android.ProgressStatus;
import com.acktos.rutaplus.android.ShowToast;
import com.acktos.rutaplus.controllers.ServiceController;
import com.acktos.rutaplus.controllers.ServiceVIPController;
import com.acktos.rutaplus.entities.Service;
import com.google.android.gms.internal.ac;
import com.squareup.picasso.Picasso;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class ServiceDetailActivity extends Activity{

	public static final String TAG_JSON_DETAIL="json_detail";
	private ActionBar actionBar;
	private Service service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_detail);

		Bundle extras=getIntent().getExtras();
		String jsonString=extras.getString(TAG_JSON_DETAIL);

		actionBar=getActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);


		// set service state into 
		try {
			JSONObject jsonObject=new JSONObject(jsonString);
			service=new Service();
			service.serviceFromJson(jsonObject);
			actionBar.setSubtitle(getString(R.string.lbl_status)+" : "+service.status);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (savedInstanceState == null) {

			ServiceDetailFragment f=ServiceDetailFragment.newInstance(jsonString);
			getFragmentManager().beginTransaction().add(R.id.container, f).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.service_list, menu);
		//return true;
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		if(id==android.R.id.home){
			Intent intent = new Intent(this, ServiceListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}

	public static class ServiceDetailFragment extends Fragment implements OnClickListener,OnRatingBarChangeListener{


		private Button btnDeferService;
		private Button btnCancelService;
        private Button btnCallDriver;
		private TextView lblDriverName;
		private TextView lblServiceType;
		private TextView lblCarPlate;
		private TextView lblDatailDate;
		private TextView lblAddress;
		private static String oldDate;
		private static String serviceId;
        private static String driverPhone;
		private RatingBar mRatingBar;
		private static ProgressDialog progress;
		private ImageView driverPhotoView;

		private boolean isDeferable=false;
		private boolean isCancelable=true;
		private boolean isRatingable=true;


		public static ServiceDetailFragment newInstance(String jsonString){

			ServiceDetailFragment f=new ServiceDetailFragment();

			Bundle args=new Bundle();
			args.putString(TAG_JSON_DETAIL, jsonString);
			f.setArguments(args);
			return f;
		}

		public Service getServiceObject(){
			try {
				String data=getArguments().getString(TAG_JSON_DETAIL);

				Service service=new Service();
				JSONObject jsonObject=new JSONObject(data);
				service.serviceFromJson(jsonObject);
				return service;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_service_detail,container, false);
			btnDeferService=(Button)rootView.findViewById(R.id.btn_defer_service);
			btnCancelService=(Button)rootView.findViewById(R.id.btn_cancel_service);
            btnCallDriver=(Button)rootView.findViewById(R.id.btn_call_driver);
			lblDriverName=(TextView)rootView.findViewById(R.id.lbl_driver_name);
			lblServiceType=(TextView)rootView.findViewById(R.id.txt_service_type);
			lblDatailDate=(TextView)rootView.findViewById(R.id.lbl_time_detail);
			lblAddress=(TextView)rootView.findViewById(R.id.lbl_address_detail);
			mRatingBar=(RatingBar) rootView.findViewById(R.id.rating_service_launch);
			driverPhotoView=(ImageView) rootView.findViewById(R.id.img_driver_photo);
			lblCarPlate=(TextView) rootView.findViewById(R.id.txt_car_plate);
			

			Service service=getServiceObject();

			Log.i(this.toString()+"onCreatedView","estado:"+service.status);
			Log.i(this.toString()+"onCreatedView","estado:"+service.serviceType);
			
			//set driver photo from server
			Picasso.with(getActivity())
			.load(service.driverPhoto)
			.placeholder(R.drawable.placeholder_driver)
			.into(driverPhotoView);

			if(service.serviceType.equals(Service.SERVICE_TYPE_VIP)){
				isDeferable=false;

				if(service.status.equals(Service.STATUS_KEY_COMPLETED) 
						|| service.status.equals(Service.STATUS_KEY_CANCELED)){

					isCancelable=false;
				}


			}else if(service.serviceType.equals(Service.SERVICE_TYPE_ELECTED)){

				if(service.status.equals(Service.STATUS_KEY_COMPLETED) 
						|| service.status.equals(Service.STATUS_KEY_DRIVER_ARRIVED)){

					isDeferable=false;
					isCancelable=false;
				}
			}

			if(!service.status.equals(Service.STATUS_KEY_COMPLETED)){
				isRatingable=false;
			}

			if(!isDeferable){
				btnDeferService.setVisibility(View.GONE);
			}
			if(!isCancelable){
				btnCancelService.setVisibility(View.GONE);
			}
			if(!isRatingable){
				mRatingBar.setEnabled(false);
			}


			btnDeferService.setOnClickListener(this);
			btnCancelService.setOnClickListener(this);
            btnCallDriver.setOnClickListener(this);
			mRatingBar.setOnRatingBarChangeListener(this);
			lblDriverName.setText(service.driver);
			lblServiceType.setText(service.serviceType);
			lblCarPlate.setText(getString(R.string.plate)+" : "+service.carPlate);
			lblAddress.setText(service.pickupAddress);
			oldDate=service.dateTime;

			lblDatailDate.setText(DateTimeUtils.toLatinDate(oldDate));
			serviceId=service.id;
            driverPhone=service.driverPhone;

			return rootView;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_defer_service:
				showDeferServiceDialog();
				break;

			case R.id.btn_cancel_service:

				showCancelDialog();
				break;

            case R.id.btn_call_driver:
                callDriver();
                break;
			}
		}

        private void callDriver(){

            if(driverPhone!=null){

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", driverPhone, null));
                startActivity(intent);
            }else{
                Toast.makeText(getActivity(),getString(R.string.msg_driver_phone_not_found),Toast.LENGTH_SHORT).show();
            }

        }

		@Override
		public void onRatingChanged(RatingBar view, float rating, boolean fromUser) {

			if(fromUser){
				//Log.i(this.toString()+"onRatingChanged","entry on rating change");
				showRatingDialog(rating);
			}

		}

		private void showDeferServiceDialog(){
			DeferServiceDialog deferDialog=new DeferServiceDialog();
			deferDialog.show(getFragmentManager(),"deferServiceDialog");
		}

		private void showRatingDialog(Float rating){
			RatingDialog ratingDialog=RatingDialog.newInstance(rating);
			ratingDialog.show(getFragmentManager(),"ratingDialog");
		}

		private void showCancelDialog(){
			CancelDialog cancelDialog=new CancelDialog();
			cancelDialog.show(getFragmentManager(),"cancelDialog");
		}
		
		private static void showResponseCancelDialog(String message,Activity activity){
			ResponseCancelDialog responseCancelDialog=ResponseCancelDialog.newInstance(message);
			responseCancelDialog.show(activity.getFragmentManager(),"cancelDialog");
		}

		public static class CancelDialog extends DialogFragment{

			public Dialog onCreateDialog(Bundle savedInstanceState) {
				// Use the Builder class for convenient dialog construction
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(R.string.msg_cancel_service)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						CancelServiceTask cancelTask=new CancelServiceTask(getActivity());
						cancelTask.execute(serviceId);
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						CancelDialog.this.getDialog().cancel();
					}
				});
				// Create the AlertDialog object and return it
				return builder.create();
			}

		}

		public static class ResponseCancelDialog extends DialogFragment{

			public static final String CANCEL_MESSAGE="CANCEL_MENSAJE"; 
			private String message;
			
			public static ResponseCancelDialog newInstance(String message){

				ResponseCancelDialog responseCancelDialog=new ResponseCancelDialog();

				Bundle args=new Bundle();
				args.putString(CANCEL_MESSAGE, message);
				responseCancelDialog.setArguments(args);
				return responseCancelDialog;
			}

			public Dialog onCreateDialog(Bundle savedInstanceState) {
				
				message=getArguments().getString(CANCEL_MESSAGE);
				// Use the Builder class for convenient dialog construction
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(message)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//ResponseCancelDialog.this.getDialog().cancel();
						Intent i=new Intent(getActivity(),ServiceListActivity.class);
						getActivity().startActivity(i);
						getActivity().finish();
					}
				});
				// Create the AlertDialog object and return it
				return builder.create();
			}

		}

		public static class DeferServiceDialog extends DialogFragment{


			private String[] deferArray;
			private View rootView;
			private Spinner spnDefer;
			private int deferValue;
			private ProgressBar deferProgress;
			private TextView msgValidateDeferService;

			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {

				LayoutInflater inflater=getActivity().getLayoutInflater();
				rootView=inflater.inflate(R.layout.dialog_defer_service, null);

				deferArray=getResources().getStringArray(R.array.defer_array);
				spnDefer=(Spinner)rootView.findViewById(R.id.spn_defer_service);
				deferProgress=(ProgressBar)rootView.findViewById(R.id.defer_service_progress);
				msgValidateDeferService=(TextView)rootView.findViewById(R.id.message_validate_defer_service);

				spnDefer.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,deferArray));

				AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
				builder.setTitle(getString(R.string.title_defer_service));
				builder.setView(rootView);
				builder.setPositiveButton(getString(R.string.lbl_defer),null);
				builder.setNegativeButton(getString(R.string.lbl_cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						DeferServiceDialog.this.getDialog().cancel();
					}
				}); 

				final AlertDialog alertDialog=builder.create();

				alertDialog.setOnShowListener(new OnShowListener() {

					@Override
					public void onShow(DialogInterface dialog) {
						Button btnPositive=alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
						btnPositive.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								int minutes=0;
								deferValue=spnDefer.getSelectedItemPosition();
								switch(deferValue){
								case 0:
									minutes=30;
									break;
								case 1:
									minutes=60;
									break;
								case 2:
									minutes=120;
									break;
								case 3:
									minutes=180;
									break;
								case 4:
									minutes=240;
									break;
								case 5:
									minutes=300;
									break;
								case 6:
									minutes=360;
									break;
								case 7:
									minutes=420;
									break;
								case 8:
									minutes=480;
									break;
								case 9:
									minutes=540;
									break;
								case 10:
									minutes=600;
									break;
								case 11:
									minutes=660;
									break;
								}

								if(validateDeferDatetimeService(oldDate)){
									DeferServiceTask deferTask=new DeferServiceTask(getActivity(),deferProgress);
									deferTask.execute(serviceId,oldDate, Integer.toString(minutes));
								}else{
									msgValidateDeferService.setText(getString(R.string.timeout_defer_service));
									msgValidateDeferService.setVisibility(View.VISIBLE);
									msgValidateDeferService.postDelayed(new Runnable() {
										public void run() {
											msgValidateDeferService.setVisibility(View.GONE);
										}
									}, 4000);

								}



							}
						});

					}
				});
				return alertDialog;
			}

			private boolean validateDeferDatetimeService(String datetime){

				boolean isDeferrable=false;

				try {
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
					Date date=sdf.parse(datetime);

					DateTime serviceTime=new DateTime(date);
					DateTime currentTime=new DateTime();
					DateTime newTime=serviceTime.plusMinutes(Service.DEFER_MINUTES);

					//Log.i("debug service time",serviceTime.toString());
					//Log.i("debug current time",currentTime.toString());
					//Log.i("debug new time",newTime.toString());

					if(currentTime.isBefore(newTime)){
						isDeferrable=true;
						Log.i("debug new time","si es deferreable");
					}

				} catch (ParseException e) {
					e.printStackTrace();
				}

				return isDeferrable;
			}


		}

		public static class RatingDialog extends DialogFragment{


			private View rootView;
			private RatingBar serviceRatingView;
			private EditText serviceCommentView;
			private ProgressBar ratingProgress;

			private float ratingValue;
			public static final String RATING_VALUE="RATING_VALUE";


			public static RatingDialog newInstance(float rating){

				RatingDialog ratingDialog=new RatingDialog();

				Bundle args=new Bundle();
				args.putFloat(RATING_VALUE, rating);
				ratingDialog.setArguments(args);
				return ratingDialog;
			}

			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {

				LayoutInflater inflater=getActivity().getLayoutInflater();
				rootView=inflater.inflate(R.layout.dialog_service_rating, null);

				ratingValue=getArguments().getFloat(RATING_VALUE);

				serviceRatingView=(RatingBar)rootView.findViewById(R.id.rating_service);
				serviceCommentView=(EditText)rootView.findViewById(R.id.comment_service);
				ratingProgress=(ProgressBar)rootView.findViewById(R.id.rating_progress);

				//set initial rating
				serviceRatingView.setRating(ratingValue);

				AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
				builder.setTitle(getString(R.string.title_service_rating));
				builder.setView(rootView);
				builder.setPositiveButton(getString(R.string.lbl_rating),null);
				builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						RatingDialog.this.getDialog().cancel();
					}
				}); 

				final AlertDialog alertDialog=builder.create();

				alertDialog.setOnShowListener(new OnShowListener() {

					@Override
					public void onShow(DialogInterface dialog) {
						Button btnPositive=alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
						btnPositive.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								(new RatingServiceTask(getActivity(), ratingProgress, RatingDialog.this))
								.execute(serviceId,Integer.toString((int)ratingValue),
										serviceCommentView.getText().toString());

							}
						});

					}
				});


				return alertDialog;
			}

		}

		private static class DeferServiceTask extends AsyncTask<String,Void,Boolean>{


			private Activity activity;
			private ProgressBar progress;

			public DeferServiceTask(Activity activity,ProgressBar progress){
				this.activity=activity;
				this.progress=progress;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progress.setVisibility(View.VISIBLE);
			}

			@Override
			protected Boolean doInBackground(String... params) {

				boolean success;
				ServiceController serviceController=new ServiceController(activity);
				success=serviceController.deferService(params[0],params[1],Integer.parseInt(params[2]));

				return success;
			}

			@Override
			protected void onPostExecute(Boolean success) {
				super.onPostExecute(success);
				if(success){
					Toast.makeText(activity,activity.getString(R.string.success_defer_service),Toast.LENGTH_LONG).show();
					Intent i=new Intent(activity,ServiceListActivity.class);
					activity.startActivity(i);
				}else{
					progress.setVisibility(View.GONE);
					Toast.makeText(activity,activity.getString(R.string.failed_defer_service),Toast.LENGTH_LONG).show();
				}

			}

		}

		private static class CancelServiceTask extends AsyncTask<String,Void,String>{

			private Activity activity;

			public CancelServiceTask(Activity activity){
				this.activity=activity;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showProgress(activity.getString(R.string.msg_waiting_cancel_service),activity);
			}

			@Override
			protected String doInBackground(String... params) {

				String message;
				ServiceVIPController serviceController=new ServiceVIPController(activity);
				message=serviceController.cancelService(params[0]);
				return message;
			}

			@Override
			protected void onPostExecute(String message){
				
				dismissProgress();
				
				if(!TextUtils.isEmpty(message)){
					showResponseCancelDialog(message,activity);
				}else{
					Toast.makeText(activity,activity.getString(R.string.failed_cancel_service), Toast.LENGTH_SHORT).show();
				}
				
				
			}
		}

		private static class RatingServiceTask extends AsyncTask<String,Void,Boolean>{

			private ProgressBar progress;
			private Activity activity;
			private DialogFragment dialog;

			public RatingServiceTask(Activity activity,ProgressBar progress,DialogFragment dialog){
				this.activity=activity;
				this.progress=progress;
				this.dialog=dialog;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progress.setVisibility(View.VISIBLE);
			}

			@Override
			protected Boolean doInBackground(String... params) {

				boolean success;
				ServiceController serviceController=new ServiceController(activity);
				success=serviceController.ratingService(params[0], params[1], params[2]);
				return success;
			}

			@Override
			protected void onPostExecute(Boolean success){
				if(success){
					Toast.makeText(activity,activity.getString(R.string.msg_service_rating_success),Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(activity,activity.getString(R.string.msg_service_rating_failed),Toast.LENGTH_LONG).show();
				}
				progress.setVisibility(View.GONE);
				dialog.dismiss();
			}
		}
		
		private static void showProgress(String message,Context context){
			if (progress == null || !progress.isShowing()){
				progress = new ProgressDialog(context);
				progress.setCancelable(false);
				progress.setCanceledOnTouchOutside(false);
				progress.setMessage(message);				
				progress.show();
			}
		}

		private static void dismissProgress(){

			if(progress!=null){
				progress.dismiss();
				progress = null;
			}

		}



	}

}


