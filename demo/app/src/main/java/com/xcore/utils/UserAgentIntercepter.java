package com.xcore.utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserAgentIntercepter implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
//        String userAgent=MainApplicationContext.getUserAgent();
        Request request=chain.request().newBuilder()
//                .removeHeader("User-Agent")
//                .addHeader("User-Agent",userAgent)
                .build();
        return chain.proceed(request);
    }
}
