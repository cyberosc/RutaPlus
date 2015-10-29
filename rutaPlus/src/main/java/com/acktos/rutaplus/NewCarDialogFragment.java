package com.acktos.rutaplus;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;




import com.acktos.rutaplus.controllers.CarController;
import com.acktos.rutaplus.entities.Car;

@Deprecated
public class NewCarDialogFragment extends DialogFragment{

	private View rootView;
	private ProgressBar progressBar;
	private EditText txtAlias;
	private EditText txtPlate;
	private EditText txtBrand;
	private TextView messageView;

	private String aliasValue;
	private String plateValue;
	private String brandValue;
	private String carId;

	private Car car;
	private OnDataChangeListener dataChangelistener;

	public static NewCarDialogFragment newInstance(String alias,String plate,String brand,String carId) {
		NewCarDialogFragment ncdf = new NewCarDialogFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putString("alias", alias);
		args.putString("plate",plate);
		args.putString("brand",brand);
		args.putString("carId",carId);
		ncdf.setArguments(args);

		return ncdf;
	}
	
	public interface OnDataChangeListener{
		public void onDataChangeCarList(ArrayList<Car> cars);
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		if(activity instanceof OnDataChangeListener){
			dataChangelistener=(OnDataChangeListener) activity;
		}else{
			throw new ClassCastException(activity.toString()+" must implement OnDataChangeListener");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle SaveInstanceState){

		aliasValue=getArguments().getString("alias");
		plateValue=getArguments().getString("plate");
		brandValue=getArguments().getString("brand");
		carId=getArguments().getString("carId");
		Log.i("debug id car",carId);

		LayoutInflater inflater=getActivity().getLayoutInflater();
		rootView=inflater.inflate(R.layout.dialog_create_new_car, null);

		txtAlias=(EditText)rootView.findViewById(R.id.txt_alias);
		txtPlate=(EditText)rootView.findViewById(R.id.txt_plate);
		txtBrand=(EditText)rootView.findViewById(R.id.txt_brand);
		messageView=(TextView)rootView.findViewById(R.id.add_car_message);
		progressBar=(ProgressBar)rootView.findViewById(R.id.new_car_progress);

		txtAlias.setText(aliasValue);
		txtPlate.setText(plateValue);
		txtBrand.setText(brandValue);


		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.create_update_car));
		builder.setView(rootView);
		builder.setPositiveButton(getString(R.string.lbl_save), null);

		builder.setNegativeButton(getString(R.string.lbl_cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				NewCarDialogFragment.this.getDialog().cancel();
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

						boolean validate=true;
						View focusView=null;

						//reset errors
						txtPlate.setError(null);
						txtBrand.setError(null);

						aliasValue=txtAlias.getText().toString();
						plateValue=txtPlate.getText().toString();
						brandValue=txtBrand.getText().toString();

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
						if(validate){
							progressBar.setVisibility(View.VISIBLE);
							car=new Car();
							car.alias=aliasValue;
							car.plate=plateValue;
							car.brand=brandValue;
							car.id=carId;

							AddCarTask addCarTask=new AddCarTask(progressBar,messageView,getActivity());
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
			Fragment dialog=context.getFragmentManager().findFragmentByTag("editCarDialog");
			if (dialog!=null){
				NewCarDialogFragment dialogCar=(NewCarDialogFragment)dialog;
				dialogCar.dismiss();
			}
			if(success){
				Toast.makeText(context,context.getString(R.string.success_edit_car),Toast.LENGTH_LONG).show();
				UpdateCarListTask updateCarListTask=new UpdateCarListTask(context);
				updateCarListTask.execute();

			}else{
				Toast.makeText(context, context.getString(R.string.failed_edit_car),Toast.LENGTH_LONG).show();
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

	/**
	 *Debería ser una clase abstracta para spinner y list view
	 */
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
			Log.i("debug onpost execute","entre");
			super.onPostExecute(cars);
			dataChangelistener.onDataChangeCarList(cars);
		
		}



	}
} 
