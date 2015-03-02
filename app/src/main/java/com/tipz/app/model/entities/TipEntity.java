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
    }

    /***
     * Leading ID of entity, must be unique also from server side
     */
    @DbPrimaryKey
    @DbBinder(dbName = DB.ID)
    @SerializedName("_id")
    public String id;

    /***
     * A unix-timestamp of created time
     */
    @DbBinder(dbName = DB.CREATED_TIMESTAMP)
    public Long createdTimestamp;

    /**
     * The title of the tip
     */
    @DbBinder(dbName = DB.TITLE)
    public String title;
}
