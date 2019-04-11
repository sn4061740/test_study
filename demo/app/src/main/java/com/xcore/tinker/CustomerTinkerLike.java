package com.xcore.tinker;

import android.annotation.TargetApi;
import android.app.Application;
import android.com.baselibrary.MyApplication;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.common.BaseCommon;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.entry.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xcore.MainApplicationContext;
import com.xcore.utils.AppFrontBackHelper;
import com.xcore.utils.CrashHandler;


@SuppressWarnings("unused")
@DefaultLifeCycle(
        application = "com.xcore.MainApplication",
        flags = ShareConstants.TINKER_ENABLE_ALL,
        loadVerifyFlag = false
)
public class CustomerTinkerLike extends DefaultApplicationLike {

    public CustomerTinkerLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag,
                                 long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }
    /**
     * install multiDex before install tinker
     * so we don't need to put the tinker lib classes in the main dex
     *
     * @param base
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(final Context base) {
        super.onBaseContextAttached(base);
        Log.e("TAG","初始化。。。");

        MultiDex.install(base);

//        TinkerManager.setUpgradeRetryEnable(true);
        TinkerManager.installTinker(this);
//        Tinker tinker = Tinker.with(getApplication());
//        TinkerManager.install(this);

        //初始化异常信息
        CrashHandler.getInstance().init(base);

        String CHANNEL="XUmeng";
        BaseCommon.CHANNEL=CHANNEL;
        BaseCommon.VERSION_CODE=56;
        BaseCommon.VERSION_NAME="1.4.1";
        BaseCommon.isTestServer=false;//设置连接服务  true 测试
        BaseCommon.HOT_VERSION=0;//热更版本号

        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(getApplication(), new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {
                //应用切到前台处理
                Log.e("TAG","切换到前台了");
            }
            @Override
            public void onBack() {
//                if(!isMain) {//不是在主页才更新
//                    Log.e("TAG","不是首页,可以更新了");
//                    String path = MainApplicationContext.hotPath;
//                    if (path == null || path.length() <= 0) {
//                        return;
//                    }
//                    TinkerManager.loadPatch(path);
//                }else{
//                    Log.e("TAG","首页,不可以更新哦~~~");
//                }
            }
        });
    }
    public static boolean isMain=false;
}
