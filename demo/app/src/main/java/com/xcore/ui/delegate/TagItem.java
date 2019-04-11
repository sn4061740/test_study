package com.xcore.ui.delegate;

import com.xcore.data.bean.Tag;

import java.util.ArrayList;
import java.util.List;

public class TagItem extends BaseItem {
    List<Tag> tags=new ArrayList<>();

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
