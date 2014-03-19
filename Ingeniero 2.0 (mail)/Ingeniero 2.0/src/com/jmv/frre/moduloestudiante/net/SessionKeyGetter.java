package com.jmv.frre.moduloestudiante.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.common.base.Function;
import com.jmv.frre.moduloestudiante.activities.sysacad.LoginActivity;
import com.jmv.frre.moduloestudiante.activities.sysacad.MaintenanceMode;
import com.jmv.frre.moduloestudiante.utils.FRReUtils;
import com.jmv.frre.moduloestudiante.utils.Utils;

public class SessionKeyGetter extends AsyncTask<String, Void, String> {

	private Activity parent;
	private HTTPScraper scraper;
	private Function<String, Void> nextFunction;

	public SessionKeyGetter(Activity parent,HTTPScraper scraper, Function<String, Void> nextFunction) {
		this.parent = parent;
		this.scraper = scraper;
		this.nextFunction = nextFunction;
	}

	@Override
	protected String doInBackground(String... params) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("legajo", params[0]));
		pairs.add(new BasicNameValuePair("password", params[1]));
		pairs.add(new BasicNameValuePair("loginbutton", "Ingresar"));

		String response = scraper.fetchPageHtmlPost(
				"http://sysacadweb.frre.utn.edu.ar/menuAlumno.asp", pairs);

		HTMLParser parser = HTMLParser.getParserFor(response);

		if (FRReUtils.isEmpty(response) || !parser.succefullyLoggin()) {
			Utils.saveToPrefs(parent,
					Utils.PREFS_LOGIN_USERNAME_KEY, null);
			Utils.saveToPrefs(parent,
					Utils.PREFS_LOGIN_PASSWORD_KEY, null);
			Intent intent = new Intent(parent, LoginActivity.class);
			parent.startActivity(intent);
			parent.finish();
			return "";
		} else if (response.equalsIgnoreCase(HTTPScraper.MAINTENANCE_MODE_ON)){
			MaintenanceMode.showHome(parent);
			return "";
		}
		
		return response;
	}
	
	 @Override
     protected void onPostExecute(final String success) {
		 nextFunction.apply(success);
     }

     @Override
     protected void onCancelled() {
         
     }
}
