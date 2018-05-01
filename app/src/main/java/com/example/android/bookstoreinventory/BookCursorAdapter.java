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
    private Context context;
    private OnSaleBtnClickListener saleBtnClickListener;
    private int id;

    public interface OnSaleBtnClickListener{
        public void onSaleBtnClick(int rowId);
    }

    public BookCursorAdapter(Context context, Cursor cursor){
        super(context,cursor,0);
        this.context = context;
        try{
            saleBtnClickListener = (OnSaleBtnClickListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement OnSaleBtnClickListener");
        }
    }
   @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v =(View) LayoutInflater.from(context).inflate(R.layout.book_item,parent,false);
        int idColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_ID);
        int rowId = cursor.getInt(idColumnIndex);
       Button saleBtn = (Button) v.findViewById(R.id.sale_btn);
       saleBtn.setTag(rowId);
        return v;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView cursorIdV= (TextView) view.findViewById(R.id.cursor_id);
        TextView nameTextV = (TextView) view.findViewById(R.id.book_name);
        TextView priceTextV = (TextView) view.findViewById(R.id.book_price);
        TextView quantityTextV = (TextView) view.findViewById(R.id.book_quantity);
        final Button saleBtn = (Button) view.findViewById(R.id.sale_btn);

        int idColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_ID);
        int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_QUANTITY);

        final int cursorId = cursor.getInt(idColumnIndex);
        String name = cursor.getString(nameColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        int cursorQuantity = cursor.getInt(quantityColumnIndex);

        cursorIdV.setText(String.valueOf(cursorId));
        nameTextV.setText(name);
        priceTextV.setText(String.valueOf(price));
        quantityTextV.setText(String.valueOf(cursorQuantity));

        saleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = (Integer) saleBtn.getTag();
                saleBtnClickListener.onSaleBtnClick(id);
            }
        });
    }
}
