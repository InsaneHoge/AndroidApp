package com.clydehoge.homestock;

import java.io.Serializable;

/**
 * By Clyde Hogenstijn 26-05-2020
 *
 */

class Article implements Serializable {
    public static final long serialVersionUID = 26052020L; //using current date as number

    private long m_Id;
    private final String mName;
    private final String mDescription;
    private final int mSortOrder;

    public Article(long id, String mName, String mDescription, int mSortOrder) {
        this.m_Id = id;
        this.mName = mName;
        this.mDescription = mDescription;
        this.mSortOrder = mSortOrder;
    }

    public long getId() {
        return m_Id;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getSortOrder() {
        return mSortOrder;
    }

    public void setId(long id) {
        this.m_Id = id;
    }

    @Override
    public String toString() {
        return "Article{" +
                "m_Id=" + m_Id +
                ", mName='" + mName + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mSortOrder=" + mSortOrder +
                '}';
    }
}
