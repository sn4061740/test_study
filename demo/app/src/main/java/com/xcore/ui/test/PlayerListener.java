package com.xcore.ui.test;

import com.xcore.data.model.SelectModel;

public interface PlayerListener {
    void onStartPlay(SelectModel selectModel);//开始请求播放地址
    void onStartPlay(SelectModel selectModel,int pos,int qualty);//开始请求播放地址
    void onBackPressed();//返回
    void onError(String model,String msg,int code);
    void onPlaySuccess(int pos);
    void playerError();//播放出错或中途一直是0b
    void onExit(long pos);
    //切换清晰度
    void onStartPlayChangeQualty(SelectModel selectModel,int qualty,int pos);
    void updatePlaySpeed(PlaySpeedModel playSpeedModel);
}
