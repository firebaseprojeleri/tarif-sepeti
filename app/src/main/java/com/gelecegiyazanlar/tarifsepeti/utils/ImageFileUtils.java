package com.gelecegiyazanlar.tarifsepeti.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

/**
 * Created by serdar on 25.07.2016
 */
public class ImageFileUtils {

    public String TAG = "ImageFileUtils";

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getImagePath(final Context context, final Uri uri) {

        String imagePath = "";

        //----------------------------------------------//
        // if current api >= 19                         //
        //----------------------------------------------//
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            Log.i("ImageFileUtils", "build version >= 19");
            if (DocumentsContract.isDocumentUri(context, uri)) {

                    // ExternalStorageProvider
                    if (isExternalStorageDocument(uri)) {

                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];

                        Log.d("Gallery Images", "external");
                        Log.d("Gallery Images", "docId : " + docId);
                        Log.d("Gallery Images", "split : " + split.toString());

                        if ("primary".equalsIgnoreCase(type)) {

                            imagePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                        }

                        // TODO handle non-primary volumes

                    }

                    // DownloadsProvider
                    else if (isDownloadsDocument(uri)) {

                        final String id = DocumentsContract.getDocumentId(uri);
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                        imagePath = getDataColumn(context, contentUri, null, null);

                    }

                    // MediaProvider
                    else if (isMediaDocument(uri)) {

                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];

                        Log.d("Gallery Images", "media");
                        Log.d("Gallery Images", "docId : " + docId);
                        Log.d("Gallery Images", "split : " + split);

                        Uri contentUri = null;
                        if ("image".equals(type)) {

                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                        } else if ("video".equals(type)) {

                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                        } else if ("audio".equals(type)) {

                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                        }

                        final String selection = "_id=?";
                        final String[] selectionArgs = new String[] {
                                split[1]
                        };

                        imagePath = getDataColumn(context, contentUri, selection, selectionArgs);

                    }

                }

                // MediaStore (and general)
                else if ("content".equalsIgnoreCase(uri.getScheme())) {

                    // Return the remote address
                    if (isGooglePhotosUri(uri))
                        imagePath = uri.getLastPathSegment();

                imagePath = getDataColumn(context, uri, null, null);
                }
                // File
                else if ("file".equalsIgnoreCase(uri.getScheme())) {

                imagePath = uri.getPath();

                }

        }

        //---------------------------------------------//
        // if current api < 19                         //
        //---------------------------------------------//
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {

            Log.i("ImageFileUtils", "build version < 19");
            Cursor cursor = null;
            try {

                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(uri, proj, null, null, null);

                int column_index = 0;
                if (cursor != null) {

                    column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                imagePath = cursor.getString(column_index);

                }else{

                    Log.i("ImageFileUtils", "the cursor is null");

                }

            } finally {

                if (cursor != null) {

                    cursor.close();

                }

            }

        }

        return imagePath;

    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);

            if (cursor != null && cursor.moveToFirst()) {

                final int index = cursor.getColumnIndexOrThrow(column);

                Log.d("Gallery Images ", "fileName: " + cursor.getString(index));

                return cursor.getString(index);

            }

        } finally {

            if (cursor != null)
                cursor.close();

        }
        return null;

    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {

        return "com.android.externalstorage.documents".equals(uri.getAuthority());

    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {

        return "com.android.providers.downloads.documents".equals(uri.getAuthority());

    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {

        return "com.android.providers.media.documents".equals(uri.getAuthority());

    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {

        return "com.google.android.apps.photos.content".equals(uri.getAuthority());

    }

    /**
     * Rotates given bitmap image
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {

            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Gets the path of the image
     */
    public static int getImageOrientation(String imagePath) {

        //rotates image for imageview
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        return orientation;
    }

}
