package com.xcore.ui.test;

import android.media.ViviTV.player.widget.DolitBaseMediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.xcore.R;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class PlayerViewInfoListener implements DolitBaseMediaPlayer.OnInfoListener{
    IPlayerViewListener iPlayerViewListener;

    public PlayerViewInfoListener(){}
    public PlayerViewInfoListener(IPlayerViewListener viewListener){
        this.iPlayerViewListener=viewListener;
    }
    //            IMediaPlayer  后面的是调用时候的顺序
    //            int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001; 4
    //            int MEDIA_INFO_AUDIO_RENDERING_START  = 10002; 5
    //            int MEDIA_INFO_AUDIO_DECODED_START    = 10003; 6
    //            int MEDIA_INFO_VIDEO_DECODED_START    = 10004; 7
    //            int MEDIA_INFO_OPEN_INPUT             = 10005; 1
    //            int MEDIA_INFO_FIND_STREAM_INFO       = 10006; 2
    //            int MEDIA_INFO_COMPONENT_OPEN         = 10007; 3
    @Override
    public boolean onInfo(Object mp, int what, int extra) {
        if (what == DolitBaseMediaPlayer.MEDIA_INFO_BUFFERING_START) {//开始缓冲
            if(iPlayerViewListener!=null){
                iPlayerViewListener.startBuffer();
            }
        }else if (what == DolitBaseMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            if(iPlayerViewListener!=null){
                iPlayerViewListener.endBuffer();
            }
        }else if(what== IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){//刚进去的时候缓冲读取信息 开始播放
            if(iPlayerViewListener!=null){
                iPlayerViewListener.startBufferPlay();
            }
        }
        return false;
    }
}
