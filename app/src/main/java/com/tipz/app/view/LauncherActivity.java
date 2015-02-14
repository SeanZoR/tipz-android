package com.tipz.app.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.tipz.app.TipzApplication;

public class LauncherActivity extends Activity {

    protected final String TAG = ((Object) this).getClass().getSimpleName();

    protected TipzApplication mApp;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        mApp = (TipzApplication) getApplicationContext();
    }


}
