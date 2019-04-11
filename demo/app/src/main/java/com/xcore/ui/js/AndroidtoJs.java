package com.xcore.ui.js;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.maning.updatelibrary.InstallUtils;
import com.xcore.MainApplicationContext;
import com.xcore.data.utils.TCallback;
import com.xcore.services.ApiFactory;
import com.xcore.ui.Config;
import com.xcore.ui.activity.AppUpdateActivity;
import com.xcore.utils.LogUtils;
import com.xcore.utils.SystemUtils;
import com.xcore.utils.ZipUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AndroidtoJs extends Object {
    Context context;

    public AndroidtoJs(){
    }
    public AndroidtoJs(Context ctx){
        context=ctx;
    }

    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public String hello() {
        Log.e("TAG","JS调用了Android的hello方法");
        return "fdsafsadaf";
    }
    //获取包列表字串
    @JavascriptInterface
    public String getPackList(){
        try {
            List<MyAppInfo> infos = getPackages();
            String v = new Gson().toJson(infos);
            return v;
        }catch (Exception ex){}
        return "";
    }
    /**
     * 卸载指定包名的应用
     * @param packageName
     */
    @JavascriptInterface
    public boolean uninstall(String packageName) {
        boolean b = checkApplication(packageName);
        if (b) {
            Uri packageURI = Uri.parse("package:".concat(packageName));
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(packageURI);
            context.startActivity(intent);
            return true;
        }
        return false;
    }
    /**
     * 判断该包名的应用是否安装
     *
     * @param packageName
     * @return
     */
    @JavascriptInterface
    public boolean checkApplication(String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.MATCH_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("TAG", "Test:"+e.toString());
        }
        return false;
    }
    @JavascriptInterface
    public String getCode(){
        Config config=MainApplicationContext.getConfig();
        String v=config.getKey();//SystemUtils.getFingerprint();
        return v;
    }
    @JavascriptInterface
    public void install(String url){
        downFile(url);
    }
    @JavascriptInterface
    public String getVersion(){
        return BaseCommon.VERSION_NAME;
    }
    @JavascriptInterface
    public int getVersionCode(){
        return BaseCommon.VERSION_CODE;
    }
    @JavascriptInterface
    public String getM3u8CachePath(){
        return MainApplicationContext.M3U8_PATH;
    }
    @JavascriptInterface
    public String getImagePath(){
        return MainApplicationContext.IMAGE_PATH;
    }
    @JavascriptInterface
    public String getLogPath(){
        return MainApplicationContext.LOG_PATH;
    }
    //日志上传
    @JavascriptInterface
    public void upLog(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String xUrl=url;
                if(xUrl==null||xUrl.length()<=0){
                    xUrl=MainApplicationContext.LOG_PATH;
                }
                File f=new File(xUrl);
                if(!f.exists()&&!f.isDirectory()){
                    return;
                }
                File[] fs=f.listFiles();
                if(fs==null||fs.length<=0){
                    return;
                }
                String outPath=LogUtils.getInnerSDCardPath() + "/Download/"+System.currentTimeMillis()+".zip";
                try {
                    ZipUtils.ZipFolder(url, outPath);
                }catch (Exception ex){}
                try{
                    final File f1=new File(outPath);
                    if(!f1.exists()){
                        return;
                    }
                    ApiFactory.getInstance().<String>uploadFile(f, new TCallback<String>() {
                        @Override
                        public void onNext(String s) {
                            Log.e("TAG",s);
                            try {
                                if (f1.exists()) {
                                    f1.delete();
                                }
                                String fPath=MainApplicationContext.LOG_PATH;
                                File file=new File(fPath);
                                if(file.exists()&&file.isDirectory()){
                                    File[] files= file.listFiles();
                                    for(File file1:files){
                                        if(file1.exists()){
                                            file1.delete();
                                        }
                                    }
                                }
                            }catch (Exception ex){}
                        }
                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                        }
                    });
                }catch (Exception ex){}
            }
        }).start();
    }
    //下载安装
    @JavascriptInterface
    public void installApkLocal(String url){
        installApk(url);
    }

    @JavascriptInterface
    public String getImei(){
        String imeiStr=SystemUtils.getM(context);
        return imeiStr;
    }

    private void downFile(String url){
        OkGo.<File>get(url)//
            .tag(this)//
            .execute(new FileCallback() {
                @Override
                public void onSuccess(Response<File> response) {
                    File file=response.body();
                    installApk(file.getAbsolutePath());
                }
                @Override
                public void downloadProgress(Progress progress) {
                    super.downloadProgress(progress);
                    Log.e("TAG","速度："+progress.speed);
                }
                @Override
                public void onError(Response<File> response) {
                    super.onError(response);
                    Log.e("TAg","下载出错");
                }
            });
    }
    private void installApk(String pathStr){
        if(context==null){
            return;
        }
        InstallUtils.installAPK(context, pathStr,
            new InstallUtils.InstallCallBack() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onFail(Exception e) {
                }
            });
    }

    //获取所有包
    private List<MyAppInfo> getPackages(){
        PackageManager packageManager= context.getPackageManager();
        List<MyAppInfo> myAppInfos = new ArrayList<MyAppInfo>();
        try {
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
            for (int i = 0; i < packageInfos.size(); i++) {
                PackageInfo packageInfo = packageInfos.get(i);
                //过滤掉系统app
                if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) != 0) {
                    continue;
                }
                MyAppInfo myAppInfo = new MyAppInfo();
                myAppInfo.setAppName(packageInfo.packageName);
                if (packageInfo.applicationInfo.loadIcon(packageManager) == null) {
                    continue;
                }
                //myAppInfo.setImage(packageInfo.applicationInfo.loadIcon(packageManager));
                myAppInfos.add(myAppInfo);
            }
        }catch (Exception e){
            Log.e("TAG","===============获取应用包信息失败");
        }
        return myAppInfos;
    }
    class MyAppInfo{
        String appName;
        String image;

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

}
