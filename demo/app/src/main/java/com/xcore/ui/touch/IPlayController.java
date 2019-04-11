package com.xcore.ui.touch;

import com.xcore.ui.enums.PlayTypeEnum;

public interface IPlayController {
    void onLocalPlay(String path);//本地播放
    void onM3u8Play(String path);//m3u8 播放
    void onHttpPlay(String path);//http 播放
    void onTorrentPlay(String path);//种子播放
    long getSpeed();//获取种子
    void onLoadSpeed(String speed);//更新加载速度
    void onError(PlayTypeEnum typeEnum);//获取路径失败
    long getPosition();
}
