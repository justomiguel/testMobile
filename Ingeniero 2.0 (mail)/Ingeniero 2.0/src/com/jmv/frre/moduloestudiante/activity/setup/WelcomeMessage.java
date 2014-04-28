package com.jmv.frre.moduloestudiante.activity.setup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.jmv.frre.moduloestudiante.NotificacionesActivity;
import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.activity.Accounts;
import com.jmv.frre.moduloestudiante.activity.K9Activity;
import com.jmv.frre.moduloestudiante.helper.HtmlConverter;
import com.swacorp.oncallpager.MainActivity;

/**
 * Displays a welcome message when no accounts have been created yet.
 */
public class WelcomeMessage extends K9Activity implements OnClickListener{

    public static void showWelcomeMessage(Context context) {
        Intent intent = new Intent(context, WelcomeMessage.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.welcome_message);

        TextView welcome = (TextView) findViewById(R.id.welcome_message);
        welcome.setText(HtmlConverter.htmlToSpanned(getString(R.string.accounts_welcome)));
        welcome.setMovementMethod(LinkMovementMethod.getInstance());

        ((Button) findViewById(R.id.next)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next: {
                NotificacionesActivity.showHomeAfterPresentation(this);
                finish();
                break;
            }
        }
    }
}
