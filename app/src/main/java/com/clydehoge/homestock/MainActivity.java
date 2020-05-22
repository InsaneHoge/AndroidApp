package com.clydehoge.homestock;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

/**
 * By Clyde Hogenstijn 20-05-20
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[] projection = { ArticleContract.Columns._ID,
                                ArticleContract.Columns.ARTICLE_NAME,
                                ArticleContract.Columns.ARTICLE_DESCRIPTION,
                                ArticleContract.Columns.ARTICLE_SORTORDER};

        ContentResolver contentResolver = getContentResolver();



/*
        ContentValues values = new ContentValues();

        here we are deleting column with id "4":
        int count = contentResolver.delete(ArticleContract.buildArticleUri(4), null, null);

        Here we are deleting all columns with a description of "Mag weg". This method is used to delete multiple columns and to prevent SQL injection attack:
        String selection = ArticleContract.Columns.ARTICLE_DESCRIPTION + " =?";
        String[] args = { "Mag weg" };
        int count = contentResolver.delete(ArticleContract.CONTENT_URI, selection, args);

        Sample code to test inserting new data into db:
        values.put(ArticleContract.Columns.ARTICLE_NAME, "New article");
        values.put(ArticleContract.Columns.ARTICLE_DESCRIPTION, "Beschrijving");
        values.put(ArticleContract.Columns.ARTICLE_SORTORDER, 2);
        Uri uri = contentResolver.insert(ArticleContract.CONTENT_URI, values);

        Sample code to test updating a column in the db:
        values.put(ArticleContract.Columns.ARTICLE_NAME, "Pickwick Thee");
        values.put(ArticleContract.Columns.ARTICLE_DESCRIPTION, "DUTCH blend");
        values.put(ArticleContract.Columns.ARTICLE_SORTORDER, 4);
        int count = contentResolver.update(ArticleContract.buildArticleUri(5), values, null, null);
        Log.d(TAG, "onCreate: " + count + " record(s) updated");
*/

        Cursor cursor = contentResolver.query(ArticleContract.CONTENT_URI,
                projection,
                null,
                null,
                ArticleContract.Columns.ARTICLE_NAME);

        if(cursor != null){
            Log.d(TAG, "onCreate: number of rows: " + cursor.getCount());
            while (cursor.moveToNext()){
                for(int i=0; i<cursor.getColumnCount(); i++){
                    Log.d(TAG, "onCreate: " + cursor.getColumnName(i) + ": " + cursor.getString(i));
                }
                Log.d(TAG, "onCreate: =.=.=.=.=.=.=.=.=.=.=.=.=.=.=.=.=.=.=.=");
            }
            cursor.close();
        }

//        used for first test
//        AppDataBase appDataBase = AppDataBase.getInstance(this);
//        final SQLiteDatabase db = appDataBase.getReadableDatabase();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
