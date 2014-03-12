package com.jmv.frre.moduloestudiante;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class ContactoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacto);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	public void makeQueryJusto(View view){
		SendEmailActivity.showHome(this, "justomiguelvargas@gmail.com");
	}
	
	public void makeQueryUnete(View view){
		SendEmailActivity.showHome(this, "cet@frre.utn.edu.ar");
	}

	public void makeQueryCarlos(View view){
		SendEmailActivity.showHome(this, "thelinkin3000@gmail.com");
	}
	
	public static void showHome(Context context) {
		Intent intent = new Intent(context, ContactoActivity.class);
		context.startActivity(intent);
	}
}
