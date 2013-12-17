package com.jmv.frre.moduloestudiante.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.common.base.Function;
import com.jmv.frre.moduloestudiante.LoginActivity;
import com.jmv.frre.moduloestudiante.MainActivity;
import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.comps.MyTableRow;
import com.jmv.frre.moduloestudiante.constants.Notas;
import com.jmv.frre.moduloestudiante.net.HTMLParser;
import com.jmv.frre.moduloestudiante.net.HTTPScraper;
import com.jmv.frre.moduloestudiante.net.NetworkTaskHandler;

import java.util.ArrayList;
import java.util.Collections;

public class ExamsMadeActivity extends LinkActivity {

	private HTTPScraper scraper;

	private Spinner spinner;

	private TableLayout materiasView;

	private TextView aprobaText;
	private TextView desaprobaText;
	private TextView ausenteText;

	private static final String DELIMITER = "#";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_exams_made);

		aprobaText = (TextView) findViewById(R.id.aprobadasTextView);
		desaprobaText = (TextView) findViewById(R.id.desaprobadasTextView);
		ausenteText = (TextView) findViewById(R.id.ausentesTextView);

		mHomeFormView = findViewById(R.id.home_form);
		mLoginStatusView = findViewById(R.id.loading_content_status);

		// reference the table layout
		materiasView = (TableLayout) findViewById(R.id.RHE);

		setAllViewsAs(View.GONE);

		spinner = (Spinner) findViewById(R.id.change_type_spinner);
		spinner.setSelection(0);
		spinner.setFocusable(true);
		spinner.requestFocus();
		spinner.setOnItemSelectedListener(spinnerListener);

		String data = getIntent().getStringExtra(LINK_EXTRA);
		if (data != null) {
			getRequest = makeGetRequest(data);
		}
		Function<String, Void> examenesFunction = new Function<String, Void>() {
			@Override
			public Void apply(String response) {
				HTMLParser parser = new HTMLParser(response);
				showProgress(false);
				if (!checkForErrors(parser, response)) {
					ArrayList<String> examList = parser.getExamsMade();
					loadExams(examList);
				}
				return null;
			}
		};

		showProgress(true);
		new NetworkTaskHandler(getRequest, examenesFunction).execute();
	}

	private void setAllViewsAs(int visibilityMode) {
		// TODO Auto-generated method stub
		for (int i = 0; i < ((ViewGroup) materiasView).getChildCount(); ++i) {
			View nextChild = ((ViewGroup) materiasView).getChildAt(i);
			if (nextChild instanceof MyTableRow) {
				nextChild.setVisibility(visibilityMode);
			}
		}
	}

	private void loadExams(ArrayList<String> examList) {
		LinearLayout layer2 = new LinearLayout(this);
		layer2.setOrientation(LinearLayout.VERTICAL);
		layer2.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));

		ArrayList<String> ausentes = new ArrayList<String>();
		ArrayList<String> aprobados = new ArrayList<String>();
		ArrayList<String> desaprobados = new ArrayList<String>();
		for (String exam : examList) {
			String[] data = exam.split(DELIMITER);
			if (data.length < 3)
				continue;
			int value = Notas.getNotaByValue(data[2]);
			data[2] = String.valueOf(value);
			if (data.length > 4) {
				String output = data[0] + DELIMITER + data[1] + DELIMITER
						+ data[2] + DELIMITER + data[4];
				if (data[2].equalsIgnoreCase("0")) {
					ausentes.add(output);
				} else {
					if (value < 4) {
						desaprobados.add(output);
					} else {
						aprobados.add(output);
					}
				}
			}
		}

		aprobaText.setText(String.valueOf(aprobados.size()));
		desaprobaText.setText(String.valueOf(desaprobados.size()));
		ausenteText.setText(String.valueOf(ausentes.size()));

		Collections.sort(aprobados);

		for (String exam : aprobados) {
			materiasView.addView(getTableRowView(exam), new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}

		Collections.sort(desaprobados);

		for (String exam : desaprobados) {
			materiasView.addView(getTableRowView(exam), new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}

		Collections.sort(ausentes);

		for (String exam : ausentes) {
			materiasView.addView(getTableRowView(exam), new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
	}

	private MyTableRow getTableRowView(String value) {
		// delcare a new row
		// add views to the row
		String[] data = value.split(DELIMITER);

		MyTableRow newRow = null;

		for (String string : data) {
			TextView text = new TextView(this);
			text.setText(string);
			text.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,
					TableRow.LayoutParams.WRAP_CONTENT));
			text.setTextColor(Color.WHITE);

			newRow = new MyTableRow(this, Integer.valueOf(data[2]));
			newRow.setLayoutParams(new TableLayout.LayoutParams(
					TableLayout.LayoutParams.MATCH_PARENT,
					TableLayout.LayoutParams.WRAP_CONTENT));

			newRow.addView(text);
		}

		return newRow;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	public static void showExamsView(Context context, String link) {
		Intent intent = new Intent(context, ExamsMadeActivity.class);
		intent.putExtra(LINK_EXTRA, link);
		context.startActivity(intent);
	}

	private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			setAllViewsAs(View.GONE);
			for (int i = 0; i < ((ViewGroup) materiasView).getChildCount(); ++i) {
				View nextChild = ((ViewGroup) materiasView).getChildAt(i);
				if (nextChild instanceof MyTableRow) {
					MyTableRow row = (MyTableRow) nextChild;
					switch (pos) {
					case 0:
						if (row.getNota() > 4) {
							row.setVisibility(View.VISIBLE);
						}
						break;
					case 1:
						if (row.getNota() == 0) {
							row.setVisibility(View.VISIBLE);
						}
						break;
					case 2:
						if (row.getNota() < 4) {
							row.setVisibility(View.VISIBLE);
						}
						break;
					default:
						setAllViewsAs(View.VISIBLE);
						break;
					}
				}
			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}

	};

}
