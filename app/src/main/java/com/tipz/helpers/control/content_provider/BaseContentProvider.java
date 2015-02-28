package com.tipz.helpers.control.content_provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.List;

/***
 * This class serves as boilerplate code for the creation of a simple content provider.
 * While the content provider main purpose is to share data among apps, it is also useful
 * to use it in your own application scope - allowing to use "stock" Android components such
 * as the  {@link android.content.CursorLoader @CursorLoader}.
 * <p>
 * The implementation is 'simple' because it gives you some automatic DB Table to ContentUri
 * transformation.
 * ContentURI's Notifications will be automatically sent, if you wish to disable them,
 * send 'WITHOUT_NOTIFY' in the URI as a parameter. (i.e. uri://address/123?WITHOUT_NOTIFY)
 * </p>
 * <p>
 * Limitations:
 * <ul>
 *     <li>Inserts will be done with 'SQLiteDatabase.CONFLICT_REPLACE'</li>
 *     <li>You cannot add selection filtering when sending a URI with ID (i.e. uri://address/123)</li>
 * </ul>
 * </p>
 *
 */
public abstract class BaseContentProvider extends ContentProvider {

    /**
     * Add this to the URI when notify is not wanted.
     */
    public static final String WITHOUT_NOTIFY = "?WITHOUT_NOTIFY";

    protected final String TAG = this.getClass().getSimpleName();

    private SQLiteOpenHelper dbHelper;

    protected static final UriMatcher mMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    @Override
    public boolean onCreate() {

        dbHelper = createSQLiteOpenHelper();

        return (dbHelper != null);
    }

    protected String getAuthority(Context context) {

        String authority = "";
        try {
            PackageManager pm = context.getPackageManager();
            android.content.ComponentName cn = new android.content.ComponentName(context
                    .getPackageName(), getClass().getName());
            ProviderInfo si;
            if (pm != null) {
                si = pm.getProviderInfo(cn, PackageManager.GET_META_DATA);
                if (si != null && si.metaData != null) {
                    authority = si.metaData.getString("authority");
                }
            }
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return authority;
    }

    protected abstract SQLiteOpenHelper createSQLiteOpenHelper();

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return insert(uri, values, db, true);
    }

    public Uri insert(Uri uri, ContentValues values, SQLiteDatabase db, boolean notify) {

        // Execute query
        long id = db.insertWithOnConflict(getTableName(uri), null, values,
                SQLiteDatabase.CONFLICT_REPLACE);

        if (notify) {
            notifyUriChanged(uri);
        }

        if (id > 0) {
            return ContentUris.withAppendedId(uri, id);
        } else {
            return null;
        }
    }

    private static final int TABLE_PATH_IN_URI = 0;

    public static String getTableName(Uri uri) {
        List<String> pathSegments = uri.getPathSegments();
        return pathSegments.get(TABLE_PATH_IN_URI);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // TODO: wrap in a try&catch - when openning the DB and when Query occurs
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursorToReturn = null;

        // If it's a CONTENT_URI Query request - without a match for specific CONTENT_URI
        if (mMatcher.match(uri) == -1) {
            // If it's a CONTENT_URI with a specific ID
            // TODO: is there a better way to check if the last segment is a number??
            List<String> pathSegments = uri.getPathSegments();
            if (pathSegments.size() > 1 && android.text.TextUtils.isDigitsOnly(
                    pathSegments.get(pathSegments.size() - 1))) {
                long id = ContentUris.parseId(uri);
                if (selection == null) {
                    cursorToReturn = db.query(getTableName(uri), projection, "_id = ?",
                            new String[]{String.valueOf(id)}, null, null, sortOrder);
                } else {
                    // TODO: check this
                    Log.e(TAG, "You cannot add selection filtering when sending an attached ID");
                }
            }
            // Else, it's a simple query with no specific ID
            else {
                cursorToReturn = db.query(getTableName(uri), projection, selection,
                        selectionArgs, null, null, sortOrder);
            }
        } else {
            cursorToReturn = querySpecific(db, mMatcher.match(uri), uri, projection, selection,
                    selectionArgs, sortOrder);
        }

        if (cursorToReturn != null) {
            cursorToReturn.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursorToReturn;
    }

    /***
     * Use this method to return a custom-made cursor result.
     * The default query uses the table name from the uri.
     * A common practice to use this method is when you wish to join between tables
     * @return Cursor with values to be returned from the content provider
     */
    protected abstract Cursor querySpecific(SQLiteDatabase db, int match, Uri uri,
                                            String[] projection,
                                            String selection, String[] selectionArgs, String sortOrder);

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(getTableName(uri), selection, selectionArgs);

        notifyUriChanged(uri);

        return result;
    }

    protected void notifyUriChanged(Uri uri) {

        // Notify the change unless Uri specified otherwise
        if (!uri.toString().contains(WITHOUT_NOTIFY)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.update(getTableName(uri), values, selection, selectionArgs);

        notifyUriChanged(uri);

        return result;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        int numberOnInserts = 0;

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();

        for (ContentValues value : values) {
            // We are sending the DB object to be consistent with the transaction we've opened
            this.insert(uri, value, db, false);
            numberOnInserts++;
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        notifyUriChanged(uri);

        return numberOnInserts;
    }

    /***
     * Helps to quickly form a URI
     * @param tableName - the table name, which will be the main URI part
     * @param authority - the autority, which is the base of the URI
     * @return URI ready
     */
    public static Uri formUri(String tableName, String authority) {
        // Parse the CONTENT_URI for this entity
        return Uri.parse(String.format("content://"+ authority +"/%s", tableName));
    }
}