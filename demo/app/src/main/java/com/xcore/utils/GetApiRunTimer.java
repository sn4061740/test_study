package com.xcore.utils;

import android.content.Context;
import android.text.TextUtils;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.data.bean.CdnBean;
import com.xcore.data.bean.VersionBean;
import com.xcore.data.utils.TCallback;
import com.xcore.presenter.view.SplashView;
import com.xcore.services.ApiSystemFactory;
import com.xcore.ui.enums.APIEnum;
import com.xcore.ui.other.TipsEnum;

/**
 * 请求API，有5次重连机会
 */
public class GetApiRunTimer {
    private SplashView view;
    private APIEnum apiEnum;
    private boolean isSuccess=false;//是否成功

    private int index=0;//当前请求次数
    private int maxNumber=5;//总的请求 5 次
    private int errorCount=0;//错误次数

    public GetApiRunTimer(APIEnum apiEnum , final SplashView view){
        this.apiEnum=apiEnum;
        this.view=view;
    }
    public void start(){
        index++;
        switch (apiEnum){
            case API:
                getAPI();
                break;
            case VERSION:
                getVersion();
                break;
            case CDN:
                getCDN();
                break;
                default:
                    break;
        }
    }
    //请求api
    private void getAPI(){
    }

    //请求版本
    private void getVersion(){//
        String url= BaseCommon.API_URL+"api/v1/system/getVersion?version="+BaseCommon.VERSION_NAME+"&versionCode="+BaseCommon.VERSION_CODE;
        final String xUrl=url;
        final long startTime=System.currentTimeMillis();
        OkGo.<String>get(xUrl)
            .removeHeader("Connection")
            .headers("Connection","close")
            .headers("User-Agent","http/service")
            .execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    String result=response.body();
                    try {
                        VersionBean versionBean = new Gson().fromJson(result, VersionBean.class);
                        if (versionBean.getStatus() != 200) {
                            MainApplicationContext.showips(versionBean.getMessage(),null,TipsEnum.TO_TIPS);
                            if(view!=null){
                                view.onStopT();
                            }
                        } else {
                            if(isSuccess){
                                return;
                            }
                            errorCount=-1;
                            isSuccess=true;
                            if(view!=null) {
                                view.onVersionResult(versionBean);
//                                String msg ="进版本了。。";// LogUtils.getException(ex);
//                                if (view != null) {
//                                    view.onToErr(msg);
//                                }
                            }
                        }
                    }catch (Exception ex){//转换异常
                        int statusCode=response.code();
                        String errMsg=LogUtils.getException(ex);
                        errMsg="版本数据转换异常,得到的数据是:"+result+",错误消息为:"+errMsg;
                        onVersionError(xUrl,errMsg,statusCode,startTime);
                        errorCount++;
                        if(index<maxNumber&&isSuccess==false){
                            start();
                        }else {
                            if(errorCount>=maxNumber&&view!=null) {
                                view.onVersionResult(null);
                            }
                        }
                    }
                }
                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    int statusCode=response.code();
                    String errMsg=LogUtils.getException(response.getException());
                    onVersionError(xUrl,errMsg,statusCode,startTime);
                    errorCount++;
                    if(index<maxNumber&&isSuccess==false){
                        start();
                    }else {
                        try {
                            if(errorCount>=maxNumber&&view!=null) {
                                view.onVersionResult(null);
                            }
                        } catch (Exception ex) {
                        }
                    }
                }
            });
    }

    private void onVersionError(String requstUrl,String errorMsg,int code,long startTime){
        try {
            String msg =errorMsg;// LogUtils.getException(ex);
            if (view != null) {
                view.onToErr(msg);
            }
        }catch (Exception ex1){}

        int statusCode=code;
        LogUtils.showLog("API请求出错,状态码:"+statusCode+" URL:"+requstUrl);
        //在这里上传错误信息
        if(requstUrl.contains("ARR")){//如果是提交错误的接口出错了就不要再请求了
            return;
        }
        long endTime=System.currentTimeMillis();
        long eT=endTime-startTime;
        String msg=errorMsg;
        try {
            msg += "|API_URL=" + requstUrl;
            msg += "|API_ERROR=API_接口|";
            LogUtils.apiRequestError(requstUrl, msg,eT,statusCode);
        }catch (Exception ex){}
    }
    //请求CDN
    private void getCDN(){
        ApiSystemFactory.getInstance().<CdnBean>getCdn(new TCallback<CdnBean>() {
            @Override
            public void onNext(CdnBean cdnBean) {
                if(isSuccess){
                    return;
                }
                try {
                    errorCount=-1;
                    isSuccess=true;
                    if(view!=null) {
                        view.onCdnResult(cdnBean);
//                        String msg ="进CDN了。。";// LogUtils.getException(ex);
//                        if (view != null) {
//                            view.onToErr(msg);
//                        }
                    }
                }catch (Exception ex){
                    try {
                        String msg = LogUtils.getException(ex);
                        if (view != null) {
                            view.onToErr(msg);
                        }
                    }catch (Exception ex1){}
                }
            }
            @Override
            public void onError(Response<CdnBean> response) {
                super.onError(response);
                try {
                    String msg = LogUtils.getException(response.getException());
                    if (view != null) {
                        view.onToErr(msg);
                    }
                }catch (Exception ex){}

                errorCount++;
                if(index<maxNumber&&isSuccess==false){
                    start();
                }else {
                    try {
                        if(errorCount>=maxNumber&&view!=null) {
                            view.onCdnResult(null);
                        }
                    } catch (Exception ex) {
                    }
                }

            }
        });
    }

}
