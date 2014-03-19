package com.jmv.frre.moduloestudiante;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SelectProfile extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_profile);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_profile, menu);
		return true;
	}

}
