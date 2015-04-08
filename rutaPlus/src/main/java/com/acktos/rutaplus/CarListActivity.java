package com.acktos.rutaplus;

import java.util.ArrayList;

import com.acktos.rutaplus.adapters.CarAdapter;
import com.acktos.rutaplus.controllers.CarController;
import com.acktos.rutaplus.entities.Car;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class CarListActivity extends Activity implements NewCarDialogFragment.OnDataChangeListener{

	private ActionBar actionBar;

	private static final String FRAGMENT_CAR_LIST_TAG="carListFragment";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_car_list);
		actionBar=getActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true); 

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment(),FRAGMENT_CAR_LIST_TAG).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		//getMenuInflater().inflate(R.menu.car_list, menu);
		//return true;
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if(id==android.R.id.home){
			Intent intent = new Intent(this, SelectServiceTypeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDataChangeCarList(ArrayList<Car> cars){
		PlaceholderFragment fragment=(PlaceholderFragment) getFragmentManager().findFragmentByTag(FRAGMENT_CAR_LIST_TAG);
		if(fragment!=null && fragment.isVisible()){
			fragment.updateAdapterCarList(cars);
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private CarController carController;
		private ListView carListView;
		private Activity activity;
		private ActionMode mActionMode=null;
		private CarAdapter carAdapter;
		private Car car;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

			activity=getActivity();
			carController=new CarController(activity);
			View rootView = inflater.inflate(R.layout.fragment_car_list,container, false);
			carListView=(ListView)rootView.findViewById(R.id.car_list);
			carListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
			carListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,int pos, long id) {
					Log.i("debug click","entre");
					if(mActionMode==null){
						carListView.setItemChecked(pos, false);
					}

				}
			});
			carListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View view,int pos, long id) {

					Log.i("debug long click","entre");
					if(mActionMode!=null){
						return false;
					}
					mActionMode=getActivity().startActionMode(mActionModeCallback);
					carListView.setItemChecked(pos, true);
					return true;
				}
			});

			return rootView;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			FindCarListTask findCarListTask=new FindCarListTask();
			findCarListTask.execute();
		}

		private ActionMode.Callback mActionModeCallback=new ActionMode.Callback(){

			private int selectedPosition;
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

				selectedPosition=carListView.getCheckedItemPosition();
				Object itemObject=carListView.getItemAtPosition(selectedPosition);
				car=(Car)itemObject;

				switch (item.getItemId()){

				case R.id.edit_item:

					mode.finish();
					NewCarDialogFragment newCarDialog=NewCarDialogFragment.newInstance(car.alias, car.plate, car.brand,car.id);
					newCarDialog.show(getFragmentManager(), "editCarDialog");
					break;

				case R.id.delete_item:
					mode.finish();
					deleteCarItem(car);
					break;
				}
				return false;
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater=mode.getMenuInflater();
				inflater.inflate(R.menu.simple_context_menu,menu);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode arg0) {
				mActionMode=null;
				selectedPosition=carListView.getCheckedItemPosition();
				carListView.setItemChecked(selectedPosition, false);
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}
		};

		public  void updateAdapterCarList(ArrayList<Car> cars){
			carAdapter=new CarAdapter(activity, cars);
			carListView.setAdapter(carAdapter);
		}

		/**
		 * handle click delete event from CAB
		 */
		public void deleteCarItem(Car car){
			DeleteCarTask deleteCarTask=new DeleteCarTask();
			deleteCarTask.execute(car);
		}

		private class DeleteCarTask extends AsyncTask<Car,Void,Boolean>{

			@Override
			protected Boolean doInBackground(Car... params) {

				boolean success=false;
				success=carController.deleteCar(car);
				return success;
			}

			@Override
			protected void onPostExecute(Boolean success) {
				super.onPostExecute(success);

				if(success){
					Toast.makeText(getActivity(),getActivity().getString(R.string.success_delete_car),Toast.LENGTH_LONG).show();
					FindCarListTask updateCarListTask=new FindCarListTask();
					updateCarListTask.execute();

				}else{
					Toast.makeText(getActivity(), getActivity().getString(R.string.failed_delete_car),Toast.LENGTH_LONG).show();
					getActivity().setProgressBarIndeterminateVisibility(false);
				}
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				getActivity().setProgressBarIndeterminateVisibility(true);
			}

		}

		private class FindCarListTask extends AsyncTask<Void,Void,ArrayList<Car>>{


			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				getActivity().setProgressBarIndeterminateVisibility(true);
			}

			@Override
			protected ArrayList<Car> doInBackground(Void... arg0) {

				return carController.getAllCars();
			}

			@Override
			protected void onPostExecute(ArrayList<Car> cars) {
				super.onPostExecute(cars);
				updateAdapterCarList(cars);
				getActivity().setProgressBarIndeterminateVisibility(false);

			}

		}
	}

}
