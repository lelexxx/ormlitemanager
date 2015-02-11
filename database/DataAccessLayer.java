package com.lelexx.ormlitemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

import static com.lelexx.ormlitemanager.database.WhereCondition.*;

/**
 * An implementation of {@link SQLiteOpenHelper} database helper with ormlite library
 *
 * ormlite library documention could be find here : http://www.ormlite.com.
 *
 * @author lelexxx <rruiz.alex@gmail.com>, french developper
 */
public class DataAccessLayer extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "DATA_BASE_NAME.sqlite"; // TODO Change with your database name

    public static final int DATABASE_VERSION = 1; // TODO Change with your database version

    //region MEMBERS

    private List<Class> mClasses;

    //endregion

    //region SINGLETON

    /** Single instance */
    private static DataAccessLayer mInstance;

    /** Create single instance, if doesn't exist, and retrieve it
     *
     * @param context Application context
     *
     * @return Single instance of DataAccessLayer.
     */
    public static DataAccessLayer getInstance(Context context, List<Class> classes){
        if(mInstance == null){
            mInstance = new DataAccessLayer(context, classes);
        }

        return mInstance;
    }

    /** Private construct
     *
     * @param context Application context
     */
    private DataAccessLayer(Context context, List<Class> classes) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mClasses = classes;
    }

    //endregion

    //region INIT DATABASE

    @Override
    public void onCreate(final SQLiteDatabase db) {
        ConnectionSource connectionSource = new AndroidConnectionSource(db);
        try {
            for(Class c : mClasses){
                TableUtils.dropTable(connectionSource, c, true);
            }
            doCreate(connectionSource);
        }
        catch (SQLException e) {
            Log.e("DataAccessLayer.onCreate", e.getMessage());
        }
        finally {
            connectionSource.closeQuietly();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ConnectionSource connectionSource = new AndroidConnectionSource(db);
        try {
            for(Class c : mClasses){
                TableUtils.dropTable(connectionSource, c, true);
            }
            doCreate(connectionSource); // Create tables again
        }
        catch (SQLException e) {
            Log.e("DataAccessLayer.onUpgrade", e.getMessage());
        }
        finally {
            connectionSource.closeQuietly();
        }

    }

    /** Create all table {@see #mCLasses}.
     *
     * @param pConnectionSource Database connection
     */
    private void doCreate(final ConnectionSource pConnectionSource) {
        try {
            for(Class c : mClasses){
                TableUtils.createTable(pConnectionSource, c);
            }
        }
        catch (SQLException e) {
            Log.e("DataAccessLayer.doCreate", e.getMessage());
        }
    }

    //endregion

    //region SELECT METHODS

    /**
     * Retrieve all object, for a given class, in database
     *
     * @param pClass DataTable class to retrieve
     * @param <T> Object type to retrieve. T has to represent Class with DataTable attribute.
     *
     * @return A List of T objects
     */
    public <T> List<T> selectAllData(final Class<T> pClass) {
        return selectDatas(pClass, null, null, 0);
    }

    /** Retrieve objects from given condition.
     *
     * @param pClass DataTable class to retrieve
     * @param where Condition to respect
     * @param orderBy Element's sort
     * @param limit Maximum number of element; give 0 to unlimited
     * @param <T> Object type to retrieve. T has to represent Class with DataTable attribute.
     *
     * @return A List of T objects
     */
    public <T> List<T> selectDatas(final Class<T> pClass, final List<WhereCondition> where, final OrderBySort orderBy, final long limit) {
        List<T> ret = null;
        ConnectionSource connectionSource = new AndroidConnectionSource(this);
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DaoManager.createDao(connectionSource, pClass);

            if (dao != null) {
                QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();

                if (where != null && where.size() > 0){
                    Where<T, Integer> whereClosure = queryBuilder.where();
                    for(WhereCondition w : where) {
                        if (w.getColumn() != null && !w.getColumn().isEmpty()) {
                            whereClosure = buildJunction(whereClosure, w);
                        }
                    }
                    queryBuilder.setWhere(whereClosure);
                }

                if (orderBy != null)
                    queryBuilder.orderBy(orderBy.getField(), orderBy.isAscending());

                if (limit != 0)
                    queryBuilder.limit(limit);

                ret = queryBuilder.query();
            }
        }
        catch (SQLException e) {
            Log.e("DataAccessLayer.selectDatas", e.getMessage());
        }
        catch (Exception e) {
            Log.e("DataAccessLayer.selectDatas", e.getMessage());
        }
        finally {
            connectionSource.closeQuietly();
        }

        return ret;
    }

    //endregion

    //region INSERT-UPDATE METHODS

    /** Insert, or update if exist, object in database
     *
     * @param data Object to insert
     * @param <T> Object type to insert. T has to represent Class with DataTable attribute.
     */
    public <T> void insertData(final T data) {
        Class<?> type = data.getClass();
        ConnectionSource connectionSource = new AndroidConnectionSource(this);
        Dao<T, Integer> dao;

        try {
            dao = (Dao<T, Integer>)DaoManager.createDao(connectionSource, type);
            if (dao != null) {
                dao.createOrUpdate(data);
            }
        }
        catch (SQLException e) {
            Log.e("DataAccessLayer.insertData", e.getMessage());
        }
        catch (Exception e) {
            Log.e("DataAccessLayer.insertData", e.getMessage());
        }
        finally {
            connectionSource.closeQuietly();
        }
    }

    //endregion

    //region DELETE METHODS

    /** Clear all data in T table
     *
     * @param pClass DataTable class to clear
     * @param <T> Object type to insert. T has to represent Class with DataTable attribute.
     *
     * @return Number of deleted row
     */
    public <T> int clearTable(final Class<T> pClass){
        int ret = 0;
        ConnectionSource connectionSource = new AndroidConnectionSource(this);
        try {
            TableUtils.clearTable(connectionSource, pClass);
        }
        catch (SQLException e) {
            Log.e("DataAccessLayer.clearTable", e.getMessage());
        }
        catch (Exception e) {
            Log.e("DataAccessLayer.clearTable", e.getMessage());
        }
        finally {
            connectionSource.closeQuietly();
        }

        return ret;
    }

    /** Delete T objects, which respect where close
     *
     * @param pClass DataTable class where object will be delete.
     * @param where Condition to respect
     * @param <T>  Object type to delete. T has to represent Class with DataTable attribute.
     *
     * @return Number of deleted row
     */
    public <T> int deleteDatas(final Class<T> pClass, WhereCondition where) {
        int ret = 0;
        ConnectionSource connectionSource = new AndroidConnectionSource(this);

        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DaoManager.createDao(connectionSource, pClass);

            if (dao != null) {
                DeleteBuilder<T, Integer> deleteBuilder = dao.deleteBuilder();

                if (where != null && where.getColumn() != null) {
                    Where<T, Integer> whereClosure = deleteBuilder.where();
                    whereClosure = buildJunction(whereClosure, where);
                    deleteBuilder.setWhere(whereClosure);
                }

                ret = deleteBuilder.delete();
            }
        }
        catch (SQLException e) {
            Log.e("DataAccessLayer.deleteDatas", e.getMessage());
        }
        catch (Exception e) {
            Log.e("DataAccessLayer.deleteDatas", e.getMessage());
        }
        finally {
            connectionSource.closeQuietly();
        }

        return ret;
    }

    //endregion

    //region PRIVATE METHODS

    /** Add where condition depending on the {@link Junction}
     *
     * @param whereClosure Where statement
     * @param where Where description
     *
     * @return QueryBuilder with where condition
     *
     * @throws java.sql.SQLException
     */
    private <T> Where<T, Integer> buildJunction(Where<T, Integer> whereClosure, WhereCondition where) throws SQLException {
        switch (where.getJunction()){
            case Junction.AND :
                if(!whereClosure.toString().equals("empty where clause"))
                    whereClosure.and();
                whereClosure = buildCompare(whereClosure, where);
                break;
            case Junction.OR :
                if(!whereClosure.toString().equals("empty where clause"))
                    whereClosure.or();
                whereClosure = buildCompare(whereClosure, where);
                break;
        }

        return whereClosure;
    }

    /** Add where condition depending on the {@link Compare}
     *
     * @param whereClosure Where statement
     * @param where Where condition object
     *
     * @return QueryBuilder with where condition
     *
     * @throws java.sql.SQLException
     */
    private <T> Where<T, Integer> buildCompare(Where<T, Integer> whereClosure, WhereCondition where) throws SQLException{
        switch (where.getCompare()){
            case Compare.EQUAL :
                whereClosure.eq(where.getColumn(), where.getValue());
                break;
            case Compare.NOTEQUAL :
                whereClosure.not().eq(where.getColumn(), where.getValue());
                break;
            case Compare.LIKE :
                whereClosure.like(where.getColumn(), where.getValue());
                break;
            case Compare.NOTLIKE :
                whereClosure.not().like(where.getColumn(), where.getValue());
                break;
            case Compare.GREATER :
                whereClosure.gt(where.getColumn(), where.getValue());
                break;
            case Compare.LOWER :
                whereClosure.lt(where.getColumn(), where.getValue());
                break;
            default :
                return whereClosure;
        }

        return whereClosure;
    }

    //endregion
}
