package com.example.android.bookstoreinventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreinventory.data.BookContract;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private  EditText mEditNameV,mEditPriceV,mEditQuantityV,mEditSupplierV,mEditSupplierPhoneV;
    private TextView mNameRequiredV, mPriceRequiredV, mQuantityRequiredV, mSupplierRequiredV, mPhoneRequiredV;
    private Button mEditMinusBtn,mEditPlusBtn,mEditDeleteBtn;
    private String mEditName,mEditSupplier;
    private int mEditQuantity, mEditSupplierPhone;
    private Double mEditPrice;
    private static final int LOADER_ID = 1;
    private Uri uriToEdit;
    private Uri uri = BookContract.BookEntry.CONTENT_URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mEditNameV = (EditText) findViewById(R.id.book_edit_name);
        mEditPriceV = (EditText) findViewById(R.id.book_edit_price);
        mEditQuantityV = (EditText) findViewById(R.id.book_edit_quantity);
        mEditSupplierV = (EditText) findViewById(R.id.book_edit_supplier);
        mEditSupplierPhoneV =(EditText) findViewById(R.id.book_edit_supplier_phone);

        mNameRequiredV = (TextView) findViewById(R.id.name_required_label);
        mPriceRequiredV = (TextView) findViewById(R.id.price_required_label);
        mQuantityRequiredV = (TextView) findViewById(R.id.quantity_required_label);
        mSupplierRequiredV = (TextView) findViewById(R.id.supplier_required_label);
        mPhoneRequiredV = (TextView) findViewById(R.id.phone_required_label);

        mEditMinusBtn = (Button) findViewById(R.id.book_edit_minus_btn);
        mEditPlusBtn = (Button) findViewById(R.id.book_edit_plus_btn);
        mEditDeleteBtn = (Button) findViewById(R.id.book_edit_delete_btn);

        Intent intent = getIntent();
        uriToEdit = intent.getData();
        if (uriToEdit == null){
            setTitle(getString(R.string.add_title));
        }else{
            setTitle(getString(R.string.edit_title));
            getLoaderManager().initLoader(LOADER_ID,null,this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ((item.getItemId())){
            case R.id.save_action:
                saveBook();
                return true;
                //todo: case for homeup action
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void saveBook(){
        //todo
        mEditName = mEditNameV.getText().toString();
        String price = mEditPriceV.getText().toString();
        String quantity = mEditQuantityV.getText().toString();
        mEditSupplier = mEditSupplierV.getText().toString();
        String phoneNumber = mEditSupplierPhoneV.getText().toString();

        //todo
        boolean isValuesEmpty = isContentValuesEmpty(mEditName,price,quantity,mEditSupplier,phoneNumber);
        if (isValuesEmpty){
            Toast.makeText(this,getString(R.string.data_empty_message),Toast.LENGTH_SHORT).show();
        }else{
            mEditName = mEditName.trim();
            mEditPrice = Double.parseDouble(price.trim());
            mEditQuantity = Integer.parseInt(quantity.trim());
            mEditSupplier = mEditSupplier.trim();
            mEditSupplierPhone = Integer.parseInt(phoneNumber.trim());

            //todo
            ContentValues values = new ContentValues();
            values.put(BookContract.BookEntry.TABLE_COLUMN_NAME,mEditName);
            values.put(BookContract.BookEntry.TABLE_COLUMN_PRICE,mEditPrice);
            values.put(BookContract.BookEntry.TABLE_COLUMN_QUANTITY,mEditQuantity);
            values.put(BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_NAME,mEditSupplier);
            values.put(BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_PHONE_NUMBER,mEditSupplierPhone);
            if (uriToEdit == null) {
                getContentResolver().insert(uri, values);
                finish();
            }else{
                getContentResolver().update(uriToEdit, values, null, null);
                finish();
                //todo: navigate back to detail activity
                Intent intent = new Intent();
                intent.setData(uriToEdit);
                NavUtils.navigateUpTo(this,intent);
            }
        }
    }

    //helper method: check if passed-in content values are empty
    private boolean isContentValuesEmpty(String name,String price,String quantity,String supplier,String phoneNumber){
        boolean isContentValuesEmpty = false;
        if (TextUtils.isEmpty(name)){
            //name can not be empty
            isContentValuesEmpty = true;
            mNameRequiredV.setText(getString(R.string.name_required));
            mNameRequiredV.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(price)){
            //price can't be null
            isContentValuesEmpty = true;
            mPriceRequiredV.setText(getString(R.string.price_required));
            mPriceRequiredV.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(quantity)){
            //quantity can't be null
            isContentValuesEmpty = true;
            mQuantityRequiredV.setText(getString(R.string.quantity_required));
            mQuantityRequiredV.setVisibility(View.VISIBLE);

        }
        if (TextUtils.isEmpty(supplier)){
            //supplier can't be null
            isContentValuesEmpty = true;
            mSupplierRequiredV.setText(getString(R.string.supplier_required));
            mSupplierRequiredV.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(phoneNumber)){
            //phone can't be null
            isContentValuesEmpty = true;
            mPhoneRequiredV.setText(getString(R.string.phone_required));
            mPhoneRequiredV.setVisibility(View.VISIBLE);
        }
        return isContentValuesEmpty;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //projection must include column_id because cursor needs id to function properly
        String[] projection = new String[]{BookContract.BookEntry.TABLE_COLUMN_ID,
                BookContract.BookEntry.TABLE_COLUMN_NAME,
                BookContract.BookEntry.TABLE_COLUMN_PRICE,
                BookContract.BookEntry.TABLE_COLUMN_QUANTITY,
                BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_NAME,
                BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_PHONE_NUMBER};
        switch (id){
            case LOADER_ID:
                return new CursorLoader(this,uriToEdit,projection,null,null,null);
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

               // byte[] photoByte = data.getBlob(photoColumnIndex);
                String name = data.getString(nameColumnIndex);
                double price = data.getDouble(priceColumnIndex);
                int quantity = data.getInt(quantityColumnIndex);
                String supplier = data.getString(supplierColumnIndex);
                int supplierPhone = data.getInt(supplierPhoneColumnIndex);

                //todo: set photo for photoview
                mEditNameV.setText(name);
                mEditPriceV.setText(String.valueOf(price));
                mEditQuantityV.setText(String.valueOf(quantity));
                mEditSupplierV.setText(supplier);
                mEditSupplierPhoneV.setText(String.valueOf(supplierPhone));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //todo: set photo for photoview
        mEditNameV.setText(null);
        mEditPriceV.setText(null);
        mEditQuantityV.setText(null);
        mEditSupplierV.setText(null);
        mEditSupplierPhoneV.setText(null);
    }
}
