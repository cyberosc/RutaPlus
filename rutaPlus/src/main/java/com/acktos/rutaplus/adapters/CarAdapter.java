package com.acktos.rutaplus.adapters;

import java.util.ArrayList;

import com.acktos.rutaplus.entities.Car;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CarAdapter extends BaseAdapter {
	
	
	private ArrayList<Car> cars;
	private LayoutInflater layoutInflater;
	
	public CarAdapter(Context context, ArrayList<Car> cars){

		this.cars=cars;
		layoutInflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return cars.size();
	}

	@Override
	public Object getItem(int position) {
		return cars.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(android.R.layout.simple_list_item_activated_2, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(android.R.id.text1);
			holder.subtitle = (TextView) convertView.findViewById(android.R.id.text2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Car car = (Car)cars.get(position);

		holder.title.setText(car.alias);
		holder.subtitle.setText(car.plate);
		
		//Log.i("debug alias:",car.alias);
		//Log.i("debug alias:",car.plate);
		return convertView;
	}

	static class ViewHolder {
		TextView title;
		TextView subtitle;
	}
	
	
}
