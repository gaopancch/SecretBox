package com.gaopan.serectbox.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by gaopan on 2017/5/26.
 */

public class DataBaseUtils {

    /**创建表，此方法主要用于根据用户名生成category表*/
    public static void createNewTable(SQLiteDatabase sqLiteDatabase){
        try {
            //如果传入的table全部都是数字会出错,如果该表已经存在也会报错
            String createTable="CREATE TABLE "+ ConstantUtils.CATEGORY_TABLE_NAME + "("
                    + "_id INTEGER PRIMARY KEY,"
                    + ConstantUtils.CATEGORY +");";
            sqLiteDatabase.execSQL(createTable);
        }catch (Exception e){

        }
    }

    /**创建表，此方法主要用于将category条目生成对应的表*/
    public static void createNewTable(SQLiteDatabase sqLiteDatabase,String table){
        try {
            //如果传入的table全部都是数字会出错,如果该表已经存在也会报错
            String createTable="CREATE TABLE "+ table+ "("
                    + "_id INTEGER PRIMARY KEY,"
                    + ConstantUtils.ITEM_TITLE+","
                    +ConstantUtils.ITEM_MESSAGE+");";
            sqLiteDatabase.execSQL(createTable);
        }catch (Exception e){

        }
    }

    /**删除数据库中的表*/
    public static void deleteTable(SQLiteDatabase sqLiteDatabase,String table){
        String sql=" DROP TABLE IF EXISTS "+table;
        sqLiteDatabase.execSQL(sql);
//      sqLiteDatabase.delete(table,null,null);
    }

    /**打印数据库中的所有表*/
    public static void printAllTable(SQLiteDatabase sqLiteDatabase){
        Cursor cursor = sqLiteDatabase.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while(cursor.moveToNext()){
            //遍历出表名
            String name = cursor.getString(0);
            Log.i("sqLiteDatabase", name);
        }
    }

    /**向表中插入数据，用于category表插入数据*/
    public static long insertDataToCategoryTable(SQLiteDatabase db, String table, String category){
        //如果返回-1则说明插入数据失败
//        StringUtils insertSql="insert into "+ConstantUtils.CATEGORY_TABLE_NAME+" values(null,?,?)";
//        db.execSQL(insertSql,new StringUtils[]{category});
//        return 0;
        ContentValues values=new ContentValues();
        values.put(ConstantUtils.CATEGORY,category);
        return db.insert(table,null,values);
    }

    /**用于向每一条category对应的表插入数据*/
    public static long insertDataToItemTable(SQLiteDatabase db, String table, String title,String message){
        //如果返回-1则说明插入数据失败
//        String insertSql="insert into "+ConstantUtils.CATEGORY_TABLE_NAME+" values(null,?,?)";
//        db.execSQL(insertSql,new StringUtils[]{category});
//        return 0;
        ContentValues values=new ContentValues();
        values.put(ConstantUtils.ITEM_TITLE,title);
        values.put(ConstantUtils.ITEM_MESSAGE,message);
        return db.insert(table,null,values);
    }

    /**删除category表中的数据，即删除分类*/
    public static int deleteCategoryData(SQLiteDatabase db, String table, String content){
        int raw = db.delete(table, ConstantUtils.CATEGORY+"=?", new String[]{content});
        Log.i("deleteCategoryData","deleteCategoryData raw="+raw);
        return raw;
    }

    /**删除item数据*/
    public static int deleteItemData(SQLiteDatabase db, String table, String title){
        int raw = db.delete(table, ConstantUtils.ITEM_TITLE+"=?", new String[]{title});
        Log.i("deleteCategoryData","deleteCategoryData raw="+raw);
        return raw;
    }

    /**修改item数据*/
    public static int updateItemData(SQLiteDatabase db, String table, String title,String message){
        ContentValues values=new ContentValues();
        values.put(ConstantUtils.ITEM_TITLE,title);
        values.put(ConstantUtils.ITEM_MESSAGE,message);
        int raw = db.update(table, values,ConstantUtils.ITEM_TITLE+"=?", new String[]{title});
        Log.i("updateItemData","updateItemData raw="+raw);
        return raw;
    }

}
