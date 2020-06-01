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

        switch (match) {
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
//                queryBuilder.setTables(StockContract.TABLE_NAME);
//                long stockId = StockContract.getStockId(uri);
//                queryBuilder.appendWhere(StockContract.Columns._ID + " = " + stockId);
//                break;
//
//            case STOCK_INFO:
//                queryBuilder.setTables(StockInfoContract.TABLE_NAME);
//                break;
//            case STOCK_INFO_ID:
//                queryBuilder.setTables(StockInfoContract.TABLE_NAME);
//                long stockInfoId = StockInfoContract.getStockInfoId(uri);
//                queryBuilder.appendWhere(StockInfoContract.Columns._ID + " = " + stockInfoId);
//                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
//        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        Log.d(TAG, "query: rows in returned cursor are " + cursor.getCount());

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ARTICLE:
                return ArticleContract.CONTENT_TYPE;
            case ARTICLE_ID:
                return ArticleContract.CONTENT_ITEM_TYPE;

//            case ARTICLE_STOCK:
//                return StockContract.Stock.CONTENT_TYPE;
//            case ARTICLE_STOCK_ID:;
//                return StockContract.Stock.CONTENT_ITEM_TYPE;
//            case STOCK_INFO:
//                return StockInfoContract.StockInfo.CONTENT_TYPE;
//            case STOCK_INFO_ID:
//                return StockinfoContract.StockInfo.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "Entering insert, called with uri: " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);

        final SQLiteDatabase db;

        Uri returnUri;
        long recordId;

        switch (match) {
            case ARTICLE:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(ArticleContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = ArticleContract.buildArticleUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;
            case ARTICLE_STOCK:
//                db = mOpenHelper.getWritableDatabase();
//                recordId = db.insert(StockContract.Stock.buildStockUri(recordId));
//                if(recordId >=0){
//                    returnUri = StockContract.Stock.buildStockUri(recordId);
//                }else{
//                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
//                }
//                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        if(recordId >=0){
            //something was inserted
            Log.d(TAG, "insert: Setting notifyChanged with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        }else{
            Log.d(TAG, "insert: nothing inserted");
        }

        Log.d(TAG, "Exiting insert, returning : " + returnUri);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is: " + match);

        final SQLiteDatabase db;
        int count;
        String selectionCriteria;

        switch (match) {
            case ARTICLE:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(ArticleContract.TABLE_NAME, selection, selectionArgs);
                break;

            case ARTICLE_ID:
                db = mOpenHelper.getWritableDatabase();
                long articleId = ArticleContract.getArticleId(uri);
                selectionCriteria = ArticleContract.Columns._ID + " = " + articleId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.delete(ArticleContract.TABLE_NAME, selectionCriteria, selectionArgs);
                break;

//            case ARTICLE_STOCK:
//                db = mOpenHelper.getWritableDatabase();
//                count = db.delete(StockContract.TABLE_NAME, selection, selectionArgs);
//                break;
//
//            case ARTICLE_STOCK_ID:
//                db = mOpenHelper.getWritableDatabase();
//                long stockId = StockContract.getArticleId(uri);
//                selectionCriteria = StockContract.Columns._ID + " = " + stockId;
//
//                if((selection != null) && (selection.length()>0)){
//                    selectionCriteria += " AND (" + selection + ")";
//                }
//                count = db.delete(StockContract.TABLE_NAME, selectionCriteria, selectionArgs);
//                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        if (count >0){
            //something was changed
            Log.d(TAG, "delete: Setting notifyChange with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "delete: nothing deleted");
        }

        Log.d(TAG, "Exiting update, returning: " + count);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update called with uri " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is: " + match);

        final SQLiteDatabase db;
        int count;
        String selectionCriteria;

        switch (match) {
            case ARTICLE:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(ArticleContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case ARTICLE_ID:
                db = mOpenHelper.getWritableDatabase();
                long articleId = ArticleContract.getArticleId(uri);
                selectionCriteria = ArticleContract.Columns._ID + " = " + articleId;

                if ((selection != null) && (selection.length() > 0)) {
                    selectionCriteria += " AND (" + selection + ")";
                }
                count = db.update(ArticleContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

//            case ARTICLE_STOCK:
//                db = mOpenHelper.getWritableDatabase();
//                count = db.update(StockContract.TABLE_NAME, values, selection, selectionArgs);
//                break;
//
//            case ARTICLE_STOCK_ID:
//                db = mOpenHelper.getWritableDatabase();
//                long stockId = StockContract.getArticleId(uri);
//                selectionCriteria = StockContract.Columns._ID + " = " + stockId;
//
//                if((selection != null) && (selection.length()>0)){
//                    selectionCriteria += " AND (" + selection + ")";
//                }
//                count = db.update(StockContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
//                break;

            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        if (count >0){
            //something was updated
            Log.d(TAG, "update: Setting notifyChange with " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            Log.d(TAG, "update: nothing updated");
        }

        Log.d(TAG, "Exiting update, returning: " + count);
        return count;
    }
}
