package com.tipz.helpers.control.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.tipz.helpers.control.database.annotations.DbBinder;
import com.tipz.helpers.control.database.annotations.DbForeignKey;
import com.tipz.helpers.control.database.annotations.DbIndex;
import com.tipz.helpers.control.database.annotations.DbPrimaryKey;
import com.tipz.helpers.control.database.annotations.DbUniquekey;

import java.lang.reflect.Field;
import java.util.ArrayList;

public abstract class DbEntityHelper extends SQLiteOpenHelper {

    private static final String TAG = "DbHelper";

    protected DbEntityHelper(Context context, String dbName, int dbVersion) {
        super(context, dbName, null, dbVersion);
    }

    public abstract ArrayList<DbEntity> getContracts();

    /**
     * Called only once to create the database first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        createTablesIfNotExists(db);
    }

    public void createTablesIfNotExists(SQLiteDatabase db) {

        for (DbEntity currContract : getContracts()) {

            StringBuilder createTable = new StringBuilder();
            createTable.append(
                    String.format("create table if not exists %s ( ", currContract.tableName()));

            StringBuilder createIndex = new StringBuilder();


            // Holds the UNIQUE part. Will append at the end of fields deceleration if exist.
            String uniqueString = null;
            ArrayList<String> mPrimaryKeys = new ArrayList<String>();

            // Run on the fields
            for (Field currField : currContract.getClass().getFields()) {
                if (currField.getDeclaredAnnotations().length > 0) {

                    DbBinder annotation = currField.getAnnotation(DbBinder.class);

                    String dbFieldName = currField.getName();
                    if (annotation != null) {
                        dbFieldName = annotation.dbName();
                    }

                    createTable.append(dbFieldName);

                    if (currField.getType() == boolean.class) {
                        createTable.append(" INTEGER ");
                    } else if (currField.getType() == String.class) {
                        createTable.append(" TEXT ");
                    } else {
                        createTable.append(" NUMBER ");
                    }

                    if (annotation != null && !annotation.isNullable()) {
                        createTable.append(" NOT NULL ");
                    }

                    if (currField.getAnnotation(DbPrimaryKey.class) != null) {
                        mPrimaryKeys.add(dbFieldName);
                    }

                    DbForeignKey foreignKeyAnnotation = currField.getAnnotation(DbForeignKey.class);

                    if (foreignKeyAnnotation != null) {
                        String dbColumnName = foreignKeyAnnotation.dbColumnName();
                        if (TextUtils.isEmpty(dbColumnName)) {
                            dbColumnName = currField.getName();
                        }

                        createTable.append(String.format(" REFERENCES %1$s(%2$s) ",
                                foreignKeyAnnotation.dbTableName(), dbColumnName));
                    }

                    DbUniquekey dbUniqueKey = currField.getAnnotation(DbUniquekey.class);
                    if (dbUniqueKey != null) {
                        uniqueString = dbUniqueKey.uniqueKey();
                    }

                    DbIndex dbIndex = currField.getAnnotation(DbIndex.class);
                    if(dbIndex != null){

                        String dbIndexName = dbIndex.dbIndexName();
                        if (TextUtils.isEmpty(dbIndexName)) {
                            dbIndexName = String.format("%1$s_%2$s_idx",currContract.tableName(),annotation.dbName());
                        }
                        createIndex.append(String.format("CREATE INDEX IF NOT EXISTS %1$s ON %2$s (%3$s); ",
                                dbIndexName, currContract.tableName(), annotation.dbName()));

                    }

                    createTable.append(",");
                }
            }

            if (mPrimaryKeys.size() > 0) {
                createTable.append(" PRIMARY KEY ( ");
                for (String key : mPrimaryKeys) {
                    createTable.append(key + ",");
                }
                createTable.deleteCharAt(createTable.length() - 1);
                createTable.append(" ) ");
            } else { // Delete the last ","
                createTable.deleteCharAt(createTable.length() - 1);
            }

            // Add UNIQUE part if exist
            if (uniqueString != null) {
                createTable.append(String.format(", UNIQUE (%1$s) ", uniqueString));
            }

            // Place ")"
            createTable.append(")");

            Log.d(TAG, "SQL CREATE TABLE: " + createTable);

            db.execSQL(createTable.toString());

            String indexes = createIndex.toString();
            if(!TextUtils.isEmpty(indexes)) {
                db.execSQL(indexes);
                Log.d(TAG, "SQL INDEXES: " + indexes);
            }
        }
    }

    /**
     * Will call drop table to each of the tables in the list
     * @param db Writable data base
     * @param tablesToDrop List of baseEntity
     */
    public void dropTablesAndRecreate(SQLiteDatabase db, ArrayList<DbEntity> tablesToDrop) {
        for (DbEntity currContract : tablesToDrop) {
            db.execSQL("DROP TABLE IF EXISTS " + currContract.tableName());
            Log.d(TAG, "DROP TABLE called for " + currContract.tableName());
        }

        // Call on create to recreate all tables
        onCreate(db);
    }

    /**
     * WIll call drop tables to all of the tables.
     * @param db Writable data base
     */
    public void dropAllTableAndRecreate(SQLiteDatabase db){
        dropTablesAndRecreate(db, getContracts());
    }
}