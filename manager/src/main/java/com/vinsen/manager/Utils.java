package com.vinsen.manager;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {

    public static final String savePath = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/manager/";

    public static void saveImage(String fileName, Bitmap bitmap) {
        try {
            File filePic = new File(savePath + fileName + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
