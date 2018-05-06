package com.example.android.bookstoreinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDbOpenHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "book_store_inventory.db";
    static final int TABLE_VERSION = 1;
    public BookDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, TABLE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SQL_TABLE = "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME +"("
                + BookContract.BookEntry.TABLE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BookContract.BookEntry.TABLE_COLUMN_NAME + " TEXT NOT NULL,"
                + BookContract.BookEntry.TABLE_COLUMN_PRICE + " DECIMAL(2) NOT NULL,"
                + BookContract.BookEntry.TABLE_COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0,"
                + BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_NAME +" TEXT NOT NULL,"
                + BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_PHONE_NUMBER + " INTEGER NOT NULL,"
                + BookContract.BookEntry.TABLE_COLUMN_PHOTO + " TEXT"
                + ");";
        db.execSQL(CREATE_SQL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //nothing for now
    }
}
