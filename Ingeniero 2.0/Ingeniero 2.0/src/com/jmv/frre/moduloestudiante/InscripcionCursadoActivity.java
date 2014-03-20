package com.jmv.frre.moduloestudiante;

import static com.jmv.frre.moduloestudiante.constants.Constants.DELIMITER;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.base.Function;
import com.jmv.frre.moduloestudiante.activities.sysacad.IncsripcionAExamen;
import com.jmv.frre.moduloestudiante.activities.sysacad.LinkActivity;
import com.jmv.frre.moduloestudiante.activities.sysacad.SysacadActivity;
import com.jmv.frre.moduloestudiante.comps.ExamenTableRow;
import com.jmv.frre.moduloestudiante.dialogs.ConfirmationDialog;
import com.jmv.frre.moduloestudiante.logic.ExamenIns;
import com.jmv.frre.moduloestudiante.net.HTMLParser;
import com.jmv.frre.moduloestudiante.net.NetworkTaskHandler;
import com.jmv.frre.moduloestudiante.utils.Utils;

import android.os.Bundle;
import android.app.Activity;
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

public class InscripcionCursadoActivity extends LinkActivity {

	private static final String LINK = "http://sysacadweb.frre.utn.edu.ar/cursosCursado.asp?";
	private static final String DELETE_LINK = "http://sysacadweb.frre.utn.edu.ar/";

	private Spinner spinner;
	private TableLayout tableView;
	protected String myCurrentResponse;
	private String examDetails;
	private ExamenIns materiaToInscribirse;


	private TableLayout tableAlreadyInscView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inscripcion_cursado);

		mHomeFormView = findViewById(R.id.home_form);
		mLoginStatusView = findViewById(R.id.loading_content_status);

		// reference the table layout
		tableView = (TableLayout) findViewById(R.id.RHE);
		tableAlreadyInscView = (TableLayout) findViewById(R.id.tablaInscriptas);

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
				HTMLParser parser = HTMLParser.getParserFor(response);
				showProgress(false);
				if (!checkForErrors(parser, response, InscripcionCursadoActivity.this)) {
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
				if (exa instanceof ExamenTableRow) {
					ExamenTableRow currentExa = (ExamenTableRow) exa;
					ExamenIns theExa = currentExa.getExamen();
					if (!theExa.getEstado().equalsIgnoreCase(
							getString(R.string.activity_ins_exam_to_compare))) {
						tableAlreadyInscView.addView(
								getTableRowViewAlreadyIns(theExa),
								new TableLayout.LayoutParams(
										LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT));
					}
				}
			}
		}

		if (((ViewGroup) tableAlreadyInscView).getChildCount() == 1) {
			tableAlreadyInscView.addView(getTableRowViewAlreadyIns(),
					new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
		}
		List<ExamenIns> examnesAIns = new ArrayList<ExamenIns>();

		for (int i = 0; i < ((ViewGroup) tableView).getChildCount(); ++i) {
			View nextChild = ((ViewGroup) tableView).getChildAt(i);
			if (nextChild instanceof ExamenTableRow) {
				examnesAIns.add(((ExamenTableRow) nextChild).getExamen());
			}
		}

		ArrayAdapter<String> adp1 = new ArrayAdapter<String>(
				InscripcionCursadoActivity.this, android.R.layout.simple_list_item_1,
				trsnformToArray(examnesAIns));
		adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adp1);

		setAllViewsAs(View.GONE);
	}

	private View getTableRowViewAlreadyIns() {
		ExamenTableRow newRow = new ExamenTableRow(this, null);

		newRow.addView(getTextForTable(String.valueOf("--")));
		newRow.addView(getTextForTable(String.valueOf("--")));

		newRow.setLayoutParams(new TableLayout.LayoutParams(
				TableLayout.LayoutParams.MATCH_PARENT,
				TableLayout.LayoutParams.WRAP_CONTENT));

		return newRow;
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
			if (data.length < 6){
				examen = new ExamenIns(Integer.parseInt(data[0]), data[1], data[2],"",Integer.parseInt(data[3]),
						Integer.parseInt(data[4]));
			} else {
				examen = new ExamenIns(Integer.parseInt(data[0]), data[1], data[2],data[3],
					Integer.parseInt(data[4]), Integer.parseInt(data[5]));
			}
		} catch (Exception e) {
			return null;
		}

		newRow = new ExamenTableRow(this, examen);

		newRow.addView(getTextForTable(String.valueOf(examen.getAnio())));
		String textToPut = getString(R.string.activity_ins_exam_status_inscripto);
		if (examen.getEstado().equalsIgnoreCase(
				getString(R.string.activity_ins_exam_to_compare))) {
			textToPut = getString(R.string.activity_ins_exam_status_no_inscripto);
		}
		newRow.addView(getTextForTable(textToPut));
		newRow.addView(getTextForTable(String.valueOf(examen.getCodigo())));

		newRow.setLayoutParams(new TableLayout.LayoutParams(
				TableLayout.LayoutParams.MATCH_PARENT,
				TableLayout.LayoutParams.WRAP_CONTENT));

		return newRow;
	}

	private View getTableRowViewAlreadyIns(ExamenIns exam) {

		ExamenTableRow newRow = new ExamenTableRow(this, exam);

		newRow.addView(getTextForTable(String.valueOf(exam.getPlan())));
		newRow.addView(getTextForTable(String.valueOf(exam.getMateria())));
		newRow.addView(getTextForTable(String.valueOf(exam.getEstado().replaceAll("Eliminar", ""))));
		
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
		case DIALOG_SHOW_INSCRIBIRSE_EXAM:
			return ConfirmationDialog.create(this, id,
					R.string.dialog_tittle_label_ins_exam,
					getString(R.string.dialog_tittle_label_ins_exam_details)
							+ materiaToInscribirse.getMateria(),
					R.string.dialog_confirm_response_has_errors_button,
					new Runnable() {
						@Override
						public void run() {
							inscribirseAExamen();
						}
					});

		case DIALOG_SHOW_DELETE_EXAM:
			return ConfirmationDialog.create(this, id,
					R.string.dialog_tittle_label_ins_exam,
					getString(R.string.dialog_tittle_label_ins_exam_delete_details)
							+ materiaToInscribirse.getMateria(),
					R.string.dialog_confirm_response_has_errors_button,
					new Runnable() {
						@Override
						public void run() {
							borrarExamen(examDetails);
						}
					});
		case DIALOG_SHOW_DELETE_EXAM_EMPTY:
			return ConfirmationDialog.create(this, id,
					R.string.dialog_tittle_label,
					 materiaToInscribirse.getMateria() +" No se puede Eliminar! :S",
					R.string.dialog_confirm_response_has_errors_button,
					new Runnable() {
						@Override
						public void run() {
							//borrarExamen(examDetails);
						}
					});
		case DIALOG_SHOW_INSCRIBIRSE_CONFIRM:
			return ConfirmationDialog
					.create(this,
							id,
							R.string.dialog_tittle_label_ins_exam_confirm,
							getString(R.string.dialog_tittle_label_ins_exam_confirm_details),
							R.string.dialog_confirm_response_has_errors_button,
							new Runnable() {
								@Override
								public void run() {
									SysacadActivity
											.showHome(InscripcionCursadoActivity.this);
								}
							});
		case DIALOG_SHOW_DELETE_CONFIRM:
			return ConfirmationDialog
					.create(this,
							id,
							R.string.dialog_tittle_label_ins_delete_exam_confirm,
							getString(R.string.dialog_tittle_label_ins_exam_confirm_delete_details),
							R.string.dialog_confirm_response_has_errors_button,
							new Runnable() {
								@Override
								public void run() {
									SysacadActivity
											.showHome(InscripcionCursadoActivity.this);
								}
							});
		}

		return super.onCreateDialog(id);
	}

	protected void inscribirseAExamen() {

		final String link = LINK + SysacadActivity.CURRENT_ID + "&plan="
				+ materiaToInscribirse.getPlan() + "&materia="
				+ materiaToInscribirse.getCodigo();

		Function<Activity, String> inscribirse = new Function<Activity, String>() {
			@Override
			public String apply(Activity context) {
				String response = scraper.fecthHtmlGet(link);
				return response;
			}
		};

		Function<String, Void> examenesFunction = new Function<String, Void>() {
			@Override
			public Void apply(String response) {
				HTMLParser parser = HTMLParser.getParserFor(response);
				showProgress(false);
				if (!checkForErrors(parser, response, InscripcionCursadoActivity.this)) {

					Function<Activity, String> inscribirsePosta = new Function<Activity, String>() {
						@Override
						public String apply(Activity context) {
							List<NameValuePair> pairs = new ArrayList<NameValuePair>();
							pairs.add(new BasicNameValuePair("plan", String
									.valueOf(materiaToInscribirse.getPlan())));
							pairs.add(new BasicNameValuePair("materia", String
									.valueOf(materiaToInscribirse.getCodigo())));
							pairs.add(new BasicNameValuePair("seleccion", "1"));
							pairs.add(new BasicNameValuePair("inscribirse",
									"Inscribirse"));

							String myResponse = scraper
									.fetchPageHtmlPost(
											"http://sysacadweb.frre.utn.edu.ar/inscripcionCursado.asp",
											pairs);

							return myResponse;
						}
					};

					Function<String, Void> responseFromInscripcion = new Function<String, Void>() {
						@Override
						public Void apply(String response) {
							HTMLParser parser = HTMLParser
									.getParserFor(response);
							showProgress(false);
							if (!checkForErrors(parser, response,
									InscripcionCursadoActivity.this)) {
								showDialog(DIALOG_SHOW_INSCRIBIRSE_CONFIRM);
							}
							return null;
						}
					};

					showProgress(true);
					new NetworkTaskHandler(inscribirsePosta,
							responseFromInscripcion).execute();

				}
				return null;
			}
		};

		showProgress(true);
		new NetworkTaskHandler(inscribirse, examenesFunction).execute();

	}

	protected void borrarExamen(String details) {

		if (materiaToInscribirse.getUrl().length() == 0){
			showDialog(DIALOG_SHOW_DELETE_EXAM_EMPTY);
			return;
		}
		final String link = DELETE_LINK + materiaToInscribirse.getUrl();

		Function<Activity, String> inscribirse = new Function<Activity, String>() {
			@Override
			public String apply(Activity context) {
				String response = scraper.fecthHtmlGet(link);
				return response;
			}
		};

		Function<String, Void> examenesFunction = new Function<String, Void>() {
			@Override
			public Void apply(String response) {
				HTMLParser parser = HTMLParser.getParserFor(response);
				showProgress(false);
				if (!checkForErrors(parser, response, InscripcionCursadoActivity.this)) {

					showDialog(DIALOG_SHOW_DELETE_CONFIRM);

				}
				return null;
			}
		};

		showProgress(true);
		new NetworkTaskHandler(inscribirse, examenesFunction).execute();

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

	public void actionBtn(View view) {
		Button theButton = (Button) view;
		if (String.valueOf(theButton.getText()).equalsIgnoreCase(
				getString(R.string.activity_ins_exam_btn_ver))) {
			showDialog(DIALOG_SHOW_DETAILS_EXAM);
		} else {
			showDialog(DIALOG_SHOW_INSCRIBIRSE_EXAM);
		}
	}

	public void actionDeleteBtn(View view) {
		Button theButton = (Button) view;
		showDialog(DIALOG_SHOW_DELETE_EXAM);
	}

	public static void showExamsView(Context context, String link) {
		Intent intent = new Intent(context, InscripcionCursadoActivity.class);
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
			Button deleteButton = (Button) findViewById(R.id.btn_delete_activity);

			theButton.setText(R.string.activity_ins_exam_btn_ins);

			deleteButton.setVisibility(View.GONE);
			deleteButton.setText(R.string.activity_ins_exam_btn_delete_ins);

			for (int i = 0; i < ((ViewGroup) tableView).getChildCount(); ++i) {
				View nextChild = ((ViewGroup) tableView).getChildAt(i);
				if (nextChild instanceof ExamenTableRow) {
					ExamenTableRow row = (ExamenTableRow) nextChild;
					if (pos + 1 == i) {
						row.setVisibility(View.VISIBLE);
						String textToPut = getString(R.string.activity_ins_exam_btn_ver);
						ExamenIns exa = row.getExamen();
						examDetails = exa.getEstado();
						materiaToInscribirse = exa;
						deleteButton.setVisibility(View.VISIBLE);
						if (exa.getEstado()
								.equalsIgnoreCase(
										getString(R.string.activity_ins_exam_to_compare))) {
							deleteButton.setVisibility(View.GONE);
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
