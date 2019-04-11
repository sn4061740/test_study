package com.xcore.utils;

import android.animation.ObjectAnimator;
import android.com.glide37.GlideUtils;
import android.com.glide37.IGlideListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.jay.config.Md5Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.ui.IADVListener;

import java.io.File;

//音量控制,跳转
public class AdvUtils {
    //加载视频
    public static void loadVideo(String videoPath, final IADVListener iadvListener){
        if(videoPath==null||videoPath.length()<=0){
            return;
        }
        String name=Md5Utils.MD5Encode(videoPath);
        String rootPath=MainApplicationContext.SD_PATH;
        String fPath=rootPath+name+".mmv";
        File file1=new File(fPath);
        if(file1.exists()){
            if(iadvListener!=null) {
                iadvListener.onLoadVideoSuccess(file1.getAbsolutePath());
            }
            return;
        }
        OkGo.<File>get(videoPath).execute(new FileCallback(rootPath,name+".mmv") {
            @Override
            public void onSuccess(Response<File> response) {
                File file=response.body();
                if(iadvListener!=null) {
                    iadvListener.onLoadVideoSuccess(file.getAbsolutePath());
                }
            }
            @Override
            public void downloadProgress(Progress progress) {
                super.downloadProgress(progress);
                LogUtils.showLog("广告下载速度:"+progress.speed);
            }
            @Override
            public void onError(Response<File> response) {
                super.onError(response);
                LogUtils.showLog("广告下载出错");
            }
        });
    }
    //加载图片
    public static void loadImage(final String imagePath, final ImageView img, final IADVListener iadvListener){
        if(imagePath==null||imagePath.length()<=0){
            return;
        }
        img.setVisibility(View.VISIBLE);
        GlideUtils.getInstance().load(imagePath, img, new IGlideListener() {
            @Override
            public void onError(String model, Exception e, long stime) {
            }
            @Override
            public void onSuccess() {
                //Log.e("TAG","加载广告图片成功");
                if(img!=null) {
//                    img.setVisibility(View.VISIBLE);
                    ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(img, "alpha", 0f, 1f);
                    fadeAnim.setDuration(1500);
                    fadeAnim.start();
                }
                if(iadvListener!=null){
                    iadvListener.onLoadImageSuccess(imagePath);
                }
            }
        });
    }

}
