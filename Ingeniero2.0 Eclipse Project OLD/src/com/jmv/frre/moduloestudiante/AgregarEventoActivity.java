package com.jmv.frre.moduloestudiante;


import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import com.jmv.frre.moduloestudiante.activities.calendar.CalendarUtil;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import static com.jmv.frre.moduloestudiante.constants.Constants.*;

public class AgregarEventoActivity extends Activity {

	public static final String CALENDAR_ID = "CALENDAR_ID";
	public static final String CALENDAR_TITLE = "CALENDAR_TITLE";
	public static final String CALENDAR_START_DATE = "CALENDAR_START_DATE";
	public static final String CALENDAR_END_DATE = "CALENDAR_END_DATE";

	public static void showAddChangeActivityHome(Context context) {
		Intent intent = new Intent(context, AgregarEventoActivity.class);
		context.startActivity(intent);
	}

	public static void showAddChangeActivityHome(Context context,	HashMap<String, String> intentMap) {
		Intent intent = new Intent(context, AgregarEventoActivity.class);
		Set<String> keys = intentMap.keySet();
		for (String key : keys) {
			intent.putExtra(key, intentMap.get(key));
		}
		context.startActivity(intent);
	}

	protected int yearFrom;
	protected int monthFrom;
	protected int dayFrom;

	private int minuteFrom;
	private int hourFrom;

	protected int yearUntil;
	protected int monthUntil;
	protected int dayUntil;

	private int minuteUntil;
	private int hourUntil;

	protected TextView fromOutput;
	private TextView fromTimeOutput;

	private Spinner spinner;
	private AutoCompleteTextView subject;
	private LinearLayout change_number_layer;
	private AutoCompleteTextView change_number;
	private boolean isAModification;
	private Long eventIDForModification;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agregar_evento);

		spinner = (Spinner) findViewById(R.id.change_type_spinner);
		spinner.setSelection(0);
		spinner.setFocusable(true);
		spinner.requestFocus();
		spinner.setOnItemSelectedListener(spinnerListener);

		subject = (AutoCompleteTextView) findViewById(R.id.autotext);

		change_number_layer = (LinearLayout) findViewById(R.id.change_number_layer);

		change_number = (AutoCompleteTextView) findViewById(R.id.change_number);

		fromOutput = (TextView) findViewById(R.id.from_selected_date);
		fromTimeOutput = (TextView) findViewById(R.id.from_selected_time);


		// Get current date by calender
		final Calendar c = Calendar.getInstance();
		
		String externalIDdata = getIntent().getStringExtra(CALENDAR_ID);
		if (externalIDdata != null){
		    try {
		    	String tittle = getIntent().getStringExtra(CALENDAR_TITLE);
				Date startDate =  CalendarUtil.DATE_FORMATTER.parse(getIntent().getStringExtra(CALENDAR_START_DATE));
				Date endDate = CalendarUtil.DATE_FORMATTER.parse(getIntent().getStringExtra(CALENDAR_END_DATE));
				c.setTimeInMillis(startDate.getTime());
				
				final Calendar untilCalendar = Calendar.getInstance();
				untilCalendar.setTimeInMillis(endDate.getTime());
				
				yearFrom = c.get(Calendar.YEAR);
				monthFrom = c.get(Calendar.MONTH);
				dayFrom = c.get(Calendar.DAY_OF_MONTH);
				hourFrom = c.get(Calendar.HOUR_OF_DAY);
				minuteFrom = c.get(Calendar.MINUTE);

				yearUntil = untilCalendar.get(Calendar.YEAR);
				monthUntil = untilCalendar.get(Calendar.MONTH);
				dayUntil = untilCalendar.get(Calendar.DAY_OF_MONTH);
				hourUntil = untilCalendar.get(Calendar.HOUR_OF_DAY);
				minuteUntil = untilCalendar.get(Calendar.MINUTE);
				
				isAModification = true;
				
				eventIDForModification = Long.valueOf(externalIDdata.trim());
				
				String[] data = tittle.split("-");
				if (data.length > 2){
					String chgNumber = data[1].substring(4).trim();
					boolean isCHG = data[1].substring(0, 4).trim().equalsIgnoreCase("CHG");
					spinner.setSelection(isCHG?0:1);
					change_number.setText(chgNumber);
					change_number_layer.setVisibility(View.VISIBLE);
					subject.setText(data[2].trim());
				} else {
					spinner.setSelection(2);
					change_number_layer.setVisibility(View.GONE);
					subject.setText(data[1].trim());
				}
				
			} catch (ParseException e) {}  
		} else {
			isAModification = false;
			yearFrom = c.get(Calendar.YEAR);
			monthFrom = c.get(Calendar.MONTH);
			dayFrom = c.get(Calendar.DAY_OF_MONTH);
			hourFrom = c.get(Calendar.HOUR_OF_DAY);
			minuteFrom = c.get(Calendar.MINUTE);

			yearUntil = yearFrom;
			monthUntil = monthFrom;
			dayUntil = dayFrom;
			hourUntil = hourFrom+3;
			minuteUntil = minuteFrom;
		}
		
		fromTimeOutput.setText(new StringBuilder().append(hourFrom)
				.append(":").append(minuteFrom));
		fromOutput.setText(new StringBuilder().append(monthFrom + 1)
				.append("-").append(dayFrom).append("-").append(yearFrom)
				.append(" "));


	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}


	public void setDateFrom(View view) {
		showDialog(FROM_DATE_PICKER_ID);
	}

	public void setTimeFrom(View view) {
		showDialog(FROM_TIME_PICKER_ID);
	}

	public void setDateUntil(View view) {
		showDialog(UNTIL_DATE_PICKER_ID);
	}

	public void setTimeUntil(View view) {
		showDialog(UNTIL_TIME__PICKER_ID);
	}

	@SuppressLint("NewApi")
	public void addToCalendar(View view) {
		Calendar cal = Calendar.getInstance();
		cal.set(yearFrom, monthFrom, dayFrom, hourFrom, minuteFrom);

		Calendar untilCal = Calendar.getInstance();
		untilCal.set(yearUntil, monthUntil, dayUntil, hourUntil, minuteUntil);

		if (cal.getTimeInMillis() >= untilCal.getTimeInMillis()){
			Toast.makeText(getApplicationContext(), R.string.calendar_event_wrong_message, Toast.LENGTH_LONG).show();
			return;
		}
		
		String chgNumber = String.valueOf(change_number.getText());
		
		if (change_number_layer.getVisibility() == View.VISIBLE && chgNumber.isEmpty()){
			Toast.makeText(getApplicationContext(), R.string.calendar_event_missing_sr_chg_number, Toast.LENGTH_LONG).show();
			return;
		}
		
		String decsription = String.valueOf(subject.getText());
		
		if (decsription.isEmpty()){
			Toast.makeText(getApplicationContext(), R.string.calendar_event_missing_description, Toast.LENGTH_LONG).show();
			return;
		}
		
		StringBuilder changeType = new StringBuilder(spinner.getSelectedItem().toString());
		
		if (change_number_layer.getVisibility() == View.VISIBLE){
			changeType.append(chgNumber);
		}

		changeType.append(" - ");
		changeType.append(decsription);
		
		if (Build.VERSION.SDK_INT >= 8) {
			try{
				CalendarUtil.pushAppointmentsToCalender(this, changeType.toString(), subject.getText().toString(), eventIDForModification, cal.getTimeInMillis(), untilCal.getTimeInMillis(), true, true, isAModification);
			} catch (Exception e){
				Toast.makeText(getApplicationContext(), R.string.calendar_event_undefined_error, Toast.LENGTH_LONG).show();
				return;
			}
		}else {
			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setType("vnd.android.cursor.item/event");
			intent.putExtra("beginTime", cal.getTimeInMillis());
			intent.putExtra("allDay", false);
			intent.putExtra("rrule", "FREQ=DAILY;COUNT=1");
			intent.putExtra("endTime", untilCal.getTimeInMillis());
			intent.putExtra("title", changeType + " - " + subject.getText());
			startActivity(intent);
		}
		
		 Toast.makeText(getApplicationContext(), R.string.calendar_event_added_message, Toast.LENGTH_LONG).show();
		 
		 MainScreenActivity.showHome(this);
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case FROM_DATE_PICKER_ID:
			return new DatePickerDialog(this, fromPickerListener, yearFrom,
					monthFrom, dayFrom);
		case FROM_TIME_PICKER_ID:
			return new TimePickerDialog(this, fromTimePickerListener, hourFrom,
					minuteFrom,
					DateFormat.is24HourFormat(AgregarEventoActivity.this));

		}
		return null;
	}

	private TimePickerDialog.OnTimeSetListener fromTimePickerListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			hourFrom = hourOfDay;
			minuteFrom = minute;
			// Show selected date
			fromTimeOutput.setText(new StringBuilder().append(hourFrom)
					.append(":").append(minuteFrom));
		}
	};

	private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			if (pos == spinner.getCount()-1){
				change_number_layer.setVisibility(View.GONE);
			} else {
				change_number_layer.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}

	};
	
	private DatePickerDialog.OnDateSetListener fromPickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {

			yearFrom = selectedYear;
			monthFrom = selectedMonth;
			dayFrom = selectedDay;

			// Show selected date
			fromOutput.setText(new StringBuilder().append(monthFrom + 1)
					.append("-").append(dayFrom).append("-").append(yearFrom)
					.append(" "));

		}
	};

}
