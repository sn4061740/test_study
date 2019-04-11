package com.xcore.services;

import android.content.Context;
import android.os.Build;

import com.common.BaseCommon;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.PostRequest;
import com.xcore.MainApplicationContext;
import com.xcore.data.utils.TCallback;
import com.xcore.utils.SystemUtils;

/**
 * 请求统一从这里发出去
 */
public class SystemApiService {
    public <T>void get(String url, TCallback<T> callback){
        int index=url.indexOf("?");
        String v=BaseCommon.VERSION_NAME;// SystemUtils.getVersion(context);
        int vCode=BaseCommon.VERSION_CODE;//SystemUtils.getVersionCode(context);
        if(index>-1){
            url+="&version="+v+"&versionCode="+vCode;
        }else{
            url+="?version="+v+"&versionCode="+vCode;
        }
        url+="&channel="+BaseCommon.CHANNEL;
        url=UrlUtils.getUrl(url);
        OkGo.<T>get(url).removeHeader("Connection")
                .headers("Connection","close")
                .headers("User-Agent",getUserAgent()).execute(callback);
    }

    public <T>void post(String url, final TCallback<T> callback, HttpParams params, HttpHeaders headers){
        url=UrlUtils.getUrl(url);
        PostRequest<T> postRequest= OkGo.<T>post(url);
        if(headers!=null){
            postRequest.headers(headers);
        }
        Context context=MainApplicationContext.context;
        String v=BaseCommon.VERSION_NAME;// SystemUtils.getVersion(context);
        int vCode=BaseCommon.VERSION_CODE;//SystemUtils.getVersionCode(context);
        if(params==null){
            params=new HttpParams();
        }
        if(params!=null){
            params.put("version",v);
            params.put("versionCode",vCode);
            params.put("channel",BaseCommon.CHANNEL);
            postRequest.params(params);
        }
        postRequest.removeHeader("Connection")
                .headers("Connection","close")
                .headers("User-Agent",getUserAgent()).execute(callback);
    }

    private String getUserAgent(){
        return "http/service";
    }
}
