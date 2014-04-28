package com.jmv.frre.moduloestudiante;

import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.K9.SplitViewMode;
import com.jmv.frre.moduloestudiante.activities.sysacad.ConectionRequired;
import com.jmv.frre.moduloestudiante.activities.sysacad.LoginActivity;
import com.jmv.frre.moduloestudiante.activities.sysacad.SysacadActivity;
import com.jmv.frre.moduloestudiante.activities.sysacad.SysacadActivity.SaveCredentialsTask;
import com.jmv.frre.moduloestudiante.activity.Accounts;
import com.jmv.frre.moduloestudiante.activity.ConfirmationDialog;
import com.jmv.frre.moduloestudiante.activity.MessageList;
import com.jmv.frre.moduloestudiante.activity.setup.AccountSettings;
import com.jmv.frre.moduloestudiante.activity.setup.AccountSetupBasicsSwat;
import com.jmv.frre.moduloestudiante.controller.MessagingController;
import com.jmv.frre.moduloestudiante.utils.Utils;
import com.swacorp.oncallpager.MainActivity;
import com.swacorp.oncallpager.OnCallHelperActivity;

import android.R.bool;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NotificacionesActivity extends AdsActivity {

	private final static int DIALOG_CONFIRM_DELETE = 1;
	private final static int DIALOG_CONFIRM_CHANGE = 2;

	private String loggedInUserName;
	private String loggedInUserPassword;
	
	private static final String EXTRA_ACCOUNT = "account";
	public static final String EXTRA_STARTUP = "startup";
	private static final String EXTRA_FOLDER = "folder";
	private static final String FROM_SPLASH = "FROM_SPLASH";

	private Button mailsBtn;
	private Button settingsBtn;
	private Button disable_profile;

	private View mLoginStatusView;
	private View mLoginFormView;

	private Button send_notification_btn;

	public static boolean USE_SYSACAD = true;

	public static void showHome(Context context) {
		Intent intent = new Intent(context, NotificacionesActivity.class);
		context.startActivity(intent);
	}

	public static void showHomeAfterPresentation(Context context) {
		Intent intent = new Intent(context, NotificacionesActivity.class);
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

	private void setInitialPreferences() {
		SharedPreferences preferences = Preferences.getPreferences(this)
				.getPreferences();
		K9.setK9Language("en");
		K9.setMobileOptimizedLayout(true);
		K9.setGesturesEnabled(true);
		K9.setAnimations(true);
		K9.setAutofitWidth(true);
		K9.setMessageListPreviewLines(5);
		K9.setMessageListStars(false);
		K9.setMessageViewReturnToList(true);
		K9.setThreadedViewEnabled(true);
		K9.setSplitViewMode(SplitViewMode.WHEN_IN_LANDSCAPE);
		Editor editor = preferences.edit();
		K9.save(editor);
		editor.commit();
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notificaciones);

		if (!isNetworkAvailable()) {
			ConectionRequired.showHome(this);
			finish();
			return;
		}

		loggedInUserName = getIntent().getStringExtra(
				Utils.PREFS_LOGIN_USERNAME_KEY);
		loggedInUserPassword = getIntent().getStringExtra(
				Utils.PREFS_LOGIN_PASSWORD_KEY);
		if (loggedInUserName == null
				&& loggedInUserPassword == null) {
			loggedInUserName = Utils.getFromPrefs(this,
					Utils.PREFS_LOGIN_USERNAME_KEY);
			loggedInUserPassword = Utils.getFromPrefs(this,
					Utils.PREFS_LOGIN_PASSWORD_KEY);
			if (loggedInUserName == null
					&& loggedInUserPassword == null) {
				// goto login page
				LoginActivity.showHome(this, NotificacionesActivity.class);
				finish();
				return;
			}
		} else {
			new SaveCredentialsTask().execute(loggedInUserName,
					loggedInUserPassword);
		}

		setInitialPreferences();

		Account account = Preferences.getPreferences(this).getDefaultAccount();

		if (account == null) {
			Intent intent = new Intent(this, Accounts.class);
			intent.putExtra(EXTRA_STARTUP, true);
			startActivity(intent);
		}

		
		addAdds(R.id.add_place);

		mailsBtn = (Button) findViewById(R.id.btn_mails);
		settingsBtn = (Button) findViewById(R.id.btn_settings);
		send_notification_btn = (Button) findViewById(R.id.send_notification_btn);
		disable_profile = (Button) findViewById(R.id.disable_profile);

		mLoginStatusView = findViewById(R.id.login_status);
		mLoginFormView = findViewById(R.id.login_form);
		
	}
	
	public class SaveCredentialsTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			Utils.saveToPrefs(NotificacionesActivity.this,
					Utils.PREFS_LOGIN_USERNAME_KEY, params[0]);
			Utils.saveToPrefs(NotificacionesActivity.this,
					Utils.PREFS_LOGIN_PASSWORD_KEY, params[1]);
			return true;
		}

	}

	@Override
	public void onBackPressed() {
		MainActivity.showHome(this);
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
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});

		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	private void checkStyleUI() {
		Account account = Preferences.getPreferences(this).getDefaultAccount();
		boolean withAcc = account != null;
		settingsBtn.setEnabled(withAcc);
		mailsBtn.setEnabled(withAcc);
		send_notification_btn.setEnabled(withAcc);
		disable_profile.setEnabled(withAcc);
	}

	@Override
	public void onResume() {
		super.onResume();
		checkStyleUI();
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

	public void changeGeneralSettings(View view) {
		Account mAccount = Preferences.getPreferences(this).getDefaultAccount();
		AccountSettings.actionSettings(this, mAccount);
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
							MainActivity.showHome(NotificacionesActivity.this);
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

		}

	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class ChangeProfileTask extends AsyncTask<Boolean, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Boolean... params) {
			Account realAccount = Preferences.getPreferences(
					NotificacionesActivity.this).getDefaultAccount();
			try {
				realAccount.getLocalStore().delete();
			} catch (Exception e) {
				// Ignore, this may lead to localStores on sd-cards that
				// are currently not inserted to be left
			}
			MessagingController.getInstance(getApplication())
					.notifyAccountCancel(NotificacionesActivity.this,
							realAccount);
			Preferences.getPreferences(NotificacionesActivity.this)
					.deleteAccount(realAccount);
			K9.setServicesEnabled(NotificacionesActivity.this);
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
				AccountSetupBasicsSwat
						.actionNewAccount(NotificacionesActivity.this);
			}
		}

		@Override
		protected void onCancelled() {
			checkStyleUI();
			showProgress(false);
		}
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class GettingIntoProfileTask extends AsyncTask<Boolean, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Boolean... params) {
			
			try {
				// Simulate network access.
				Thread.sleep(500);
			} catch (InterruptedException e) {

			}finally{
				
			}
			return params[0];
		}

		@Override
		protected void onPostExecute(final Boolean shouldChooseNewAccount) {
			showProgress(false);
		}

		@Override
		protected void onCancelled() {
			showProgress(false);
		}
	}


	public void deleteAccount(boolean shouldChooseNewAccount) {
		showProgress(true);
		new ChangeProfileTask().execute(shouldChooseNewAccount);
	}

}
