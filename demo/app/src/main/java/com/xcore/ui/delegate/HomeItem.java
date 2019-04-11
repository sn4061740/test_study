package com.xcore.ui.delegate;

import com.xcore.data.bean.HomeBean;
import com.xcore.ui.touch.IHomeOnClick;

public class HomeItem extends BaseItem {
    private HomeBean.HomeDataItem homeDataItem;
    private IHomeOnClick homeOnClick;
    private int index=0;

    public HomeBean.HomeDataItem getHomeDataItem() {
        return homeDataItem;
    }

    public void setHomeDataItem(HomeBean.HomeDataItem homeDataItem) {
        this.homeDataItem = homeDataItem;
    }

    public IHomeOnClick getHomeOnClick() {
        return homeOnClick;
    }

    public void setHomeOnClick(IHomeOnClick homeOnClick) {
        this.homeOnClick = homeOnClick;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
