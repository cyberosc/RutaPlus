package com.acktos.rutaplus;

import java.util.ArrayList;

import com.acktos.rutaplus.adapters.PlaceAdapter;
import com.acktos.rutaplus.controllers.PlaceController;
import com.acktos.rutaplus.entities.Place;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;

public class PlaceListActivity extends Activity {
	
	ActionBar actionBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_service_list);
		
		actionBar=getActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true); 

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceListFragment()).commit();
		}
	}

	

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceListFragment extends Fragment {
		
		private PlaceController placeController;
		private PlaceAdapter placeAdapter;
		private Activity activity;
		
		private ListView placeListView;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			
			activity=getActivity();
			placeController=new PlaceController(getActivity());
			View rootView = inflater.inflate(R.layout.fragment_place_list,container, false);
			placeListView=(ListView)rootView.findViewById(R.id.place_list);
			return rootView;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			FindPlaceListTask findPlaceListTask=new FindPlaceListTask();
			findPlaceListTask.execute();
		}
		
		public  void updateAdapterPlaceList(ArrayList<Place> places){
			placeAdapter=new PlaceAdapter(activity,places);
			placeListView.setAdapter(placeAdapter);
		}

		private class FindPlaceListTask extends AsyncTask<Void,Void,ArrayList<Place>>{


			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				getActivity().setProgressBarIndeterminateVisibility(true);
			}

			@Override
			protected ArrayList<Place> doInBackground(Void... arg0) {

				return placeController.getAllPlaces();
			}

			@Override
			protected void onPostExecute(ArrayList<Place> services) {
				super.onPostExecute(services);
				updateAdapterPlaceList(services);
				getActivity().setProgressBarIndeterminateVisibility(false);

			}

		}
	}

}
