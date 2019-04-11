package com.xcore.ui.touch;

public interface IPlayClickListenner {
    void onFinish();
    boolean onCollect(boolean isCollect);
    boolean onLike();
    boolean onNolike();
    void onClickError();
    void onClickComment();
    boolean onDown();
    boolean onShare();
}
