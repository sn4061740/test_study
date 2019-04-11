package com.xcore.ui.test;

public interface LineChangeListener {
    void changeSuccess(String path);//切换成功, 返回播放地址
    void changeError();//切换失败
    void onProgress(long speed);
    void setStreamId(String sId);
}
