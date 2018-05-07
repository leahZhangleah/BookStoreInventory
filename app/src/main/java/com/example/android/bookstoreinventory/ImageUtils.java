package com.example.android.bookstoreinventory;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {
    public static String mCurrentPhotoPath;
    private ImageUtils(){}
    public static void setPic(String mPhotoPath, ImageView mEditPhotoV){
        int targetW = mEditPhotoV.getWidth();
        int targetH = mEditPhotoV.getHeight();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPhotoPath,bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW/targetW,photoH/targetH);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath,bmOptions);
        mEditPhotoV.setImageBitmap(bitmap);
    }

    public static File createImageFile(Context mContext) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_"+ timeStamp;
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()&&!storageDir.mkdirs()){
            Log.d(EditActivity.class.getName(),"failed to create directory");
        }
        File externalFile = File.createTempFile(imageFileName,".jpg",storageDir);
        Log.i(EditActivity.class.getName(),"the full directory is: "+externalFile);
        mCurrentPhotoPath = externalFile.getAbsolutePath();
        Log.i(MainActivity.class.getName(),"the current photo path is: "+mCurrentPhotoPath);
        return externalFile;
    }

    public static String getRealPathFromUrl(Context mContext,Uri uri){
        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(uri,projection,null,null,null);
        if (cursor.moveToFirst()){
            int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String realPath = cursor.getString(dataColumnIndex);
            Log.i(ImageUtils.class.getName(),"THE REAL PATH IS: "+realPath);
            return realPath;
        }else{
            return null;
        }
    }



}
