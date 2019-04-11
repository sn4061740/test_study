package com.xcore.services;

import com.common.BaseCommon;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.PutRequest;
import com.xcore.MainApplicationContext;
import com.xcore.data.utils.TCallback;
import com.xcore.ui.Config;
import com.xcore.utils.NumberUtils;
import com.xcore.utils.SystemUtils;

import java.util.List;

public class LogService {

    public <T> void get(String url, final TCallback<T> callback){
        List<String> recodeList=BaseCommon.recodeList;
        if(recodeList==null||recodeList.size()<=0){
            url=BaseCommon.API_URL+url;
        }else{
            int vNumber=NumberUtils.getRandom(recodeList.size());
            String xUrl=recodeList.get(vNumber);
            url=xUrl+url;
        }
        int index=url.indexOf("?");
        String v=BaseCommon.VERSION_NAME;
        int vCode=BaseCommon.VERSION_CODE;
        if(index>-1){
            url+="&version="+v+"&versionCode="+vCode;
        }else{
            url+="?version="+v+"&versionCode="+vCode;
        }
        Config config=MainApplicationContext.getConfig();
        String fCode=config.getKey();
        url+="&DeviceNo="+fCode+"&channel="+BaseCommon.CHANNEL;
        OkGo.<T>get(url)
                .removeHeader("Connection")
                .headers("Connection","close")
                .headers("User-Agent",getUserAgent())
                .execute(callback);
    }

    public <T> void post(String url, final TCallback<T> callback, HttpParams params, HttpHeaders headers){
        List<String> recodeList=BaseCommon.recodeList;
        if(recodeList==null||recodeList.size()<=0){
            url=BaseCommon.API_URL+url;
        }else{
            int vNumber=NumberUtils.getRandom(recodeList.size());
            String xUrl=recodeList.get(vNumber);
            url=xUrl+url;
        }

        url=UrlUtils.getUrl(url);
        PostRequest<T> postRequest= OkGo.<T>post(url);
        if(headers!=null){
            postRequest.headers(headers);
        }
        String v=BaseCommon.VERSION_NAME;
        int vCode=BaseCommon.VERSION_CODE;
        if(params==null){
            params=new HttpParams();
        }
        params.put("version",v);
        params.put("versionCode",vCode);
        Config config=MainApplicationContext.getConfig();
        String fCode=config.getKey();
        params.put("DeviceNo",fCode);
        params.put("channel",BaseCommon.CHANNEL);
        postRequest.params(params);

        postRequest.removeHeader("Connection")
                .headers("Connection","close")
                .headers("User-Agent",getUserAgent())
                .execute(callback);
    }
    //日志put 请求
    public <T>void put(String url,final TCallback<T> callback, HttpParams params, HttpHeaders headers){
        List<String> recodeList=BaseCommon.recodeList;
        if(recodeList==null||recodeList.size()<=0){
            url=BaseCommon.API_URL+url;
        }else{
            int vNumber=NumberUtils.getRandom(recodeList.size());
            String xUrl=recodeList.get(vNumber);
            url=xUrl+url;
        }
        url=UrlUtils.getUrl(url);
        PutRequest<T> putRequest=OkGo.put(url);
        if(params!=null){
            putRequest.params(params);
        }
        if(headers!=null){
            putRequest.headers(headers);
        }
        putRequest.params("version",BaseCommon.VERSION_NAME);
        putRequest.params("versionCode",BaseCommon.VERSION_CODE);
        Config config=MainApplicationContext.getConfig();
        String fCode=config.getKey();
        params.put("DeviceNo",fCode);
        putRequest
                .removeHeader("Connection")
                .headers("Connection","close")
                .headers("User-Agent",getUserAgent())
                .execute(callback);
    }

    private String getUserAgent(){
        return "http/service";
    }
}
