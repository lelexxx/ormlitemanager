package com.lelexx.ormlitemanager.database;

/**
 * WhereCondition allow you to create where condition easily.
 *
 * @author lelexxx <rruiz.alex@gmail.com>, french developper
 */
public class WhereCondition {

    public static class Junction {

        public static final int AND = 1;

        public static final int OR = 2;

        private static boolean isAvailable(int junction){
            return junction == AND || junction == OR;
        }
    }

    public static class Compare {

        public static final int EQUAL = 1;

        public static final int NOTEQUAL = 2;

        public static final int LIKE = 3;

        public static final int NOTLIKE = 4;
		
		public static final int GREATER = 5;
		
		public static final int LOWER = 6;

        private static boolean isAvailable(int compare){
            return compare == EQUAL || compare == NOTEQUAL || compare == LIKE || compare == NOTLIKE || compare == GREATER || compare == LOWER;
        }
    }

    private String mColumn;

    private Object mValue;

    private int mJunction;

    private int mCompare;

    /** Construct
     *
     * @param column Column name tpo check
     * @param compare id of a compare {@link Compare }
     * @param value Value to test. It must be primary type or String
     * @param junction id of a junction {@link Junction }
     */
    public WhereCondition(String column, int compare, Object value, int junction) {
        if(!Compare.isAvailable(compare)){
            throw new IllegalArgumentException("Second argument isn't an avaible compare.");
        }

        if( !(value instanceof Number) && !(value instanceof String) ){
            throw new IllegalArgumentException("Third argument must be a string or a number.");
        }

        this.mColumn = column;
        this.mValue = value;
        this.mJunction = junction;
        this.mCompare = compare;
    }

    public String getColumn() {
        return mColumn;
    }

    public Object getValue() {
        return mValue;
    }

    public int getJunction() {
        return mJunction;
    }

    public int getCompare() {
        return this.mCompare;
    }
}
