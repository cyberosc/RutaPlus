package com.acktos.rutaplus;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;



import android.widget.Toast;

import com.acktos.rutaplus.adapters.ServiceAdapter;
import com.acktos.rutaplus.controllers.ServiceController;
import com.acktos.rutaplus.entities.Service;


/**
 * Simple list fragment to show user's services through REST API.
 */
public class ServiceListFragment extends Fragment {

	private ServiceController serviceController;
	private ServiceAdapter serviceAdapter;
	private Activity activity;
	private OnServiceSelectedListener serviceListener;
	private static final String DEBUG_TAG="debug ServiceListFragment";

	private ListView serviceListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		activity=getActivity();
		serviceController=new ServiceController(getActivity());
		View rootView = inflater.inflate(R.layout.fragment_service_list,container, false);
		serviceListView=(ListView)rootView.findViewById(R.id.service_list);
		serviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int pos, long id) {

				Object o=parent.getItemAtPosition(pos);
				Service service=(Service)o;
				String statusService=service.status;

				if( !statusService.equals(Service.STATUS_KEY_CANCELED)
						&& !statusService.equals(Service.STATUS_KEY_FAILED)
						&& !statusService.equals(Service.STATUS_KEY_PENDING)){

					serviceListener.onServiceSelected(service);
				}
				else{
					Toast.makeText(getActivity(), R.string.msg_service_no_info, Toast.LENGTH_LONG).show();
				}

			}
		});
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		FindServiceListTask findServiceListTask=new FindServiceListTask();
		findServiceListTask.execute();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			serviceListener=(OnServiceSelectedListener)activity;
		}catch(ClassCastException e	){
			throw new ClassCastException(activity.toString()+"must implements OnServiceSelectedListener");
		}
	}

	public  void updateAdapterServiceList(ArrayList<Service> services){
		serviceAdapter=new ServiceAdapter(activity, services);
		serviceListView.setAdapter(serviceAdapter);
	}

	public interface OnServiceSelectedListener {
		public void onServiceSelected(Service service);
	}

	private class FindServiceListTask extends AsyncTask<Void,Void,ArrayList<Service>>{


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			getActivity().setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected ArrayList<Service> doInBackground(Void... arg0) {

			return serviceController.getAllServices();

		}

		@Override
		protected void onPostExecute(ArrayList<Service> services) {
			super.onPostExecute(services);
			updateAdapterServiceList(services);
			getActivity().setProgressBarIndeterminateVisibility(false);

		}

	}
}