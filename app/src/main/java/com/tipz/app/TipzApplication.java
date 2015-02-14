package com.tipz.app;

import android.app.Application;
import android.preference.PreferenceManager;

import com.tipz.app.control.util.BasicStringSecurity;
import com.tipz.app.control.util.SecuredPreferenceUtil;

public class TipzApplication extends Application {

    private SecuredPreferenceUtil prefs;
    private char[] mPrefSecureKey = ("TipzApplicationSecuredKey!!!").toCharArray();

    public SecuredPreferenceUtil getPrefs() {
        if (prefs == null){
            // Set up a preferences manager (with basic security)
            prefs = new SecuredPreferenceUtil(getResources(),
                    PreferenceManager.getDefaultSharedPreferences(this),
                    new BasicStringSecurity(mPrefSecureKey));
        }

        return prefs;
    }
}