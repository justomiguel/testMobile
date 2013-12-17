package com.jmv.frre.moduloestudiante;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Function;
import com.jmv.frre.moduloestudiante.activities.ExamsMadeActivity;
import com.jmv.frre.moduloestudiante.activities.LinkActivity;
import com.jmv.frre.moduloestudiante.comps.MyButton;
import com.jmv.frre.moduloestudiante.constants.HomePageImages;
import com.jmv.frre.moduloestudiante.constants.HomePageLinks;
import com.jmv.frre.moduloestudiante.net.HTMLParser;
import com.jmv.frre.moduloestudiante.net.NetworkTaskHandler;
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

public class MainActivity extends LinkActivity {

    private NetworkTask mNetworkTask = null;

    private String loggedInUserName;
    private String loggedInUserPassword;

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
        loggedInUserName = getIntent().getStringExtra(Utils.PREFS_LOGIN_USERNAME_KEY);
        loggedInUserPassword = getIntent().getStringExtra(Utils.PREFS_LOGIN_PASSWORD_KEY);
        if (FRReUtils.isNull(loggedInUserName) && FRReUtils.isNull(loggedInUserPassword)) {
            loggedInUserName = Utils.getFromPrefs(MainActivity.this, Utils.PREFS_LOGIN_USERNAME_KEY);
            loggedInUserPassword = Utils.getFromPrefs(MainActivity.this, Utils.PREFS_LOGIN_PASSWORD_KEY);
            if (FRReUtils.isNull(loggedInUserName) && FRReUtils.isNull(loggedInUserPassword)) {
                //goto login page
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        } else {
            new SaveCredentialsTask().execute(loggedInUserName, loggedInUserPassword);
        }

        mHomeStatusMessageView = (TextView) findViewById(R.id.loading_status_message);

        loadContent(loggedInUserName, loggedInUserPassword);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
        mNetworkTask.execute();
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

    public class ButtonNameComparator implements Comparator<Button> {

        @Override
        public int compare(Button button, Button button2) {
            Integer btn1 = Integer.valueOf(button.getText().length());
            Integer btn2 = Integer.valueOf(button2.getText().length());
            return btn1.compareTo(btn2);
        }
    }

    protected void doActivity(final MyButton buttonName) {
        showProgress(true);
        switch (buttonName.getEnumLink()) {
            case AVISOS:
                Function<Activity, String> getRequest = makeGetRequest(buttonName.getLink());
                Function<String, Void> avisosFunction = new Function<String, Void>() {
                    @Override
                    public Void apply(String response) {
                        HTMLParser parser = new HTMLParser(response);
                        showProgress(false);
                        if (!checkForErrors(parser, response)){
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

    protected int getImageByEnum(MyButton btn) {
        return HomePageImages.getLinkByValue(btn.getEnumLink());
    }


    public class NetworkTask extends AsyncTask<String, Void, Boolean> {

        private MainActivity parent;

        public NetworkTask(MainActivity parent) {
            this.parent = parent;
        }

        
        @Override
        protected Boolean doInBackground(String... params) {
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("legajo", loggedInUserName));
            pairs.add(new BasicNameValuePair("password", loggedInUserPassword));
            pairs.add(new BasicNameValuePair("loginbutton", "Ingresar"));

            String response = scraper.fetchPageHtmlPost("http://sysacadweb.frre.utn.edu.ar/menuAlumno.asp", pairs);

            HTMLParser parser = new HTMLParser(response);
            if (FRReUtils.isEmpty(response) || !parser.succefullyLoggin()) {
                Utils.saveToPrefs(MainActivity.this, Utils.PREFS_LOGIN_USERNAME_KEY, null);
                Utils.saveToPrefs(MainActivity.this, Utils.PREFS_LOGIN_PASSWORD_KEY, null);
                Intent intent = new Intent(parent, LoginActivity.class);
                startActivity(intent);
                return false;
            }

            HashMap<HomePageLinks, List<String>> links = parser.getHomeLinks();

            Set<HomePageLinks> linksKeys = links.keySet();

            List<MyButton> btns = new ArrayList<MyButton>();
            for (HomePageLinks key : linksKeys) {
                final String content = links.get(key).get(1);
                final MyButton myButton = new MyButton(this.parent);
                myButton.setTextColor(Color.BLACK);
                myButton.setEnumLink(key);
                final String linkUri = links.get(key).get(0);
                myButton.setLink(linkUri);
                myButton.setText(content);
                myButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doActivity(myButton);
                    }
                });
                btns.add(myButton);
            }

            Collections.sort(btns, new ButtonNameComparator());

            for (MyButton btn : btns) {
                LinearLayout layer1 = new LinearLayout(this.parent);
                layer1.setOrientation(LinearLayout.HORIZONTAL);
                layer1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

                ImageView imageView1 = new ImageView(this.parent);
                imageView1.setBackgroundResource(getImageByEnum(btn));

                layer1.addView(imageView1, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));


                LinearLayout layer2 = new LinearLayout(this.parent);
                layer2.setOrientation(LinearLayout.VERTICAL);
                layer2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                layer2.addView(btn, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                layer1.addView(layer2);

                LinearLayout ll = (LinearLayout) findViewById(R.id.button_place);
                ll.addView(layer1);

            }

            return true;
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
