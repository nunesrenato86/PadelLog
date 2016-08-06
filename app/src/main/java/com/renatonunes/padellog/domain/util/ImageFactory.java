package com.renatonunes.padellog.domain.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Renato on 06/08/2016.
 */
public class ImageFactory {
    /**
     * Creates a new jpg file that can be used to store an image.
     *
     * @return null if the file couldn't be created.
     */

    public static File newFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File imageFile;

        try {
            imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

        } catch (IOException e) {
            return null;
        }

        return imageFile;
    }
}
