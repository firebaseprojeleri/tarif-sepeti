package com.gelecegiyazanlar.tarifsepeti.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PermissionUtils {

    public static final int MY_PERMISSIONS_REQUEST_READ_WRITE_EXTERNAL_STORAGE = 123;

    public static void requestRecordAudioPermission(Activity activity) {

        ActivityCompat.requestPermissions(activity, new
                String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_WRITE_EXTERNAL_STORAGE);

    }

    public static boolean checkRecordReadWriteExternalPermission(Context context) {

        int result = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);

        int result1 = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

}
