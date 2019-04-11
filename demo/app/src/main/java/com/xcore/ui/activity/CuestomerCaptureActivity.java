package com.xcore.ui.activity;

import android.os.Bundle;

import com.android.tu.loadingdialog.LoadingDailog;
import com.jwsd.libzxing.activity.CaptureActivity;

import java.util.Timer;
import java.util.TimerTask;

public class CuestomerCaptureActivity extends CaptureActivity implements CaptureActivity.ShowLoadingListener {
    protected LoadingDailog dialog;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setShowLoadingListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        try {
//            MobclickAgent.onResume(this);
//        }catch (Exception ex){}
    }

    @Override
    protected void onPause() {
        super.onPause();
//        try {
////        MobclickAgent.onPageEnd(className);
//            MobclickAgent.onPause(this);
////        MobclickAgent.onPageEnd(className);
//        }catch (Exception ex){}
    }
    private Timer timer;
    @Override
    public void show() {
        try {
            LoadingDailog.Builder loadBuilder;
            loadBuilder = new LoadingDailog.Builder(this)
                    .setMessage("图片识别中...")
                    .setCancelable(false)
                    .setCancelOutside(false);
            dialog = loadBuilder.create();
            dialog.show();

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    } catch (Exception ex) {
                    }
                }
            }, 2000);
        }catch (Exception ex){}
    }

    @Override
    public void cancel() {
        if(dialog!=null){
            dialog.cancel();
        }
        if(timer!=null){
            timer.cancel();
        }
        timer=null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.cancel();
        }
        dialog = null;
        try {
            if (timer != null) {
                timer.cancel();
            }
            timer = null;
        }catch (Exception ex){}
    }
}
