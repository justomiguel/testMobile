package com.jmv.frre.moduloestudiante.activities.calendar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
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
	public ArrayList<String> items; // container to store some random calendar
									// items

	public int [][] matrix = new int[12][31];
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar_view);

		month = Calendar.getInstance();
		onNewIntent(getIntent());

		items = new ArrayList<String>();
		adapter = new CalendarAdapter(this, month);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(adapter);

		try {
			Resources res = getResources();
			InputStream in_s = res.openRawResource(R.raw.fechas_examen);

			for (int i = 0; i < 12; i++) {
				for (int j = 0; j < 31; j++) {
					matrix[i][j]=0;
				}
			}
			
			if (in_s != null) {

				InputStreamReader tmp = new InputStreamReader(in_s);

				BufferedReader reader = new BufferedReader(tmp);

				String str;

				StringBuilder buf = new StringBuilder();

				while ((str = reader.readLine()) != null && !str.trim().isEmpty()) {
					String[] line = str.split("-");
					int posFila = Integer.parseInt(line[1]);
					int posColumna = Integer.parseInt(line[2]);
					matrix[posFila][posColumna]=1;
				}

				in_s.close();
			}
		} catch (Exception e) {

		}

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
					
					int i = Integer.parseInt(String.valueOf(android.text.format.DateFormat.format("MM", month)));
					int j = Integer.parseInt(day);
					intent.putExtra(
							"detalle", matrix[i][j]);
					setResult(RESULT_OK, intent);
					finish();
				}

			}
		});
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
		month.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]),
				Integer.parseInt(dateArr[2]));
	}

	public Runnable calendarUpdater = new Runnable() {

		@Override
		public void run() {
			items.clear();

			int i = Integer.parseInt(String.valueOf(android.text.format.DateFormat.format("MM", month)));
			for (int j = 0; j < 31; j++) {
				if (matrix[i][j]==1){
					items.add(Integer.toString(j));
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
