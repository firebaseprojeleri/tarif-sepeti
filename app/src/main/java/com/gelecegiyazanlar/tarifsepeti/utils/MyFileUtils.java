package com.gelecegiyazanlar.tarifsepeti.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;

/**
 * Created by serdar on 12.08.2016
 */
public class MyFileUtils {

    public static final String TAG = "MyFileUtils";

    public static String getFileName(String fileName){

        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0){

            return fileName.substring(0, fileName.lastIndexOf("."));

        }else return "";

    }

    public static void trimCache(Context context) {

        try {

            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {

                deleteDir(dir);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public static boolean deleteDir(File dir) {

        if (dir != null && dir.isDirectory()) {

            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {

                Log.i("deleting chache files", "" + children[i]);

                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {

                    return false;
                }

            }

        }

        // The directory is now empty so delete it
        return dir.delete();
    }


}
