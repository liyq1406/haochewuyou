package com.haoche51.buyerapp.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.haoche51.buyerapp.dao.BrandDAO;
import com.haoche51.buyerapp.dao.SeriesDAO;
import com.haoche51.buyerapp.util.HCSpUtils;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "haoche51.db";
    private static final int DB_VERSION = 10; //brand 表添加字段

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BrandDAO.CREATE_TABLE);
        db.execSQL(SeriesDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BrandDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SeriesDAO.TABLE_NAME);
        onUpgradeDb(db);
    }

    private void onUpgradeDb(SQLiteDatabase db) {
        onCreate(db);
        //重新导入车系表数据
        HCSpUtils.saveData(HCSpUtils.HAS_IMPORT_SERIES_TABLE, false);
    }

    @Override
    protected void finalize() throws Throwable {
        getWritableDatabase().close();
        Log.d("hcwywellcome", "finalize-----------when db close-----------");
        super.finalize();
    }

    public static void query() {

    }
}
