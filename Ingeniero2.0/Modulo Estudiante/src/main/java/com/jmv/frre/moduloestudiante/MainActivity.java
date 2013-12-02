package com.jmv.frre.moduloestudiante;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jmv.frre.moduloestudiante.constants.HomePageLinks;
import com.jmv.frre.moduloestudiante.net.HTMLParser;
import com.jmv.frre.moduloestudiante.net.HTTPScraper;
import com.jmv.frre.moduloestudiante.utils.FRReUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import android.view.ViewGroup.LayoutParams;

public class MainActivity extends ActionBarActivity {

    public static final String CONTEXT = "context";

    private View mHomeFormView;
    private View mLoginStatusView;
    private TextView mHomeStatusMessageView;

    private NetworkTask mNetworkTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mHomeFormView = findViewById(R.id.home_form);
        mLoginStatusView = findViewById(R.id.loading_content_status);

        mHomeFormView.setVisibility(View.GONE);

        init();

    }

    private void init() {
        String loggedInUserName = getIntent().getStringExtra(Utils.PREFS_LOGIN_USERNAME_KEY);
        String loggedInUserPassword = getIntent().getStringExtra(Utils.PREFS_LOGIN_PASSWORD_KEY);
        if (FRReUtils.isNull(loggedInUserName) && FRReUtils.isNull(loggedInUserPassword)){
            loggedInUserName = Utils.getFromPrefs(MainActivity.this, Utils.PREFS_LOGIN_USERNAME_KEY);
            loggedInUserPassword = Utils.getFromPrefs(MainActivity.this, Utils.PREFS_LOGIN_PASSWORD_KEY);
            if (FRReUtils.isNull(loggedInUserName) && FRReUtils.isNull(loggedInUserPassword)){
                //goto login page
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return;
            }
        } else {
            new SaveCredentialsTask().execute(loggedInUserName, loggedInUserPassword);
        }

        mHomeStatusMessageView = (TextView) findViewById(R.id.loading_status_message);

        loadContent(loggedInUserName, loggedInUserPassword);
    }

    @Override
     protected void onResume() {
        super.onResume();
        //init();
    }

    private void loadContent(String loggedInUserName, String loggedInUserPassword) {
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        mHomeStatusMessageView.setText(R.string.loadin_content_progress);
        showProgress(true);
        mNetworkTask = new NetworkTask(this);
        mNetworkTask.execute(loggedInUserName, loggedInUserPassword);
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

            mHomeFormView.setVisibility(View.VISIBLE);
            mHomeFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mHomeFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mHomeFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Utils.saveToPrefs(MainActivity.this, Utils.PREFS_LOGIN_USERNAME_KEY, null);
            Utils.saveToPrefs(MainActivity.this, Utils.PREFS_LOGIN_PASSWORD_KEY, null);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SaveCredentialsTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Utils.saveToPrefs(MainActivity.this, Utils.PREFS_LOGIN_USERNAME_KEY, params[0]);
            Utils.saveToPrefs(MainActivity.this, Utils.PREFS_LOGIN_PASSWORD_KEY, params[1]);
            return true;
        }

    }

    public class ButtonNameComparator implements Comparator<Button>{

        @Override
        public int compare(Button button, Button button2) {
            Integer btn1 = Integer.valueOf(button.getText().length());
            Integer btn2 = Integer.valueOf(button2.getText().length());
            return btn1.compareTo(btn2);
        }
    }
    public class NetworkTask extends AsyncTask<String, Void, Boolean> {

        private MainActivity parent;

        public NetworkTask(MainActivity parent) {
            this.parent = parent;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("legajo", params[0]));
            pairs.add(new BasicNameValuePair("password", params[1]));
            pairs.add(new BasicNameValuePair("loginbutton", "Ingresar"));

            HTTPScraper scraper = new HTTPScraper();

            String response = scraper.fetchPageHtmlPost("http://sysacadweb.frre.utn.edu.ar/menuAlumno.asp", pairs);

            HTMLParser parser = new HTMLParser(response);
            if (FRReUtils.isEmpty(response) || !parser.succefullyLoggin()){
                Utils.saveToPrefs(MainActivity.this, Utils.PREFS_LOGIN_USERNAME_KEY, null);
                Utils.saveToPrefs(MainActivity.this, Utils.PREFS_LOGIN_PASSWORD_KEY, null);
                Intent intent = new Intent(parent, LoginActivity.class);
                startActivity(intent);
                return false;
            }

            HashMap<HomePageLinks, String> links = parser.getHomeLinks();

            Set<HomePageLinks> linksKeys = links.keySet();

            List<Button> btns = new ArrayList<Button>();
            for(HomePageLinks key : linksKeys){
                final String content = links.get(key);
                Button myButton = new Button(this.parent);
                myButton.setTextColor(Color.WHITE);
                myButton.setText(content);
                myButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doActivity(content);
                    }
                });
                btns.add(myButton);
            }

            Collections.sort(btns, new ButtonNameComparator());

            for(Button btn : btns){
                LinearLayout ll = (LinearLayout)findViewById(R.id.button_place);
                LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                ll.addView(btn, lp);
            }

            return true;
        }

        protected void doActivity(String buttonName){
               buttonName.toString();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mNetworkTask = null;
            showProgress(false);

            if (success) {

            } else {

            }
        }

        @Override
        protected void onCancelled() {
            mNetworkTask = null;
            showProgress(false);
        }

    }
}
