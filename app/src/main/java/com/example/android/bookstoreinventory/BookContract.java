package com.example.android.bookstoreinventory;

import android.provider.BaseColumns;

public class BookContract {
    private BookContract(){}
    public class BookEntry implements BaseColumns{
        static final String TABLE_NAME = "inventory";
        static final String TABLE_COLUMN_ID = BaseColumns._ID;
        static final String TABLE_COLUMN_NAME = "product_name";
        static final String TABLE_COLUMN_PRICE = "price";
        static final String TABLE_COLUMN_QUANTITY = "quantity";
        static final String TABLE_COLUMN_SUPPLIER_NAME = "supplier_name";
        static final String TABLE_COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    }
}
