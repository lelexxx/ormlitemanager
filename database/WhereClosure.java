package com.lelexx.ormlitemanager.database;

public class WhereClosure {

    private String mField;

    private Object mValue;

    public WhereClosure(String field, Object value) {
        this.mField = field;
        this.mValue = value;
    }

    public String getField() {
        return mField;
    }

    public Object getValue() {
        return mValue;
    }
}
