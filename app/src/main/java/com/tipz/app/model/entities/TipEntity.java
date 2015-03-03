package com.tipz.app.model.entities;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;
import com.tipz.app.control.content_provider.TipzContentProvider;
import com.tipz.helpers.control.content_provider.ProviderEntity;
import com.tipz.helpers.control.database.annotations.DbBinder;
import com.tipz.helpers.control.database.DbEntity;
import com.tipz.helpers.control.database.annotations.DbPrimaryKey;
import com.tipz.helpers.control.database.annotations.DbUniquekey;

import java.io.Serializable;

import static com.tipz.helpers.control.content_provider.BaseContentProvider.formUri;

/**
 * This entity is a "tip" entity from TipzApi
 * The naming convention is built to enable Gson instance to convert all fields from lower
 * case with underscores to camel case and vice versa.
 */
public class TipEntity implements Serializable, DbEntity, ProviderEntity {

    public static final String TABLE_NAME = "Tip";

    public static final Uri CONTENT_URI = formUri(TABLE_NAME, TipzContentProvider.authority);

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @Override
    public Uri getProviderUri() {
        return CONTENT_URI;
    }


    public static class DB {
        public static final String ID = "_id";
        public static final String CREATED_TIMESTAMP = "createdTimestamp";
        public static final String TITLE = "title";
        public static final String IS_FAVORITE = "IS_FAVORITE";
    }

    /***
     * Leading ID of entity, must be unique also from server side
     */
    @DbPrimaryKey
    @DbBinder(dbName = DB.ID)
    @SerializedName("_id")
    public String id;

    /**
     * A unix-timestamp of created time
     */
    @DbBinder(dbName = DB.CREATED_TIMESTAMP)
    public Long createdTimestamp;

    /**
     * The title of the tip
     */
    @DbBinder(dbName = DB.TITLE)
    public String title;

    /**
     * Favorite mark, handled locally right now
     */
    @DbBinder(dbName = DB.IS_FAVORITE)
    public boolean isFavorite = false;

    /**
     * Gets an existing values of a tip from a specific ID
     * and saves them to current Entity
     *
     * @param provider - ContentProvider reference
     */
    public void initWithPreviousValuesFromProvider(ContentResolver provider) {

        // Handle the isFavorite value
        Cursor favoriteQuery = null;
        try {
            favoriteQuery = provider.query(TipEntity.CONTENT_URI,
                    new String[]{TipEntity.DB.IS_FAVORITE},
                    TipEntity.DB.ID + "=?",
                    new String[]{id},
                    null);

            // Return true if the there's a positive value in the Favorite column
            isFavorite = favoriteQuery.moveToFirst() &&
                    favoriteQuery.getInt(favoriteQuery.getColumnIndex(DB.IS_FAVORITE)) != 0;
        } finally {
            // Make sure the cursor is being closed if it was opened
            if (favoriteQuery != null && !favoriteQuery.isClosed()) {
                favoriteQuery.close();
            }
        }
    }
}
