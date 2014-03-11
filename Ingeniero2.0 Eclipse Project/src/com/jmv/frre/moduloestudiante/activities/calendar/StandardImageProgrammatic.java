package com.jmv.frre.moduloestudiante.activities.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;

import com.jmv.frre.moduloestudiante.R;
import com.jmv.frre.moduloestudiante.activities.sysacad.SysacadActivity;
import com.jmv.frre.moduloestudiante.utils.Utils;
import com.polites.android.GestureImageView;

public class StandardImageProgrammatic extends Activity {
	
	protected GestureImageView view;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.empty);
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        view = new GestureImageView(this);
        int image =  getIntent().getIntExtra(
				Utils.PREFS_LOGIN_IMAGE, R.drawable.calendario);
        view.setImageResource(image);
        view.setLayoutParams(params);
        
        ViewGroup layout = (ViewGroup) findViewById(R.id.layout);
        
        layout.addView(view);
    }
    
    public static void showHome(Context context, int imageToSHow) {
		Intent intent = new Intent(context, StandardImageProgrammatic.class);	
        intent.putExtra(Utils.PREFS_LOGIN_IMAGE, imageToSHow);
		context.startActivity(intent);
	}
}