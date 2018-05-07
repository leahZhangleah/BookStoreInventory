package com.example.android.bookstoreinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreinventory.data.BookContract;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,View.OnTouchListener{
    private ImageView mEditPhotoV;
    private  EditText mEditNameV,mEditPriceV,mEditQuantityV,mEditSupplierV,mEditSupplierPhoneV;
    private TextView mNameRequiredV, mPriceRequiredV, mQuantityRequiredV, mSupplierRequiredV, mPhoneRequiredV,mAddPhotoV;
    private Button mEditMinusBtn,mEditPlusBtn,mEditDeleteBtn;
    private String mEditName,mEditSupplier;
    private int mEditQuantity;
    private Double mEditPrice;
    private static final int LOADER_ID = 1;
    private Uri uriToEdit;
    private Uri uri = BookContract.BookEntry.CONTENT_URI;
    private boolean mIsTouched = false;
    private static final int PHOTO_FROM_ACTION_CODE = 2;
    private Uri selectedImageUri;
    private String mPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //init views
        mEditPhotoV = (ImageView) findViewById(R.id.book_edit_photo);
        mEditNameV = (EditText) findViewById(R.id.book_edit_name);
        mEditPriceV = (EditText) findViewById(R.id.book_edit_price);
        mEditQuantityV = (EditText) findViewById(R.id.book_edit_quantity);
        mEditSupplierV = (EditText) findViewById(R.id.book_edit_supplier);
        mEditSupplierPhoneV =(EditText) findViewById(R.id.book_edit_supplier_phone);
        mAddPhotoV = (TextView) findViewById(R.id.add_photo_tv);

        //set touch listener for views to check if any change has been made
        mEditPhotoV.setOnTouchListener(this);
        mEditNameV.setOnTouchListener(this);
        mEditPriceV.setOnTouchListener(this);
        mEditQuantityV.setOnTouchListener(this);
        mEditSupplierV.setOnTouchListener(this);
        mEditSupplierPhoneV.setOnTouchListener(this);

        //image btn click listener to choose or capture photo
        mEditPhotoV.setOnClickListener(imageChooseClickListener);

        //init views to remind user to fill in required info
        mNameRequiredV = (TextView) findViewById(R.id.name_required_label);
        mPriceRequiredV = (TextView) findViewById(R.id.price_required_label);
        mQuantityRequiredV = (TextView) findViewById(R.id.quantity_required_label);
        mSupplierRequiredV = (TextView) findViewById(R.id.supplier_required_label);
        mPhoneRequiredV = (TextView) findViewById(R.id.phone_required_label);

        //btn init and touch listener
        mEditMinusBtn = (Button) findViewById(R.id.book_edit_minus_btn);
        mEditPlusBtn = (Button) findViewById(R.id.book_edit_plus_btn);
        mEditMinusBtn.setOnTouchListener(this);
        mEditPlusBtn.setOnTouchListener(this);

        mEditDeleteBtn = (Button) findViewById(R.id.book_edit_delete_btn);
        mEditDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBook(uriToEdit);
            }
        });

        Intent intent = getIntent();
        uriToEdit = intent.getData();
        if (uriToEdit == null){
            setTitle(getString(R.string.add_title));
            mAddPhotoV.setVisibility(View.VISIBLE);
            mEditDeleteBtn.setVisibility(View.GONE);
        }else{
            setTitle(getString(R.string.edit_title));
            getLoaderManager().initLoader(LOADER_ID,null,this);
        }

        mEditMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditQuantity = Integer.parseInt(mEditQuantityV.getText().toString());
                if (mEditQuantity > 0){
                    mEditQuantity -= 1;
                    mEditQuantityV.setText(Integer.toString(mEditQuantity));
                }else{
                    Toast.makeText(getBaseContext(),getString(R.string.quantity_less_than_zero_msg),Toast.LENGTH_SHORT).show();
                }
            }
        });

        mEditPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditQuantity = Integer.parseInt(mEditQuantityV.getText().toString());
                mEditQuantity += 1;
                mEditQuantityV.setText(Integer.toString(mEditQuantity));
            }
        });
    }

    //image btn click listener to choose or capture photo
    private View.OnClickListener imageChooseClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI);
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try{
                photoFile = ImageUtils.createImageFile(getBaseContext());
            }catch (IOException e){
                e.printStackTrace();
            }
            if (photoFile!=null){
                selectedImageUri = FileProvider.getUriForFile(getBaseContext(),"com.example.android.fileprovider",photoFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,selectedImageUri);
            }

            String title = "Photo From";
            Intent chooserIntent = Intent.createChooser(pickIntent, title);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
            startActivityForResult(chooserIntent, PHOTO_FROM_ACTION_CODE);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PHOTO_FROM_ACTION_CODE) {
            Bitmap bitmap = null;
            if (data == null) {
                //from camera
                mPhotoPath = ImageUtils.mCurrentPhotoPath;
                Log.i(EditActivity.class.getName(),"now the photo path is: "+mPhotoPath);
                ImageUtils.setPic(mPhotoPath,mEditPhotoV);
            } else {
                //from photo or gallery
                    selectedImageUri = data.getData();
                    Log.i(EditActivity.class.getName(), "selected image uri is:" + selectedImageUri + "iscamera is false");
                    mPhotoPath = ImageUtils.getRealPathFromUrl(getBaseContext(),selectedImageUri);
                    Log.i(EditActivity.class.getName(),"now the photo path is: "+mPhotoPath);
                    ImageUtils.setPic(mPhotoPath,mEditPhotoV);
            }

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mIsTouched = true;
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mIsTouched){
            discardConfirmationDialog();
        }else{
            super.onBackPressed();
        }
    }
    private void discardConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_edit_message);
        builder.setPositiveButton(R.string.delete_positive_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog!= null){
                    dialog.dismiss();
                    finish();
                }
            }
        });
        builder.setNegativeButton(R.string.delete_negative_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog!= null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void saveBook(){
        //todo
        mEditName = mEditNameV.getText().toString();
        String price = mEditPriceV.getText().toString();
        String quantity = mEditQuantityV.getText().toString();
        mEditSupplier = mEditSupplierV.getText().toString();
        String phoneNumber = mEditSupplierPhoneV.getText().toString();

        boolean isValuesEmpty = isContentValuesEmpty(mEditName,price,quantity,mEditSupplier,phoneNumber);
        if (isValuesEmpty){
            Toast.makeText(this,getString(R.string.data_empty_message),Toast.LENGTH_SHORT).show();
        }else{
            mEditName = mEditName.trim();
            mEditPrice = Double.parseDouble(price.trim());
            mEditQuantity = Integer.parseInt(quantity.trim());
            mEditSupplier = mEditSupplier.trim();

            ContentValues values = new ContentValues();
            values.put(BookContract.BookEntry.TABLE_COLUMN_NAME,mEditName);
            values.put(BookContract.BookEntry.TABLE_COLUMN_PRICE,mEditPrice);
            values.put(BookContract.BookEntry.TABLE_COLUMN_QUANTITY,mEditQuantity);
            values.put(BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_NAME,mEditSupplier);
            values.put(BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_PHONE_NUMBER,phoneNumber);
            if (mPhotoPath != null){
                values.put(BookContract.BookEntry.TABLE_COLUMN_PHOTO,mPhotoPath);
            }
            if (uriToEdit == null) {
                getContentResolver().insert(uri, values);
                finish();
            }else{
                getContentResolver().update(uriToEdit, values, null, null);
                finish();
                Intent intent = new Intent();
                intent.setData(uriToEdit);
                NavUtils.navigateUpTo(this,intent);
            }
        }
    }

    private void deleteBook(final Uri uriToEdit){
        if (uriToEdit!= null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_message);
            builder.setPositiveButton(R.string.delete_positive_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog!= null){
                        int rowDeletedId = getContentResolver().delete(uriToEdit,null,null);
                        if (rowDeletedId > 0){
                            Toast.makeText(getBaseContext(),getString(R.string.delete_successful_msg),Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getBaseContext(),getString(R.string.delete_unsuccessful_msg),Toast.LENGTH_SHORT).show();
                        }
                    }
                    dialog.dismiss();
                    finish();
                    Intent intent = new Intent(EditActivity.this,MainActivity.class);
                    NavUtils.navigateUpTo(EditActivity.this,intent);
                }
            });
            builder.setNegativeButton(R.string.delete_negative_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog != null){
                        dialog.dismiss();
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
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
                BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_PHONE_NUMBER,
                BookContract.BookEntry.TABLE_COLUMN_PHOTO};
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
                int photoColumnIndex = data.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_PHOTO);
                int nameColumnIndex = data.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_NAME);
                int priceColumnIndex = data.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_PRICE);
                int quantityColumnIndex = data.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_QUANTITY);
                int supplierColumnIndex = data.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_NAME);
                int supplierPhoneColumnIndex = data.getColumnIndex(BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_PHONE_NUMBER);

                String photoPath = data.getString(photoColumnIndex);
                String name = data.getString(nameColumnIndex);
                double price = data.getDouble(priceColumnIndex);
                int quantity = data.getInt(quantityColumnIndex);
                String supplier = data.getString(supplierColumnIndex);
                String supplierPhone = data.getString(supplierPhoneColumnIndex);

                if (photoPath == null || photoPath.isEmpty()){
                    mAddPhotoV.setVisibility(View.VISIBLE);
                    mEditPhotoV.setBackgroundResource(R.drawable.icon_book_blue);
                }else{
                    ImageUtils.setPic(photoPath,mEditPhotoV);
                    mAddPhotoV.setVisibility(View.VISIBLE);
                }
                mEditNameV.setText(name);
                mEditPriceV.setText(String.valueOf(price));
                mEditQuantityV.setText(String.valueOf(quantity));
                mEditSupplierV.setText(supplier);
                mEditSupplierPhoneV.setText(supplierPhone);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEditPhotoV.setImageBitmap(null);
        mEditNameV.setText(null);
        mEditPriceV.setText(null);
        mEditQuantityV.setText(null);
        mEditSupplierV.setText(null);
        mEditSupplierPhoneV.setText(null);
    }
}
