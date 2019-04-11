package com.xcore.ui.delegate;

import com.xcore.data.bean.AdvBean;

public class AdvItem extends BaseItem{
    public AdvItem(){}

    public AdvItem(AdvBean advBean){
        this.advBean=advBean;
    }

    private AdvBean advBean;

    public AdvBean getAdvBean() {
        return advBean;
    }

    public void setAdvBean(AdvBean advBean) {
        this.advBean = advBean;
    }
}
