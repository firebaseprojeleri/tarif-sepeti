package com.gelecegiyazanlar.tarifsepeti.utils;

import android.app.Activity;
import android.net.Uri;

import com.gelecegiyazanlar.tarifsepeti.R;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.view.CropImageView;

import java.io.File;

/**
 * Created by serdar on 12.08.2016
 */
public class UCropUtils{

        public static void openUCropWithUri(Activity activity, Uri sourceUri) {

            int maxWidth = 1280;
            int maxHeight = 720;

            if (sourceUri != null) {

                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                String croppedImageName = "cropped_image" + ts + ".png";

                Uri destinationUri = Uri.fromFile(new File(activity.getCacheDir(), croppedImageName));

                UCrop uCrop = UCrop.of(sourceUri, destinationUri);

                UCrop.Options options = new UCrop.Options();

                options.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
                options.setToolbarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
                options.setActiveWidgetColor(activity.getResources().getColor(R.color.colorPrimary));

                options.setAspectRatioOptions(4,

                        new AspectRatio("1x1", 1, 1),
                        new AspectRatio("2x1", 2, 1),
                        new AspectRatio(activity.getResources().getString(R.string.original), CropImageView.DEFAULT_ASPECT_RATIO, CropImageView.DEFAULT_ASPECT_RATIO),
                        new AspectRatio("16x9", 16, 9),
                        new AspectRatio("4x3", 4, 3)

                );

                uCrop.withOptions(options);

                uCrop.withMaxResultSize(maxWidth, maxHeight)
                        .start(activity);

            }

        }

}
