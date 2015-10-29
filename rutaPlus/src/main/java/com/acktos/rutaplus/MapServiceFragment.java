package com.acktos.rutaplus;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationRequest;


/**
 * Sub class of Fragment that shows a map of google maps,
 * recognize the best available user location and query the approximate address of this point,
 * through google play services.
 */
public class MapServiceFragment extends Fragment
implements
ConnectionCallbacks,
OnConnectionFailedListener,
LocationListener,
OnMyLocationButtonClickListener, 
OnCameraChangeListener {

	private GoogleMap mMap;
	private LocationClient mLocationClient;
	private View rootView;
	private MapView mMapView;
	//private ProgressBar mProgress;
	private ActionBar actionBar;
	private OnChangeAddressListener listener;
	public static final String ARG_ADDRESS="address";
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000)         // 5 seconds
			.setFastestInterval(16)    // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		Log.i(this.toString()+"onCreateView","onCreateView()");

		rootView = inflater.inflate(R.layout.fragment_map, container, false);
		actionBar=getActivity().getActionBar();
		//mProgress= (ProgressBar)rootView.findViewById(R.id.address_progress);
		mMapView = (MapView) rootView.findViewById(R.id.map_service);
		mMapView.onCreate(savedInstanceState);
		setUpMapIfNeeded();
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(this.toString()+"onResume","onResume()");
		mMapView.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();

	}

	@Override
	public void onPause() {
		mMapView.onPause();
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	@Override
	public void onDestroy() {
		mMapView.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	@Override
	public boolean onMyLocationButtonClick() {
		Toast.makeText(getActivity(), "click en my location button", Toast.LENGTH_SHORT).show();
		// Return false so that we don't consume the event and the default behavior still occurs
		// (the camera animates to the user's current position).
		return true;
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLocationClient.requestLocationUpdates(REQUEST,this);
		markMyLocation();
	}

	/**
	 * Moves the map camera to the user last know location.
	 */
	public void markMyLocation(){

		if (mLocationClient != null && mLocationClient.isConnected()) {


			Location location=mLocationClient.getLastLocation();
			if(location!=null){
				LatLng latlng=new LatLng(location.getLatitude(), location.getLongitude());
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));  
			}else{
				markDefaultLocation();
				Toast.makeText(getActivity(),getString(R.string.location_service_error), Toast.LENGTH_LONG).show();
			}

		}
	}

    /**
     * Moves the camera to a default location if it class has not found any earlier.
     */
	private void markDefaultLocation(){

		String coordinateString=getString(R.string.loc_bogota_default);

		LatLng latlng=new LatLng(Double.parseDouble(coordinateString.split(",")[0]),
				Double.parseDouble(coordinateString.split(",")[1]));
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,12) );
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			mMap = ((MapView) rootView.findViewById(R.id.map_service)).getMap();
			if (mMap != null) {
				MapsInitializer.initialize(getActivity());
				setUpMap();
			}
		}
	}
	private void setUpMap() {
		mMap.setOnMyLocationButtonClickListener(this);
		mMap.setOnCameraChangeListener(this);

	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getActivity(),this,this); // OnConnectionFailedListener
		}
	}

	@Override
	public void onCameraChange(CameraPosition camaraPosition) {
		//Log.i("debug camera",camaraPosition.target.toString());
		getAddress(camaraPosition.target);

	}

	private void getAddress(LatLng latlng){
		GetAddressTask getAddressTask=new GetAddressTask(getActivity());
		getAddressTask.execute(latlng);

	}

	@Override
	public void onLocationChanged(Location arg0) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.e("connection failed",""+connectionResult.getErrorCode());
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnChangeAddressListener) {
			listener = (OnChangeAddressListener) activity;
		} else {
			throw new ClassCastException(activity.toString()+ " must implement MyListFragment.OnItemSelectedListener");
		}
	}

	public void updateAddress(String address){
		listener.OnChangeAddress(address);
	}


    /**
     * Queries asynchronously the address of a given coordinates.
     */
	private class GetAddressTask extends AsyncTask<LatLng,Void,String>{

		private Context context;
		private int MAX_RESULTS=1;

		public GetAddressTask(Context context) {
			super();
			this.context=context;
		}

		@Override
		protected String doInBackground(LatLng... args) {

			String addressText=null;
			List<Address> addresses=null;
			Geocoder geocoder=new Geocoder(context,Locale.getDefault());
			LatLng latlng=args[0];

			try {
				addressText=latlng.latitude+","+latlng.longitude;
				Log.i("debug latlng",addressText);
				addresses=geocoder.getFromLocation(latlng.latitude, latlng.longitude, MAX_RESULTS);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("MapServiceFragment", "IO Exception in getFromLocation()");
				return addressText;
			}catch (IllegalArgumentException e2) {
				// Error message to post in the log
				String errorString = "Illegal arguments " +Double.toString(latlng.latitude) +" , " +Double.toString(latlng.longitude) +" passed to address service";
				Log.e("LocationSampleActivity", errorString);
				e2.printStackTrace();
				//return errorString;
				return addressText;
			}

			if(isCancelled()){
				return addressText;
				//return "Task cancelled";
			}

			if(addresses!=null && addresses.size()>0){
				Address address=addresses.get(0);
				addressText += ";"+String.format("%s",address.getMaxAddressLineIndex() > 0 ?address.getAddressLine(0) : "");

			}
			return addressText;
		}

		@Override
		protected void onCancelled() {
			Log.e("debug task","taks cancelled");
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(String address) {
			super.onPostExecute(address);
			try{
				actionBar.setSubtitle(address.split(";")[1]);
			}catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
			}

			updateAddress(address);
		}

		@Override
		protected void onPreExecute() {
			//mProgress.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}


	}

	public interface OnChangeAddressListener{
		public void OnChangeAddress(String address);
	}
	
}
