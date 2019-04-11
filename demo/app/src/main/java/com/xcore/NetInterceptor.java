package com.xcore;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetInterceptor implements Interceptor {
    //对有些版本比如:小米M5,Meizu M5,金立M5...等等ssl 访问报错。。
    @Override
    public Response intercept(Chain chain) throws IOException {
        try {
            Request.Builder builder = chain.request().newBuilder();
            Request request = builder.build();
            return chain.proceed(request);
        }catch (Exception ex){
            throw new IOException(ex);
    }
    }
}
