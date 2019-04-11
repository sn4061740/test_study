package com.xcore.cache.beans;

import java.io.Serializable;

public class XCommentBean implements Serializable {
    public String id;//唯一ID
    public String shortId;//评论ID
    public String cDelete="0";//是否点赞
    public String cTime;//更新时间
}
