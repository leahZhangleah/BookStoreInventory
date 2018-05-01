package com.example.android.bookstoreinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class BookProvider extends ContentProvider {
    private static BookDbOpenHelper bookDbOpenHelper;
    private static final int BOOK = 100;
    private static final int BOOK_ID = 101;
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        mUriMatcher.addURI(BookContract.BookEntry.CONTENT_AUTHORITY, BookContract.BookEntry.BOOK_PATH, BOOK);
        Log.i("BookProvider.class","two types of urimatcher" + mUriMatcher);
        mUriMatcher.addURI(BookContract.BookEntry.CONTENT_AUTHORITY, BookContract.BookEntry.BOOK_PATH +"/#", BOOK_ID);
    }
    @Override
    public boolean onCreate() {
        bookDbOpenHelper = new BookDbOpenHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = mUriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = bookDbOpenHelper.getReadableDatabase();
        Cursor cursor;
        switch (match){
            case BOOK:
                cursor = sqLiteDatabase.query(BookContract.BookEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry.TABLE_COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI: " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = mUriMatcher.match(uri);
        switch (match){
            case BOOK:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            default:
                throw new IllegalArgumentException("unknown URI: "+uri +" with match "+match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = mUriMatcher.match(uri);
        switch (match){
            case BOOK:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("Cannot insert unknown URI: " + uri);
        }
    }

    //helper method
    private Uri insertBook(Uri uri, ContentValues values){
        SQLiteDatabase sqLiteDatabase = bookDbOpenHelper.getWritableDatabase();
        long rowInsertedId = sqLiteDatabase.insert(BookContract.BookEntry.TABLE_NAME,null,values);
        if (rowInsertedId == -1){
            Toast.makeText(getContext(),"Insertion failed for URI: "+ uri, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(),"Insertion successful", Toast.LENGTH_SHORT).show();
        }

        Uri returnedUri = ContentUris.withAppendedId(uri,rowInsertedId);
        getContext().getContentResolver().notifyChange(returnedUri,null);
        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = bookDbOpenHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int rowDeletedId;
        switch (match){
            case BOOK:
                rowDeletedId =sqLiteDatabase.delete(BookContract.BookEntry.TABLE_NAME,selection,selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                return rowDeletedId;
            case BOOK_ID:
                selection = BookContract.BookEntry.TABLE_COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDeletedId = sqLiteDatabase.delete(BookContract.BookEntry.TABLE_NAME,selection,selectionArgs);
                getContext().getContentResolver().notifyChange(uri,null);
                return rowDeletedId;
            default:
                throw new IllegalArgumentException("Deletion failed for URI: "+uri);
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = mUriMatcher.match(uri);
        switch (match){
            case BOOK:
                return updatePet(uri,values,selection,selectionArgs);
            case BOOK_ID:
                selection = BookContract.BookEntry.TABLE_COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Updation failed for URI: " + uri);
        }
    }

    //helper method
    private int updatePet(Uri uri,ContentValues values,String selection, String[] selectionArgs){
        SQLiteDatabase sqLiteDatabase = bookDbOpenHelper.getWritableDatabase();
        int rowUpdatedId = sqLiteDatabase.update(BookContract.BookEntry.TABLE_NAME,values,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return rowUpdatedId;
    }

}
