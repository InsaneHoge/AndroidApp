package com.clydehoge.homestock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * By Clyde Hogenstijn 20-05-20
 * Basic database class for the app.
 * <p>
 * Only class that should use this is {@link AppProvider}!
 */

class AppDataBase extends SQLiteOpenHelper {
    private static final String TAG = "AppDataBase";

    public static final String DATABASE_NAME = "HomeStock.db";
    public static final int DATABASE_VERSION = 1;

    /**
     * @param instance: this is used to implement the AppDataBase class as a Singleton
     */
    private static AppDataBase instance = null;

    /**
     * This class will create the data base but will also provide the connection to the data base.
     * This class is private because we only want one instance of the class created. This is to prevent multiple
     * "SQLite users" from creating the same data base tag simultaneously.
     */
    private AppDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDataBase: constructor");
    }

    /**
     * Get an instance of the app's singleton database helper object.
     *
     * @param context the content providers context.
     * @return a SQLite database helper object.
     */
    static AppDataBase getInstance(Context context) {
        if (instance == null) {
            Log.d(TAG, "getInstance: creating new instance");
            instance = new AppDataBase(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");
        String sSQL; //Use a String variable to facilitate logging

        sSQL = "CREATE TABLE " + ArticleContract.TABLE_NAME + " ("
                + ArticleContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + ArticleContract.Columns.ARTICLE_NAME + " TEXT NOT NULL, " //TODO change to barcode
                + ArticleContract.Columns.ARTICLE_DESCRIPTION + " TEXT, "
                + ArticleContract.Columns.ARTICLE_SORTORDER + " INTEGER);";
        Log.d(TAG, sSQL);
        db.execSQL(sSQL);

        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");
        switch (oldVersion) {
            case 1:
                //upgrade logic from version 1
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown newVersion: " + newVersion);
        }
        Log.d(TAG, "onUpgrade: ends");
    }
}
