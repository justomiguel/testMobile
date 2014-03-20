package com.jmv.frre.moduloestudiante;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.jmv.frre.moduloestudiante.R;

public class SendNotificationActivity extends Activity {

	private Spinner spinner;
	private Spinner spinnerMails;
	
	private String to;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_notification);
		
		this.spinner = (Spinner) findViewById(R.id.listas);
		this.spinnerMails = (Spinner) findViewById(R.id.mails);
		
		spinner.setOnItemSelectedListener(spinnerListenerSubject);
		spinner.setSelection(0);
		spinner.setFocusable(true);
		spinner.requestFocus();
	}

	private AdapterView.OnItemSelectedListener spinnerListenerSubject = new AdapterView.OnItemSelectedListener() {

		

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			to = spinnerMails.getItemAtPosition(pos).toString().trim();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}

	};
	
	public void sendEmail(View view){
		  String subject = "[INFO]";
		  String message = "";

		  Intent email = new Intent(Intent.ACTION_SEND);
		  email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
		  //email.putExtra(Intent.EXTRA_CC, new String[]{ to});
		  //email.putExtra(Intent.EXTRA_BCC, new String[]{to});
		  email.putExtra(Intent.EXTRA_SUBJECT, subject);
		  email.putExtra(Intent.EXTRA_TEXT, message);

		  //need this to prompts email client only
		  email.setType("message/rfc822");

		  startActivity(Intent.createChooser(email, "Elegi tu cliente de E-mail:"));
	}
	
	public static void showSendNotificationActivityHome(Context context) {
		Intent intent = new Intent(context, SendNotificationActivity.class);
		context.startActivity(intent);
	}

}
