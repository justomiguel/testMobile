package com.swacorp.oncallpager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ConfigurationInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.Build;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.jmv.frre.moduloestudiante.AdsActivity;
import com.jmv.frre.moduloestudiante.AppRater;
import com.jmv.frre.moduloestudiante.MapActivity;
import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.Account;
import com.jmv.frre.moduloestudiante.AgregarEventoActivity;
import com.jmv.frre.moduloestudiante.ContactoActivity;
import com.jmv.frre.moduloestudiante.HorariosCursado;
import com.jmv.frre.moduloestudiante.K9;
import com.jmv.frre.moduloestudiante.Preferences;
import com.jmv.frre.moduloestudiante.K9.SplitViewMode;
import com.jmv.frre.moduloestudiante.SendNotificationActivity;
import com.jmv.frre.moduloestudiante.activities.calendar.CalendarioAcademico;
import com.jmv.frre.moduloestudiante.activities.sysacad.SysacadActivity;
import com.jmv.frre.moduloestudiante.activity.Accounts;
import com.jmv.frre.moduloestudiante.activity.ConfirmationDialog;
import com.jmv.frre.moduloestudiante.activity.MessageList;
import com.jmv.frre.moduloestudiante.activity.setup.AccountSettings;
import com.jmv.frre.moduloestudiante.activity.setup.AccountSetupBasicsSwat;
import com.jmv.frre.moduloestudiante.activity.setup.Prefs;
import com.jmv.frre.moduloestudiante.activity.setup.WelcomeMessage;
import com.jmv.frre.moduloestudiante.controller.MessagingController;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

public class MainActivity extends AdsActivity {

	public static final boolean DEBUG = false;

	private final static int DIALOG_CONFIRM_DELETE = 1;
	private final static int DIALOG_CONFIRM_CHANGE = 2;

	private static final String EXTRA_ACCOUNT = "account";
	public static final String EXTRA_STARTUP = "startup";
	private static final String EXTRA_FOLDER = "folder";
	private static final String FROM_SPLASH = "FROM_SPLASH";

	private Button setProfilesBtn;
	private Button mailsBtn;
	private Button settingsBtn;
	private Button disable_profile;

	private View mLoginStatusView;
	private View mLoginFormView;

	private Button changeProfilesbtn;

	private ImageView imageView;

	private Button send_notification_btn;

	public static boolean USE_SYSACAD = true;

	public static void showHome(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}

	public static void showHomeAfterPresentation(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra(FROM_SPLASH, "true");
		context.startActivity(intent);
	}

	public Intent actionHandleFolderIntent(Context context) {
		Intent intent = new Intent(context, MessageList.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		Account account = Preferences.getPreferences(this).getDefaultAccount();
		intent.putExtra(EXTRA_ACCOUNT, account.getUuid());
		intent.putExtra(EXTRA_FOLDER, "INBOX");
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity);

		addAdds(R.id.add_place);
		
		AppRater.app_launched(this);
		
		Account[] accounts = Preferences.getPreferences(this).getAccounts();

		setInitialPreferences();

		Intent intent = getIntent();
		
		/*if (accounts.length < 1 && intent.getStringExtra(FROM_SPLASH) == null) {
			WelcomeMessage.showWelcomeMessage(this);
			finish();
			return;
		}*/

		imageView = (ImageView) findViewById(R.id.imageView);
		
		//checkProdLayer = (LinearLayout) findViewById(R.id.check_prod_layer);
		//checkProdLayer.setVisibility(View.VISIBLE);
						
		setProfilesBtn = (Button) findViewById(R.id.button_profile);
		
		mailsBtn = (Button) findViewById(R.id.btn_mails);
		settingsBtn = (Button) findViewById(R.id.btn_settings);
		send_notification_btn = (Button) findViewById(R.id.send_notification_btn);
		disable_profile = (Button) findViewById(R.id.disable_profile);

		mLoginStatusView = findViewById(R.id.login_status);
		mLoginFormView = findViewById(R.id.login_form);

	}

	private void setInitialPreferences() {
		SharedPreferences preferences = Preferences.getPreferences(this)
				.getPreferences();
		K9.setK9Language("en");
		K9.setMobileOptimizedLayout(true);
		K9.setGesturesEnabled(true);
		K9.setMessageViewReturnToList(true);
		K9.setThreadedViewEnabled(false);
		K9.setSplitViewMode(SplitViewMode.WHEN_IN_LANDSCAPE);
		Editor editor = preferences.edit();
		K9.save(editor);
		editor.commit();
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

	@Override
	public void onResume() {
		super.onResume();
		checkStyleUI();
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
	
	private void checkStyleUI() {
		Account account = Preferences.getPreferences(this).getDefaultAccount();
		
		//setVisibilityToImages(View.GONE);
		
		if (account != null) {
			settingsBtn.setVisibility(View.VISIBLE);
			mailsBtn.setVisibility(View.VISIBLE);
			//changeProfilesIcon.setVisibility(View.VISIBLE);
			send_notification_btn.setVisibility(View.VISIBLE);
			disable_profile.setVisibility(View.VISIBLE);
		} else {
			disable_profile.setVisibility(View.GONE);
			send_notification_btn.setVisibility(View.GONE);
			imageView.setVisibility(View.VISIBLE);
			settingsBtn.setVisibility(View.GONE);
			mailsBtn.setVisibility(View.GONE);
			//changeProfilesIcon.setVisibility(View.GONE);
		}
	}

	private void setVisibilityToImages(int gone) {
		imageView.setVisibility(gone);
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
		// automaticgetTheally handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}

	public void showEmails(View view) {
		Account account = Preferences.getPreferences(this).getDefaultAccount();
		if (account != null) {
			Intent intent = actionHandleFolderIntent(this);
			startActivity(intent);
		} else {
			AccountSetupBasicsSwat.actionNewAccount(this);
			finish();
		}
	}
	
	public void sendNotification(View view) {
		SendNotificationActivity.showSendNotificationActivityHome(this);
	}

	public void deleteCurrentProfile(View view) {
		showDialog(DIALOG_CONFIRM_DELETE);
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
	
	public void changeGeneralSettings(View view) {
		Account mAccount = Preferences.getPreferences(this).getDefaultAccount();
		AccountSettings.actionSettings(this, mAccount);
	}

	public void changeGlobalSettings(View view) {
		Prefs.actionPrefs(this);
	}

	@Override
	public Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_CONFIRM_DELETE:
			return ConfirmationDialog.create(this, id,
					R.string.dialog_confirm_delete_account_title, "",
					R.string.dialog_confirm_delete_confirm_button,
					R.string.dialog_confirm_delete_cancel_button,
					new Runnable() {
						@Override
						public void run() {
							deleteAccount(false);
						}
					}, new Runnable() {
						@Override
						public void run() {

						}
					});
		case DIALOG_CONFIRM_CHANGE:
			return ConfirmationDialog.create(this, id,
					R.string.dialog_confirm_change_account_title, "",
					R.string.dialog_confirm_delete_confirm_button,
					R.string.dialog_confirm_delete_cancel_button,
					new Runnable() {
						@Override
						public void run() {
							deleteAccount(true);
						}
					}, new Runnable() {
						@Override
						public void run() {

						}
					});
		}

		return super.onCreateDialog(id);
	}

	public void setProfile(View view) {
		Account realAccount = Preferences.getPreferences(this)
				.getDefaultAccount();
		if (realAccount != null) {
			showDialog(DIALOG_CONFIRM_CHANGE);
		} else {
			Intent intent = new Intent(this, Accounts.class);
			intent.putExtra(EXTRA_STARTUP, true);
			startActivity(intent);
		}

	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class ChangeProfileTask extends AsyncTask<Boolean, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Boolean... params) {
			Account realAccount = Preferences.getPreferences(MainActivity.this)
					.getDefaultAccount();
			try {
				realAccount.getLocalStore().delete();
			} catch (Exception e) {
				// Ignore, this may lead to localStores on sd-cards that
				// are currently not inserted to be left
			}
			MessagingController.getInstance(getApplication())
					.notifyAccountCancel(MainActivity.this, realAccount);
			Preferences.getPreferences(MainActivity.this).deleteAccount(
					realAccount);
			K9.setServicesEnabled(MainActivity.this);
			try {
				// Simulate network access.
				Thread.sleep(2500);
			} catch (InterruptedException e) {

			}
			return params[0];
		}

		@Override
		protected void onPostExecute(final Boolean shouldChooseNewAccount) {
			checkStyleUI();
			showProgress(false);
			if (shouldChooseNewAccount) {
				AccountSetupBasicsSwat.actionNewAccount(MainActivity.this);
			}
		}

		@Override
		protected void onCancelled() {
			checkStyleUI();
			showProgress(false);
		}
	}

	public void deleteAccount(boolean shouldChooseNewAccount) {
		showProgress(true);
		new ChangeProfileTask().execute(shouldChooseNewAccount);
	}
}
