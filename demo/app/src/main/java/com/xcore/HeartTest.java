package com.xcore;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.xcore.data.BaseBean;
import com.xcore.data.bean.TokenBean;
import com.xcore.data.utils.DataUtils;
import com.xcore.data.utils.TCallback;
import com.xcore.services.ApiFactory;
import com.xcore.services.ApiSystemFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 心跳
 */
public class HeartTest {
    private Timer heartTimer;

    public void start(){
        //心跳
        long hTimer=MainApplicationContext.HEAT_TIMER;
        if(hTimer>0){
            if(heartTimer!=null){
                heartTimer.cancel();
                heartTimer=null;
            }
            heartTimer=new Timer();
            heartTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    toHeart();
                }
            },3000,hTimer);
        }
    }
    //心跳
    private void toHeart(){
        try {
            ApiFactory apiFactory = ApiFactory.getInstance();
            if (apiFactory == null || heartTimer == null) {//正在刷新token 停止心跳,等待刷新token
                //destroy();
                return;
            }
            apiFactory.<String>toHeart(new TCallback<String>() {
                @Override
                public void onNext(String s) {
                    try {
                        BaseBean baseBean = new Gson().fromJson(s, BaseBean.class);
                        if (baseBean.getStatus() == 403) {
                            MainApplicationContext.toLogin("");
                        }
                    } catch (Exception ex) {
                    }
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                }
            });
        }catch (Exception ex){}
    }
    public void destroy(){
        if(heartTimer!=null){
            heartTimer.cancel();
        }
        heartTimer=null;
    }
}
