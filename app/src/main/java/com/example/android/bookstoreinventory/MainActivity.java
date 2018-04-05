package com.example.android.bookstoreinventory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private BookDbOpenHelper bookDbOpenHelper;
    private final String dataInsertResult = "Insert book";
    private final String dataReadResult = "Retrieved book";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bookDbOpenHelper = new BookDbOpenHelper(this);
        insertBook();
        readBook();
    }

    private void insertBook(){
        SQLiteDatabase db = bookDbOpenHelper.getWritableDatabase();
        ContentValues book1 = new ContentValues();
        book1.put(BookContract.BookEntry.TABLE_COLUMN_NAME,"EAT PRAY LOVE");
        book1.put(BookContract.BookEntry.TABLE_COLUMN_PRICE,19.99);
        book1.put(BookContract.BookEntry.TABLE_COLUMN_QUANTITY,20);
        book1.put(BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_NAME,"ABC MAGAZINE");
        book1.put(BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_PHONE_NUMBER,1234);

        long rowId = db.insert(BookContract.BookEntry.TABLE_NAME,null,book1);
        if (rowId == -1){
            Log.i(dataInsertResult,"failed");
        }else {
            Log.i(dataInsertResult,"successful" + rowId);
        }
    }

    private void readBook(){
        SQLiteDatabase db = bookDbOpenHelper.getReadableDatabase();
        String[] columns = new String[]{BookContract.BookEntry.TABLE_COLUMN_NAME, BookContract.BookEntry.TABLE_COLUMN_PRICE};
        String selection = BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_NAME + "=?";
        String[] selectionArgs = new String[]{"ABC MAGAZINE"};
        Cursor cursor = db.query(BookContract.BookEntry.TABLE_NAME,columns,selection,selectionArgs,null,null,null);

        try{
            while (cursor.moveToNext()){
                int nameIndex = cursor.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_NAME);
                int priceIndex = cursor.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_PRICE);
                String name = cursor.getString(nameIndex);
                double price = cursor.getDouble(priceIndex);
                Log.i(dataReadResult,"name: "+ name + " price: " + price);
            }
        } finally {
            cursor.close();
        }
    }
}
