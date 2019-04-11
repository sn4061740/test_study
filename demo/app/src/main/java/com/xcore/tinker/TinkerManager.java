package com.xcore.tinker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;
import android.util.Log;
import android.widget.Toast;

import com.tencent.tinker.entry.ApplicationLike;
import com.tencent.tinker.lib.listener.DefaultPatchListener;
import com.tencent.tinker.lib.listener.PatchListener;
import com.tencent.tinker.lib.patch.AbstractPatch;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.reporter.DefaultLoadReporter;
import com.tencent.tinker.lib.reporter.DefaultPatchReporter;
import com.tencent.tinker.lib.reporter.LoadReporter;
import com.tencent.tinker.lib.reporter.PatchReporter;
import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.lib.util.UpgradePatchRetry;
import com.xcore.MainApplicationContext;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class TinkerManager {
    private static ApplicationLike mAppLike;
    private static boolean isInstalled=false;

    public static void installTinker(ApplicationLike applicationLike){
        mAppLike=applicationLike;
        if(isInstalled){
            return;
        }
        TinkerInstaller.install(mAppLike);
//        Context context=getApplicationContext();
//        TinkerInstaller.install(mAppLike,
//                new DefaultLoadReporter(context),
//                new DefaultPatchReporter(context),
//                new DefaultPatchListener(context),
//                TinkerService.class, new UpgradePatch());
        isInstalled=true;
    }
    /**
     * you can specify all class you want.
     * sometimes, you can only install tinker in some process you want!
     *
     * @param appLike
     */
    public static void install(ApplicationLike appLike) {
        if (isInstalled) {
            Log.e("TAG", "install tinker, but has installed, ignore");
            return;
        }
        LoadReporter loadReporter=new DefaultLoadReporter(appLike.getApplication());
        PatchListener patchListener=new DefaultPatchListener(appLike.getApplication());
        PatchReporter patchReporter=new DefaultPatchReporter(appLike.getApplication());
        AbstractPatch abstractPatch=new UpgradePatch();

        TinkerInstaller.install(appLike,loadReporter,patchReporter,patchListener,TinkerService.class,abstractPatch);

        isInstalled = true;
    }


    public static void loadPatch(final String path){
        final Context context=getApplicationContext();
        if(context==null){
            return;
        }
        try {
            File file=new File(path);
            if(!file.exists()){
                Log.e("TAG","patch文件不存在");
                return;
            }
            if (Tinker.isTinkerInstalled()) {
//                Toast.makeText(context,"开始patch",Toast.LENGTH_LONG).show();
                TinkerInstaller.onReceiveUpgradePatch(context, path);
//                check();
            } else {
                //Toast.makeText(context,"没有初始化Tinker",Toast.LENGTH_LONG).show();
                Log.e("TAG", "没有初始化Tinker");
            }
        }catch (Exception e){}
    }
    //    private static void check(){
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                String path=MainApplicationContext.hotPath;
//                File file=new File(path);
//                if(!file.exists()){
//                    handler.sendEmptyMessage(0);
//                }
//            }
//        },0,1000);
//    }
    static Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MainApplicationContext.finishAllActivity();
        }
    };
    private static Context getApplicationContext(){
        if(mAppLike==null){
            return null;
        }
        return mAppLike.getApplication().getApplicationContext();
    }
}
