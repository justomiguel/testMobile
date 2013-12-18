package com.jmv.frre.moduloestudiante.net;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.common.base.Function;

/**
 * Created by Cleo on 12/10/13.
 */
public class NetworkTaskHandler extends AsyncTask<String, Void, String> {

    private final Function<String, Void> nextFunction;
    private Function<Activity, String> function;

    public NetworkTaskHandler(Function<Activity, String> function, Function<String, Void> nextFunction) {
        this.function = function;
        this.nextFunction = nextFunction;
    }

    @Override
    protected String doInBackground(String... params) {
            return params!= null && params.length > 0 && params[0]!=null? params[0]:function.apply(null);
    }

    @Override
    protected void onPostExecute(final String success) {
        nextFunction.apply(success);
    }

    @Override
    protected void onCancelled() {

    }

}