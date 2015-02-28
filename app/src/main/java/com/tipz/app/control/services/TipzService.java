package com.tipz.app.control.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class TipzService extends WakefulIntentService {

    private static final String ACTION_GET_TIPS = "com.tipz.app.control.services.action.ACTION_GET_TIPS";
    private static final String TAG = "TipzService";

    /**
     * Get an intent to start this service to perform action Get Tips with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static Intent actionGetTipsIntent(Context context) {
        Intent intent = new Intent(context, TipzService.class);
        intent.setAction(ACTION_GET_TIPS);
        return intent;
    }

    public TipzService() {
        super("TipzService");
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_TIPS.equals(action)) {
                handleGetTips();
            }
        }

    }

    /**
     * Handle get tips in the provided background thread
     */
    private void handleGetTips() {
        // TODO: implement
        Log.d(TAG, "in handleGetTips");

        // https://raw.githubusercontent.com/tipz/tipz-android/gh-pages/android-tips.json
    }
}
