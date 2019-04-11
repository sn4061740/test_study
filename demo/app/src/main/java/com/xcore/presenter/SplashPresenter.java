package com.xcore.presenter;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.xcore.MainApplicationContext;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.JsonDataBean;
import com.xcore.data.utils.TCallback;
import com.xcore.presenter.view.SplashView;
import com.xcore.services.ApiSystemFactory;
import com.xcore.services.UrlUtils;
import com.xcore.ui.OnCheckSpeedListenner;
import com.xcore.ui.enums.APIEnum;
import com.xcore.ui.enums.CDNType;
import com.xcore.utils.CDNCheckSpeed;
import com.xcore.utils.GetApiRunTimer;
import com.xcore.utils.JSONCheckUtils;

import java.util.ArrayList;
import java.util.List;

public class SplashPresenter extends BasePresent<SplashView>{

    final Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0) {
                if (view != null) {
                    view.onJsonResult(null);
                }
            }else if(msg.what==1){
                JsonDataBean jsonDataBean= (JsonDataBean) msg.obj;
                if(view!=null){
                    view.onJsonResult(jsonDataBean);
                }
            }else if(msg.what==10){
                getCdn();
            }
        }
    };

    boolean successJsonBoo=false;
    /**
     * 请求 json
     */
    public void getJson(){
        if(!checkNetwork()){
            return;
        }
        List<String> jsList=new ArrayList<>();
        for(String vStr:BaseCommon.jsonList){
            jsList.add(UrlUtils.getUrl(vStr));
        }
        BaseCommon.jsonList=jsList;
        new JSONCheckUtils(new OnCheckSpeedListenner() {
            @Override
            public void onSuccess(String path) {
            }
            @Override
            public void onSuccess(JsonDataBean jsonDataBean) {
                if(view!=null){
                    if(successJsonBoo==false) {
                        successJsonBoo = true;
                        Message msg=new Message();
                        msg.what=1;
                        msg.obj=jsonDataBean;
                        handler.sendMessage(msg);
                    }
                }
            }
            @Override
            public void onError(String err) {
                if(view!=null){
                    view.onToErr(err);
                }
            }
            @Override
            public void onCompleteError() {
                if(view!=null) {
                    handler.sendEmptyMessage(0);
                }
            }
        }).start();
    }
    private int apiIndex=0;
    //api 速度测试
    public void apiSpeed(){
        List<String> apiList=BaseCommon.apiList;
        if(apiList!=null&&apiList.size()<2&&apiList.size()>0){
            String apiUrl=apiList.get(0);
            BaseCommon.API_URL=UrlUtils.getUrl(apiUrl);
            //获取cdn
            getCdn();
            return;
        }
        new CDNCheckSpeed(new OnCheckSpeedListenner() {
            @Override
            public void onSuccess(String path) {
                if(apiIndex>0){
                    return;
                }
                apiIndex++;
                getCdn();
            }
            @Override
            public void onSuccess(JsonDataBean jsonDataBean) {
            }
            @Override
            public void onError(String err) {
                if(view!=null){
                    view.onToErr(err);
                }
            }
            @Override
            public void onCompleteError() {
                if(apiIndex>0){
                    return;
                }
                apiIndex++;
                getCdn();
            }
        }).start(CDNType.API);
    }
    /**
     * 请求版本
     */
    public void getVersion(){
        if(!checkNetwork()){
            return;
        }
        new GetApiRunTimer(APIEnum.VERSION,view).start();
    }

    /**
     * 请求cdn
     */
    public void getCdn(){
        if(BaseCommon.isTestServer) {//是测试服
            BaseCommon.API_URL = "";
        }
        if(!checkNetwork()){
            return;
        }
        new GetApiRunTimer(APIEnum.CDN,view).start();
        //toLaunchApplication();
    }
    //启动次数
    public void toLaunchApplication(){
        new Thread(new Runnable() {
            @Override
            public void run() {
            ApiSystemFactory.getInstance().<String>toLaunchApplication(new TCallback<String>() {
                @Override
                public void onNext(String s) {
                    Log.e("TAG","启动次数："+s);
                }
            });
            }
        }).start();
    }

}
