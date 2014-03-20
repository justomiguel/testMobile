package com.jmv.frre.moduloestudiante.activities.calendar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.utils.Utils;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CalendarView extends Activity {

	public Calendar month;
	public CalendarAdapter adapter;
	public Handler handler;
	public HashMap<Integer, String> items; // container to store some random calendar
									// items

	public HashMap<SpecialDates, String[][]> specialDates;
	public HashMap<SpecialDates, Integer> specialDatesResources = new HashMap<SpecialDates, Integer>();

	{
		specialDatesResources.put(SpecialDates.MESA_EXAMEN, R.raw.fechas_examen);
		specialDatesResources.put(SpecialDates.FERIADO, R.raw.fechas_feriados);
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar_view);

		month = Calendar.getInstance();
		onNewIntent(getIntent());

		items = new HashMap<Integer, String>();
		adapter = new CalendarAdapter(this, month);

		specialDates = new HashMap<SpecialDates, String[][]>();
		for (SpecialDates specialDateKey : SpecialDates.values()) {
			specialDates.put(specialDateKey, new String[12][31]);
			Integer resourceId = specialDatesResources.get(specialDateKey);
			if (resourceId!=null){
				getDatesFromFiles(specialDateKey, resourceId);
			}
		}
		
		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(adapter);

		handler = new Handler();
		handler.post(calendarUpdater);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

		TextView previous = (TextView) findViewById(R.id.previous);
		previous.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (month.get(Calendar.MONTH) == month
						.getActualMinimum(Calendar.MONTH)) {
					month.set((month.get(Calendar.YEAR) - 1),
							month.getActualMaximum(Calendar.MONTH), 1);
				} else {
					month.set(Calendar.MONTH, month.get(Calendar.MONTH) - 1);
				}
				refreshCalendar();
			}
		});

		TextView next = (TextView) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (month.get(Calendar.MONTH) == month
						.getActualMaximum(Calendar.MONTH)) {
					month.set((month.get(Calendar.YEAR) + 1),
							month.getActualMinimum(Calendar.MONTH), 1);
				} else {
					month.set(Calendar.MONTH, month.get(Calendar.MONTH) + 1);
				}
				refreshCalendar();

			}
		});

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				TextView date = (TextView) v.findViewById(R.id.date);
				if (date instanceof TextView && !date.getText().equals("")) {

					Intent intent = new Intent();
					String day = date.getText().toString();
					if (day.length() == 1) {
						day = "0" + day;
					}
					// return chosen date as string format
					intent.putExtra(
							"date",
							android.text.format.DateFormat.format("yyyy-MM",
									month) + "-" + day);

					int i = Integer.parseInt(String
							.valueOf(android.text.format.DateFormat.format(
									"MM", month)))-1;
					int j = Integer.parseInt(day)-1;
					
					StringBuilder builder = new StringBuilder();
					
					for (SpecialDates specialDateKey : SpecialDates.values()) {
						String value = specialDates.get(specialDateKey)[i][j];
						if (value!=null){
							builder.append(specialDateKey.toString())
								.append(":")
								.append(value);
							builder.append("&");
						}
					}
					intent.putExtra("detalle", builder.toString());
					setResult(RESULT_OK, intent);
					finish();
				}

			}
		});
	}

	private void getDatesFromFiles(SpecialDates specialDateKey, int resourceID) {
		try {
			Resources res = getResources();
			InputStream in_s = res.openRawResource(resourceID);

			String[][] matrix = specialDates.get(specialDateKey);

			if (in_s != null) {

				InputStreamReader tmp = new InputStreamReader(in_s);

				BufferedReader reader = new BufferedReader(tmp);

				String str;

				StringBuilder buf = new StringBuilder();

				while ((str = reader.readLine()) != null
						&& !str.trim().isEmpty()) {
					String[] line = str.split("-");
					int posFila = Integer.parseInt(line[1])-1;
					int posColumna = Integer.parseInt(line[2])-1;
					String detail = "";
					if (line.length==4){
						detail = line[3];
					}
					matrix[posFila][posColumna] = detail;
				}

				in_s.close();
			}
		} catch (Exception e) {

		}
	}

	public void refreshCalendar() {
		TextView title = (TextView) findViewById(R.id.title);

		adapter.refreshDays();
		adapter.notifyDataSetChanged();
		handler.post(calendarUpdater); // generate some random calendar items

		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}

	public void onNewIntent(Intent intent) {
		String date = intent.getStringExtra("date");
		String[] dateArr = date.split("-"); // date format is yyyy-mm-dd
		month.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1])-1,
				Integer.parseInt(dateArr[2]));
	}

	public Runnable calendarUpdater = new Runnable() {

		@Override
		public void run() {
			items.clear();

			int i = Integer
					.parseInt(String.valueOf(android.text.format.DateFormat
							.format("MM", month)))-1;
			for (int j = 0; j < 31; j++) {
				
				StringBuilder builder = new StringBuilder("");
				
				for (SpecialDates specialDateKey : SpecialDates.values()) {
					String value = specialDates.get(specialDateKey)[i][j];
					if (value!=null){
						builder.append(specialDateKey.toString())
							.append(":")
							.append(value);
						builder.append("&");
					}
				}
				String value = builder.toString();
				if (!value.isEmpty()){
					items.put(j, value);
				}
				
			}
			adapter.setItems(items);
			adapter.notifyDataSetChanged();
		}
	};

	public static void showHome(Context context) {
		Intent intent = new Intent(context, CalendarView.class);
		context.startActivity(intent);
	}

}
