package com.xcore.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.common.BaseCommon;
import com.google.gson.Gson;
import com.jay.config.Md5Utils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.maning.updatelibrary.InstallUtils;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BasePopActivity;
import com.xcore.data.bean.JsonDataBean;
import com.xcore.data.bean.VersionBean;
import com.xcore.data.utils.TCallback;
import com.xcore.services.ApiSystemFactory;
import com.xcore.ui.OnCheckSpeedListenner;
import com.xcore.ui.enums.CDNType;
import com.xcore.utils.CDNCheckSpeed;
import com.xcore.utils.LogUtils;

import java.io.File;

public class AppUpdateActivity extends BasePopActivity {
    private RelativeLayout proRelativeLayout;
    private ProgressBar progressBar;
    private Button btnInstall;
    private TextView txtPro;
    private Button btnUpdate;

    private String pathStr;
    private VersionBean versionBean;
    private String downUrl="";

    private Toast toast;
    private void show(String msg){
        if(toast==null){
            toast=Toast.makeText(this,msg,Toast.LENGTH_SHORT);
        }else{
            toast.setText(msg);
        }
        toast.show();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_app_update;
    }
    @Override
    protected void initViews(Bundle savedInstanceState) {
        try {
            Intent intent = getIntent();
            String versionBeanStr = intent.getStringExtra("versionBean");
            versionBean = new Gson().fromJson(versionBeanStr, VersionBean.class);

            progressBar = findViewById(R.id.pro_progress);

            txtPro = findViewById(R.id.txtPro);
            proRelativeLayout = findViewById(R.id.proRelativeLayout);
            proRelativeLayout.setVisibility(View.GONE);

            TextView txtVersion = findViewById(R.id.txtVersion);
            TextView txtRemark = findViewById(R.id.txtRemark);
            if (versionBean != null && versionBean.getData() != null) {
                txtVersion.setText("v" + versionBean.getData().getName());
                String mark = versionBean.getData().getRemark();
                if (TextUtils.isEmpty(mark)) {
                    mark = "";
                }
                if (txtRemark != null) {
                    txtRemark.setText(Html.fromHtml(mark));
                }
            }

            btnUpdate = findViewById(R.id.btn_update);
            findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (versionBean == null || versionBean.getData() == null) {
                        show("没有版本信息");
                        return;
                    }
                    btnUpdate.setVisibility(View.GONE);
                    proRelativeLayout.setVisibility(View.VISIBLE);
                    onDownApk();
                }
            });

            btnInstall = findViewById(R.id.btn_install);
            btnInstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    installApk();
                }
            });
            btnInstall.setVisibility(View.GONE);

            testApkSpeed();
        }catch (Exception ex){}
    }

    protected LoadingDailog dialog=null;
    private boolean isLoad=false;

    //速度
    private void testApkSpeed(){
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==0){
                    if(dialog!=null){
                        dialog.cancel();
                    }
                }
            }
        };
        LoadingDailog.Builder loadBuilder;
        loadBuilder=new LoadingDailog.Builder(this)
                .setMessage("加载中...")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog=loadBuilder.create();
        if(dialog!=null){
            dialog.show();
        }
        new CDNCheckSpeed(new OnCheckSpeedListenner() {
            @Override
            public void onSuccess(String path) {
                if(isLoad==false){
                    isLoad=true;
                    Message message=new Message();
                    message.what=0;
                    handler.sendMessage(message);
                }
            }
            @Override
            public void onSuccess(JsonDataBean jsonDataBean) {
            }
            @Override
            public void onError(String msg) {
            }
            @Override
            public void onCompleteError() {
                if(isLoad==false){
                    isLoad=true;
                    Message message=new Message();
                    message.what=0;
                    handler.sendMessage(message);
                }
            }
        }).start(CDNType.APK);
    }

    //成功
    private void onFileSuccess(File file){
        try {
            //保存到本地  82599dd9f6bc8adedae1a0949a1da380
            if (file == null) {
                show("文件出错,请重新下载!");
                progressBar.setProgress(0);
                txtPro.setText("已下载0%");
                proRelativeLayout.setVisibility(View.GONE);

                btnInstall.setVisibility(View.GONE);
                btnUpdate.setText("重新下载");
                btnUpdate.setVisibility(View.VISIBLE);
                downError("返回成功,获取到的 File 为null,下载出错", LogUtils.VERSION_CDOE_NULL_ERROR);
                return;
            }
            String value = Md5Utils.getMD5(file);//MD5Encoder.getFileMD5(file);
            //校验md5 码
            if (!versionBean.getData().getMd5().equals(value)) {
                show("校验码不正确,可能被串改,请重新下载!");

                progressBar.setProgress(0);
                txtPro.setText("已下载0%");
                proRelativeLayout.setVisibility(View.GONE);

                btnInstall.setVisibility(View.GONE);
                btnUpdate.setText("重新下载");
                btnUpdate.setVisibility(View.VISIBLE);

                downError("下载成功,但是校验失败", LogUtils.VERSION_CDOE_V_ERROR);
                return;
            }

            downSuccess();
            proRelativeLayout.setVisibility(View.GONE);
            btnInstall.setVisibility(View.VISIBLE);
            pathStr = file.getAbsolutePath();
            //安装
            installApk();
        }catch (Exception ex){}
    }

    //更新apk
    private void onDownApk(){
        startDownn();

        String url= BaseCommon.APK_URL+versionBean.getData().getDownUrl();
        downUrl=url;
        OkGo.<File>get(url)//
                .tag(this)//
                .execute(new FileCallback() {
                    @Override
                    public void onSuccess(Response<File> response) {
                        File file=response.body();
                        onFileSuccess(file);
                    }
                    @Override
                    public void downloadProgress(Progress progress) {
                        super.downloadProgress(progress);
                        try {
//                        proRelativeLayout.setVisibility(View.VISIBLE);
                            int value = (int) (progress.currentSize * 1.0 / progress.totalSize * 100);
                            if (value > 0 && value < 5) {
                                value = 5;
                            }
                            progressBar.setProgress(value);
                            txtPro.setText("已完成" + value + "%");
                        }catch (Exception ex){}
                    }
                    @Override
                    public void onError(Response<File> response) {
                        super.onError(response);
                        show("下载出错,请重新下载!");
                        try {
                            progressBar.setProgress(0);
                            txtPro.setText("已下载0%");
                            proRelativeLayout.setVisibility(View.GONE);

                            btnInstall.setVisibility(View.GONE);
                            btnUpdate.setText("重新下载");
                            btnUpdate.setVisibility(View.VISIBLE);

                            String msgErr = "下载出错|";
                            if (response != null) {
                                int code = response.code();
                                String eStr = LogUtils.getException(response.getException());
                                msgErr = msgErr + "|CODE=" + code + "|" + eStr;
                            }
                            downError(msgErr, LogUtils.VERSION_CDOE_ERROR);
                        }catch (Exception ex){}
                    }
                });
    }

    private void installApk(){
        InstallUtils.installAPK(AppUpdateActivity.this, pathStr,
            new InstallUtils.InstallCallBack() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFail(Exception e) {
                }
            });
    }

    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                MainApplicationContext.finishAllActivity();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dialog!=null){
            dialog.dismiss();
        }
        dialog=null;
    }


    //开始下载
    private void startDownn(){
        try {
            ApiSystemFactory.getInstance().<String>startDownVersion(versionBean.getData().getInsideVersion(), new TCallback<String>() {
                @Override
                public void onNext(String s) {
                    Log.e("TAG", s);
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    try {
                        Log.e("TAG", "开始下载出错");
                        int code = response.code();
                        String msg = LogUtils.getException(response.getException());
                        msg = "开始升级出错,状态码CODE=" + code + "|errMsg=" + msg;
                        downError(msg, LogUtils.VERSION_CDOE_K_ERROR);
                    } catch (Exception ex) {
                    }
                }
            });
        }catch (Exception ex){}
    }
    //下载错误
    private void downError(String errMsg,int code){
        //TODO...
        errMsg="当前版本名称="+BaseCommon.VERSION_NAME+" |CODE="+BaseCommon.VERSION_CODE+"|"+errMsg;
        LogUtils.versionRequestError(downUrl,errMsg,code);
    }
    //下载成功
    private  void downSuccess(){
        ApiSystemFactory.getInstance().<String>downVersionSuccess(versionBean.getData().getInsideVersion(), new TCallback<String>() {
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
