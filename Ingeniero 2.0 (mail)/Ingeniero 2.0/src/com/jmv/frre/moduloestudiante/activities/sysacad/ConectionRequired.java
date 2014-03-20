package com.jmv.frre.moduloestudiante.activities.sysacad;

import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.R.layout;
import com.jmv.frre.moduloestudiante.R.menu;
import com.jmv.frre.moduloestudiante.utils.Utils;
import com.swacorp.oncallpager.MainActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class ConectionRequired extends Activity {

	public static void showHome(Context parent) {
		Intent intent = new Intent(parent, ConectionRequired.class);
		parent.startActivity(intent);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conection_required);
	}

	 public void goHome(View view){
	    	MainActivity.showHome(this);
	    }
	    
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.conection_required, menu);
		return true;
	}
	
	public void retryLogin(View view){
		Utils.saveToPrefs(ConectionRequired.this,
				Utils.PREFS_LOGIN_USERNAME_KEY, null);
		Utils.saveToPrefs(ConectionRequired.this,
				Utils.PREFS_LOGIN_PASSWORD_KEY, null);
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

}
