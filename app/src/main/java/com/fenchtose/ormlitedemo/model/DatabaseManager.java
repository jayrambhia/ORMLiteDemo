package com.fenchtose.ormlitedemo.model;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by Jay Rambhia on 05/04/15.
 */
public class DatabaseManager {
    private DatabaseHelper databaseHelper = null;

    public DatabaseHelper getHelper(Context context) {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }

        return databaseHelper;
    }

    public void releaseHelper() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
