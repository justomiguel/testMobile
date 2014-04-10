package com.jmv.frre.moduloestudiante;

import java.util.List;

import com.jmv.frre.moduloestudiante.activities.calendar.CalendarioAcademico;
import com.jmv.frre.moduloestudiante.activities.calendar.StandardImageProgrammatic;
import com.jmv.frre.moduloestudiante.activities.sysacad.IncsripcionAExamen;
import com.jmv.frre.moduloestudiante.comps.ExamenTableRow;
import com.jmv.frre.moduloestudiante.dialogs.ConfirmationDialog;
import com.jmv.frre.moduloestudiante.logic.ExamenIns;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class HorariosCursado extends AdsActivity {

	private static final int DIALOG_SHOW_ERROR = 0;

	private Spinner spinner;
	private Spinner spinnerCarreras;

	private String carreraActual;
	private int anio_actual;

	int imageToShow = 0;

	private SharedPreferences prefs;

	public static void showHome(Context context) {
		Intent intent = new Intent(context, HorariosCursado.class);
		context.startActivity(intent);
	}

	@Override
	public Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_SHOW_ERROR:
			return ConfirmationDialog.create(this, id,
					R.string.dialog_tittle_label,
					"No estan disponibles los horarios aun",
					R.string.dialog_confirm_response_has_errors_button,
					new Runnable() {
						@Override
						public void run() {
							// MainActivity.showHome(LinkActivity.this);
						}
					});

		}

		return super.onCreateDialog(id);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_horarios_cursado);

		addAdds(R.id.hc_add_place);

		prefs = this.getSharedPreferences(
				"horariosCursado_ingeniero_app", 0);

		int carrera = prefs.getInt("carrera", 0);
		int anio = prefs.getInt("anio_save", 0);

		spinner = (Spinner) findViewById(R.id.change_type_spinner);
		spinnerCarreras = (Spinner) findViewById(R.id.change_type_years);

		spinner.setOnItemSelectedListener(spinnerListener);
		spinner.setFocusable(true);
		spinner.requestFocus();

		
		spinnerCarreras.setFocusable(true);
		spinnerCarreras.requestFocus();
		spinnerCarreras.setOnItemSelectedListener(spinnerListenerYears);
		
		spinner.setSelection(carrera);
		spinnerCarreras.setSelection(anio);
		

	}

	public void seeCursadoIMG(View view) {
		if (imageToShow != 0) {
			StandardImageProgrammatic.showHome(this, imageToShow);
		} else {
			showDialog(DIALOG_SHOW_ERROR);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		return true;
	}

	private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			String carrera = spinner.getItemAtPosition(pos).toString();
			carreraActual = carrera;
			int max = 5;
			if (carrera.equalsIgnoreCase("TSP")) {
				max = 2;
			} else if (carrera.equalsIgnoreCase("LAR")) {
				max = 4;
			}

			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt("carrera", pos);
			editor.commit();
			
			ArrayAdapter<String> adp1 = new ArrayAdapter<String>(
					HorariosCursado.this, android.R.layout.simple_list_item_1,
					trsnformToArray(max));
			adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerCarreras.setAdapter(adp1);
			
			int anio = prefs.getInt("anio_save", 0);
			spinnerCarreras.setSelection(max >= anio?anio:0);
		}

		private String[] trsnformToArray(int max) {
			String[] names = new String[max];
			for (int i = 0; i < names.length; i++) {
				names[i] = "Anio " + (i + 1);
			}
			return names;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}

	};

	private AdapterView.OnItemSelectedListener spinnerListenerYears = new AdapterView.OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			String year = spinnerCarreras.getItemAtPosition(pos).toString()
					.split(" ")[1];
			anio_actual = Integer.parseInt(year);
			
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt("anio_save", pos);
			editor.commit();
			
			// if (carreraActual.equalsIgnoreCase("ISI")){
			imageToShow = HorariosCursado.this.getResources().getIdentifier(
					carreraActual.toLowerCase() + anio_actual, "drawable",
					HorariosCursado.this.getPackageName());
			// }
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}

	};
}
