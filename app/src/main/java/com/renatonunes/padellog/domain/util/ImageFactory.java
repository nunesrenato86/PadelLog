package com.renatonunes.padellog.domain.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
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

    public static Bitmap imgStrToImage(String imgStr){
        byte[] imageAsBytes = Base64.decode(imgStr.getBytes(), Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    public static Boolean imgIsLarge(Uri photoUri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(photoUri.getPath()).getAbsolutePath(), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        return (imageHeight > 600) || (imageHeight > 600);
    }


    public static String getBase64Image(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] bytes = baos.toByteArray();

        String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);

        return base64Image;
    }




}
