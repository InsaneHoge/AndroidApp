package com.clydehoge.homestock;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class AddEditActivityFragment extends Fragment {
    private static final String TAG = "AddEditActivityFragment";

    public enum FragmentEditMode {EDIT, ADD}

    private FragmentEditMode mMode;

    private EditText mArticleTextView;
    private EditText mDescriptionTextView;
    private EditText mSortOrderTextView;
    private Button mAddButton;
    private OnSaveClicked mSaveListener = null;

    interface OnSaveClicked {
        void onSaveClicked();
    }

    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: starts");
        super.onAttach(context);

        //Activities containing this fragment must implement it's callbacks
        Activity activity = getActivity();
        if (!(activity instanceof OnSaveClicked)) {
            throw new ClassCastException(activity.getClass().getSimpleName() + " must implement AddEditActivityFragment.OnSaveClicked interface.");
        }

        mSaveListener = (OnSaveClicked) activity;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: starts");
        super.onDetach();
        mSaveListener = null;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        mArticleTextView = (EditText) view.findViewById(R.id.addedit_name);
        mDescriptionTextView = (EditText) view.findViewById(R.id.addedit_description);
        mSortOrderTextView = (EditText) view.findViewById(R.id.addedit_sortorder);
        mAddButton = (Button) view.findViewById(R.id.addedit_save);

        Bundle arguments = getArguments();

        final Article article;
        if (arguments != null) {
            Log.d(TAG, "onCreateView: retrieving article details.");

            article = (Article) arguments.getSerializable(Article.class.getSimpleName());
            if (article != null) {
                Log.d(TAG, "onCreateView: Article details found, editing...");
                mArticleTextView.setText(article.getName());
                mDescriptionTextView.setText(article.getDescription());
                mSortOrderTextView.setText(Integer.toString(article.getSortOrder()));
                mMode = FragmentEditMode.EDIT;
            } else {
                //No article, so we must add a new article and not eddit an existing one
                mMode = FragmentEditMode.ADD;
            }
        } else {
            article = null;
            Log.d(TAG, "onCreateView: No arguments, adding new record");
            mMode = FragmentEditMode.ADD;
        }

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Update the data base if at least one field has changed.
                There is no need to access the database unless this has happened.
                 */
                int so; //to save repeated conversions to int. * might change this for "barcode"
                if (mSortOrderTextView.length() > 0) {
                    so = Integer.parseInt(mSortOrderTextView.getText().toString());
                } else {
                    so = 0;
                }

                ContentResolver contentResolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();

                switch (mMode) {
                    case EDIT:
                        if (!mArticleTextView.getText().toString().equals(article.getName())) {
                            values.put(ArticleContract.Columns.ARTICLE_NAME, mArticleTextView.getText().toString());
                        }
                        if (!mDescriptionTextView.getText().toString().equals(article.getDescription())) {
                            values.put(ArticleContract.Columns.ARTICLE_DESCRIPTION, mDescriptionTextView.getText().toString());
                        }
                        if (so != 0) {
                            values.put(ArticleContract.Columns.ARTICLE_SORTORDER, so);
                        }
                        if (values.size() != 0) {
                            Log.d(TAG, "onClick: updating article");
                            contentResolver.update(ArticleContract.buildArticleUri(article.getId()), values, null, null);
                        }
                        break;
                    case ADD:
                        if (mArticleTextView.length() > 0) {
                            Log.d(TAG, "onClick: adding new article");
                            values.put(ArticleContract.Columns.ARTICLE_NAME, mArticleTextView.getText().toString());
                            values.put(ArticleContract.Columns.ARTICLE_DESCRIPTION, mDescriptionTextView.getText().toString());
                            values.put(ArticleContract.Columns.ARTICLE_SORTORDER, so);
                            contentResolver.insert(ArticleContract.CONTENT_URI, values);
                        }
                        break;
                }
                Log.d(TAG, "onClick: Done edeting");

                if (mSaveListener != null) {
                    mSaveListener.onSaveClicked();
                }
            }
        });
        Log.d(TAG, "onCreateView: Exiting...");

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
