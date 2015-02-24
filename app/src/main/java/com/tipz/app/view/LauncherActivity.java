package com.tipz.app.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.util.Log;

import com.tipz.app.BuildConfig;
import com.tipz.app.R;
import com.tipz.app.TipzApplication;

public class LauncherActivity extends Activity {

    protected final String TAG = ((Object) this).getClass().getSimpleName();

    protected TipzApplication mApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }

        super.onCreate(savedInstanceState);

        mApp = (TipzApplication) getApplicationContext();

        // Update the number of application launches
        mApp.getPrefs().applyInt(R.string.pref_app_launch_times,
                mApp.getPrefs().getInt(R.string.pref_app_launch_times, 0) + 1);

        // Start the main activity
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }
}