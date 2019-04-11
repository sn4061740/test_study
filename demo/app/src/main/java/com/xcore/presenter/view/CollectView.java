package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.RCBean;

public interface CollectView extends BaseView {
    void onCollect(RCBean rcBean);
    void onRecode(RCBean rcBean,int curPage);
}
