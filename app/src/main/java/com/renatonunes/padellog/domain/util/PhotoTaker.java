package com.renatonunes.padellog.domain.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by Renato on 06/08/2016.
 */
public class PhotoTaker {
    public static final int REQUEST_TAKE_PHOTO = 101;
    public static final int REQUEST_PICK_PHOTO = 102;

    private final Activity mActivity;

    public PhotoTaker(Activity activity) {
        mActivity = activity;
    }

    public boolean takePhoto(File outputFile) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            // Continue only if the File was successfully created
            if (outputFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
                mActivity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                return true;
            }
        }

        return false;
    }

    public boolean pickPhoto(File outputFile) {
        Intent pickPictureIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Ensure that there's a camera activity to handle the intent
        if (pickPictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            // Continue only if the File was successfully created
            if (outputFile != null) {
                pickPictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
                mActivity.startActivityForResult(pickPictureIntent, REQUEST_PICK_PHOTO);
                return true;
            }
        }

        /*
        * Intent galeriaIntent = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
startActivity(galeriaIntent);
        * */

        return false;
    }


}
