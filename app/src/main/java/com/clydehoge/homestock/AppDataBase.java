package com.clydehoge.homestock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Basic database class for the app
 *
 * Only class that should use this is AppProvider!
 */

class AppDataBase extends SQLiteOpenHelper {
    private static final String TAG = "AppDataBase";

    public static final String DATABASE_NAME = "HomeStock.db";
    public static final int DATABASE_VERSION = 1;

    //Implement AppDataBase as a Singleton
    private static AppDataBase instance = null;

    private AppDataBase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDataBase: constructor");
    }

    /**
     * Get an instance of the app's singleton database helper object
     *
     * @param context the content providers context.
     * @return a SQLite database helper object.
     */
    static AppDataBase getInstance(Context context){
        if (instance == null){
            Log.d(TAG, "getInstance: creating new instance");
            instance = new AppDataBase(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");
        String sSQL; //Use a String variable to facilitate logging
//        sSQL = "CREATE TABLE Article(_id INTEGER PRIMARY KEY NOT NULL, Name TEXT NOT NULL, Description TEXT, SortOrder INTEGER, CategoryID INTEGER);";
        sSQL = "CREATE TABLE " + ArticleContract.TABLE_NAME + " ("
                + ArticleContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + ArticleContract.Columns.ARTICLE_NAME + " TEXT NOT NULL, "
                + ArticleContract.Columns.ARTICLE_DESCRIPTION + " TEXT, "
                + ArticleContract.Columns.ARTICLE_SORTORDER + " INTEGER);";
        Log.d(TAG, sSQL);
        db.execSQL(sSQL);

        Log.d(TAG,"onCreate: ends");

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
