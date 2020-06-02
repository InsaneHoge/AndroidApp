package com.clydehoge.homestock;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * created by Clyde Hogenstijn 31-05-20
 */

class CursorRecyclerViewAdaptor extends RecyclerView.Adapter<CursorRecyclerViewAdaptor.ArticleViewHolder> {
    private static final String TAG = "CursorRecyclerViewAdapt";
    private Cursor mCursor;
    private OnArticleClickListener mListener;

    interface OnArticleClickListener {
        void onEditClick(Article article);

        void onDeleteClick(Article article);
    }

    public CursorRecyclerViewAdaptor(Cursor mCursor, OnArticleClickListener mlistener) {
        Log.d(TAG, "CursorRecyclerViewAdaptor: Constructor called");
        this.mCursor = mCursor;
        this.mListener = mlistener;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_list_items, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
//        Log.d(TAG, "onBindViewHolder: starts with position " + position);

        if ((mCursor == null) || (mCursor.getCount() == 0)) {
            Log.d(TAG, "onBindViewHolder: providing instructions");
            holder.name.setText(R.string.instructions_header);
            holder.description.setText(R.string.instructions);
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        } else {
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }

            final Article article = new Article(mCursor.getLong(mCursor.getColumnIndex(ArticleContract.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndex(ArticleContract.Columns.ARTICLE_NAME)),
                    mCursor.getString(mCursor.getColumnIndex(ArticleContract.Columns.ARTICLE_DESCRIPTION)),
                    mCursor.getInt(mCursor.getColumnIndex(ArticleContract.Columns.ARTICLE_SORTORDER)));

            holder.name.setText(article.getName());
            holder.description.setText(article.getDescription());
            holder.editButton.setVisibility(View.VISIBLE); //TODO add onClickListener
            holder.deleteButton.setVisibility(View.VISIBLE); //TODO add onClickListener

            View.OnClickListener buttonListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.d(TAG, "onClick: starts");
                    switch (v.getId()) {
                        case R.id.ali_edit:
                            if (mListener != null) {
                                mListener.onEditClick(article);
                            }
                            break;
                        case R.id.ali_delete_article:
                            if (mListener != null) {
                                mListener.onDeleteClick(article);
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("onClick found unexpected button with id " + v.getId());
                    }

//                    Log.d(TAG, "onClick: button with id " + v.getId() + " clicked");
//                    Log.d(TAG, "onClick: article name is " + article.getName());
                }
            };

            holder.editButton.setOnClickListener(buttonListener);
            holder.deleteButton.setOnClickListener(buttonListener);
        }

    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount: starts");
        if ((mCursor == null) || (mCursor.getCount() == 0)) {
            return 1; // We need to populate a single Viewholder with instructions
        } else {
//            Log.d(TAG, "getItemCount: mCursor count is " + mCursor.getCount());
            return mCursor.getCount();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.
     * The returned old Cursor is <em>not</em> closed
     *
     * @param newCursor: the new Cursor to be used
     * @return Retruns the previously set Cursor, or null if there wasn't one.
     * Note: if the given new Cursor is the same instance as the previously set
     * Cursor, null is also returned.
     */
    Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null) {
            // notify the observers about the new Cursor
            notifyDataSetChanged();
        } else {
            //notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ArticleViewHolder";

        TextView name = null;
        TextView description = null;
        ImageButton editButton = null;
        ImageButton deleteButton = null;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
//            Log.d(TAG, "ArticleViewHolder: starts");

            this.name = (TextView) itemView.findViewById(R.id.atl_name);
            this.description = (TextView) itemView.findViewById(R.id.ali_description);
            this.editButton = (ImageButton) itemView.findViewById(R.id.ali_edit);
            this.deleteButton = (ImageButton) itemView.findViewById(R.id.ali_delete_article);
        }
    }

}
