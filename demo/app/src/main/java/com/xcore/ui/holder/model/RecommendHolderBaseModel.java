package com.xcore.ui.holder.model;

import com.shuyu.common.model.RecyclerBaseModel;
import com.xcore.ui.touch.IHomeClickLinstenner;

public class RecommendHolderBaseModel extends RecyclerBaseModel {
    private int id;
    private Object value;
    private Object otherValue;

    private IHomeClickLinstenner iHomeOnClick;

    public Object getOtherValue() {
        return otherValue;
    }

    public void setOtherValue(Object otherValue) {
        this.otherValue = otherValue;
    }

    public IHomeClickLinstenner getiHomeOnClick() {
        return iHomeOnClick;
    }

    public void setiHomeOnClick(IHomeClickLinstenner iHomeOnClick) {
        this.iHomeOnClick = iHomeOnClick;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
