package com.jmv.frre.moduloestudiante.activities;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.jmv.frre.moduloestudiante.ExamenIns;
import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.comps.ExamenTableRow;
import com.jmv.frre.moduloestudiante.dialogs.ConfirmationDialog;
import com.jmv.frre.moduloestudiante.net.HTMLParser;
import com.jmv.frre.moduloestudiante.net.NetworkTaskHandler;

import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;

import static com.jmv.frre.moduloestudiante.constants.Constants.DELIMITER;

public class IncsripcionAExamen extends LinkActivity {

	private Spinner spinner;
	private TableLayout tableView;
	protected String myCurrentResponse;
	private String examDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incsripcion_aexamen);

		mHomeFormView = findViewById(R.id.home_form);
		mLoginStatusView = findViewById(R.id.loading_content_status);

		// reference the table layout
		tableView = (TableLayout) findViewById(R.id.RHE);

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
				myCurrentResponse = response;
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
		new NetworkTaskHandler(getRequest, examenesFunction)
				.execute(myCurrentResponse != null ? myCurrentResponse : null);
	}

	protected void loadExams(ArrayList<String> examList) {
		for (String exam : examList) {
			View exa = getTableRowView(exam);
			if (exa != null) {
				tableView.addView(exa, new TableLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
		}

		List<ExamenIns> examnesAIns = new ArrayList<ExamenIns>();

		for (int i = 0; i < ((ViewGroup) tableView).getChildCount(); ++i) {
			View nextChild = ((ViewGroup) tableView).getChildAt(i);
			if (nextChild instanceof ExamenTableRow) {
				examnesAIns.add(((ExamenTableRow) nextChild).getExamen());
			}
		}

		ArrayAdapter<String> adp1 = new ArrayAdapter<String>(
				IncsripcionAExamen.this, android.R.layout.simple_list_item_1,
				trsnformToArray(examnesAIns));
		adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adp1);

		setAllViewsAs(View.GONE);
	}

	private String[] trsnformToArray(List<ExamenIns> exams) {
		String[] names = new String[exams.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = exams.get(i).getPlan() + " - "
					+ exams.get(i).getMateria();
		}
		return names;
	}

	private View getTableRowView(String exam) {
		// delcare a new row
		// add views to the row
		String[] data = exam.split(DELIMITER);

		ExamenTableRow newRow = null;

		ExamenIns examen = null;
		try {
			examen = new ExamenIns(Integer.parseInt(data[0]), data[1], data[2],
					Integer.parseInt(data[3]), Integer.parseInt(data[4]));
		} catch (Exception e) {
			return null;
		}

		newRow = new ExamenTableRow(this, examen);
		
		newRow.addView(getTextForTable(String.valueOf(examen.getAnio())));
		String textToPut = getString(R.string.activity_ins_exam_status_inscripto);
		if (examen.getEstado().equalsIgnoreCase(getString(R.string.activity_ins_exam_to_compare))){
			textToPut = getString(R.string.activity_ins_exam_status_no_inscripto);
		}
		newRow.addView(getTextForTable(textToPut));
		newRow.addView(getTextForTable(String.valueOf(examen.getCodigo())));
		
		newRow.setLayoutParams(new TableLayout.LayoutParams(
				TableLayout.LayoutParams.MATCH_PARENT,
				TableLayout.LayoutParams.WRAP_CONTENT));

		return newRow;
	}

	private View getTextForTable(String string) {
		TextView text = new TextView(this);
		text.setText(string);
		text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		text.setPadding(1, 1, 1, 1);
		TableRow.LayoutParams params = new TableRow.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT,
				TableRow.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		text.setLayoutParams(params);
		text.setTextColor(Color.WHITE);
		return text;
	}
	
	@Override
    public Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_SHOW_DETAILS_EXAM:
                return ConfirmationDialog.create(this, id,
                        R.string.dialog_tittle_label_exam, examDetails,
                        R.string.dialog_confirm_response_has_errors_button,
                        new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
        }

        return super.onCreateDialog(id);
    }

	private void setAllViewsAs(int visibilityMode) {
		// TODO Auto-generated method stub
		for (int i = 0; i < ((ViewGroup) tableView).getChildCount(); ++i) {
			View nextChild = ((ViewGroup) tableView).getChildAt(i);
			if (nextChild instanceof ExamenTableRow) {
				nextChild.setVisibility(visibilityMode);
			}
		}
	}
	
	public void actionBtn(View view){
		Button theButton = (Button) view;
		if (String.valueOf(theButton.getText()).equalsIgnoreCase(getString(R.string.activity_ins_exam_btn_ver))){
			showDialog(DIALOG_SHOW_DETAILS_EXAM);
		} else {
			
		}
	}

	public static void showExamsView(Context context, String link) {
		Intent intent = new Intent(context, IncsripcionAExamen.class);
		intent.putExtra(LINK_EXTRA, link);
		context.startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			Button theButton = (Button) findViewById(R.id.btn_activity);
			theButton.setText(R.string.activity_ins_exam_btn_ins);
			for (int i = 0; i < ((ViewGroup) tableView).getChildCount(); ++i) {
				View nextChild = ((ViewGroup) tableView).getChildAt(i);
				if (nextChild instanceof ExamenTableRow) {
					ExamenTableRow row = (ExamenTableRow) nextChild;
					if (pos+1 == i){
						row.setVisibility(View.VISIBLE);
						String textToPut = getString(R.string.activity_ins_exam_btn_ver);
						examDetails = row.getExamen().getEstado();
						if (row.getExamen().getEstado().equalsIgnoreCase(getString(R.string.activity_ins_exam_to_compare))){
							textToPut = getString(R.string.activity_ins_exam_btn_ins);
						}
						theButton.setText(textToPut);
					} else {
						row.setVisibility(View.GONE);
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
