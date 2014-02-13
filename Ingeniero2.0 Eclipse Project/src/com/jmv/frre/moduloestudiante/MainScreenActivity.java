package com.jmv.frre.moduloestudiante;

import java.util.Date;

import com.google.common.base.Function;
import com.jmv.frre.moduloestudiante.activities.calendar.CalendarioAcademico;
import com.jmv.frre.moduloestudiante.activities.sysacad.ConectionRequired;
import com.jmv.frre.moduloestudiante.activities.sysacad.LinkActivity;
import com.jmv.frre.moduloestudiante.activities.sysacad.LoginActivity;
import com.jmv.frre.moduloestudiante.activities.sysacad.SysacadActivity;
import com.jmv.frre.moduloestudiante.net.HTMLParser;
import com.jmv.frre.moduloestudiante.net.SessionKeyGetter;
import com.jmv.frre.moduloestudiante.utils.FRReUtils;
import com.jmv.frre.moduloestudiante.utils.Utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainScreenActivity extends LinkActivity {

	public static String CURRENT_ID = "";
	
	private String loggedInUserName;
	private String loggedInUserPassword;

	private TextView sessionStatusText;
	private Button sessionStatusIconOk;
	private Button sessionStatusIconBad;
	private SessionChecker sessionChecker;

	private TextView sysacadMobileStatusUsr;

	private TextView currentTime;

	private TextView current_session_key;

	private Button button_sysacad;

	private Button button_calendar;

	private TextView current_time_label;

	private TextView current_session_key_label;

	private View delete_current_profile;
	
	public static boolean USE_SYSACAD = false;

	public static void showHome(Context parent) {
		Intent intent = new Intent(parent, MainScreenActivity.class);
		parent.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_screen);
		
		button_calendar = (Button) findViewById(R.id.calendario);
		
		sessionStatusText = (TextView) findViewById(R.id.session_status);
		sysacadMobileStatusUsr = (TextView) findViewById(R.id.current_user);
		current_session_key = (TextView) findViewById(R.id.current_session_key);
		
		button_sysacad = (Button) findViewById(R.id.button_sysacad);
		
		current_time_label = (TextView) findViewById(R.id.current_time_label);
		current_session_key_label = (TextView) findViewById(R.id.current_session_key_label);
		
		delete_current_profile = (View) findViewById(R.id.delete_current_profile);
		
		currentTime = (TextView) findViewById(R.id.current_time);
		sessionStatusIconOk = (Button) findViewById(R.id.session_status_icon_ok);
		sessionStatusIconBad = (Button) findViewById(R.id.session_status_icon_bad);
		
		if (USE_SYSACAD){
			if (!isNetworkAvailable()){
				ConectionRequired.showHome(this);
				finish();
				return;
			}
			
			loggedInUserName = getIntent().getStringExtra(
					Utils.PREFS_LOGIN_USERNAME_KEY);
			loggedInUserPassword = getIntent().getStringExtra(
					Utils.PREFS_LOGIN_PASSWORD_KEY);
			if (FRReUtils.isNull(loggedInUserName)
					&& FRReUtils.isNull(loggedInUserPassword)) {
				loggedInUserName = Utils.getFromPrefs(this,
						Utils.PREFS_LOGIN_USERNAME_KEY);
				loggedInUserPassword = Utils.getFromPrefs(this,
						Utils.PREFS_LOGIN_PASSWORD_KEY);
				if (FRReUtils.isNull(loggedInUserName)
						&& FRReUtils.isNull(loggedInUserPassword)) {
					// goto login page
					Intent intent = new Intent(this, LoginActivity.class);
					startActivity(intent);
					finish();
					return;
				}
			} else {
				new SaveCredentialsTask().execute(loggedInUserName,loggedInUserPassword);
			}
			
			getNewToken();
			
		} else {
			
			sessionStatusText.setVisibility(View.GONE);
			sysacadMobileStatusUsr.setVisibility(View.GONE);
			current_session_key.setVisibility(View.GONE);			
			current_session_key_label.setVisibility(View.GONE);
			current_time_label.setVisibility(View.GONE);
			currentTime.setVisibility(View.GONE);
			sessionStatusIconOk.setVisibility(View.GONE);
			sessionStatusIconBad.setVisibility(View.GONE);
			delete_current_profile.setVisibility(View.GONE);
		}
		
		
	}

	private boolean isNetworkAvailable() {
	     ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	     NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	     return activeNetworkInfo != null; 
	}
	
	private void getNewToken() {
		
		setSessionCheckerStatus(false);
		button_sysacad.setEnabled(false);
		
		Function<String, Void> afterLogin = new Function<String, Void>() {
			@Override
			public Void apply(String response) {
				HTMLParser parser = HTMLParser.getParserFor(response);
				if (!checkForErrors(parser, response, MainScreenActivity.this)) {
					CURRENT_ID = parser.getSessionID();
					setSessionCheckerStatus(true);
					if (sessionChecker!=null){
						sessionChecker.cancel();
					}
					
					button_sysacad.setEnabled(true);
					sessionChecker = null;
					sessionChecker = new SessionChecker(3*60000, 1000);
					sessionChecker.start();
				}
				return null;
			}
		};
		
		new SessionKeyGetter(this, scraper, afterLogin).execute(loggedInUserName, loggedInUserPassword);
	}

	public void deleteCurrentProfile(View view){
		LoginActivity.resetUser(this);
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
	
	
	public void about(View view){
		showDialog(DIALOG_SHOW_ABOUT);
	}
	
	private void setSessionCheckerStatus(boolean b) {
		if (b){
			sessionStatusText.setText(R.string.sysacad_mobile_status_ready);
			sysacadMobileStatusUsr.setText(CURRENT_ID.equalsIgnoreCase("")?"":CURRENT_ID.substring(3, CURRENT_ID.indexOf("-")));
			sysacadMobileStatusUsr.setVisibility(View.VISIBLE);
			
			current_session_key.setText(CURRENT_ID.equalsIgnoreCase("")?"":CURRENT_ID.substring(CURRENT_ID.indexOf("-")+1));
			current_session_key.setVisibility(View.VISIBLE);
			
			sessionStatusIconBad.setVisibility(View.GONE);
			sessionStatusIconOk.setVisibility(View.VISIBLE);
		} else {
			
			sessionStatusText.setText(R.string.sysacad_mobile_status_getting_token);
			
			current_session_key.setText(R.string.sysacad_mobile_status_key);
			currentTime.setText(R.string.sysacad_mobile_status_time);
			
			sysacadMobileStatusUsr.setVisibility(View.GONE);
			current_session_key.setVisibility(View.GONE);
			sessionStatusIconBad.setVisibility(View.VISIBLE);
			sessionStatusIconOk.setVisibility(View.GONE);
		}
	}
	
	

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	protected void onPause() {
		if (sessionChecker!=null && USE_SYSACAD){
			this.sessionChecker.cancel();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (sessionChecker!=null && USE_SYSACAD){
			getNewToken();
		}
		super.onResume();
	}

	@Override
	protected void onStop() {
		if (sessionChecker!=null && USE_SYSACAD){
			this.sessionChecker.start();
		}
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}

	public class SessionChecker extends CountDownTimer {

		public SessionChecker(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			getNewToken();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			Date d = new Date(millisUntilFinished);
			currentTime.setText(Utils.DATE_HOUR_FORMATTER.format(d));
		}

	}
	
	public class SaveCredentialsTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			Utils.saveToPrefs(MainScreenActivity.this,
					Utils.PREFS_LOGIN_USERNAME_KEY, params[0]);
			Utils.saveToPrefs(MainScreenActivity.this,
					Utils.PREFS_LOGIN_PASSWORD_KEY, params[1]);
			return true;
		}

	}
}
