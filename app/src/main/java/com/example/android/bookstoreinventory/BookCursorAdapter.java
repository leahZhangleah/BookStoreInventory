package com.example.android.bookstoreinventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookstoreinventory.data.BookContract;

public class BookCursorAdapter extends CursorAdapter {
    int quantity;

    public BookCursorAdapter(Context context, Cursor cursor){
        super(context,cursor,0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.book_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameText = (TextView) view.findViewById(R.id.book_name);
        TextView priceText = (TextView) view.findViewById(R.id.book_price);
        TextView quantityText = (TextView) view.findViewById(R.id.book_quantity);
        Button saleBtn = (Button) view.findViewById(R.id.sale_btn);

        int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_QUANTITY);

        String name = cursor.getString(nameColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        quantity = cursor.getInt(quantityColumnIndex);

        nameText.setText(name);
        priceText.setText(String.valueOf(price));
        quantityText.setText(String.valueOf(quantity));
        //todo: set method for salebtn
    }
}
