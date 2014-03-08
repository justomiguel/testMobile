package com.jmv.frre.moduloestudiante;


import com.jmv.frre.moduloestudiante.activities.calendar.CalendarioAcademico;
import com.jmv.frre.moduloestudiante.activities.sysacad.SysacadActivity;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainScreenActivity extends Activity {

	private Button button_sysacad;
	private Button button_calendar;
	
	public static boolean USE_SYSACAD = true;

	public static void showHome(Context parent) {
		Intent intent = new Intent(parent, MainScreenActivity.class);
		parent.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_screen);
		
		button_sysacad = (Button) findViewById(R.id.button_sysacad);
		button_calendar = (Button) findViewById(R.id.calendario);
	}
	
	public void seeSysacad(View view){
		if (USE_SYSACAD){
			SysacadActivity.showHome(this);
		} else {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://sysacadweb.frre.utn.edu.ar/"));
			startActivity(browserIntent);
		}
	}
	
	public void seeCalendar(View view){
		CalendarioAcademico.showHome(this);
	}
	
	public void addToCalendar(View view){
		AgregarEventoActivity.showAddChangeActivityHome(this);
	}
	
	public void seeCampus(View view){
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://frre.cvg.utn.edu.ar/"));
		startActivity(browserIntent);
	}
	
	public void goUnete(View view){
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pages/UNETE-FRRe/121499131207059"));
		startActivity(browserIntent);
	}
	
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}

	
}
