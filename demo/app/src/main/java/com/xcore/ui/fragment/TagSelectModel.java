package com.xcore.ui.fragment;

import com.xcore.data.bean.Tag;
import java.io.Serializable;
import java.util.List;

public class TagSelectModel implements Serializable {
    private String shortId;
    private String name;
    private boolean selected=false;

    private List<Tag> list;

    public List<Tag> getList() {
        return list;
    }

    public void setList(List<Tag> list) {
        this.list = list;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
