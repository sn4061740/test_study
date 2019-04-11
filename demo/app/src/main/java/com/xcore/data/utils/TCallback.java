package com.xcore.data.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.base.Request;
import com.xcore.MainApplicationContext;
import com.xcore.data.BaseBean;
import com.xcore.data.bean.JsonDataBean;
import com.xcore.data.bean.TokenBean;
import com.xcore.services.ApiSystemFactory;
import com.xcore.ui.other.TipsEnum;
import com.xcore.utils.LogUtils;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import okhttp3.Response;

public abstract class TCallback<T> extends AbsCallback<T> {

    private Type type;
    private Class<T> clazz;

    public TCallback() {
    }

    public TCallback(Type type) {
        this.type = type;
    }

    public TCallback(Class<T> clazz) {
        this.clazz = clazz;
    }

    private long startTime=0;
    private String requstUrl="";
    private String paramsStr="";

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        try {
            startTime=System.currentTimeMillis();
            requstUrl=request.getUrl();
        }catch (Exception ex){}
        String token=MainApplicationContext.getToken();
        if(token!=null&&token.length()>0){
            request.removeHeader("Authorization");
            request.headers("Authorization",token);
        }
//        LogUtils.showLog("请求:"+requstUrl);
    }

    //成功的统一回调
    public abstract void onNext(T t);

    //设置统一回调方式
    @Override
    public void onSuccess(com.lzy.okgo.model.Response<T> response) {
        //得到数据判断状态码 status 200 成功  500 错误 403 token 过期
        try {
            T xT=response.body();
            if(xT==null){
                onError(response);
                return;
            }
            if(xT instanceof String){
                onNext(xT);
                return;
            }else if(xT instanceof TokenBean){
                onNext(xT);
                return;
            }else if(xT instanceof JsonDataBean){
                onNext(xT);
                return;
            }
            BaseBean b= (BaseBean) response.body();
            if(b.getStatus()==200){
                onNext(xT);
            }else if(b.getStatus()==500){
                onError(null);
                MainApplicationContext.showips(b.getMessage(),null,TipsEnum.TO_TIPS);
            }else if(b.getStatus()==403){//TOKEN 失效,刷新
                onError(null);
                try {
                    MainApplicationContext.toLogin("");
                    MainApplicationContext.showips("登录失效,请重新登录", null, TipsEnum.TO_TIPS);
                }catch (Exception ex){}
            }else if(b.getStatus()==501){//资源权限拒绝
                onError(null);
                MainApplicationContext.showips(b.getMessage(),null,TipsEnum.TO_TIPS);
            }else if(b.getStatus()==510){//资源权限拒绝   观缓存次数不足看/
                onError(null);
                MainApplicationContext.showips(b.getMessage(),null,TipsEnum.TO_SPREAD);
            }else if(b.getStatus()==402){//跳到登录
                onError(null);
                MainApplicationContext.showips(b.getMessage(),null,TipsEnum.TO_LOGIN);
            }else if(b.getStatus()==1404){//没有找到电影
                MainApplicationContext.showips(b.getMessage(),null,TipsEnum.TO_TIPS);
                onError(null);
            }else{
                onError(null);
                MainApplicationContext.showips(b.getMessage(),null,TipsEnum.TO_TIPS);
            }
//            LogUtils.showLog(requstUrl+"返回："+b.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onError(com.lzy.okgo.model.Response<T> response) {
        if(response==null){
            return;
        }
        int statusCode=response.code();
        LogUtils.showLog("API请求出错,状态码:"+statusCode+" URL:"+requstUrl);
        //在这里上传错误信息
        if(requstUrl.contains("ARR")){//如果是提交错误的接口出错了就不要再请求了
            return;
        }
        long endTime=System.currentTimeMillis();
        long eT=endTime-startTime;
        String msg="请求出错";
        try {
            msg=LogUtils.getException(response.getException());
            if (TextUtils.isEmpty(msg)) {
                msg = "请求出错";
            }
        }catch (Exception e){}
        try {
            msg += "|API_URL=" + requstUrl;
            msg += "|API_ERROR=API_接口|";
            LogUtils.apiRequestError(requstUrl, msg,eT,statusCode);
        }catch (Exception ex){}
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T convertResponse(Response response) throws Throwable {
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用

        //详细自定义的原理和文档，看这里： https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
        if (type == null) {
            if (clazz == null) {
                Type genType = getClass().getGenericSuperclass();
                type = ((ParameterizedType) genType).getActualTypeArguments()[0];
            } else {
                JsonConvert<T> convert = new JsonConvert<>(clazz);
                return convert.convertResponse(response);
            }
        }
        JsonConvert<T> convert = new JsonConvert<>(type);
        return convert.convertResponse(response);
    }

}