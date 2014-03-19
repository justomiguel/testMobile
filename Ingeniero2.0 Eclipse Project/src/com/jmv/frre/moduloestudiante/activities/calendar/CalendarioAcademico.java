package com.jmv.frre.moduloestudiante.activities.calendar;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jmv.frre.moduloestudiante.HorariosCursado;
import com.jmv.frre.moduloestudiante.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class CalendarioAcademico extends Activity {

	static final int DATE_DIALOG_ID = 0;
	static final int PICK_DATE_REQUEST = 1;
	private View linearLayout;
	private TextView date_chossed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendario_academico);
		linearLayout = findViewById(R.id.details_form);
		date_chossed = (TextView) findViewById(R.id.date_chossed);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calendario_academico, menu);
		return true;
	}

	public void showCalendar(View view) {
		if (((LinearLayout) linearLayout).getChildCount() > 0)
			((LinearLayout) linearLayout).removeAllViews();
		
		date_chossed.setText(R.string.calendar_activity_details_title_content);
		
		Intent intent = new Intent(this, CalendarView.class);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		intent.putExtra("date", formatter.format(new Date()));
		startActivityForResult(intent, PICK_DATE_REQUEST);
	}
	
	public void showCalendarImage(View view) {
		int calendar =  this.getResources().getIdentifier("calendario_imagen", "drawable",  this.getPackageName());;
		StandardImageProgrammatic.showHome(this, calendar);
	}
	
	public void showCalendarImageTsp(View view) {
		int calendar =  this.getResources().getIdentifier("calendario_imagen_tsp", "drawable",  this.getPackageName());;
		StandardImageProgrammatic.showHome(this, calendar);
	}


	public static void showHome(Context context) {
		Intent intent = new Intent(context, CalendarioAcademico.class);
		context.startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_DATE_REQUEST) {
			if (resultCode == RESULT_OK) {
				
				date_chossed.setText(data.getStringExtra("date"));
				
				String detalle = data.getStringExtra("detalle");
				
				if (detalle!=null && detalle.length()!=0){
					String[] events = detalle.split("&");
					for (String string : events) {
						TextView valueTV = new TextView(this);
						valueTV.setText(string);
						valueTV.setId(5);
						valueTV.setTextColor(Color.WHITE);
						valueTV.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
						((LinearLayout) linearLayout).addView(valueTV);
					}
				} else {
					TextView valueTV = new TextView(this);
					valueTV.setText("DIA LIBRE");
					valueTV.setId(5);
					valueTV.setTextColor(Color.WHITE);
					valueTV.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					((LinearLayout) linearLayout).addView(valueTV);
				}
				

			}
		}
	}
}
