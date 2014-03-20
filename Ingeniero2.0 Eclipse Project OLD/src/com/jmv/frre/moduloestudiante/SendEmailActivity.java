package com.jmv.frre.moduloestudiante;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import com.jmv.frre.moduloestudiante.activities.calendar.CalendarioAcademico;
import com.jmv.frre.moduloestudiante.activities.sysacad.ConectionRequired;
import com.jmv.frre.moduloestudiante.mail.GMailSender;

import javax.mail.*;
import javax.mail.internet.*;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SendEmailActivity extends Activity {

	Button buttonSend;
	TextView textTo;
	Spinner textSubject;
	EditText textMessage;

	private String to;
	private String subject;
	private EditText fromMessage;
	private String message;
	
	private static String MAIL = "mail_to_someone";

	public static void showHome(Context context, String mailTo) {
		Intent intent = new Intent(context, SendEmailActivity.class);
		intent.putExtra(MAIL, mailTo);
		context.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_email);

		buttonSend = (Button) findViewById(R.id.buttonSend);
		
		textTo = (TextView) findViewById(R.id.editTextTo);
		to = getIntent().getStringExtra(MAIL);
		textTo.setText(to);
		textTo.requestFocus();

		textSubject = (Spinner) findViewById(R.id.editTextSubject);

		textSubject.setOnItemSelectedListener(spinnerListenerSubject);
		textSubject.setSelection(0);
		textSubject.setFocusable(true);
		textSubject.requestFocus();

		textMessage = (EditText) findViewById(R.id.editTextMessage);

		fromMessage = (EditText) findViewById(R.id.from);

		if (!isNetworkAvailable()) {
			ConectionRequired.showHome(this);
			finish();
			return;
		}
		
		buttonSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				message = textMessage.getText().toString();
				new SendMailTask().execute("");

			}
		});
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}
	
	private AdapterView.OnItemSelectedListener spinnerListenerSubject = new AdapterView.OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			subject = textSubject.getItemAtPosition(pos).toString();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}

	};

	private class SendMailTask extends AsyncTask<String, Void, Void> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(SendEmailActivity.this,
					"Aguanta!", "Enviando Mensaje", true, false);
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			progressDialog.dismiss();
			MainScreenActivity.showHome(SendEmailActivity.this);
		}

		@Override
		protected Void doInBackground(String... messages) {

			try {
				GMailSender sender = new GMailSender(
						"ingeniero2.0app@gmail.com", "caracoles123");
				sender.sendMail(subject,  fromMessage.getText()
						.toString()+" - "+message, fromMessage.getText()
						.toString(), to);
			} catch (Exception e) {

			}

			return null;
		}

	}
}
