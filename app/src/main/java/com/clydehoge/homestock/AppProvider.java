package com.clydehoge.homestock;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * By Clyde Hogenstijn 20-05-20
 * <p>
 * Provider for the HomeStock app. This is the only class that knows about {@link AppDataBase}
 */

public class AppProvider extends ContentProvider {
    public static final String TAG = "AppProvider";

    private AppDataBase mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final String CONTENT_AUTHORITY = "com.clydehoge.homestock.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int ARTICLE = 100;
    private static final int ARTICLE_ID = 101;

    private static final int ARTICLE_STOCK = 200;
    private static final int ARTICLE_STOCK_ID = 201;

    /*
        private static final int TOTAL_STOCK = 300;
        private static final int TOTAL_STOCK_ID = 301;
     */

    private static final int STOCK_INFO = 400;
    private static final int STOCK_INFO_ID = 401;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        //vb content://com.clydehoge.homestock.provider/Article
        matcher.addURI(CONTENT_AUTHORITY, ArticleContract.TABLE_NAME, ARTICLE);
        //vb content://com.clydehoge.homestock.provider/Article/9
        matcher.addURI(CONTENT_AUTHORITY, ArticleContract.TABLE_NAME + "/#", ARTICLE_ID);

//        matcher.addURI(CONTENT_AUTHORITY, StockContract.TABLE_NAME, ARTICLE_STOCK);
//        matcher.addURI(CONTENT_AUTHORITY, StockContract.TABLE_NAME + "/#", ARTICLE_STOCK_ID);
//
//        matcher.addURI(CONTENT_AUTHORITY, StockInfoContract.TABLE_NAME, STOCK_INFO);
//        matcher.addURI(CONTENT_AUTHORITY, StockInfoContract.TABLE_NAME + "/#", STOCK_INFO_ID);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = AppDataBase.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query: called with URI " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "query: match is " + match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch(match){
            case ARTICLE:
                queryBuilder.setTables(ArticleContract.TABLE_NAME);
                break;
            case ARTICLE_ID:
                queryBuilder.setTables(ArticleContract.TABLE_NAME);
                long articleId = ArticleContract.getArticleId(uri);
                queryBuilder.appendWhere(ArticleContract.Columns._ID + " = " + articleId);
                break;

//            case ARTICLE_STOCK:
//                queryBuilder.setTables(StockContract.TABLE_NAME);
//                break;
//            case ARTICLE_STOCK_ID:
//                queryBuilder.setTables(ArticleContract.TABLE_NAME);
//                long stockId = StockContract.getStockId(uri);
//                queryBuilder.appendWhere(StockContract.Columns._ID + " = " + stockId);
//                break;
//
//            case STOCK_INFO:
//                queryBuilder.setTables(StockInfoContract.TABLE_NAME);
//                break;
//            case STOCK_INFO_ID:
//                queryBuilder.setTables(ArticleContract.TABLE_NAME);
//                long stockInfoId = StockInfoContract.getStockInfoId(uri);
//                queryBuilder.appendWhere(StockInfoContract.Columns._ID + " = " + stockInfoId);
//                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
