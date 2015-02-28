package com.tipz.app.control.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.tipz.app.model.entities.TipEntity;
import com.tipz.helpers.control.database.DbEntity;
import com.tipz.helpers.control.database.DbEntityHelper;

import java.util.ArrayList;

/***
 * Manages the creation, upgrades and instantiation of the Tipz Database
 */
public class TipzDB extends DbEntityHelper {

    public TipzDB(Context context, int dbVersion) {
        super(context, "TipzDB", dbVersion);
    }

    @Override
    public ArrayList<DbEntity> getContracts() {
        ArrayList<DbEntity> contracts = new ArrayList<DbEntity>();
        contracts.add(new TipEntity());
        return contracts;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nothing here yet
    }
}
