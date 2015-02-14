package com.tipz.app;

import android.app.Application;
import android.preference.PreferenceManager;

import com.tipz.app.control.util.BasicStringSecurity;
import com.tipz.app.control.util.SecuredPreferenceUtil;

public class TipzApplication extends Application {

    protected static final String TAG = "TipzApplication";

    private SecuredPreferenceUtil prefs;
    private char[] mPrefSecureKey = ("TipzApplicationSecuredKey!!!").toCharArray();

    /**
     * Saves a boolean representing whether the app is currently started with a new version
     */
    private boolean mIsNewVersion;

    @Override
    public void onCreate() {
        super.onCreate();

        CheckIfAppUpdated();
    }

    private void CheckIfAppUpdated() {
        // Compare current version with last saved
        int currVersion = BuildConfig.VERSION_CODE;
        int previousVersion = getPrefs().getInt(R.string.pref_last_version_code);

        // Determine if we are using a new version
        mIsNewVersion = currVersion > previousVersion;

        // If we have a new version
        if (mIsNewVersion) {
            // Update to the new version in the prefs
            getPrefs().applyInt(R.string.pref_last_version_code, currVersion);
        }
    }

    /**
     * Checks if the current version is increased since the last version that was saved in prefs.
     *
     * @return true if version increased.
     */
    public boolean getIsNewVersion() {
        return mIsNewVersion;
    }

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