package com.clydehoge.homestock;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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

    public static final int DIALOG_ID_DELETE = 1;
    public static final int DIALOG_ID_CANCEL_EDIT = 2;

    private AlertDialog mDialog = null; //module scope because we need to dismiss it in onStop

    //e.g. when orientation changes, to avoid memory leaks
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
                showAboutDialog();
                break;
            case R.id.menumain_generate:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showAboutDialog() {
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(messageView);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Log.d(TAG, "onClick: Entering messageView.onClick, showing= "+ mDialog.isShowing());
                if((mDialog != null && mDialog.isShowing())){
                    mDialog.dismiss();
                }
            }
        });

        builder.setTitle(R.string.app_name);
        builder.setIcon((R.mipmap.ic_launcher));

        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);

        TextView tv = (TextView) messageView.findViewById(R.id.about_version);
        tv.setText("v" + BuildConfig.VERSION_NAME);

        mDialog.show();
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
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_DELETE);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.deldiag_message, article.getId(), article.getName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);

        args.putLong("ArticleID", article.getId());

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
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
        switch (dialogID) {
            case DIALOG_ID_DELETE:
                Long articleID = arg.getLong("ArticleID");
                if (BuildConfig.DEBUG && articleID == 0)
                    throw new AssertionError("Article ID is zero!"); //This code is only used for testing. It will not appear in the final code for the app.
                getContentResolver().delete(ArticleContract.buildArticleUri(articleID), null, null);
            case DIALOG_ID_CANCEL_EDIT:
                //no action needed
                break;
        }
    }

    @Override
    public void onNegativeDialogResult(int dialogID, Bundle arg) {
        Log.d(TAG, "onNegativeDialogResult: called");
        switch (dialogID) {
            case DIALOG_ID_DELETE:
                //no action needed
                break;
            case DIALOG_ID_CANCEL_EDIT:
                AddEditActivityFragment fragment;
                FragmentManager fragmentManager = getSupportFragmentManager();

                fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.articles_details_container);
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
//                finish();
                break;
        }
    }

    @Override
    public void onDialogCancelled(int dialogID) {
        Log.d(TAG, "onDialogCancelled: called");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called");
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.articles_details_container);
        if ((fragment == null) || fragment.canClose()) {
            super.onBackPressed();
        } else {
            AppDialog dialog = new AppDialog();
            Bundle args = new Bundle();
            args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_CANCEL_EDIT);
            args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
            args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
            args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDiag_negative_caption);
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDialog != null && mDialog.isShowing()){
         mDialog.dismiss();
        }
    }
}
