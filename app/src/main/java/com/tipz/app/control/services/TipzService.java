package com.tipz.app.control.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.tipz.app.R;
import com.tipz.app.model.entities.TipEntity;
import com.tipz.app.model.rest.TipzApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class TipzService extends WakefulIntentService {

    private static final String ACTION_GET_TIPS = "com.tipz.app.control.services.action.ACTION_GET_TIPS";
    private static final String TAG = "TipzService";
    private TipzApi mTipzApi;

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

    /***
     * Note: This method runs on the UI Thread
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // The following code creates a new Gson instance that will convert all fields from lower
        // case with underscores to camel case and vice versa. It also registers a type adapter for
        // the Date class. This DateTypeAdapter will be used anytime Gson encounters a Date field.
        // The gson instance is passed as a parameter to GsonConverter, which is a wrapper
        // class for converting types.
        // (Source: http://square.github.io/retrofit/)
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        // Creating the rest adapter (Retrofit)
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.tipz_api_base_url))
                .setConverter(new GsonConverter(gson))
                .build();

        mTipzApi = restAdapter.create(TipzApi.class);
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
        List<TipEntity> tipEntities = mTipzApi.listTips();

        ContentValues[] allContent = new ContentValues[tipEntities.size()];

        // Create content values for each entity
        int contentIndex = 0;
        for (TipEntity tip : tipEntities) {

            // Before inserting the new values from the API, we must take
            // locally saved values and set them so we will not override them.
            // (The ContentProvider is inserting with "CONFLICT_REPLACE" flag)
            tip.initWithPreviousValuesFromProvider(getContentResolver());
            
            ContentValues content = new ContentValues();
            content.put(TipEntity.DB.ID, tip.id);
            content.put(TipEntity.DB.CREATED_TIMESTAMP, tip.createdTimestamp);
            content.put(TipEntity.DB.TITLE, tip.title);
            content.put(TipEntity.DB.IS_FAVORITE, tip.isFavorite);

            // Add the content to the all of contents
            allContent[contentIndex] = content;
            contentIndex++;
        }

        // Store the entities in a database
        int inserted = getContentResolver().bulkInsert(TipEntity.CONTENT_URI, allContent);
        Log.d(TAG, String.format("Inserted %d tips", inserted));
    }
}