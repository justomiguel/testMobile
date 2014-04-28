package com.swacorp.oncallpager;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.Build;
import android.widget.Toast;

import com.jmv.frre.moduloestudiante.Account;
import com.jmv.frre.moduloestudiante.AdsActivity;
import com.jmv.frre.moduloestudiante.AppRater;
import com.jmv.frre.moduloestudiante.MapActivity;
import com.jmv.frre.moduloestudiante.NotificacionesActivity;
import com.jmv.frre.moduloestudiante.Preferences;
import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.AgregarEventoActivity;
import com.jmv.frre.moduloestudiante.ContactoActivity;
import com.jmv.frre.moduloestudiante.HorariosCursado;
import com.jmv.frre.moduloestudiante.activities.calendar.CalendarioAcademico;
import com.jmv.frre.moduloestudiante.activities.sysacad.SysacadActivity;
import com.jmv.frre.moduloestudiante.activity.Accounts;
import com.jmv.frre.moduloestudiante.activity.setup.Prefs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

public class MainActivity extends AdsActivity {

	public static final boolean DEBUG = false;

	public static final String EXTRA_STARTUP = "startup";

	private View mLoginStatusView;
	private View mLoginFormView;

	public static boolean USE_SYSACAD = true;

	public static void showHome(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity);

		addAdds(R.id.add_place);
		
		AppRater.app_launched(this);
		

	}

	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
							mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					});

		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
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
	
	public void makeQuery(View view){
		ContactoActivity.showHome(this);
	}
	
	public void seeCursado(View view){
		HorariosCursado.showHome(this);
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

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	
	public void getThere(View view) {
		final ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
		if (supportsEs2){
			MapActivity.showHome(this);
		} else {
			Toast.makeText(this, "Tu cel no soporta OpenGl! :S",  Toast.LENGTH_LONG).show();
		}
	}
	
	public void startOnCallHelper(View view) {
		Intent intent = new Intent(this, OnCallHelperActivity.class);
		this.startActivity(intent);
	}
	
	
	public void changeGlobalSettings(View view) {
		Prefs.actionPrefs(this);
	}

	public void setProfile(View view) {
		NotificacionesActivity.showHome(this);

	}
	
}
