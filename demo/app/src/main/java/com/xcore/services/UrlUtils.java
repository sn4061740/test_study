package com.xcore.services;

import android.os.Build;

public class UrlUtils {
    public static String getUrl(String url){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {//小于26 的用 http 请求
            url=url.replace("https","http");
        }
        return url;
    }
}
