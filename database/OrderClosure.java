package com.lelexx.ormlitemanager.database;

public class OrderClosure {

    private String mField;

    private boolean mIsAscending;

    public OrderClosure(String field, boolean isAscending) {
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
