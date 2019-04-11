package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.data.bean.PlayerBean;
import com.xcore.data.bean.RCBean;

public interface MeView extends BaseView{
    void onResult(PlayerBean playerBean);
    void onCollect(RCBean rcBean);
    void onRecode(RCBean rcBean);
}
