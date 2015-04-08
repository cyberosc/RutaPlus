package com.acktos.rutaplus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;

import com.acktos.rutaplus.ServiceActivity.MyPlacesDialogFragment;
import com.acktos.rutaplus.ServiceActivity.MyPlacesTask;
import com.acktos.rutaplus.adapters.DestinationAdapter;
import com.acktos.rutaplus.adapters.PlaceAdapter;
import com.acktos.rutaplus.android.ProgressStatus;
import com.acktos.rutaplus.controllers.CarController;
import com.acktos.rutaplus.controllers.DestinationController;
import com.acktos.rutaplus.controllers.PlaceController;
import com.acktos.rutaplus.controllers.ServiceController;
import com.acktos.rutaplus.entities.Car;
import com.acktos.rutaplus.entities.Destination;
import com.acktos.rutaplus.entities.Place;
import com.acktos.rutaplus.entities.Service;
import com.acktos.rutaplus.interfaces.MyDestinationListener;
import com.acktos.rutaplus.interfaces.MyPlacesListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;

public class CreateNewServiceFragment extends Fragment implements OnClickListener{

	private View rootView;
	private String latlng;
	private EditText txtAddress;
	//private EditText txtPlace;
	private EditText txtAddressDestination;
	private static EditText txtDate;
	private static EditText txtTime;
	private String address;
	//private Button btnAddNewService;
	private ImageButton btnNewCar;
	private static Spinner spnrCars;
	private View serviceForm;
	private View progressView;
	private ImageButton btnListDestination;

	private String addressValue;
	private String addressDestinationValue;
	private String placeValue;
	private String dateValue;
	private String timeValue;
	private String datetimeValue;

	//Attributes
	private String placeId;
	private String addressLatLng;
	private static DestinationAdapter desAdapter;
	private static ArrayList<Destination> des;
	private String destinationId;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		updateCarList(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_create_new_service, container, false);
		//btnAddNewService=(Button)rootView.findViewById(R.id.btn_add_new_service);
		txtAddress=(EditText)rootView.findViewById(R.id.txt_address);
		txtAddressDestination=(EditText)rootView.findViewById(R.id.txt_address_destination);
		//txtPlace=(EditText)rootView.findViewById(R.id.txt_place);
		txtDate=(EditText)rootView.findViewById(R.id.txt_date);
		txtTime=(EditText)rootView.findViewById(R.id.txt_time);
		btnNewCar=(ImageButton)rootView.findViewById(R.id.btn_add_car);
		spnrCars=(Spinner) rootView.findViewById(R.id.spinner_cars);
		progressView=(View)rootView.findViewById(R.id.add_service_progress);
		serviceForm=(View)rootView.findViewById(R.id.service_form);
		btnListDestination=(ImageButton)rootView.findViewById(R.id.btn_list_destination);
		
		
		
		//destination adapter
		des=new ArrayList<Destination>();
		desAdapter=new DestinationAdapter(getActivity(), des);
		
		placeId=null;
		destinationId=null;

		txtDate.setOnClickListener(this);
		txtTime.setOnClickListener(this);
		btnNewCar.setOnClickListener(this);
		btnListDestination.setOnClickListener(this);

		Bundle extras=getArguments();
		addressLatLng=extras.getString(MapServiceFragment.ARG_ADDRESS);
		placeId=extras.getString(PlaceController.TAG_PLACE_ID);


		if(placeId!=null){
			address=addressLatLng;
			txtAddress.setEnabled(false);
		}else{

			try{

				address=addressLatLng.split(";")[1].split("-")[0]+"-";
				latlng=addressLatLng.split(";")[0];

			}catch(ArrayIndexOutOfBoundsException e){
				e.getMessage();
			}catch(NullPointerException e){
				e.getMessage();
			}
		}

		txtAddress.setText(address);
		return rootView;
	}

	@Override
	public void onClick(View v) {

		switch(v.getId()){
		case R.id.txt_date:
			//Toast.makeText(getActivity(), "entre a click date", Toast.LENGTH_LONG).show();
			DialogFragment datePickerDialog=new DatePickerFragment();
			datePickerDialog.show(getFragmentManager(),"datePickerDialog");
			break;

		case R.id.txt_time:
			//Toast.makeText(getActivity(), "entre a click time", Toast.LENGTH_LONG).show();
			DialogFragment timePickerDialog=new TimePickerFragment();
			timePickerDialog.show(getFragmentManager(),"timePickerDialog");
			break;

		case R.id.btn_add_car:

			DialogFragment newCarDialog=new NewCarDialogFragment();
			newCarDialog.show(getFragmentManager(),"newCarDialog");
			break;
		case R.id.btn_list_destination:

			DialogFragment myDestinationDialog=new MyDestinationDialogFragment();
			myDestinationDialog.show(getFragmentManager(),"myDestinationDialog");
			break;
			/*case R.id.btn_add_new_service:
			Log.i("debug click","click btn add ew service");
			attempCreateNewService();*/

		default: break;

		}

	}
	
	
	public void updateDestinationField(String destinationId,String address){
		
		if(destinationId=="0"){
			txtAddressDestination.setText(null);
			txtAddressDestination.setEnabled(true);
		}else{
			this.destinationId=destinationId;
			txtAddressDestination.setText(address);
			txtAddressDestination.setEnabled(false);
		}
		
	}
	
	private void updateCarList(Context context){
		UpdateCarListTask updateCarListTask=new UpdateCarListTask(context);
		updateCarListTask.execute();

	}

	public void attempCreateNewService(){

		//check if a attemp is in course


		//reset error
		txtAddress.setError(null);
		//txtPlace.setError(null);
		txtDate.setError(null);
		txtTime.setError(null);
		txtAddressDestination.setError(null);

		//get values
		addressValue=txtAddress.getText().toString();
		addressDestinationValue=txtAddressDestination.getText().toString();
		//placeValue=txtPlace.getText().toString();
		datetimeValue=txtDate.getText().toString()+" "+txtTime.getText().toString();


		Object item=spnrCars.getSelectedItem();
		Car carItem=(Car)item;

		String carValue="";

		if(carItem!=null){
			carValue=carItem.id;
		}

		//check valid fields
		boolean validate=true;
		View focusView=null;


		/*if(TextUtils.isEmpty(placeValue)){
			txtPlace.setError(getString(R.string.error_field_required));
			focusView = txtPlace;
			validate = false;
		}*/
		if(TextUtils.isEmpty(addressDestinationValue)){
			txtAddressDestination.setError(getString(R.string.error_field_required));
			focusView = txtAddressDestination;
			validate = false;
		}
		if(TextUtils.isEmpty(addressValue)){
			txtAddress.setError(getString(R.string.error_field_required));
			focusView = txtAddress;
			validate = false;
		}
		/*if(TextUtils.isEmpty(dateValue)){
			txtDate.setError(getString(R.string.error_field_required));
			focusView = txtDate;
			validate = false;
		}
		if(TextUtils.isEmpty(timeValue)){
			txtTime.setError(getString(R.string.error_field_required));
			focusView = txtTime;
			validate = false;
		}*/

		if(validate){

			if(carValue.isEmpty()){
				DialogFragment validateCarDialog=new ValidateCarDialogFragment();
				validateCarDialog.show(getFragmentManager(),"validateCarDialog");
				return;
			}else if(!validateDatetimeService(datetimeValue,1)){
				DialogFragment validateDatetimeDialog=new ValidateDatetimeDialogFragment();
				validateDatetimeDialog.show(getFragmentManager(),"validateDatetimeDialog");
				return;
			}else{
				//Service service=new Service();
				//service.latlng=latlng;
				//service.address=addressValue;
				//service.place=placeValue;
				//service.carId=carValue;
				//service.dateTime="";
				//service.destinationAddress=addressDestinationValue;
				String dateTime=null;

				SimpleDateFormat sdfApp=new SimpleDateFormat("yyyy-MM-dd hh:mm a",Locale.getDefault());
				SimpleDateFormat sdfServer=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());

				try {
					Date timestamp=sdfApp.parse(datetimeValue);
					dateTime=sdfServer.format(timestamp);
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				//service.dateTime=dateTime;
				
				//without payment
				//Intent i=new Intent(getActivity(),ListCreditCardsActivity.class);
				Intent i=new Intent(getActivity(),ServiceConfirmActivity.class);
				
				i.putExtra(SelectServiceTypeActivity.TYPE_SERVICE,SelectServiceTypeActivity.TYPE_ELECTED);
				i.putExtra(Service.KEY_PLACE_ID,placeId);
				i.putExtra(Service.KEY_DESTINATION_ID,destinationId);
				i.putExtra(Service.KEY_PICKUP_ADDRESS,addressValue);
				i.putExtra(Service.KEY_DESTINATION_ADDRESS,addressDestinationValue);
				i.putExtra(Service.KEY_CAR_ID,carValue);
				i.putExtra(Service.KEY_ELECTED_DATETIME,dateTime);
				i.putExtra(Service.KEY_COORDINATES,latlng);
				i.putExtra(SelectServiceTypeActivity.KEY_REFERER, ServiceActivity.class.getName());
				
				getActivity().startActivity(i);
				//AddNewServiceTask addNewServiceTask=new AddNewServiceTask(getActivity());
				//addNewServiceTask.execute(service);
			}

		}else{
			focusView.requestFocus();
		}

	}

	public static boolean validateDatetimeService(String datetime,int plusHours){
		boolean success=false;

		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm a",Locale.getDefault());
			Date date=sdf.parse(datetime);

			DateTime serviceTime=new DateTime(date);
			DateTime currentTime=new DateTime();
			DateTime newTime=currentTime.plusHours(plusHours);

			Log.i("debug service time",serviceTime.toString());
			Log.i("debug current time",currentTime.toString());
			Log.i("debug new time",newTime.toString());

			if(serviceTime.isAfter(newTime)){

				Log.i("debug time","si es mayor");
				return true;
			}else{
				Log.i("debug time","no es mayor");
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return success;
	}

	private void populateSpinner(ArrayList<?> arrayList, Spinner spinner,Context context){

		ArrayAdapter<?> adapter=new ArrayAdapter(context,android.R.layout.simple_spinner_item,arrayList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.notifyDataSetChanged();
		spinner.setAdapter(adapter);

	}

	public static class DatePickerFragment extends DialogFragment implements OnDateSetListener{

		final Calendar c=Calendar.getInstance();

		@Override
		public Dialog onCreateDialog(Bundle saveInstanceState){

			int year=c.get(Calendar.YEAR);
			int month=c.get(Calendar.MONTH);
			int day=c.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(),this,year,month,day);
		}


		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {

			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
			c.set(Calendar.YEAR,year);
			c.set(Calendar.MONTH,monthOfYear);
			c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
			txtDate.setText(sdf.format(c.getTime()));
		}

	}

	public static class TimePickerFragment extends DialogFragment implements OnTimeSetListener{

		final Calendar c=Calendar.getInstance();

		@Override
		public Dialog onCreateDialog(Bundle saveInstanceState){

			int hour=c.get(Calendar.HOUR_OF_DAY);
			int minute=c.get(Calendar.MINUTE);

			return new TimePickerDialog(getActivity(), this, hour, minute,false);
		}


		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

			SimpleDateFormat sdf=new SimpleDateFormat("hh:mm a",Locale.getDefault());
			c.set(Calendar.HOUR_OF_DAY,hourOfDay);
			c.set(Calendar.MINUTE,minute);

			txtTime.setText(sdf.format(c.getTime()));
		}

	}

	public static class NewCarDialogFragment extends DialogFragment{

		private View rootView;
		private ProgressBar progressBar;
		private EditText txtAlias;
		private EditText txtPlate;
		private EditText txtBrand;
		private EditText txtLine;
		private EditText txtColor;
		private TextView messageView;
		//private Spinner spnrCars;

		private String aliasValue;
		private String plateValue;
		private String brandValue;
		private String lineValue;
		private String colorValue;

		private Car car;


		@Override
		public Dialog onCreateDialog(Bundle SaveInstanceState){


			LayoutInflater inflater=getActivity().getLayoutInflater();
			rootView=inflater.inflate(R.layout.dialog_create_new_car, null);

			txtAlias=(EditText)rootView.findViewById(R.id.txt_alias);
			txtPlate=(EditText)rootView.findViewById(R.id.txt_plate);
			txtBrand=(EditText)rootView.findViewById(R.id.txt_brand);
			txtLine=(EditText)rootView.findViewById(R.id.txt_line);
			txtColor=(EditText)rootView.findViewById(R.id.txt_color);
			messageView=(TextView)rootView.findViewById(R.id.add_car_message);
			progressBar=(ProgressBar)rootView.findViewById(R.id.new_car_progress);
			//spnrCars=(Spinner)rootView.findViewById(R.id.spinner_cars);


			inflater.inflate(R.layout.fragment_create_new_service, null);

			AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
			builder.setTitle(getString(R.string.create_new_car));
			builder.setView(rootView);
			builder.setPositiveButton(getString(R.string.lbl_save), null); // prevent closing the dialog

			builder.setNegativeButton(getString(R.string.lbl_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					NewCarDialogFragment.this.getDialog().cancel();
				}
			});  

			final AlertDialog alertDialog=builder.create();


			alertDialog.setOnShowListener(new OnShowListener() {//override positive button click

				@Override
				public void onShow(DialogInterface dialog) {
					Button btnPositive=alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
					btnPositive.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							boolean validate=true;
							View focusView=null;

							//reset errors
							txtAlias.setError(null);
							txtPlate.setError(null);
							txtBrand.setError(null);
							txtLine.setError(null);
							txtColor.setError(null);

							aliasValue=txtAlias.getText().toString();
							plateValue=txtPlate.getText().toString();
							brandValue=txtBrand.getText().toString();
							lineValue=txtLine.getText().toString();
							colorValue=txtColor.getText().toString();

							if(TextUtils.isEmpty(plateValue)){
								txtPlate.setError(getString(R.string.error_field_required));
								focusView=txtPlate;
								validate=false;
							}
							if(TextUtils.isEmpty(brandValue)){
								txtBrand.setError(getString(R.string.error_field_required));
								focusView=txtBrand;
								validate=false;
							}
							if(TextUtils.isEmpty(lineValue)){
								txtLine.setError(getString(R.string.error_field_required));
								focusView=txtLine;
								validate=false;
							}
							if(validate){
								progressBar.setVisibility(View.VISIBLE);
								car=new Car();
								car.alias=aliasValue;
								car.plate=plateValue;
								car.brand=brandValue;
								car.line=lineValue;
								car.color=colorValue;

								AddCarTask addCarTask=new  CreateNewServiceFragment().new AddCarTask(progressBar,messageView,getActivity());
								addCarTask.execute(car);

							}else{
								focusView.requestFocus();
							}
						}
					});

				}
			});

			return alertDialog;
		}

	} 

	public static class ValidateDatetimeDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.failed_validate_datetime_service)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					ValidateDatetimeDialogFragment.this.getDialog().cancel();
				}
			});

			return builder.create();
		}
	}

	public static class ValidateCarDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.failed_validate_car_service)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					ValidateCarDialogFragment.this.getDialog().cancel();
				}
			});

			return builder.create();
		}
	}

	// My Destinations Dialog Fragment
	public static class MyDestinationDialogFragment extends DialogFragment{

		private View rootView;
		private ProgressBar progressBar;
		private TextView messageView;
		private ListView mDesList;
		private MyDestinationListener mDesListener;



		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);

			try{
				mDesListener=(MyDestinationListener) activity;
			}catch(ClassCastException e){
				throw new ClassCastException(activity.toString()+ " must implement MyDestinationListener");
			}
		}


		@Override
		public Dialog onCreateDialog(Bundle SaveInstanceState){


			LayoutInflater inflater=getActivity().getLayoutInflater();
			rootView=inflater.inflate(R.layout.dialog_my_places, null);

			mDesList=(ListView)rootView.findViewById(R.id.my_places_list);
			messageView=(TextView)rootView.findViewById(R.id.my_places_message);
			progressBar=(ProgressBar)rootView.findViewById(R.id.my_places_progress);

			mDesList.setAdapter(desAdapter);
			mDesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {


					Object item=parent.getItemAtPosition(position);
					Destination destination=(Destination)item;

					mDesListener.onDestinationItemSelected(destination.id, destination.address);
					getDialog().cancel();
				}
			});

			AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
			builder.setTitle(getString(R.string.my_destination));
			builder.setView(rootView); 

			builder.setNegativeButton(getString(R.string.lbl_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					MyDestinationDialogFragment.this.getDialog().cancel();
				}
			});  


			GetAllDestinationTask allDestinationTask=new GetAllDestinationTask(getActivity(),progressBar,messageView);
			allDestinationTask.execute();

			final AlertDialog alertDialog=builder.create();

			return alertDialog;
		}

	} 

	private class AddCarTask extends AsyncTask<Car,Void,Boolean>{

		private CarController carController;
		private ProgressBar progressBar;
		private Activity context;


		public AddCarTask(ProgressBar progressBar,TextView messageView,Activity context){
			this.progressBar=progressBar;
			this.context=context;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			progressBar.setVisibility(View.GONE);
			if(success){
				Toast.makeText(context,context.getString(R.string.success_add_car),Toast.LENGTH_LONG).show();
				UpdateCarListTask updateCarListTask=new UpdateCarListTask(context);
				updateCarListTask.execute();
				Fragment dialog=context.getFragmentManager().findFragmentByTag("newCarDialog");
				if (dialog!=null){
					NewCarDialogFragment dialogCar=(NewCarDialogFragment)dialog;
					dialogCar.dismiss();
				}
			}else{
				Toast.makeText(context, context.getString(R.string.failed_add_car),Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(success);
		}

		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
			super.onPreExecute();

		}

		@Override
		protected Boolean doInBackground(Car... params) {
			boolean succesAddCar=false;
			carController=new CarController(context);
			succesAddCar=carController.addCarService(params[0]);

			return succesAddCar;
		}

	}
	
	private class UpdateCarListTask extends AsyncTask<Void,Void,ArrayList<Car>>{

		private CarController carController;
		private Context context;
		private ArrayList<Car> cars;


		public UpdateCarListTask(Context context){
			this.context=context;
			carController=new CarController(this.context);
		}
		@Override
		protected ArrayList<Car> doInBackground(Void... params) {
			cars=new ArrayList<Car>();
			cars=carController.getAllCars();
			return cars;
		}
		@Override
		protected void onPostExecute(ArrayList<Car> cars) {
			super.onPostExecute(cars);
			populateSpinner(cars, spnrCars,context);

		}



	}

	private static class GetAllDestinationTask extends AsyncTask<Void,Void,ArrayList<Destination>>{

		private Context context;
		private DestinationController desController;
		private ArrayList<Destination> des;
		private ProgressBar mLoader;
		private TextView mMessage;
		

		public GetAllDestinationTask(Context context,ProgressBar loader,TextView message){
			mLoader=loader;
			mMessage=message;
			this.context=context;
			desController=new DestinationController(this.context);
		}
		@Override
		protected ArrayList<Destination> doInBackground(Void... params) {
			des=new ArrayList<Destination>();
			des=desController.getAllDestination();
			return des;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoader.setVisibility(View.VISIBLE);
			mMessage.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected void onPostExecute(ArrayList<Destination> des) {
			super.onPostExecute(des);
			mLoader.setVisibility(View.GONE);
			mMessage.setVisibility(View.GONE);

			if(des!=null){

				Log.i("debug arrayList",des.toString());
				updateAdapterDestinationList(des);
				
				if(des.size()<=0){
					mMessage.setText(context.getString(R.string.empty_my_places));
					mMessage.setVisibility(View.VISIBLE);
					mMessage.setPadding(0, 20, 0, 0);
				}

			}
			else{
				mMessage.setText(context.getString(R.string.failed_find_my_destination));
			}

		}
		
	}
	
	private static void updateAdapterDestinationList(ArrayList<Destination> desList){
		
		Destination destination=new Destination();
		destination.id="0";
		destination.address="Ninguno";
		
		desList.add(0,destination);
		
		des.clear();
		des.addAll(desList);
		desAdapter.notifyDataSetChanged();

	}

	private class AddNewServiceTask extends AsyncTask<Service,Void,Boolean>{


		private Context context;

		public AddNewServiceTask(Context context){
			this.context=context;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			ProgressStatus.show(getActivity(),progressView,serviceForm,true);
		}

		@Override
		protected Boolean doInBackground(Service... params) {

			ServiceController serviceController=new ServiceController(getActivity());
			boolean success=false;
			success=serviceController.addService(params[0]);
			return success;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);
			if(success){
				Intent i=new Intent(context,ServiceListActivity.class);
				context.startActivity(i);
				Toast.makeText(context, context.getString(R.string.success_add_service),Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(context, context.getString(R.string.failed_add_service),Toast.LENGTH_LONG).show();
			}
			ProgressStatus.show(getActivity(),progressView,serviceForm,false);
		}

	}

}