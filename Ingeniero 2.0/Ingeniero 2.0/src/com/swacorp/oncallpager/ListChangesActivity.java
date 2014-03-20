package com.swacorp.oncallpager;

import static com.swacorp.oncallpager.utils.Constants.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.jmv.frre.moduloestudiante.R;
import com.swacorp.oncallpager.utils.CalendarEvent;
import com.swacorp.oncallpager.utils.CalendarUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ListChangesActivity extends Activity {

	private Spinner spinner;
	private View mLoginStatusView;
	private View mLoginFormView;
	private TextView fromOutput;
	private TextView untilOutput;

	protected int yearFrom;
	protected int monthFrom;
	protected int dayFrom;

	protected int yearUntil;
	protected int monthUntil;
	protected int dayUntil;
	
	private LinearLayout change_details_layer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_changes);

		spinner = (Spinner) findViewById(R.id.change_spinner);
		spinner.setSelection(0);
		spinner.setFocusable(true);
		spinner.requestFocus();
		spinner.setOnItemSelectedListener(spinnerListener);

		mLoginStatusView = findViewById(R.id.login_status_list_changes);
		mLoginFormView = findViewById(R.id.home_form_list_changes);

		fromOutput = (TextView) findViewById(R.id.from_selected_date);

		untilOutput = (TextView) findViewById(R.id.until_selected_date);

		change_details_layer = (LinearLayout) findViewById(R.id.change_details_layer);
		change_details_layer.setVisibility(View.GONE);

		final Calendar c = Calendar.getInstance();

		yearFrom = c.get(Calendar.YEAR);
		monthFrom = c.get(Calendar.MONTH);
		dayFrom = c.get(Calendar.DAY_OF_MONTH) - 1;

		yearUntil = yearFrom;
		monthUntil = monthFrom;
		dayUntil = dayFrom + 2;

		fromOutput.setText(new StringBuilder().append(monthFrom + 1)
				.append("-").append(dayFrom).append("-").append(yearFrom)
				.append(" "));

		untilOutput.setText(new StringBuilder().append(monthUntil + 1)
				.append("-").append(dayUntil).append("-").append(yearUntil)
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

	public void searchChanges(View view) {
		Calendar cal = Calendar.getInstance();
		cal.set(yearFrom, monthFrom, dayFrom);

		Calendar untilCal = Calendar.getInstance();
		untilCal.set(yearUntil, monthUntil, dayUntil);

		if (cal.getTimeInMillis() >= untilCal.getTimeInMillis()) {
			Toast.makeText(getApplicationContext(),
					R.string.calendar_event_wrong_message, Toast.LENGTH_LONG)
					.show();
			return;
		}
		showProgress(true);
		new GetCalendarEventsTask().execute(cal.getTimeInMillis(),
				untilCal.getTimeInMillis());
	}

	public void setDateFrom(View view) {
		showDialog(FROM_DATE_PICKER_ID);
	}

	public void setDateUntil(View view) {
		showDialog(UNTIL_DATE_PICKER_ID);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case FROM_DATE_PICKER_ID:
			return new DatePickerDialog(this, fromPickerListener, yearFrom,
					monthFrom, dayFrom);
		case UNTIL_DATE_PICKER_ID:
			return new DatePickerDialog(this, untilPickerListener, yearUntil,
					monthUntil, dayUntil);

		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_changes, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		OnCallHelperActivity.showOnCallHelperHome(this);
	}
	
	public void removeFromToCalendar(View view) {
		TextView idTv = (TextView) findViewById(R.id.id_change_descrip);
		try {
			CalendarUtil.deleteEventWithID(this, Long.valueOf(String.valueOf(idTv.getText()).trim()));
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), getString(R.string.calendar_event_deleted_error)+e.getMessage(), Toast.LENGTH_LONG).show();
			return;
		}
		Toast.makeText(getApplicationContext(), R.string.calendar_event_deleted, Toast.LENGTH_LONG).show();
		searchChanges(null);
		change_details_layer.setVisibility(View.GONE);
		return;
	}
	
	public void addToCalendar(View view) {
		TextView idTv = (TextView) findViewById(R.id.id_change_descrip);
		TextView tittle = (TextView) findViewById(R.id.title_change_descrip);
		TextView startD = (TextView) findViewById(R.id.start_date_change_descrip);
		TextView endDate = (TextView) findViewById(R.id.end_date_change_descrip);
		
		HashMap<String, String> intentMap = new HashMap<String, String>();
		intentMap.put(AddChangeActivity.CALENDAR_ID, String.valueOf(idTv.getText()));
		intentMap.put(AddChangeActivity.CALENDAR_TITLE, String.valueOf(tittle.getText()));
		intentMap.put(AddChangeActivity.CALENDAR_START_DATE, String.valueOf(startD.getText()));
		intentMap.put(AddChangeActivity.CALENDAR_END_DATE, String.valueOf(endDate.getText()));
		
		AddChangeActivity.showAddChangeActivityHome(this, intentMap);

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

	public static void showListActivityHome(Context onCallHelperActivity) {
		Intent intent = new Intent(onCallHelperActivity,
				ListChangesActivity.class);
		onCallHelperActivity.startActivity(intent);
	}

	private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			String data = (String) spinner.getItemAtPosition(pos);
			if (data != null) {
				for (CalendarEvent event : currentCalendarEvents) {
					String myData = event.getId()+" - "+event.getTittle();
					if (myData.equalsIgnoreCase(data)){
						change_details_layer.setVisibility(View.VISIBLE);
						TextView idTv = (TextView) findViewById(R.id.id_change_descrip);
						idTv.setText(String.valueOf(event.getId()));
						TextView tittle = (TextView) findViewById(R.id.title_change_descrip);
						tittle.setText(event.getTittle());
						TextView startD = (TextView) findViewById(R.id.start_date_change_descrip);
						startD.setText(event.getStartDate());
						TextView endDate = (TextView) findViewById(R.id.end_date_change_descrip);
						endDate.setText(event.getEndDate());
					}
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}

	};
	public List<CalendarEvent> currentCalendarEvents;

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});

		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public class GetCalendarEventsTask extends
			AsyncTask<Long, Void, List<CalendarEvent>> {

		@Override
		protected List<CalendarEvent> doInBackground(Long... params) {
			ArrayList<CalendarEvent> events = CalendarUtil.readCalendarEvent(
					ListChangesActivity.this, params[0], params[1]);
			return events;
		}

		@Override
		protected void onPostExecute(final List<CalendarEvent> events) {
			showProgress(false);
			if (events != null) {
				currentCalendarEvents = events;
				ArrayAdapter<String> adp1 = new ArrayAdapter<String>(
						ListChangesActivity.this,
						android.R.layout.simple_list_item_1,
						trsnformToArray(events));
				adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(adp1);
			}
		}

		private String[] trsnformToArray(List<CalendarEvent> events) {
			String[] names = new String[events.size()];
			for (int i = 0; i < names.length; i++) {
				names[i] = events.get(i).getId()+ " - "
						+ events.get(i).getTittle();
			}
			return names;
		}

		@Override
		protected void onCancelled() {
			showProgress(false);
		}
	}

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

	private DatePickerDialog.OnDateSetListener untilPickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {

			yearUntil = selectedYear;
			monthUntil = selectedMonth;
			dayUntil = selectedDay;

			// Show selected date
			untilOutput.setText(new StringBuilder().append(monthUntil + 1)
					.append("-").append(dayUntil).append("-").append(yearUntil)
					.append(" "));

		}
	};

}
