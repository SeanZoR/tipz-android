package com.tipz.app.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.tipz.app.R;
import com.tipz.app.TipzApplication;

public class LauncherActivity extends Activity {

    protected final String TAG = ((Object) this).getClass().getSimpleName();

    protected TipzApplication mApp;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        mApp = (TipzApplication) getApplicationContext();

        // Update the number of application launches
        mApp.getPrefs().applyInt(R.string.pref_app_launch_times,
                mApp.getPrefs().getInt(R.string.pref_app_launch_times, 0) + 1);
    }
}