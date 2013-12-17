package com.jmv.frre.moduloestudiante.net;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.common.base.Function;

/**
 * Created by Cleo on 12/10/13.
 */
public class NetworkTaskHandler extends AsyncTask<Function, Void, String> {

    private final Function<String, Void> nextFunction;
    private Function<Activity, String> function;

    public NetworkTaskHandler(Function function, Function nextFunction) {
        this.function = function;
        this.nextFunction = nextFunction;
    }

    @Override
    protected String doInBackground(Function... params) {
            return function.apply(null);
    }

    @Override
    protected void onPostExecute(final String success) {
        nextFunction.apply(success);
    }

    @Override
    protected void onCancelled() {

    }

}