package com.jmv.frre.moduloestudiante.activities.sysacad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Function;
import com.jmv.frre.moduloestudiante.MainScreenActivity;
import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.comps.MyButton;
import com.jmv.frre.moduloestudiante.constants.HomePageImages;
import com.jmv.frre.moduloestudiante.constants.HomePageLinks;
import com.jmv.frre.moduloestudiante.net.HTMLParser;
import com.jmv.frre.moduloestudiante.net.NetworkTaskHandler;
import com.jmv.frre.moduloestudiante.net.SessionKeyGetter;
import com.jmv.frre.moduloestudiante.utils.FRReUtils;
import com.jmv.frre.moduloestudiante.utils.Utils;

import java.util.Comparator;
import java.util.Date;

public class SysacadActivity extends LinkActivity {

	String homeLink = "http://sysacadweb.frre.utn.edu.ar/";

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

	private TextView current_time_label;

	private TextView current_session_key_label;

	private View delete_current_profile;

	public static void showHome(Context context) {
		Intent intent = new Intent(context, SysacadActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHomeFormView = findViewById(R.id.home_form);
		mLoginStatusView = findViewById(R.id.loading_content_status);
		mHomeFormView.setVisibility(View.GONE);
		init();
	}

	public void actionBtn(View view) {
		showDialog(DIALOG_SHOW_ABOUT);
	}

	public void actionBtnExams(View view) {
		IncsripcionAExamen.showExamsView(this, homeLink
				+ HomePageLinks.INSCRIPCION_A_EXAMEN.getLink() + "?"
				+ CURRENT_ID);
	}

	public void academicStatus(View view) {
		ExamsMadeActivity.showExamsView(this,
				homeLink + HomePageLinks.EXAMENES.getLink() + "?" + CURRENT_ID);
	}

	private void init() {
		mHomeStatusMessageView = (TextView) findViewById(R.id.loading_status_message);
		mHomeStatusMessageView.setText(R.string.loadin_content_progress);

		showProgress(false);
		
		sessionStatusText = (TextView) findViewById(R.id.session_status);
		sysacadMobileStatusUsr = (TextView) findViewById(R.id.current_user);
		current_session_key = (TextView) findViewById(R.id.current_session_key);

		delete_current_profile = (View) findViewById(R.id.delete_current_profile);

		current_time_label = (TextView) findViewById(R.id.current_time_label);
		current_session_key_label = (TextView) findViewById(R.id.current_session_key_label);

		currentTime = (TextView) findViewById(R.id.current_time);
		sessionStatusIconOk = (Button) findViewById(R.id.session_status_icon_ok);
		sessionStatusIconBad = (Button) findViewById(R.id.session_status_icon_bad);

		if (!isNetworkAvailable()) {
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
			new SaveCredentialsTask().execute(loggedInUserName,
					loggedInUserPassword);
		}

		getNewToken();
	}

	public void about(View view) {
		showDialog(DIALOG_SHOW_ABOUT);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	private void setSessionCheckerStatus(boolean b) {
		if (b) {
			sessionStatusText.setText(R.string.sysacad_mobile_status_ready);
			sysacadMobileStatusUsr.setText(CURRENT_ID.equalsIgnoreCase("") ? ""
					: CURRENT_ID.substring(3, CURRENT_ID.indexOf("-")));
			sysacadMobileStatusUsr.setVisibility(View.VISIBLE);

			current_session_key.setText(CURRENT_ID.equalsIgnoreCase("") ? ""
					: CURRENT_ID.substring(CURRENT_ID.indexOf("-") + 1));
			current_session_key.setVisibility(View.VISIBLE);

			sessionStatusIconBad.setVisibility(View.GONE);
			sessionStatusIconOk.setVisibility(View.VISIBLE);
		} else {

			sessionStatusText
					.setText(R.string.sysacad_mobile_status_getting_token);

			current_session_key.setText(R.string.sysacad_mobile_status_key);
			currentTime.setText(R.string.sysacad_mobile_status_time);

			sysacadMobileStatusUsr.setVisibility(View.GONE);
			current_session_key.setVisibility(View.GONE);
			sessionStatusIconBad.setVisibility(View.VISIBLE);
			sessionStatusIconOk.setVisibility(View.GONE);
		}
	}

	private void getNewToken() {

		setSessionCheckerStatus(false);

		Function<String, Void> afterLogin = new Function<String, Void>() {
			@Override
			public Void apply(String response) {
				HTMLParser parser = HTMLParser.getParserFor(response);
				if (!checkForErrors(parser, response, SysacadActivity.this)) {
					CURRENT_ID = parser.getSessionID();
					setSessionCheckerStatus(true);
					if (sessionChecker != null) {
						sessionChecker.cancel();
					}

					sessionChecker = null;
					sessionChecker = new SessionChecker(3 * 60000, 1000);
					sessionChecker.start();
				}
				return null;
			}
		};

		new SessionKeyGetter(this, scraper, afterLogin).execute(
				loggedInUserName, loggedInUserPassword);
	}

	public void deleteCurrentProfile(View view) {
		LoginActivity.resetUser(this);
	}

	@Override
	protected void onPause() {
		if (sessionChecker != null) {
			this.sessionChecker.cancel();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (sessionChecker != null) {
			getNewToken();
		}
		super.onResume();
	}

	@Override
	protected void onStop() {
		if (sessionChecker != null) {
			this.sessionChecker.start();
		}
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		MainScreenActivity.showHome(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			LoginActivity.resetUser(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class ButtonNameComparator implements Comparator<Button> {

		@Override
		public int compare(Button button, Button button2) {
			Integer btn1 = Integer.valueOf(button.getText().length());
			Integer btn2 = Integer.valueOf(button2.getText().length());
			return btn1.compareTo(btn2);
		}
	}

	protected void doActivity(final MyButton buttonName) {
		switch (buttonName.getEnumLink()) {
		case AVISOS:
			Function<Activity, String> getRequest = makeGetRequest(buttonName
					.getLink());
			Function<String, Void> avisosFunction = new Function<String, Void>() {
				@Override
				public Void apply(String response) {
					HTMLParser parser = HTMLParser.getParserFor(response);
					showProgress(false);
					if (!checkForErrors(parser, response, SysacadActivity.this)) {
						showDialog(DIALOG_SHOW_INFO);
					}
					return null;
				}
			};
			new NetworkTaskHandler(getRequest, avisosFunction).execute();
			break;
		case EXAMENES:
			showProgress(false);
			ExamsMadeActivity.showExamsView(this, buttonName.getLink());
			break;
		case ESTADO_ACADEMICO:
			break;
		case MATERIAS_DEL_PLAN:
			break;
		case INSCRIPCION_A_EXAMEN:
			showProgress(false);

			break;
		case CAMBIO_DE_CONTRASENA:
			break;
		case INSCRIPCION_A_CURSADO:
			break;
		case CORRELATIVIDAD_PARA_RENDIR:
			break;
		case CORRELATIVIDAD_PARA_CURSAR:
			break;
		case CURSADO_NOTAS_ENCUESTAS:
			break;
		case SALIR:
			break;
		}

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
			Utils.saveToPrefs(SysacadActivity.this,
					Utils.PREFS_LOGIN_USERNAME_KEY, params[0]);
			Utils.saveToPrefs(SysacadActivity.this,
					Utils.PREFS_LOGIN_PASSWORD_KEY, params[1]);
			return true;
		}

	}

	protected int getImageByEnum(MyButton btn) {
		return HomePageImages.getLinkByValue(btn.getEnumLink());
	}
	/*
	 * public class NetworkTask extends AsyncTask<String, Void, Boolean> {
	 * 
	 * private SysacadActivity parent; private ArrayList<MyButton> btns;
	 * 
	 * public NetworkTask(SysacadActivity parent) { this.parent = parent; }
	 * 
	 * @Override protected Boolean doInBackground(String... params) {
	 * 
	 * if (btns == null){ HashMap<HomePageLinks, List<String>> links =
	 * parser.getHomeLinks();
	 * 
	 * Set<HomePageLinks> linksKeys = links.keySet();
	 * 
	 * if (links.isEmpty()){ currentError = "Empty Response";
	 * SysacadActivity.showHome(SysacadActivity.this); }
	 * 
	 * String firstLink = links.get(HomePageLinks.AVISOS).get(0); CURRENT_ID =
	 * firstLink.substring(firstLink.indexOf("id="));
	 * 
	 * btns = new ArrayList<MyButton>(); for (HomePageLinks key : linksKeys) {
	 * final String content = links.get(key).get(1); final MyButton myButton =
	 * new MyButton(this.parent); myButton.setEnumLink(key); final String
	 * linkUri = links.get(key).get(0); myButton.setLink(linkUri);
	 * myButton.setText(content); myButton.setEnabled(key.isEnabled());
	 * myButton.setTextColor(key.isEnabled() ? Color.BLACK : Color.GRAY);
	 * myButton.setOnClickListener(new View.OnClickListener() {
	 * 
	 * @Override public void onClick(View view) { doActivity(myButton); } });
	 * btns.add(myButton); }
	 * 
	 * Collections.sort(btns, new ButtonNameComparator());
	 * 
	 * for (MyButton btn : btns) { if (btn.isEnabled()) { LinearLayout layer1 =
	 * new LinearLayout(this.parent);
	 * layer1.setOrientation(LinearLayout.HORIZONTAL);
	 * layer1.setLayoutParams(new LayoutParams( LayoutParams.MATCH_PARENT,
	 * LayoutParams.MATCH_PARENT));
	 * 
	 * ImageView imageView1 = new ImageView(this.parent);
	 * imageView1.setBackgroundResource(getImageByEnum(btn));
	 * 
	 * layer1.addView(imageView1, new LayoutParams( LayoutParams.WRAP_CONTENT,
	 * LayoutParams.WRAP_CONTENT));
	 * 
	 * LinearLayout layer2 = new LinearLayout(this.parent);
	 * layer2.setOrientation(LinearLayout.VERTICAL); layer2.setLayoutParams(new
	 * LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	 * layer2.addView(btn, new LayoutParams( LayoutParams.MATCH_PARENT,
	 * LayoutParams.WRAP_CONTENT));
	 * 
	 * layer1.addView(layer2);
	 * 
	 * LinearLayout ll = (LinearLayout) findViewById(R.id.button_place);
	 * ll.addView(layer1); }
	 * 
	 * }
	 * 
	 * } return true; }
	 * 
	 * @Override protected void onPostExecute(final Boolean success) {
	 * mNetworkTask = null; showProgress(false);
	 * 
	 * if (success) {
	 * 
	 * } else {
	 * 
	 * } }
	 * 
	 * @Override protected void onCancelled() { mNetworkTask = null;
	 * showProgress(false); }
	 * 
	 * }
	 */
}
