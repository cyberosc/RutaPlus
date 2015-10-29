package com.acktos.rutaplus.adapters;

import java.util.ArrayList;

import com.acktos.rutaplus.R;
import com.acktos.rutaplus.entities.CreditCard;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple BaseAdapter subclass to handle {@link CreditCard} items into {@link android.widget.ListView}
 */
public class CreditCardsAdapter extends BaseAdapter {
	
	
	private ArrayList<CreditCard> cards;
	private LayoutInflater layoutInflater;
	
	public CreditCardsAdapter(Context context, ArrayList<CreditCard> cards){

		this.cards=cards;
		layoutInflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return cards.size();
	}

	@Override
	public Object getItem(int position) {
		return cards.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.credit_cards_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.txt_card_name);
			holder.subtitle = (TextView) convertView.findViewById(R.id.txt_card_reference);
			holder.image = (ImageView) convertView.findViewById(R.id.card_img);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		CreditCard card = (CreditCard)cards.get(position);
		holder.title.setText(card.termination);
		holder.subtitle.setText(card.name);
		
		
		if(card.type.equals(CreditCard.CARD_TYPE_VISA)){
			holder.image.setImageResource(R.drawable.ic_visa);
			
		}else if(card.type.equals(CreditCard.CARD_TYPE_AMEX)){
			holder.image.setImageResource(R.drawable.ic_amex);
			
		}else if(card.type.equals(CreditCard.CARD_TYPE_MASTERCARD)){
			holder.image.setImageResource(R.drawable.ic_master);
			
		}else {
			holder.image.setImageResource(R.drawable.ic_diners);
		}
		
		return convertView;
	}

	static class ViewHolder {
		
		TextView title;
		TextView subtitle;
		ImageView image;
		
	}
	
	
}
