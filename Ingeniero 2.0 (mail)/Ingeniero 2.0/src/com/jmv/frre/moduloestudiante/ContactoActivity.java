package com.jmv.frre.moduloestudiante;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class ContactoActivity extends Activity {

	private View uneteView;
	private View silvanaView;
	private View justoView;
	private View carlosView;
	private Spinner spinner;
	
	private ArrayList<View> views;
	private String to = "justomiguelvargas@gmail.com";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacto);
		
		this.uneteView = findViewById(R.id.unete_container);
		this.justoView = findViewById(R.id.justo_container);
		this.carlosView = findViewById(R.id.carlos_container);
		this.silvanaView = findViewById(R.id.sil_container);
		
		this.spinner = (Spinner) findViewById(R.id.editTextSubject);
		spinner.setOnItemSelectedListener(spinnerListenerSubject);
		spinner.setSelection(0);
		spinner.setFocusable(true);
		spinner.requestFocus();
		
		views = new ArrayList<View>();
		views.add(uneteView);
		views.add(justoView);
		views.add(carlosView);
		views.add(silvanaView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	public void makeQueryJusto(View view){
		String subject = "[Consulta]";
		  String message = "";

		  Intent email = new Intent(Intent.ACTION_SEND);
		  email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
		  email.putExtra(Intent.EXTRA_SUBJECT, subject);
		  email.putExtra(Intent.EXTRA_TEXT, message);

		  //need this to prompts email client only
		  email.setType("message/rfc822");

		  startActivity(Intent.createChooser(email, "Elegi tu cliente de E-mail:"));
	}
	
	public static void showHome(Context context) {
		Intent intent = new Intent(context, ContactoActivity.class);
		context.startActivity(intent);
	}
	
	private AdapterView.OnItemSelectedListener spinnerListenerSubject = new AdapterView.OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			to = spinner.getItemAtPosition(pos).toString().split("-")[1].trim();
			
			View viewToShow = justoView;
			if (pos == 1){
				viewToShow = carlosView;
			} else if (pos == 2){
				viewToShow = silvanaView;
			} else if (pos == 3){
				viewToShow = uneteView;
			}
			
			setVisible(viewToShow);
		}

		private void setVisible(View viewToShow) {
			for (View viewToHide : views) {
				if (!viewToHide.equals(viewToShow)){
					viewToHide.setVisibility(View.GONE);
				}
			}
			viewToShow.setVisibility(View.VISIBLE);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}

	};

}
