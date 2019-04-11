package com.xcore.picgen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {
    public static final String TAG = "BitmapUtil";


    public static boolean saveImage(Bitmap bitmap, String absPath) {
        return saveImage(bitmap, absPath, 80);
    }

    public static boolean saveImage(Bitmap bitmap, String absPath, int quality) {
        if (!createNewFile(absPath)) {
            Log.w(TAG, "create file failed.");
            return false;
        }
        try {
            File outFile = new File(absPath);
            FileOutputStream fos = new FileOutputStream(outFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "failed to write image content");
            return false;
        }
        return true;
    }
    public static boolean saveImage(Bitmap bitmap, String absPath, int quality, Activity activity) {
        if (!createNewFile(absPath)) {
            return false;
        }
        try {
            File outFile = new File(absPath);
            if(outFile.exists()){
                outFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(outFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();

            try {
                //播放到相册
                MediaStore.Images.Media.insertImage(activity.getContentResolver(), outFile.getAbsolutePath(), outFile.getName(), null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
//             最后通知图库更新
            Uri contentUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                contentUri = FileProvider.getUriForFile(activity, "com.xcore.provider", outFile);
            } else {
                contentUri = Uri.parse("file://" + outFile.getAbsolutePath());
            }
            activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean createNewFile(String path) {
        try {
            if (TextUtils.isEmpty(path))
                return false;
            File file = new File(path);
            if (file.exists())
                return true;
            boolean isOk = false;
            try {
                isOk = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return isOk;
        }catch (Exception ex){}
        return false;
    }
}
