package com.jmv.frre.moduloestudiante.customcomponents;

import java.util.ArrayList;

import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.R.id;
import com.jmv.frre.moduloestudiante.R.layout;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<CustomItem> items;
 
	public ImageAdapter(Context context, ArrayList<CustomItem> items) {
		this.context = context;
		this.items = items;
	}
 
	public View getView(int position, View convertView, ViewGroup parent) {
 
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View gridView;
 
		if (convertView == null) {
 
			gridView = new View(context);
 
			// get layout from mobile.xml
			gridView = inflater.inflate(R.layout.mobile, null);
 
			// set image based on selected text
			ImageView imageView = (ImageView) gridView
					.findViewById(R.id.grid_item_image);
 
			// set value into textview
			TextView textView = (TextView) gridView
					.findViewById(R.id.grid_item_label);
			textView.setText("");
 
			TextView textView2 = (TextView) gridView
					.findViewById(R.id.grid_item_label_2);
			
			CustomItem item = getItemByPosition(position);
			
			textView2.setText(item.getLabel());
			
			imageView.setImageResource(item.getIdResource());
			
			item.setTheView(gridView);
 
		} else {
			gridView = (View) convertView;
		}
 
		return gridView;
	}
 
	private CustomItem getItemByPosition(Integer position) {
		for (CustomItem item : items) {
			if (item.getPosition().equals(position)){
				return item;
			}
		}
		return null;
	}

	@Override
	public int getCount() {
		return items.size();
	}
 
	@Override
	public Object getItem(int position) {
		return null;
	}
 
	@Override
	public long getItemId(int position) {
		return 0;
	}

}
