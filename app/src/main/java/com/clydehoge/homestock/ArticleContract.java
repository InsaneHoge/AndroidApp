package com.clydehoge.homestock;

import android.provider.BaseColumns;

public class ArticleContract {

    static final String TABLE_NAME = "Article";

    //Article fields
    public static class Columns{
        public static final String _ID = BaseColumns._ID;
        public static final String ARTICLE_NAME = "Name";
        public static final String ARTICLE_DESCRIPTION = "Description";
        public static final String ARTICLE_SORTORDER = "SortOrder";

        private Columns(){
            // prevent instantiation
        }
    }
}
