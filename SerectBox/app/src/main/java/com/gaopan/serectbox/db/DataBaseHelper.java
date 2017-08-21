package com.gaopan.serectbox.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gaopan.serectbox.utils.ConstantUtils;

/**
 * Created by gaopan on 2017/5/26.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    SQLiteDatabase sqLiteDatabase;
//    String CREATE_TABLE_SQL="";

    public DataBaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
//        CREATE_TABLE_SQL="CREATE TABLE "+ ConstantUtils.CATEGORY_TABLE_NAME + "("
//                + "_id INTEGER PRIMARY KEY,"
//                + ConstantUtils.CATEGORY +");";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.sqLiteDatabase=db;
//        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
