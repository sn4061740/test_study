package com.xcore.utils;

import android.util.Log;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.xcore.data.bean.JsonDataBean;
import com.xcore.services.UrlUtils;
import com.xcore.ui.OnCheckSpeedListenner;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class JSONCheckUtils {
    private OnCheckSpeedListenner checkSpeedListenner;
    List<String> list = new ArrayList<>();
    private Timer timer;//超时器

    public JSONCheckUtils(OnCheckSpeedListenner onCheckSpeedListenner){
        this.checkSpeedListenner=onCheckSpeedListenner;
        List<String> vlist = BaseCommon.jsonList;
        for(String value:vlist){
            list.add(value);
        }
    }
    //JSON
    public void start(){
        //取消请求
        //OkGo.getInstance().cancelTag(this);
        checkJson();
    }
    //检测json
    private void checkJson(){
        stopTimer();
        if(list==null||list.size()<=0){
            if(checkSpeedListenner!=null) {
                checkSpeedListenner.onCompleteError();
            }
            return;
        }
        startTimer();
        final long startTime=System.currentTimeMillis();
        final String url=UrlUtils.getUrl(list.remove(0));
        OkGo.<String>get(url).tag(this).retryCount(0).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                stopTimer();
                String resStr ="";
                try {
                    resStr=response.body();
                    Log.e("TAG",resStr);
                    JsonDataBean jsonDataBean = new Gson().fromJson(resStr, JsonDataBean.class);
                    if(checkSpeedListenner!=null){
                        checkSpeedListenner.onSuccess(jsonDataBean);
                    }
                }catch (Exception ex){//得到的不是对的数据
                    try {
                        int code = response.code();
                        LogUtils.showLog("得到数据转换失败:" + url + "  CODE:" + code);
                        long endTime = System.currentTimeMillis();
                        long value = endTime - startTime;
                        String msg =LogUtils.getException(response.getException());
                        if (msg.length()<=0) {
                            msg = "请求出错,请求JSON 测速,没有获取到异常信息,返回的数据是::"+resStr;
                        }
                        msg += "|API_URL=" + url;
                        msg += "|API_ERROR=API_测速";
                        LogUtils.apiRequestError(url, msg,value,code);
                        checkJson();
                    }catch (Exception ex1){}
                }
            }
            @Override
            public void onError(Response<String> response) {
                stopTimer();
                try {
                    int code = response.code();
                    long endTime = System.currentTimeMillis();
                    long value = endTime - startTime;
                    String msg =LogUtils.getException(response.getException());
                    if (msg.length()<=0) {
                        msg = "请求出错,请求JSON 测速,没有获取到异常信息,进错误了";
                    }
                    msg += "|API_URL=" + url;
                    msg += "|API_ERROR=API_测速";
                    LogUtils.apiRequestError(url, msg,value,code);
                }catch (Exception ex1){}
                finally {
                    checkJson();
                }
            }
        });
    }
    //开始计时
    private void startTimer(){
        if(timer!=null){
            timer.cancel();
        }
        timer=null;
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                start();
            }
        },10000);
    }

    private void stopTimer(){
        if(timer!=null){
            timer.cancel();
        }
        timer=null;
    }

}
