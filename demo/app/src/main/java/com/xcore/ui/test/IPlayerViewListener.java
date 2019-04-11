package com.xcore.ui.test;

public interface IPlayerViewListener{
    void onError(String msg,int code);
    void startBuffer();//开始缓冲
    void endBuffer();//结束缓冲
    void  startBufferPlay();//开始播放
}
