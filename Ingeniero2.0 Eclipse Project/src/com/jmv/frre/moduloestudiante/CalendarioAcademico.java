package com.jmv.frre.moduloestudiante;

import com.jmv.frre.moduloestudiante.activities.sysacad.SysacadActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;

public class CalendarioAcademico extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendario_academico);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calendario_academico, menu);
		return true;
	}

	public static void showHome(Context context) {
		Intent intent = new Intent(context, CalendarioAcademico.class);
		context.startActivity(intent);
	}


}
