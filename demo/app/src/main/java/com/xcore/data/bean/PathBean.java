package com.xcore.data.bean;

import com.xcore.data.BaseBean;

import java.io.Serializable;
import java.util.List;

public class PathBean extends BaseBean{
    private List<PathDataBean> data;

    public List<PathDataBean> getData() {
        return data;
    }

    public void setData(List<PathDataBean> data) {
        this.data = data;
    }
}
