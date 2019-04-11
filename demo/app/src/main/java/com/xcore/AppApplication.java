package com.xcore;

import android.com.baselibrary.MyApplication;
import android.com.glide37.GlideUtils;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.common.BaseCommon;
import com.xcore.cache.CacheManager;
import com.xcore.utils.AppFrontBackHelper;
import com.xcore.utils.CrashHandler;


public class AppApplication extends MyApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        //you must install multiDex whatever tinker is installed!
        MultiDex.install(this);

        MainApplicationContext.application= this;
        MainApplicationContext.context=this.getBaseContext();
        MainApplicationContext.onCreate();

//        HttpProxy.init(new OkgoHttpProxy());

        //初始化本地缓存信息
        CacheManager.getInstance().init();
        //初始化异常信息
        CrashHandler.getInstance().init(this);

//        //////////////////////////////////////////////////////////////
//        ////////////////////////// 友盟统计 ///////////////////////////
//        ////////////////////////////////////////////////////////////
//        /**
//         * 初始化common库
//         * 参数1:上下文，不能为空
//         * 参数2:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
//         * 参数3:Push推送业务的secret   ikingUmeng:IK     Umeng:官网  AUmeng:IK Other01  DUmeng
//         */
//        //ADV_B  ADV_A   MM_A  修改版本 basecommon  SplashPresenter修改连接测试。。 token　验证到期时间
//        try {
            CHANNEL="Umeng";
            BaseCommon.CHANNEL=CHANNEL;
            BaseCommon.VERSION_CODE=48;
            BaseCommon.VERSION_NAME="1.3.6";
            BaseCommon.isTestServer=false;//设置连接服务  true 测试
//            UMConfigure.init(this, "5ba468bfb465f54b5d00016b", BaseCommon.CHANNEL, UMConfigure.DEVICE_TYPE_PHONE, null);
//
//            UMConfigure.setLogEnabled(true);
//            UMConfigure.setEncryptEnabled(true);
//
//            MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
//            MobclickAgent.setSessionContinueMillis(3000);
//        }catch (Exception ex){}

        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(AppApplication.this, new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {
                //应用切到前台处理
                //Log.e("TAG","切换到前台了");
            }
            @Override
            public void onBack() {
                //应用切到后台处理
                //Log.e("TAG","切换到后台了");
                //好像除了登录其他地方好像没用到吧.. 刷新 token 的成功了直接保存就可以了
//                Config config=MainApplicationContext.getConfig();
//                config.save();
            }
        });

    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.e("TAG","onTrimMemory");
        GlideUtils.getInstance().onTrimMemory(this,level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        GlideUtils.getInstance().onLowMemory(this);
    }

}
