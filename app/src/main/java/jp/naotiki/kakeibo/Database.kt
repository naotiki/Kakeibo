package jp.naotiki.kakeibo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DBHelper(context: Context, databaseName:String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, databaseName, factory, version) {

    override fun onCreate(database: SQLiteDatabase?) {
        database?.execSQL("create table if not exists KakeiboTable (_id integer primary key autoincrement,year text,month text,day text, product text, price integer, tag text)");
    }

    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            database?.execSQL("alter table KakeiboTable add column deleteFlag integer default 0")
        }
    }
}