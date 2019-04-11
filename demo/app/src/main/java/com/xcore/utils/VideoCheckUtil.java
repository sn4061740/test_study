package com.xcore.utils;

import com.xcore.MainApplicationContext;

//视频相关的检查
public class VideoCheckUtil {
    //是否支持硬解码
    public static boolean isSupportMediaCodecHardDecoder=false;

    public static void init(){
        new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                isSupportMediaCodecHardDecoder = MediaCodecUtil.isSupportMediaCodecHardDecoder();
            }catch (Exception ex){}
            }
        }).start();
    }

    public static boolean getMediaCodecEnabled(){
        boolean boo=MainApplicationContext.mediaCodecEnabled;
        if(boo&&isSupportMediaCodecHardDecoder){
            return true;
        }
        return false;
    }
}
