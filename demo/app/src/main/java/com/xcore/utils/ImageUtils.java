package com.xcore.utils;

import com.common.BaseCommon;

public class ImageUtils {
    public static String getRes(String path){
        if(path==null||path.length()<=0){
            return "";
        }
        String res= BaseCommon.RES_URL+path;
        return res;
    }

}
