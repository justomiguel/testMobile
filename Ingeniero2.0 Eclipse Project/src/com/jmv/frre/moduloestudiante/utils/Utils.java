package com.jmv.frre.moduloestudiante.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * // Saving user credentials on successful login case
 Utils.saveToPrefs(YourActivity.this, PREFS_LOGIN_USERNAME_KEY, username);
 Utils.saveToPrefs(YourActivity.this, PREFS_LOGIN_PASSWORD_KEY, password);

 // To retrieve values back
 String loggedInUserName = Utils.getFromPrefs(YourActivity.this, PREFS_LOGIN_USERNAME_KEY);
 String loggedInUserPassword = Utils.getFromPrefs(YourActivity.this, PREFS_LOGIN_PASSWORD_KEY);
 * Created by Cleo on 12/1/13.
 */
@SuppressLint("SimpleDateFormat")
public class Utils {
	
	public static final SimpleDateFormat DATE_HOUR_FORMATTER = new SimpleDateFormat(
			"mm:ss", Locale.ENGLISH);
	
	public static String currentID = "";
	
    public static final String PREFS_LOGIN_USERNAME_KEY = "__USERNAME__" ;
    public static final String PREFS_LOGIN_PASSWORD_KEY = "__PASSWORD__" ;

    // Saves value to shared preferences with specified key
    public static void saveToPrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }
    
    @SuppressLint("SimpleDateFormat")
	public static String getDateByPattern(String pattern){
    	SimpleDateFormat yearGetter = new SimpleDateFormat("yyyy");
    	Date myDate = new Date();
    	SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd 'de' MMMM yyyy", new Locale("es", "ES"));
    	Date date;
		try {
			date = formatter.parse(pattern+" "+yearGetter.format(myDate));
		} catch (ParseException e) {
			date = myDate;
		}
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    // Retrieve value from shared preferences against given key
    public static String getFromPrefs(Context context, String key) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(key, null);
        // NULL is the default value if nothing found in shared preferences against specified key
    }


}