package com.xcore;

import android.Manifest;
import android.com.baselibrary.MyApplication;
import android.com.lockpattern.widget.ILockPatternListener;
import android.com.lockpattern.widget.LockPatternActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.common.BaseCommon;
import com.xcore.base.BasePopActivity;
import com.xcore.cache.CacheManager;
import com.xcore.ui.Config;
import com.xcore.utils.DensityUtil;
import com.xcore.utils.LogUtils;
import com.xcore.utils.PermissionsChecker;
import com.xcore.utils.SystemUtils;

public class RunActivity extends BasePopActivity {

    final private static int PERMISSIONS_CODE = 29; // 请求码

    static final String[] PERMISSIONS = new String[]{
//            Manifest.permission.INTERNET,
//            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.ACCESS_WIFI_STATE,
////            Manifest.permission.ACCESS_NETWORK_STATE,
//            Manifest.permission.CHANGE_NETWORK_STATE,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.READ_CONTACTS,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_SETTINGS,
//            Manifest.permission.ACCESS_FINE_LOCATION
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.READ_LOGS,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
//                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.CAMERA,
            Manifest.permission.WAKE_LOCK
//            Manifest.permission.STATUS_BAR
//            Manifest.permission.REQUEST_INSTALL_PACKAGES
//            Manifest.permission.WRITE_SETTINGS,
//                Manifest.permission.INTERNET
    };

    private PermissionsChecker permissionsChecker;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            MyApplication application= new MyApplication();
            application.init(RunActivity.this,BaseCommon.CHANNEL);
            MainApplicationContext.application= application;
            MainApplicationContext.context=RunActivity.this.getApplicationContext();
            MainApplicationContext.onCreate();
        }catch (Exception ex){}
        try {//解决第一次重启问题
            if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
                finish();
                return;
            }
            Intent intent = getIntent();
//            Log.e(TAG, "scheme:" + intent.getScheme());
            Uri uri = intent.getData();
//            Log.e(TAG, "scheme: " + uri.getScheme());
//            Log.e(TAG, "host: " + uri.getHost());
//            Log.e(TAG, "port: " + uri.getPort());
//            Log.e(TAG, "path: " + uri.getPath());
//            Log.e(TAG, "queryString: " + uri.getQuery());
//            Log.e(TAG, "queryParameter: " + uri.getQueryParameter("shortid"));
            MainApplicationContext.SHORT_ID=uri.getQueryParameter("shortid");
            //保存到 config 中去吧...

            //先全部清理掉,登录过后才有的
            Config config=MainApplicationContext.getConfig();
            config.remove(config.TOKEN);
            config.remove(config.CURRENT_EXPIRES_IN);
            config.remove(config.TOKEN_EXPIRES_IN);
            config.remove(config.REFRESH_TOKEN);
        }catch (Exception ex){}
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
//        String pxTodp150=DensityUtil.px2dip(RunActivity.this,150)+"";
//        String pxTodp500=DensityUtil.px2dip(RunActivity.this,500)+"";
//        String pxTodp200=DensityUtil.px2dip(RunActivity.this,200)+"";
//
//        Log.e("TAG","150="+pxTodp150+"  200="+pxTodp200+"  500="+pxTodp500);
        permissionsChecker = new PermissionsChecker(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (permissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        } else {
            showMainActivity();
        }
    }
    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, PERMISSIONS_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == PERMISSIONS_CODE &&
                resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
        } else {
            showMainActivity();
        }
    }
    boolean isOpenShow=false;
    //验证成功
    private void showMainActivity() {
        if(isOpenShow){
            return;
        }
        isOpenShow=true;
        LockPatternActivity.toActivity(RunActivity.this,lockPatternListener,LockPatternActivity.LockType.OPEN,true);
    }
    ILockPatternListener lockPatternListener=new ILockPatternListener() {
        @Override
        public void onOpenSuccess() {//打开成功
            toSplashActivity();
        }
        @Override
        public void onForgetPassword() {
            Log.e("TAG","忘记密码");
            Config config=MainApplicationContext.getConfig();
            config.remove("upass");
            config.save();//如果不登录,下次就没有密码,就不会自动登录了

            toSplashActivity();
        }
        @Override
        public void onSetSuccess() {
        }
        @Override
        public void onNonePass() {//没有设置密码
            LogUtils.showLog("没有设置手势密码");
            toSplashActivity();
        }
        @Override
        public void onCancelOpenSuccess() {//取消打开
            finish();
        }

        @Override
        public void onCancelSetSuccess() {//取消设置
        }
    };

    //到
    private void toSplashActivity(){
        Intent intent = new Intent(RunActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

}