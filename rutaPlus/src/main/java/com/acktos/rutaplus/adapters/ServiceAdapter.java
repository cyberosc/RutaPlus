package com.acktos.rutaplus.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.acktos.rutaplus.R;
import com.acktos.rutaplus.android.DateTimeUtils;
import com.acktos.rutaplus.controllers.ServiceController;
import com.acktos.rutaplus.entities.Service;

import java.util.ArrayList;

/**
 * A simple BaseAdapter subclass to handle {@link Service} items into {@link android.widget.ListView}
 */
public class ServiceAdapter extends BaseAdapter {
	
	
	private ArrayList<Service> services;
	private LayoutInflater layoutInflater;
	
	public ServiceAdapter(Context context, ArrayList<Service> services){

		this.services=services;
		layoutInflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return services.size();
	}

	@Override
	public Object getItem(int position) {
		return services.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.service_item_list, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.subtitle = (TextView) convertView.findViewById(R.id.subtitle);
			holder.status = (TextView) convertView.findViewById(R.id.status);
			holder.serviceType = (TextView) convertView.findViewById(R.id.service_type);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Service service = (Service)services.get(position);
		
		String status=service.status;
		holder.title.setText(service.pickupAddress);
		
		String dateService=service.dateTime;
		
		
		if(status.equals(ServiceController.STATUS_KEY_PENDING)){
			holder.status.setTextColor(Color.rgb(225, 157, 2));
		}
		if(status.equals(ServiceController.STATUS_KEY_APPROVED)){
			holder.status.setTextColor(Color.rgb(66, 125, 72));
		}
		if(status.equals(ServiceController.STATUS_KEY_ONTRACK)){
			holder.status.setTextColor(Color.rgb(52, 156, 227));
			status=ServiceController.STATUS_KEY_APPROVED;
		}
		if(status.equals(ServiceController.STATUS_KEY_COMPLETED)){
			holder.status.setTextColor(Color.rgb(65, 65, 65));
		}
		if(status.equals(ServiceController.STATUS_KEY_CANCELED)){
			holder.status.setTextColor(Color.rgb(144, 13, 12));
		}
		if(status.equals(ServiceController.STATUS_KEY_DEFER)){
			holder.status.setTextColor(Color.rgb(65, 37, 17));
			dateService=service.dateTimeDefer;
		}
		if(status.equals(ServiceController.STATUS_KEY_IN_PLACE)){
			holder.status.setTextColor(Color.rgb(52,156, 227));

		}
				
		holder.subtitle.setText(DateTimeUtils.toLatinDate(dateService));
		holder.status.setText(status);
		holder.serviceType.setText(service.serviceType);
		
		return convertView;
	}

	static class ViewHolder {
		TextView title;
		TextView subtitle;
		TextView status;
		TextView serviceType;
	}
	
	
}
