package com.jmv.frre.moduloestudiante.activities.sysacad;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.jmv.frre.moduloestudiante.MainScreenActivity;
import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.net.HTMLParser;
import com.jmv.frre.moduloestudiante.net.HTTPScraper;
import com.jmv.frre.moduloestudiante.utils.FRReUtils;
import com.jmv.frre.moduloestudiante.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {
 

    public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
  
    private UserLoginTask mAuthTask = null;

    // Values for email and password at the time of the login attempt.
    private String mlegajo;
    private String mPassword;

    // UI references.
    private EditText mLegajoView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Set up the login form.

        this.setTitle(R.id.activity_login_title);

        mlegajo = getIntent().getStringExtra(EXTRA_EMAIL);
        mLegajoView = (EditText) findViewById(R.id.email);
        mLegajoView.setText(mlegajo);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.loading_status_message);
       
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }


    public void goHome(View view){
    	MainScreenActivity.showHome(this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mLegajoView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mlegajo = mLegajoView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 1) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mlegajo)) {
            mLegajoView.setError(getString(R.string.error_field_required));
            focusView = mLegajoView;
            cancel = true;
        } else {
            try {
                Integer.parseInt(mlegajo);
            } catch (NumberFormatException e){
                mLegajoView.setError(getString(R.string.error_invalid_email));
                focusView = mLegajoView;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            mAuthTask = new UserLoginTask(this);
            mAuthTask.execute(mlegajo, mPassword);
        }
    }

    @Override
    public void onBackPressed() {
        MainScreenActivity.showHome(this);
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
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
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


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<String, Void, String> {

        private LoginActivity parent;

        public UserLoginTask(LoginActivity parent) {
            this.parent = parent;
        }

        @Override
        protected String doInBackground(String... params) {


            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("legajo", params[0]));
            pairs.add(new BasicNameValuePair("password", params[1]));
            pairs.add(new BasicNameValuePair("loginbutton", "Ingresar"));

            HTTPScraper scraper = new HTTPScraper();

            String response = scraper.fetchPageHtmlPost("http://sysacadweb.frre.utn.edu.ar/menuAlumno.asp", pairs);

            HTMLParser parser = HTMLParser.getParserFor(response);
            if (FRReUtils.isEmpty(response) || !parser.succefullyLoggin()){
                return null;
            }
            // TODO: register the new account here.
            return "Justo";
        }

        @Override
        protected void onPostExecute(final String name) {
            mAuthTask = null;
            showProgress(false);

            if (name!=null) {
                Intent intent = new Intent(this.parent, SysacadActivity.class);
                intent.putExtra(Utils.PREFS_LOGIN_USERNAME_KEY, mlegajo);
                intent.putExtra(Utils.PREFS_LOGIN_PASSWORD_KEY, mPassword);
                intent.putExtra(Utils.PREFS_LOGIN_PASSWORD_USER, name);
                startActivity(intent);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


	public static void resetUser(Activity activity) {
		Utils.saveToPrefs(activity,
				Utils.PREFS_LOGIN_USERNAME_KEY, null);
		Utils.saveToPrefs(activity,
				Utils.PREFS_LOGIN_PASSWORD_KEY, null);
		Intent intent = new Intent(activity, LoginActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}
}
