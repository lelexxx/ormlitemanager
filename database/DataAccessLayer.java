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
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * An implementation of {@link SQLiteOpenHelper} database helper with ormlite library
 *
 * ormlite library documention could be find here : http://www.ormlite.com.
 *
 * @author lelexxx <rruiz.alex@gmail.com>, french developper
 */
public class DataAccessLayer extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "DATA_BASE_NAME.sqlite"; /// TO-DO Change with your database name

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
     * @return Single instance of DataAccessLayer class.
     */
    public static DataAccessLayer getInstance(Context context, List<Class> classes){
        if(mInstance == null){
            mInstance = new DataAccessLayer(context, classes);
        }

        return mInstance;
    }

    /** Private construct
     *
     * @param context Context Application context
     */
    private DataAccessLayer(Context context, List<Class> classes) {
        super(context, DATABASE_NAME, null, 1);

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

    /** Create all table {@see #mCLasses}, define when object was create.
     *
     * @param pConnectionSource
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
     * Retrieve all object, for a given class, of database
     *
     * @param pClass
     * @param <T>
     *
     * @return A List of T objects
     */
    public <T> List<T> selectAllData(final Class<T> pClass) {
        return selectDatas(pClass, null, null, 0);
    }

    /** Select data which respect where closure.
     *
     * @param pClass
     * @param where
     * @param orderBy
     * @param limit
     * @param <T>
     * @return
     */
    public <T> List<T> selectDatas(final Class<T> pClass, final WhereClosure where, final OrderClosure orderBy, final long limit) {
        List<T> ret = null;
        ConnectionSource connectionSource = new AndroidConnectionSource(this);
        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DaoManager.createDao(connectionSource, pClass);

            if (dao != null) {
                QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();

                if (where != null && where.getField() != null)
                    queryBuilder.where().eq(where.getField(), where.getValue());

                if (orderBy != null)
                    queryBuilder.orderBy(orderBy.getField(), orderBy.isAscending());

                if (limit != 0)
                    queryBuilder.limit(limit);

                ret = queryBuilder.query();
            }
        }
        catch (SQLException e) {
            Log.e("DataAccessLayer.selectEqData", e.getMessage());
        }
        catch (Exception e) {
            Log.e("DataAccessLayer.getAllData", e.getMessage());
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
     * @param <T>
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
            Log.e("DataAccessLayer.storeSingleData", e.getMessage());
        }
        catch (Exception e) {
            Log.e("DataAccessLayer.storeSingleData", e.getMessage());
        }
        finally {
            connectionSource.closeQuietly();
        }
    }

    //endregion

    //region DELETE METHODS

    /** Clear all data in T table
     *
     * @param pClass
     * @param <T>
     * @return
     */
    public <T> int clearTable(final Class<T> pClass){
        int ret = 0;
        ConnectionSource connectionSource = new AndroidConnectionSource(this);
        try {
            TableUtils.clearTable(connectionSource, pClass);
        }
        catch (SQLException e) {
            Log.e("DataAccessLayer.deleteEqDatas", e.getMessage());
        }
        catch (Exception e) {
            Log.e("DataAccessLayer.storeSingleData", e.getMessage());
        }
        finally {
            connectionSource.closeQuietly();
        }

        return ret;
    }

    /** Delete objects of type T, which respect where close
     *
     * @param pClass
     * @param where
     * @param <T>
     *
     * @return int, number of deleted row
     */
    public <T> int deleteDatas(final Class<T> pClass, WhereClosure where) {
        int ret = 0;
        ConnectionSource connectionSource = new AndroidConnectionSource(this);

        try {
            Dao<T, Integer> dao = (Dao<T, Integer>) DaoManager.createDao(connectionSource, pClass);

            if (dao != null) {
                DeleteBuilder<T, Integer> deleteBuilder = dao.deleteBuilder();

                if (where != null && where.getField() != null)
                    deleteBuilder.where().eq(where.getField(), where.getValue());

                ret = deleteBuilder.delete();
            }
        }
        catch (SQLException e) {
            Log.e("DataAccessLayer.deleteEqDatas", e.getMessage());
        }
        catch (Exception e) {
            Log.e("DataAccessLayer.deleteEqDatas", e.getMessage());
        }
        finally {
            connectionSource.closeQuietly();
        }

        return ret;
    }

    //endregion
}
