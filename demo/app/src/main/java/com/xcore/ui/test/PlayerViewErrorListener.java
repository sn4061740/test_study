package com.xcore.ui.test;

import android.media.ViviTV.player.widget.DolitBaseMediaPlayer;
import android.net.Uri;

import com.xcore.utils.LogUtils;

public class PlayerViewErrorListener implements DolitBaseMediaPlayer.OnErrorListener {
    IPlayerViewListener iPlayerViewListener;
    //100 为播放器内部返回错误码,70003 为播放器返回错误码
    private int ERROR_100=900003;
    private int ERROR_70003=900004;
    private int ERROR_900005=900005;

    public PlayerViewErrorListener(){}

    public PlayerViewErrorListener(IPlayerViewListener viewListener){
        this.iPlayerViewListener=viewListener;
    }

    @Override
    public boolean onError(Object mp, int what, int extra, long currentPosition) {
        if(iPlayerViewListener!=null){
            iPlayerViewListener.onError("一般为超时，网络状态不佳",ERROR_900005);
        }
        return false;
    }

    @Override
    public boolean onMediaError(int what) {
        if(iPlayerViewListener!=null){
            iPlayerViewListener.onError("视频中断，一般是视频源异常或者不支持的视频类型。",ERROR_100);
        }
        return false;
    }

    @Override
    public void onOpenError(Exception e, Uri uri) {
        if(iPlayerViewListener!=null){
            String eStr = LogUtils.getException(e);
            iPlayerViewListener.onError("打开视频失败:"+eStr,ERROR_70003);
        }
    }
    //m3u8 播放失败
    public void onM3u8Error(String msg,int code){
        if(iPlayerViewListener!=null){
            iPlayerViewListener.onError("M3u8 播放失败:"+msg,code);
        }
    }
}
