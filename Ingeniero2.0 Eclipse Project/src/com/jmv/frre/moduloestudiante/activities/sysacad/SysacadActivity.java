package com.jmv.frre.moduloestudiante.activities.sysacad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Function;
import com.jmv.frre.moduloestudiante.MainScreenActivity;
import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.R.id;
import com.jmv.frre.moduloestudiante.R.layout;
import com.jmv.frre.moduloestudiante.R.menu;
import com.jmv.frre.moduloestudiante.R.string;
import com.jmv.frre.moduloestudiante.comps.MyButton;
import com.jmv.frre.moduloestudiante.constants.HomePageImages;
import com.jmv.frre.moduloestudiante.constants.HomePageLinks;
import com.jmv.frre.moduloestudiante.net.HTMLParser;
import com.jmv.frre.moduloestudiante.net.NetworkTaskHandler;
import com.jmv.frre.moduloestudiante.utils.FRReUtils;
import com.jmv.frre.moduloestudiante.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.view.ViewGroup.LayoutParams;

public class SysacadActivity extends LinkActivity {
	
	String homeLink = "http://sysacadweb.frre.utn.edu.ar/";

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

	public void actionBtn(View view){
		showDialog(DIALOG_SHOW_ABOUT);
	}
	
	public void actionBtnExams(View view){
		IncsripcionAExamen.showExamsView(this, homeLink+HomePageLinks.INSCRIPCION_A_EXAMEN.getLink()+"?"+MainScreenActivity.CURRENT_ID);
	}
	
	public void academicStatus(View view){
		ExamsMadeActivity.showExamsView(this, homeLink+HomePageLinks.EXAMENES.getLink()+"?"+MainScreenActivity.CURRENT_ID);
	}
	
	private void init() {
		mHomeStatusMessageView = (TextView) findViewById(R.id.loading_status_message);
		mHomeStatusMessageView.setText(R.string.loadin_content_progress);
		showProgress(false);
		
	}

	@Override
	public void onBackPressed() {
		MainScreenActivity.showHome(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
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

	protected int getImageByEnum(MyButton btn) {
		return HomePageImages.getLinkByValue(btn.getEnumLink());
	}
/*
	public class NetworkTask extends AsyncTask<String, Void, Boolean> {

		private SysacadActivity parent;
		private ArrayList<MyButton> btns;

		public NetworkTask(SysacadActivity parent) {
			this.parent = parent;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			
			if (btns == null){
				HashMap<HomePageLinks, List<String>> links = parser.getHomeLinks();

				Set<HomePageLinks> linksKeys = links.keySet();

				if (links.isEmpty()){
					currentError = "Empty Response";
					SysacadActivity.showHome(SysacadActivity.this);
				}
				
				String firstLink = links.get(HomePageLinks.AVISOS).get(0);
				CURRENT_ID = firstLink.substring(firstLink.indexOf("id="));

				btns = new ArrayList<MyButton>();
				for (HomePageLinks key : linksKeys) {
					final String content = links.get(key).get(1);
					final MyButton myButton = new MyButton(this.parent);
					myButton.setEnumLink(key);
					final String linkUri = links.get(key).get(0);
					myButton.setLink(linkUri);
					myButton.setText(content);
					myButton.setEnabled(key.isEnabled());
					myButton.setTextColor(key.isEnabled() ? Color.BLACK
							: Color.GRAY);
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
					if (btn.isEnabled()) {
						LinearLayout layer1 = new LinearLayout(this.parent);
						layer1.setOrientation(LinearLayout.HORIZONTAL);
						layer1.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));

						ImageView imageView1 = new ImageView(this.parent);
						imageView1.setBackgroundResource(getImageByEnum(btn));

						layer1.addView(imageView1, new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));

						LinearLayout layer2 = new LinearLayout(this.parent);
						layer2.setOrientation(LinearLayout.VERTICAL);
						layer2.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT));
						layer2.addView(btn, new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT));

						layer1.addView(layer2);

						LinearLayout ll = (LinearLayout) findViewById(R.id.button_place);
						ll.addView(layer1);
					}

				}

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

	}*/
}
