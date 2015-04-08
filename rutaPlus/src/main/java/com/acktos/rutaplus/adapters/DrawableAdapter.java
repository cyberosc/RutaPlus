package com.acktos.rutaplus.adapters;

import java.util.ArrayList;

import com.acktos.rutaplus.R;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class DrawableAdapter extends BaseAdapter {
	private ArrayList<String> items;
	private LayoutInflater layoutInflater;
	private boolean isEnterprise;

	public DrawableAdapter(Context context, ArrayList<String> items,boolean isEnterprise){

		this.items=items;
		layoutInflater=LayoutInflater.from(context);
		this.isEnterprise=isEnterprise;
	}
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}



	@Override
	public boolean isEnabled(int position) {

		if (position==0 || position==1 || position==2){
			return false;
		}
		return super.isEnabled(position);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		convertView = layoutInflater.inflate(R.layout.drawer_list_item, null);

		TextView title = (TextView) convertView.findViewById(R.id.item_drawer);
		ImageView icon = (ImageView) convertView.findViewById(R.id.ic_drawer);


		String item = items.get(position);
		int icResource=0;

		switch(position){
		case 2:
			icResource=R.drawable.ic_id;
			break;
		case 3:
			icResource=R.drawable.ic_action_my_services;
			break;

		case 4:
			
			if(isEnterprise){
				icResource=R.drawable.ic_share;
			}else{
				icResource=R.drawable.ic_my_cards;
			}
			
			break;
		case 5:
			
			if(isEnterprise){
				icResource=R.drawable.ic_my_cars;
			}else{
				icResource=R.drawable.ic_my_rates;
			}
			
			break;
		case 6:
			
			if(isEnterprise){
				icResource=R.drawable.ic_logout;
			}else{
				icResource=R.drawable.ic_share;
			}
			
			break;
		case 7:
			icResource=R.drawable.ic_my_cars;
			break;
		case 8:
			icResource=R.drawable.ic_logout;
			break;

			/*case 4:
			icResource=R.drawable.ic_my_cars;
			break;*/

		}

		if(position==0){
			icon.setVisibility(View.GONE);
			title.setTextSize(TypedValue.COMPLEX_UNIT_SP,26);

		}else if(position==1){
			icon.setVisibility(View.GONE);
			title.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
			title.setTextColor(Color.GRAY);
			title.setLines(2);

		}else{
			icon.setImageResource(icResource);
		}

		title.setText(item);
		return convertView;
	}


}
