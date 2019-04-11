package com.xcore.ui;

import com.xcore.data.bean.JsonDataBean;

public interface OnCheckSpeedListenner {
    void onSuccess(String path);
    void onSuccess(JsonDataBean jsonDataBean);
    void onError(String err);
    void onCompleteError();//全部都错误了且全部完成了
}
