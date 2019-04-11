package com.xcore.utils;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.jay.config.DownConfig;
import com.jay.config.Md5Utils;
import com.jay.down.FileDownloadered;
import com.jay.down.listener.DownloadProgressListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.maning.updatelibrary.InstallUtils;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.data.bean.VersionBean;
import com.xcore.data.utils.TCallback;
import com.xcore.services.ApiSystemFactory;

import java.io.File;
import java.util.List;

public class AppUpdateUtils {
    private Context context;
    private VersionBean.VersionData versionData;
    public boolean isNull=false;
    private String downUrl="";

    public AppUpdateUtils(Context context, String verStr){
        this.context=context;
        try {
            versionData = new Gson().fromJson(verStr, VersionBean.VersionData.class);
            if(versionData==null){
                versionData=new VersionBean.VersionData();
                versionData.setName("");
                versionData.setDownUrl("");
            }
            tipsDownApk( "发现新版本" + versionData.getName() + ",马上更新或到官网下载最新版本!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //弹窗提示
    private void tipsDownApk(String msg){
        try {
            // 这里的属性可以一直设置，因为每次设置后返回的是一个builder对象
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            // 设置提示框的标题
            builder.setTitle("版本升级").
                    setIcon(R.drawable.ic_launcher). // 设置提示框的图标
                    setMessage(msg).// 设置要显示的信息
                    setPositiveButton("确定", new DialogInterface.OnClickListener() {// 设置确定按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    downApk();//下载最新的版本程序
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });//设置取消按钮,null是什么都不做，并关闭对话框
            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(true);
            // 显示对话框
            alertDialog.show();
        }catch (Exception ex){}
    }
    private boolean isCalcel=false;
    private boolean isUserCancel=false;
    private void downApk(){
        try {
            isUserCancel=false;
            if (context != null) {
                if (versionData == null) {
                    return;
                }
                if (versionData.getDownUrl() == null || versionData.getDownUrl().length() <= 0) {
                    return;
                }
                String apkUrl = "";
                try {
                    List<String> apks = BaseCommon.apkLists;
                    int num = NumberUtils.getRandom(apks.size());
                    apkUrl = apks.get(num);
                } catch (Exception ex) {
                    apkUrl = BaseCommon.APK_URL;
                }
                if (apkUrl == null || apkUrl.length() <= 0) {
                    return;
                }
                try {
                    Toast toast = Toast.makeText(context, "开始下载", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } catch (Exception ex) {
                }
                String downloadUrl = apkUrl + versionData.getDownUrl();
//                systemDown(downloadUrl);
                startDownn();
                downApk(downloadUrl);
            }
        }catch (Exception ex){}
    }
    //系统下载
    private void systemDown(String downloadUrl){
        // 创建下载请求
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        /*
         * 设置在通知栏是否显示下载通知(下载进度), 有 3 个值可选:
         *    VISIBILITY_VISIBLE:                   下载过程中可见, 下载完后自动消失 (默认)
         *    VISIBILITY_VISIBLE_NOTIFY_COMPLETED:  下载过程中和下载完成后均可见
         *    VISIBILITY_HIDDEN:                    始终不显示通知
         */
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置通知的标题和描述
            request.setTitle("千寻视频");
//        request.setDescription(versionData.getRemark());
        /*
         * 设置允许使用的网络类型, 可选值:
         *     NETWORK_MOBILE:      移动网络
         *     NETWORK_WIFI:        WIFI网络
         *     NETWORK_BLUETOOTH:   蓝牙网络
         * 默认为所有网络都允许
         */
        // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // 添加请求头
        request.addRequestHeader("User-Agent", "Chrome Mozilla/5.0");
        // 设置下载文件的保存位置
//            File saveFile = new File(Environment.getExternalStorageDirectory(), "demo.apk");
//            request.setDestinationUri(Uri.fromFile(saveFile));
        /*
         * 2. 获取下载管理器服务的实例, 添加下载任务
         */
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        // 将下载请求加入 下载队列, 返回一个下载ID
        long downloadId = manager.enqueue(request);
        Log.e("TAG","下载ID:="+downloadId);
        // 如果中途想取消下载, 可以调用remove方法, 根据返回的下载ID取消下载, 取消下载后下载保存的文件将被删除
        // manager.remove(downloadId);

    }
    //浏览器下载
    private void browserDown(String downloadUrrl){
        //调用浏览器下载
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(downloadUrrl));
        context.startActivity(intent);
    }
    //自定义下载
    private void downApk(String apkUrl){
        downUrl=apkUrl;
        final ProgressDialog progressDialog = new ProgressDialog(context);
        try {
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("正在下载新版本");
            progressDialog.setCancelable(false);//不能手动取消下载进度对话框
            progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "取消下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        isCalcel = true;
                        isUserCancel=true;
                        OkGo.getInstance().cancelTag(AppUpdateUtils.this);
                        Toast toast = Toast.makeText(context, "已取消下载", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        downError("用户自己取消下载了", LogUtils.VERSION_CODE_CANCEL);

                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    } catch (Exception ex) {
                    }
                }
            });
            progressDialog.show();
        }catch (Exception ex){}

        OkGo.<File>get(apkUrl)//
            .tag(AppUpdateUtils.this)//
            .execute(new FileCallback() {
                @Override
                public void onSuccess(Response<File> response) {
                    File file=response.body();
                    if(file==null){
                        tipsDownApk("下载出错,如重试无法更新请到官网下载最新版本!");
                        downError("返回成功,获取到的 File 为null,下载出错",LogUtils.VERSION_CDOE_NULL_ERROR);
                        if(progressDialog!=null) {
                            progressDialog.dismiss();
                        }
                        return;
                    }
                    if(progressDialog!=null) {
                        progressDialog.dismiss();
                    }
                    onFileSuccess(file);
                }
                @Override
                public void downloadProgress(Progress progress) {
                    super.downloadProgress(progress);
    //                        proRelativeLayout.setVisibility(View.VISIBLE);
                    int value= (int) (progress.currentSize*1.0/progress.totalSize*100);
                    progressDialog.setProgress(value);
                }
                @Override
                public void onError(Response<File> response) {
                    if(!isUserCancel) {
                        String msgErr = "下载出错|";
                        if (response != null) {
                            int code = response.code();
                            String eStr = LogUtils.getException(response.getException());
                            msgErr = msgErr + "|CODE=" + code + "|" + eStr;
                        }
                        downError(msgErr, LogUtils.VERSION_CDOE_ERROR);
                    }
                    if(!isCalcel) {
                        tipsDownApk("下载出错,如重试无法更新请到官网下载最新版本!");
                        if(progressDialog!=null) {
                            progressDialog.dismiss();
                        }
                    }
                }
            });
    }

    //成功
    private void onFileSuccess(File file){
        //保存到本地  82599dd9f6bc8adedae1a0949a1da380
        String value= Md5Utils.getMD5(file);//MD5Encoder.getFileMD5(file);
        //校验md5 码
        if(!versionData.getMd5().equals(value)){
            Log.e("TAG","校验码不正确,可能被串改,请重新下载");
            tipsDownApk("校验码不正确,可能被串改,请重新下载或到官网下载最新版本!");
            downError("下载成功,但是校验失败",LogUtils.VERSION_CDOE_V_ERROR);
            return;
        }
        downSuccess();
        String pathStr=file.getAbsolutePath();
//        //安装
        installApk(pathStr);
    }
    private void installApk(String pathStr){
        InstallUtils.installAPK(context, pathStr,
            new InstallUtils.InstallCallBack() {
                @Override
                public void onSuccess() {
                    Log.e("TAG","打开安装中...");
                    //Toast.makeText(AppUpdateActivity.this, "安装程序成功", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFail(Exception e) {
                    //tv_info.setText("安装失败:" + e.toString());
                    Log.e("TAG","安装失败");
                }
            });
    }

    public void onDestroy(){
    }
    //开始下载
    private void startDownn(){
        ApiSystemFactory.getInstance().<String>startDownVersion(versionData.getInsideVersion(), new TCallback<String>() {
            @Override
            public void onNext(String s) {
                Log.e("TAG",s);
            }
            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                Log.e("TAG","开始下载出错");
                int code=response.code();
                String msg=LogUtils.getException(response.getException());
                msg="开始升级出错,状态码CODE="+code+"|errMsg="+msg;
                downError(msg,LogUtils.VERSION_CDOE_K_ERROR);
            }
        });
    }
    //下载错误
    private void downError(String errMsg,int code){
        //TODO...
        errMsg="当前版本名称="+BaseCommon.VERSION_NAME+" |CODE="+BaseCommon.VERSION_CODE+"|"+errMsg;
        LogUtils.versionRequestError(downUrl,errMsg,code);
    }
    //下载成功
    private  void downSuccess(){
        ApiSystemFactory.getInstance().<String>downVersionSuccess(versionData.getInsideVersion(), new TCallback<String>() {
            @Override
            public void onNext(String s) {
                Log.e("TAG","升级成功:"+s);
            }
            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                Log.e("TAG","下载成功请求出错");
            }
        });
    }

}
