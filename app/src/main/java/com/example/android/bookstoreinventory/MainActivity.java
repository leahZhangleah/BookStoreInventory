package com.example.android.bookstoreinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookstoreinventory.data.BookContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 0;
    private Uri uri = BookContract.BookEntry.CONTENT_URI;
    BookCursorAdapter bookCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView bookList = (ListView) findViewById(R.id.book_list);
        bookCursorAdapter = new BookCursorAdapter(this,null);
        bookList.setAdapter(bookCursorAdapter);
        getLoaderManager().initLoader(LOADER_ID,null,this);

        FloatingActionButton floatingActionButton = findViewById(R.id.float_btn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                startActivity(intent);
            }
        });

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                Uri uriForDetail = ContentUris.withAppendedId(uri,id);
                intent.setData(uriForDetail);
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //todo
        //projection must include column_id because cursor needs id to function properly
        String[] projection = new String[]{BookContract.BookEntry.TABLE_COLUMN_ID,
                BookContract.BookEntry.TABLE_COLUMN_NAME,
                BookContract.BookEntry.TABLE_COLUMN_PRICE,
                BookContract.BookEntry.TABLE_COLUMN_QUANTITY};
        switch (id){
            case LOADER_ID:
                return new CursorLoader(this,uri,projection,null,null,null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        bookCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookCursorAdapter.swapCursor(null);
    }

    private void insertBook(){
        ContentValues book1 = new ContentValues();
        book1.put(BookContract.BookEntry.TABLE_COLUMN_NAME,"EAT PRAY LOVE");
        book1.put(BookContract.BookEntry.TABLE_COLUMN_PRICE,19.99);
        book1.put(BookContract.BookEntry.TABLE_COLUMN_QUANTITY,20);
        book1.put(BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_NAME,"ABC MAGAZINE");
        book1.put(BookContract.BookEntry.TABLE_COLUMN_SUPPLIER_PHONE_NUMBER,1234);

        Uri returnedUri = getContentResolver().insert(uri,book1);
    }

}
