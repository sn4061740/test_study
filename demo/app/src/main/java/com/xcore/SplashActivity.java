package com.xcore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.xcore.base.MvpActivity;
import com.xcore.cache.CacheManager;
import com.xcore.data.bean.AdvBean;
import com.xcore.data.bean.CdnBean;
import com.xcore.data.bean.JsonDataBean;
import com.xcore.data.bean.VersionBean;
import com.xcore.presenter.SplashPresenter;
import com.xcore.presenter.view.SplashView;
import com.xcore.services.UrlUtils;
import com.xcore.tinker.Utils;
import com.xcore.ui.Config;
import com.xcore.ui.IHotListener;
import com.xcore.ui.LoginController;
import com.xcore.ui.activity.AppUpdateActivity;
import com.xcore.ui.activity.LoginActivity;
import com.xcore.ui.activity.OpenScreenAdvActivity;
import com.xcore.ui.enums.CDNType;
import com.xcore.ui.other.TipsEnum;
import com.xcore.utils.CDNCheckSpeed;
import com.xcore.utils.SystemUtils;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends MvpActivity<SplashView,SplashPresenter> implements SplashView {

    private TextView infoTxt;
    private String  infoStr="";
    private int time=1;
    private Timer timer;
    LoginController loginController;
    private AdvBean advBean=null;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        TextView txtVersion= findViewById(R.id.txt_version);
        String v=SystemUtils.getVersion(this);
        txtVersion.setText(v);

        loginController=new LoginController(SplashActivity.this);

        try {
            infoTxt = findViewById(R.id.txt_info);
            infoStr = "检测加速通道中...";
            if (infoTxt != null) {
                infoTxt.setText(infoStr + "" + time);
            }
        }catch (Exception ex){}
        final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (infoTxt != null) {
                        infoTxt.setText(infoStr + "" + time);
                    }
                }catch (Exception ex){}
            }
        };
        if(timer==null){
            timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    time++;
                    handler.sendEmptyMessage(0);
                    try {
                        if (infoTxt == null) {
                            stopTimer();
                        }
                    }catch (Exception ex){
                        stopTimer();
                    }
                }
            },1000,1000);
        }
    }
    private void stopTimer(){
        if(timer!=null){
            timer.cancel();
        }
        timer=null;
    }
    @Override
    protected void initData() {
        presenter.getJson();
    }
        @Override
    public void onBack() {
    }
    @Override
    public SplashPresenter initPresenter() {
        return new SplashPresenter();
    }

    @Override
    public void onJsonResult(JsonDataBean jsonDataBean1) {
        final JsonDataBean jsonDataBean=jsonDataBean1;
        if(jsonDataBean==null){//直接到官网
            //从其他浏览器打开
            stopTimer();
            infoStr="获取加速通道失败,请到官网下载最新版本或进官方群联系客服处理,谢谢!";
            infoTxt.setText(infoStr+"");
            return;
        }
        try{
            if(!TextUtils.isEmpty(jsonDataBean.getApi())){
                BaseCommon.apiList.add(UrlUtils.getUrl(jsonDataBean.getApi()));
            }
        }catch (Exception e){}
        try{
            if(!TextUtils.isEmpty(jsonDataBean.getApi1())){
                BaseCommon.apiList.add(UrlUtils.getUrl(jsonDataBean.getApi1()));
            }
        }catch (Exception e){}
        try{
            if(!TextUtils.isEmpty(jsonDataBean.getApi2())){
                BaseCommon.apiList.add(UrlUtils.getUrl(jsonDataBean.getApi2()));
            }
        }catch (Exception e){}
        try{
            if(!TextUtils.isEmpty(jsonDataBean.getApi3())){
                BaseCommon.apiList.add(UrlUtils.getUrl(jsonDataBean.getApi3()));
            }
        }catch (Exception e){}
        try{
            if(!TextUtils.isEmpty(jsonDataBean.getApi4())){
                BaseCommon.apiList.add(UrlUtils.getUrl(jsonDataBean.getApi4()));
            }
        }catch (Exception e){}
        try{
            if(!TextUtils.isEmpty(jsonDataBean.getApi5())){
                BaseCommon.apiList.add(UrlUtils.getUrl(jsonDataBean.getApi5()));
            }
        }catch (Exception e){}
        try{
            if(!TextUtils.isEmpty(jsonDataBean.getApi6())){
                BaseCommon.apiList.add(UrlUtils.getUrl(jsonDataBean.getApi6()));
            }
        }catch (Exception e){}
        infoStr="获取线路配置...";
        presenter.apiSpeed();
    }
    private boolean isShowCdnErr=false;

    public void onToErr(String msg){
        reportError(SplashActivity.this,msg);
    }

    @Override
    public void onCdnResult(CdnBean cdnBean) {
        if(cdnBean==null){
            if(isShowCdnErr==false) {
                isShowCdnErr = true;
                stopTimer();
                MainApplicationContext.showips("获取线路配置失败,请稍后重试或到官网下载最新版本!", SplashActivity.this, TipsEnum.TO_TIPS);
            }
            return;
        }
        infoStr="检查版本更新...";
        final CdnBean.CdnItem cdnItem= cdnBean.getData();
        if(cdnItem==null){
            presenter.getVersion();
            return;
        }
        try {
            Config config= MainApplicationContext.getConfig();
            List<CdnBean.CdnDataItem> imgCdnDataItems = cdnItem.getImageList();
            if (imgCdnDataItems != null && imgCdnDataItems.size() > 0) {
                for (CdnBean.CdnDataItem item : imgCdnDataItems) {
                    BaseCommon.resLists.add(UrlUtils.getUrl(item.getUrl()));
                }
                BaseCommon.RES_URL = BaseCommon.resLists.get(0);
                config.put("RES_URL",BaseCommon.RES_URL);
            }
            List<CdnBean.CdnDataItem> torrentCdnDataItems = cdnItem.getTorrentList();
            if (torrentCdnDataItems != null && torrentCdnDataItems.size() > 0) {
                for (CdnBean.CdnDataItem item : torrentCdnDataItems) {
                    BaseCommon.videoLists.add(UrlUtils.getUrl(item.getUrl()));
                }
                BaseCommon.VIDEO_URL = BaseCommon.videoLists.get(0);
                config.put("VIDEO_URL",BaseCommon.VIDEO_URL);
            }
            List<CdnBean.CdnDataItem> httpCdnDataItems = cdnItem.getHttpAccelerateList();
            if (httpCdnDataItems != null && httpCdnDataItems.size() > 0) {
                for (CdnBean.CdnDataItem item : httpCdnDataItems) {
                    BaseCommon.httpAccelerateLists.add(UrlUtils.getUrl(item.getUrl()));
                }
                BaseCommon.HTTP_URL = BaseCommon.httpAccelerateLists.get(0);
                config.put("HTTP_URL",BaseCommon.HTTP_URL);
            }
            List<CdnBean.CdnDataItem> apkCdnDataItems = cdnItem.getApkDownList();
            if (apkCdnDataItems != null && apkCdnDataItems.size() > 0) {
                for (CdnBean.CdnDataItem item : apkCdnDataItems) {
                    BaseCommon.apkLists.add(UrlUtils.getUrl(item.getUrl()));
                }
                BaseCommon.APK_URL = BaseCommon.apkLists.get(0);
                config.put("APK_URL",BaseCommon.APK_URL);
            }
            List<CdnBean.CdnDataItem> m3u8Items = cdnItem.getM3U8List();
            if (m3u8Items != null && m3u8Items.size() > 0) {
                for (CdnBean.CdnDataItem item : m3u8Items) {
                    BaseCommon.m3u8List.add(UrlUtils.getUrl(item.getUrl()));
                }
                BaseCommon.M3U8_URL = BaseCommon.m3u8List.get(0);
                config.put("M3U8_URL",BaseCommon.M3U8_URL);
            }
            List<CdnBean.CdnDataItem> recodeList= cdnItem.getRecordList();
            if(recodeList!=null&&recodeList.size()>0){
                for(CdnBean.CdnDataItem item:recodeList){
                    BaseCommon.recodeList.add(UrlUtils.getUrl(item.getUrl()));
                }
            }
            List<AdvBean> advBeans= cdnItem.getAdvList();
            if(advBeans!=null&&advBeans.size()>0){
                advBean=advBeans.get(0);
                String toUrl=advBean.getToUrl();
                String x=advBean.getVideoPath();
                String v=advBean.getImagePath();
                advBean.setImagePath(UrlUtils.getUrl(v));
                advBean.setVideoPath(UrlUtils.getUrl(x));
                advBean.setToUrl(UrlUtils.getUrl(toUrl));
            }
            BaseCommon.testUrlMaps = cdnItem.getTestUrlList();
            BaseCommon.downSpeedTestList = cdnItem.getDownSpeedTestList();
            BaseCommon.trackerList = cdnItem.getTrackerList();
            BaseCommon.hList = cdnItem.getHttpAccelerateList();
            BaseCommon.pList = cdnItem.getTorrentList();
            BaseCommon.mList = cdnItem.getM3U8List();
            BaseCommon.ccList=cdnItem.getCcList();

            //MainApplicationContext.PLAY_FUN = cdnItem.getEnableHTTP();
//            MainApplicationContext.DEFALUT_LINE = cdnItem.getDefaultLine();

            MainApplicationContext.playSpeedLog = cdnItem.isPlaySpeedLogEnable();
            MainApplicationContext.imageErrorLog = cdnItem.isImageErrorLogEnable();
            MainApplicationContext.apiErrorLog = cdnItem.isApiErrorLogEnable();
            MainApplicationContext.socketTestLog = cdnItem.isSocketTestEnable();
            MainApplicationContext.cacheSpeedLog = cdnItem.isCacheLogEnable();
            MainApplicationContext.torrentMD5CheckEnable = cdnItem.isTorrentMD5CheckEnable();
            MainApplicationContext.umengErrorLog=cdnItem.isUmengErrorEnable();
            MainApplicationContext.connectionCloseBoo=cdnItem.isConnectionCloseBoo();
            MainApplicationContext.webpImageEnabled=cdnItem.isImageWebPEnable();

            //MainApplicationContext.setUmengLog();
        }catch (Exception ex){}
        //发测试版本的时候这里打开就行了
        if(cdnItem.isTestClientRunEnable()==false&&MainApplicationContext.testClientLog==true){
            //MainApplicationContext.showips("测试版暂不可用,请到官网下载最新正式版本,非常感谢您参与测试!",this,"");
            MainApplicationContext.showips("测试版暂不可用,请到官网下载最新正式版本,非常感谢您参与测试!",SplashActivity.this,TipsEnum.TO_TIPS);
            return;
        }

        //检查热更新。。。
        new HotService(SplashActivity.this,hotListener).start();
        //先请求版本,没有更新的再测试速度
        presenter.getVersion();
        presenter.toLaunchApplication();
    }

    IHotListener hotListener=new IHotListener() {
        @Override
        public void onHotSuccess() {
        }
        @Override
        public void onHotCancel() {
            //infoStr="检查版本更新...";
            //先请求版本,没有更新的再测试速度
            //presenter.getVersion();
        }
    };

    private boolean isShowVersionErr=false;
    @Override
    public void onVersionResult(VersionBean versionBean) {
        stopTimer();
        if(versionBean==null||versionBean.getData()==null){
            if(isShowVersionErr==false) {
                isShowVersionErr = true;
//                MainApplicationContext.showips("检查版本失败,请稍后重试或到官网下载最新版本!", SplashActivity.this, "");
                MainApplicationContext.showips("检查版本失败,请稍后重试或到官网下载最新版本!",SplashActivity.this,TipsEnum.TO_TIPS);
            }
            return;
        }
        int code= SystemUtils.getVersionCode(this);
        long hearTimer=versionBean.getData().getHeartbeatTime();
        MainApplicationContext.HEAT_TIMER=hearTimer;
        if (versionBean.getData().getInsideVersion() > code) {
            //有更新
            Intent intent = new Intent(SplashActivity.this, AppUpdateActivity.class);
            String versionBeanStr = new Gson().toJson(versionBean);
            intent.putExtra("versionBean", versionBeanStr);
            startActivity(intent);
            return;
        }else{//没有更新
            //另一个线程检测速度
            //其他的测速每一个单独的启一个线程来处理
            newThread();
            onSpeedFinalResult();
        }
    }


    @Override
    public void onError(String msg) {
        try {
            toast(msg);
        }catch (Exception ex){}
        stopTimer();
        //到登录
        gotoActivity(LoginActivity.class,true);
    }

    @Override
    public void onStopT() {
        stopTimer();
    }

    @Override
    public void onSpeedFinalResult() {
        stopTimer();
        //这里应该是要进入广告页面才对。
        if(advBean!=null) {
            OpenScreenAdvActivity.toActivity(SplashActivity.this, advBean,"0");
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return;
        }
        if(loginController==null){
            loginController=new LoginController(SplashActivity.this);
        }
        loginController.toLogin();
    }
    /**
     * 检测速度
     */
    private void newThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
            new CDNCheckSpeed().start(CDNType.RES);
            new CDNCheckSpeed().start(CDNType.TOR);
            new CDNCheckSpeed().start(CDNType.HTTP);
            new CDNCheckSpeed().start(CDNType.M3U8);
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        loginController=null;
        super.onDestroy();
        stopTimer();
        Log.e("TAG","SplashActivity onDestroy...");
    }
}
