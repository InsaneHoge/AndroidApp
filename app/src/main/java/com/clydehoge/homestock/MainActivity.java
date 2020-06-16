package com.clydehoge.homestock;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * By Clyde Hogenstijn 20-05-20
 */

public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdaptor.OnArticleClickListener,
        AddEditActivityFragment.OnSaveClicked, AppDialog.DialogEvents {
    private static final String TAG = "MainActivity";

    /*
    mTwoPane is to see if  we are in 2-pane mode.
    i.e. running in landscape on a tablet
     */
    private boolean mTwoPane = false;

    private static final String ADD_EDIT_FRAGMENT = "AddEditFragment";
    public static final int DELETE_DIALOG_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.articles_details_container) != null) {
            //The detail container will only be present in the large-screen layouts (res/values-land and res/values-sw600dp).
            //If this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public void onSaveClicked() {
        Log.d(TAG, "onSaveClicked: starts");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.articles_details_container);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();

//            does the same as code above:
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.remove(fragment);
//            fragmentTransaction.commit();
        }
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
        switch (id) {
            case R.id.menumain_addArticle:
                articleEditRequest(null);
                break;
            case R.id.menumain_viewArticleStock:
                break;
            case R.id.menumain_settings:
                break;
            case R.id.menumain_showAbout:
                break;
            case R.id.menumain_generate:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditClick(Article article) {
        articleEditRequest(article);
    }

    @Override
    public void onDeleteClick(Article article) {
        Log.d(TAG, "onDeleteClick: starts");

        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DELETE_DIALOG_ID);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deldiag_message, article.getId(), article.getName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);

        args.putLong("ArticleID", article.getId());

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);

//        getContentResolver().delete(ArticleContract.buildArticleUri(article.getId()), null, null);
    }

    private void articleEditRequest(Article article) {
        Log.d(TAG, "articleEditRequest: starts");
        if (mTwoPane) {
            Log.d(TAG, "articleEditRequest: in two-pane mode (tablet)");
            AddEditActivityFragment fragment = new AddEditActivityFragment();

            Bundle arguments = new Bundle();
            arguments.putSerializable(Article.class.getSimpleName(), article);
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction().replace(R.id.articles_details_container, fragment).commit();

//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.articles_details_container, fragment); // replace calls remove and then add method. will work even if there are no fragments to replace
//            fragmentTransaction.commit();
        } else {
            Log.d(TAG, "articleEditRequest: in single-pane mode (phone)");
            //in single-pane mode, start the detail activity for the selected item Id.
            Intent detailIntent = new Intent(this, AddEditActivity.class);
            if (article != null) { //edit a article
                detailIntent.putExtra(Article.class.getSimpleName(), article);
                startActivity(detailIntent);
            } else { // add new article
                startActivity((detailIntent));
            }
        }
    }

    @Override
    public void onPositiveDialogResult(int dialogID, Bundle arg) {
        Log.d(TAG, "onPositiveDialogResult: called");
        Long articleID = arg.getLong("ArticleID");
        if(BuildConfig.DEBUG && articleID == 0) throw new AssertionError("Article ID is zero!"); //This code is only used for testing. It will not appear in the final code for the app.
        getContentResolver().delete(ArticleContract.buildArticleUri(articleID), null, null);
    }

    @Override
    public void onNegativeDialogResult(int dialogID, Bundle arg) {
        Log.d(TAG, "onNegativeDialogResult: called");
    }

    @Override
    public void onDialogCancelled(int dialogID) {
        Log.d(TAG, "onDialogCancelled: called");
    }
}



/*
        code used for initial testing:

        String[] projection = {ArticleContract.Columns._ID,
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

/*
    Cursor cursor = contentResolver.query(ArticleContract.CONTENT_URI,
            projection,
            null,
            null,
            ArticleContract.Columns.ARTICLE_NAME);

        if (cursor != null) {
                Log.d(TAG, "onCreate: number of rows: " + cursor.getCount());
                while (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
        Log.d(TAG, "onCreate: " + cursor.getColumnName(i) + ": " + cursor.getString(i));
        }
        Log.d(TAG, "onCreate: =.=.=.=.=.=.=.=.=.=.=.=.=.=.=.=.=.=.=.=");
        }
        cursor.close();
        }

//        used for first test
//        AppDataBase appDataBase = AppDataBase.getInstance(this);
//        final SQLiteDatabase db = appDataBase.getReadableDatabase();
 */