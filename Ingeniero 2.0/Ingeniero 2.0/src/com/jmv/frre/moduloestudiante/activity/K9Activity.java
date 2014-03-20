package com.jmv.frre.moduloestudiante.activity;

import android.os.Bundle;
import android.view.MotionEvent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jmv.frre.moduloestudiante.activity.K9ActivityCommon.K9ActivityMagic;
import com.jmv.frre.moduloestudiante.activity.misc.SwipeGestureDetector.OnSwipeGestureListener;


public class K9Activity extends SherlockFragmentActivity implements K9ActivityMagic {

    private K9ActivityCommon mBase;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        mBase = K9ActivityCommon.newInstance(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        mBase.preDispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void setupGestureDetector(OnSwipeGestureListener listener) {
        mBase.setupGestureDetector(listener);
    }
}
