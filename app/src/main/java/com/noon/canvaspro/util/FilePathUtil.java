package com.noon.canvaspro.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by ril on 4/2/18.
 */

public class FilePathUtil {

    static final String TAG = FilePathUtil.class.getSimpleName();

    public static Bitmap    getImageFromPath(String path) {
        if (null != path) {
            File imageFile = new File(path);
            if (null != imageFile && imageFile.exists()) {
                /*BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 4;*/
                Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                return imageBitmap;
            }
        }
        return null;
    }

    public static void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }


    public static File getStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }
}

