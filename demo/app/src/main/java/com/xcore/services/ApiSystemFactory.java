package com.xcore.services;

import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.xcore.MainApplicationContext;
import com.xcore.data.bean.JsonDataBean;
import com.xcore.data.bean.SpeedDataBean;
import com.xcore.data.bean.TokenBean;
import com.xcore.data.utils.TCallback;
import com.xcore.ui.Config;
import com.xcore.utils.LogUtils;
import com.xcore.utils.SystemUtils;


public class ApiSystemFactory {
    private SystemApiService apiService=new SystemApiService();
    private LogService logService=new LogService();
    private static Config config;

    private ApiSystemFactory(){}

    private static ApiSystemFactory instance;
    public static ApiSystemFactory getInstance(){
        if(instance==null){
            instance=new ApiSystemFactory();
            config=MainApplicationContext.getConfig();
        }
        return instance;
    }
    /**
     * 测试速度
     * @param url
     * @param callback
     */
    public void getTestSpeed(String url,TCallback<String> callback){
        apiService.<String>get(url,callback);
    }


    /**
     * 得到json
     */
    public void getJson(String jsonUrl, final TCallback<JsonDataBean> callback){
        String url=jsonUrl+"data.json";
        apiService.get(url,callback);
    }

    private boolean isToken=false;
    private int refreshCounnt=0;

    /**
     * 刷新 TOKEN
     * @param
     */
    public void refreshToken(){
        //心跳停止了就不能刷新访问了
        if(isToken){//正在刷新token中 避免同时重复刷新TOKEN
            return;
        }
        if(config==null){
            config=MainApplicationContext.getConfig();
        }
        config.remove(config.TOKEN);
        String refreshToken=config.get(config.REFRESH_TOKEN);
        isToken=true;
        String url= BaseCommon.API_URL+"api/token";
        String basic= Base64.encodeToString("NEWONEAV:ADMIN".getBytes(),Base64.DEFAULT).trim();

        HttpHeaders headers=new HttpHeaders();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Basic "+basic);

        HttpParams params=new HttpParams();
        params.put("grant_type","refresh_token");
        params.put("refresh_token",refreshToken);
        params.put("version",BaseCommon.VERSION_NAME);
        params.put("versionCode",BaseCommon.VERSION_CODE);
        final String xUrl=UrlUtils.getUrl(url);
        OkGo.<String>post(xUrl)
                .headers(headers)
                .params(params).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                refreshCounnt=0;
                String tokenStr=response.body();
                try {
                    TokenBean tokenBean=new Gson().fromJson(tokenStr,TokenBean.class);
                    if(!TextUtils.isEmpty(tokenBean.getError())&&tokenBean.getError().contains("invalid_client")){
                        MainApplicationContext.toLogin("");
                        return;
                    }else if(!TextUtils.isEmpty(tokenBean.getError())&&tokenBean.getError().contains("invalid_grant")){//刷新 token 的 refresh_token 失效了
                        MainApplicationContext.toLogin("");
                        return;
                    }
                    isToken=false;
                    if(config==null) {
                        config = MainApplicationContext.getConfig();
                    }
                    config.put(config.TOKEN,tokenBean.getAccess_token());
                    config.put(config.REFRESH_TOKEN,tokenBean.getRefresh_token());
                    config.put(config.TOKEN_EXPIRES_IN,tokenBean.getExpires_in()+"");
                    config.put(config.CURRENT_EXPIRES_IN,System.currentTimeMillis()+"");

                    MainApplicationContext.startHeart();
                    //成功后保存
                    config.save();
                }catch (Exception ex){
                    String msg = "JSON 转换错误,返回:"+tokenStr+"|API_URL=" + xUrl;
                    msg += "|API_ERROR=API_接口|";
                    LogUtils.apiRequestError(xUrl, msg,0,909);

                    MainApplicationContext.toLogin("");
                }
            }
            @Override
            public void onError(Response<String> response) {
                isToken = false;
                refreshCounnt++;
                int statusCode=response.code();
                String msg="刷新token请求出错|";
                try {
                    msg+=LogUtils.getException(response.getException());
                    if (TextUtils.isEmpty(msg)) {
                        msg = "刷新token请求出错";
                    }
                }catch (Exception e){}
                try {
                    msg += "|API_URL=" + xUrl;
                    msg += "|API_ERROR=API_接口|";
                    LogUtils.apiRequestError(xUrl, msg,0,statusCode);
                }catch (Exception ex){}
                restartRefresh();
            }
        });
    }
    //重新刷新token  5次重连
    public void restartRefresh(){
        if(refreshCounnt>4){
            refreshCounnt=0;
            MainApplicationContext.toLogin("");
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {//1秒后再请求
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                refreshToken();
                }
            }).start();
        }
    }

    //得到token
    public <T> void getToken(final TCallback<T> callback, String... args) {
        String url=BaseCommon.API_URL+"api/token";
        HttpParams params=new HttpParams();
        String basic= Base64.encodeToString("NEWONEAV:ADMIN".getBytes(),Base64.DEFAULT).trim();
        params.put("grant_type","password");
        params.put("username",args[0]);
        params.put("password",args[1]);
        Config config=MainApplicationContext.getConfig();
        String code=config.getKey();

        params.put("appDeviceCode",code);
        params.put("channel",BaseCommon.CHANNEL);
        params.put("version",BaseCommon.VERSION_NAME);
        params.put("versionCode",BaseCommon.VERSION_CODE);

        HttpHeaders headers=new HttpHeaders();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Basic "+basic);
        final String xUrl=UrlUtils.getUrl(url);
        OkGo.<String>post(xUrl)
                .headers(headers)
                .params(params)
                .execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String tokenStr=response.body();
                try {
                    TokenBean tokenBean=new Gson().fromJson(tokenStr,TokenBean.class);
                    callback.onNext((T) tokenBean);
                }catch (Exception ex){
                    if(callback!=null) {
                        callback.onError(null);
                    }
                    String msg = "TOKEN接口 JSON 转换错误,返回:"+tokenStr+"|API_URL=" + xUrl;
                    msg += "|API_ERROR=API_接口|";
                    LogUtils.apiRequestError(xUrl, msg,0,909);
                }
            }
            @Override
            public void onError(Response<String> response) {
                if(callback!=null) {
                    callback.onError((Response<T>) response);
                }
                int statusCode=response.code();
                String msg="TOKEN 请求出错|";
                try {
                    msg+=LogUtils.getException(response.getException());
                    if (TextUtils.isEmpty(msg)) {
                        msg = "TOKEN 请求出错";
                    }
                }catch (Exception e){}
                try {
                    msg += "|API_URL=" + xUrl;
                    msg += "|API_ERROR=API_接口|";
                    LogUtils.apiRequestError(xUrl, msg,0,statusCode);
                }catch (Exception ex){}
            }
        });
    }
    //得到版本信息
    public <T> void getVersion(TCallback<T> callback) {
        String url= BaseCommon.API_URL+"api/v1/system/getVersion";
        apiService.<T>get(url,callback);
    }
    //得到版本信息
    public <T> void getVersion(TCallback<T> callback,String x) {
        String url= BaseCommon.API_URL+"api/v1/system/getVersion?key=officialWebsite";
        apiService.<T>get(url,callback);
    }
    //开始下载
    public <T> void startDownVersion(int key,TCallback<T> callback){

        try {
            String url = BaseCommon.API_URL + "api/v1/system/addVersionUpgrade";
            Config config= MainApplicationContext.getConfig();
            String fCode =config.getKey();
            HttpParams params = new HttpParams();
            params.put("key", key);
            params.put("DeviceNo", fCode);
            params.put("channel",BaseCommon.CHANNEL);
            apiService.<T>post(url, callback, params, null);
        }catch (Exception ex){}
    }
    //开始下载
    public <T> void downVersionSuccess(int key,TCallback<T> callback){
        try {
            String url = BaseCommon.API_URL + "api/v1/system/VersionUpgradeSuccess";
            Config config=MainApplicationContext.getConfig();
            String fCode = config.getKey();
            HttpParams params = new HttpParams();
            params.put("key", key);
            params.put("DeviceNo", fCode);
            params.put("channel",BaseCommon.CHANNEL);
            apiService.<T>post(url, callback, params, null);
        }catch (Exception ex){}
    }

    //得到CDN
    public <T> void getCdn(TCallback<T> callback) {
        String url= BaseCommon.API_URL+"api/v1/system/getCdn";
        apiService.<T>get(url,callback);
    }
    //注册
    public <T> void toRegister(TCallback<T> callback, HttpParams params){
        String url= BaseCommon.API_URL+"api/v1/appuser/registered";
        url=UrlUtils.getUrl(url);
        PostRequest<T> postRequest= OkGo.<T>post(url);

        String v=BaseCommon.VERSION_NAME;
        int vCode=BaseCommon.VERSION_CODE;
        if(params==null){
            params=new HttpParams();
        }
        if(params!=null){
            params.put("version",v);
            params.put("versionCode",vCode);
            params.put("channel",BaseCommon.CHANNEL);
            postRequest.params(params);
        }
        postRequest.execute(callback);
    }
    //检查热更
    public <T> void getHotUpdate(TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/system/getHotUpdate";
        apiService.<T>get(url,callback);
    }

    //检查热更
    public <T> void getHotUpdateV1(TCallback<T> callback){
        String url= BaseCommon.API_URL+"api/v1/system/getHotUpdateV1";
        apiService.<T>get(url,callback);
    }

    //获取游客信息
    public <T> void getGuest(HttpParams params,TCallback<T> callback){
        String url=BaseCommon.API_URL+"api/v1/appuser/touristsLanding";
        url=UrlUtils.getUrl(url);
        PostRequest<T> postRequest= OkGo.<T>post(url);
        String v=BaseCommon.VERSION_NAME;
        int vCode=BaseCommon.VERSION_CODE;
        if(params==null){
            params=new HttpParams();
        }
        if(params!=null){
            params.put("version",v);
            params.put("versionCode",vCode);
            params.put("channel",BaseCommon.CHANNEL);
            postRequest.params(params);
        }
        postRequest.execute(callback);
    }

    //启动次数
    public <T> void toLaunchApplication(TCallback<T> callback){
        String Imei=SystemUtils.getM(MainApplicationContext.context);
        String url="api/v1/system/LaunchApplication?mCode="+Imei;
        logService.<T>get(url,callback);
    }

}
