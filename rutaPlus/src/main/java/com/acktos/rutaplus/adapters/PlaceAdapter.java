package com.acktos.rutaplus.adapters;

import java.util.ArrayList;

import com.acktos.rutaplus.R;
import com.acktos.rutaplus.controllers.ServiceController;
import com.acktos.rutaplus.entities.Place;
import com.acktos.rutaplus.entities.Service;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @deprecated  simple BaseAdapter subclass to handle {@link Place} items into {@link android.widget.ListView}
 */
public class PlaceAdapter extends BaseAdapter {
	
	
	private ArrayList<Place> places;
	private LayoutInflater layoutInflater;
	
	public PlaceAdapter(Context context, ArrayList<Place> places){

		this.places=places;
		layoutInflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return places.size();
	}

	@Override
	public Object getItem(int position) {
		return places.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(android.R.layout.simple_list_item_1, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(android.R.id.text1);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Place place = (Place)places.get(position);
		holder.title.setText(place.address);
		
		
		return convertView;
	}

	static class ViewHolder {
		TextView title;
	}
	
	
}
