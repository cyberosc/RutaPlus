package com.acktos.rutaplus.adapters;

import java.util.ArrayList;

import com.acktos.rutaplus.R;
import com.acktos.rutaplus.controllers.ServiceController;
import com.acktos.rutaplus.entities.Destination;
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
 * @deprecated A simple BaseAdapter subclass to handle {@link Destination} items into {@link android.widget.ListView}
 */
public class DestinationAdapter extends BaseAdapter {
	
	
	private ArrayList<Destination> des;
	private LayoutInflater layoutInflater;
	
	public DestinationAdapter(Context context, ArrayList<Destination> des){

		this.des=des;
		layoutInflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return des.size();
	}

	@Override
	public Object getItem(int position) {
		return des.get(position);
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
		
		Destination destination = (Destination)des.get(position);
		holder.title.setText(destination.address);
		
		
		return convertView;
	}

	static class ViewHolder {
		TextView title;
	}
	
	
}
