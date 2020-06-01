package com.clydehoge.homestock;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import static com.clydehoge.homestock.AppProvider.CONTENT_AUTHORITY_URI;
import static com.clydehoge.homestock.AppProvider.CONTENT_AUTHORITY;

/**
 * By Clyde Hogenstijn 20-05-20
 */

public class ArticleContract {

    static final String TABLE_NAME = "Article";

    //Article fields
    public static class Columns{
        public static final String _ID = BaseColumns._ID;
        public static final String ARTICLE_NAME = "Name";   //TODO change to barcode and coressponding code
        public static final String ARTICLE_DESCRIPTION = "Description"; //TODO change in db to "not null"
        public static final String ARTICLE_SORTORDER = "SortOrder";
        // TODO public static final String ARTICLE_CATEGORY = "category";


        private Columns(){
            // prevent instantiation
        }
    }

    /**
     * The URI to access the Article table.
     */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME);

    static final String CONTENT_TYPE = "vnd.amdroid.cursor.dir/nvd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;

    static Uri buildArticleUri(long articleId){
        return ContentUris.withAppendedId(CONTENT_URI, articleId);
    }

    static long getArticleId( Uri uri){
        return ContentUris.parseId(uri);
    }
}
