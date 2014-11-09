package com.lelexx.ormlitemanager.database;

/**
 * OrderBySort allow you to create orderby condition easily.
 *
 * @author lelexxx <rruiz.alex@gmail.com>, french developper
 */
public class OrderBySort {

    private String mField;

    private boolean mIsAscending;

    public OrderBySort(String field, boolean isAscending) {
        this.mField = field;
        this.mIsAscending = isAscending;
    }

    public String getField() {
        return mField;
    }

    public boolean isAscending() {
        return mIsAscending;
    }
}
