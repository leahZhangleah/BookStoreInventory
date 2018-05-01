package com.example.android.bookstoreinventory;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bookstoreinventory.data.BookContract;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    ImageView mDetailPhotoV;
    TextView mDetailNameV,mDetailPriceV,mDetailQuantityV,mDetailSupplierV,mDetailSupplierPhoneV;
    ImageButton mcallBtn;
    private static final int LOADER_ID = 1;
    Uri uriForDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        uriForDetail = intent.getData();

        //mDetailPhotoV = (ImageView) findViewById(R.id.book_detail_photo);
        mDetailNameV = (TextView) findViewById(R.id.book_detail_name);
        mDetailPriceV = (TextView) findViewById(R.id.book_detail_price);
        mDetailQuantityV = (TextView) findViewById(R.id.book_detail_quantity);
        mDetailSupplierV = (TextView) findViewById(R.id.book_detail_supplier);
        mDetailSupplierPhoneV = (TextView) findViewById(R.id.book_detail_supplier_phone);
        mcallBtn = (ImageButton) findViewById(R.id.book_detail_phone_btn);

        mcallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.fromParts("tel",mDetailSupplierPhoneV.getText().toString(),null));
                startActivity(intent);
            }
        });

        //todo:set intent on photo view

        if (uriForDetail != null){
            getLoaderManager().initLoader(LOADER_ID,null,this);
        }else{
            throw new IllegalArgumentException("invalid url");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{BookContract.BookEntry.TABLE_COLUMN_ID,
                BookContract.BookEntry.TABLE_COLUMN_NAME,
                BookContract.BookEntry.TABLE_COLUMN_PRICE,
                BookContract.BookEntry.TABLE_COLUMN_QUANTITY,
                BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_NAME,
                BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_PHONE_NUMBER};
        switch (id){
            case LOADER_ID:
                return new CursorLoader(this,uriForDetail,projection,null,null,null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null){
            if (data.moveToFirst()){
                //int photoColumnIndex = data.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_PHOTO);
                int nameColumnIndex = data.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_NAME);
                int priceColumnIndex = data.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_PRICE);
                int quantityColumnIndex = data.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_QUANTITY);
                int supplierColumnIndex = data.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_NAME);
                int supplierPhoneColumnIndex = data.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_PHONE_NUMBER);

                //byte[] photoByte = data.getBlob(photoColumnIndex);
                String name = data.getString(nameColumnIndex);
                double price = data.getDouble(priceColumnIndex);
                int quantity = data.getInt(quantityColumnIndex);
                String supplier = data.getString(supplierColumnIndex);
                int supplierPhone = data.getInt(supplierPhoneColumnIndex);

                //todo: set photo for photoview

                mDetailNameV.setText(name);
                mDetailPriceV.setText(String.valueOf(price));
                mDetailQuantityV.setText(String.valueOf(quantity));
                mDetailSupplierV.setText(supplier);
                mDetailSupplierPhoneV.setText(String.valueOf(supplierPhone));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //todo
        mDetailNameV.setText(null);
        mDetailPriceV.setText(null);
        mDetailQuantityV.setText(null);
        mDetailSupplierV.setText(null);
        mDetailSupplierPhoneV.setText(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_action:
                Intent intent = new Intent(DetailActivity.this,EditActivity.class);
                intent.setData(uriForDetail);
                startActivity(intent);
                return true;
                //todo:home
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
