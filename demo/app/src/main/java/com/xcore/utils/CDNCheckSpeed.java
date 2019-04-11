package com.xcore.utils;

import android.text.TextUtils;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.data.bean.JsonDataBean;
import com.xcore.services.UrlUtils;
import com.xcore.ui.Config;
import com.xcore.ui.OnCheckSpeedListenner;
import com.xcore.ui.enums.CDNType;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class CDNCheckSpeed {
    private CDNType type;
    private boolean isSuccess=false;
    private OnCheckSpeedListenner onCheckSpeedListenner;

    private int totalLen=0;//总条数
    private int len=0;//当前错误条数
    List<String> list = new ArrayList<>();

    public CDNCheckSpeed(){}
    public CDNCheckSpeed(OnCheckSpeedListenner checkSpeedListenner){
        this.onCheckSpeedListenner=checkSpeedListenner;
    }

    //根据类型检测
    public void start(final CDNType type1){
        this.type=type1;
        try {
            switch (type1) {
                case API://api
                    list = BaseCommon.apiList;
                    break;
                case TOR://种子
                    list = BaseCommon.videoLists;
                    break;
                case RES://图片
                    list = BaseCommon.resLists;
                    break;
                case HTTP://http
                    list = BaseCommon.httpAccelerateLists;
                    break;
                case M3U8://m3u8
                    list = BaseCommon.m3u8List;
                    break;
                case APK://apk
                    list = BaseCommon.apkLists;
                    break;
            }
            if ((list == null || list.size() <= 0)) {
                error(null, null, 0,0);
            } else if (list != null && list.size() == 1 && type1 != CDNType.JSON) {
                success(list.get(0));
            } else {//检测
                totalLen = list.size();
                check();
            }
        }catch (Exception ex){}
    }
    //检测
    private void check(){
        final List<String> uList=list;
        for(String url:uList){
            String u=UrlUtils.getUrl(url);
            startCheck(u);
        }
    }
    //开始检测
    private void startCheck(final String url){
        final long startTime=System.currentTimeMillis();
        OkGo.<String>get(url).retryCount(0).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                success(url);
            }
            @Override
            public void onError(Response<String> response) {
                try {
                    int v = response.code();
                    LogUtils.showLog("测试速度失败:" + url + "  CODE:" + v);
                    long endTime = System.currentTimeMillis();
                    long value = endTime - startTime;
                    Throwable throwable = response.getException();
                    error(throwable, url, value,v);
                }catch (Exception ex){}
            }
        });
    }
    //成功
    private void success(String path){
        final String pathStr=UrlUtils.getUrl(path);
        if(!isEmpty(pathStr)&&isSuccess==false) {
            isSuccess = true;
            Config config=MainApplicationContext.getConfig();
            switch (type) {
                case API://api
                    BaseCommon.API_URL = pathStr;
                    LogUtils.showLog("设置API地址："+pathStr);
                    config.put("API_URL",pathStr);
                    break;
                case TOR://种子
                    BaseCommon.VIDEO_URL = pathStr;
                    LogUtils.showLog("设置TOR地址："+pathStr);
                    config.put("VIDEO_URL",pathStr);
                    break;
                case RES://图片
                    BaseCommon.RES_URL = pathStr;
                    LogUtils.showLog("设置RES地址："+pathStr);
                    config.put("RES_URL",pathStr);
                    break;
                case HTTP://http
                    BaseCommon.HTTP_URL = pathStr;
                    LogUtils.showLog("设置HTTP地址："+pathStr);
                    config.put("HTTP_URL",pathStr);
                    break;
                case M3U8://m3u8
                    BaseCommon.M3U8_URL = pathStr;
                    LogUtils.showLog("设置M3U8地址："+pathStr);
                    config.put("M3U8_URL",pathStr);
                    break;
                case APK://apk
                    BaseCommon.APK_URL = pathStr;
                    LogUtils.showLog("设置APK地址："+pathStr);
                    config.put("APK_URL",pathStr);
                    break;
            }
            if(onCheckSpeedListenner!=null){
                onCheckSpeedListenner.onSuccess(pathStr);
            }
        }
    }
    //错误
    private void error(final Throwable throwable, final String url, final long timer, final int code){
        len++;
        if(throwable==null&&url==null&&timer==0){
            if(onCheckSpeedListenner!=null){
                onCheckSpeedListenner.onCompleteError();
            }
            return;
        }
        try {
            String msg ="";
            if(throwable!=null){
                msg=LogUtils.getException(throwable);
                if (TextUtils.isEmpty(msg)) {
                    msg = "请求出错,没有获取到异常信息";
                }
            }else {
                msg="请求出错,没有获取到异常信息";
            }
            msg += "|API_URL=" + url;
            msg += "|API_ERROR=API_测速";
            LogUtils.apiRequestError(url, msg,timer,code);
            if(onCheckSpeedListenner!=null){
                onCheckSpeedListenner.onError(msg);
            }
        }catch (Exception e){}
        if(len>=totalLen){
            if(onCheckSpeedListenner!=null){
                onCheckSpeedListenner.onCompleteError();
            }
        }
    }
    //判断
    private boolean isEmpty(String s){
        return s==null||s.length()<=0;
    }
}
