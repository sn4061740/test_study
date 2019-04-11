package com.xcore.down;

import com.jay.config.Md5Utils;
import com.jay.config.Status;

public class CacheModel {
    private String url;
    private String title;
    private String conver;
    private int prent;
    private String taskId;
    private String shortId;

    private long curSize;
    private long totalSize;
    private Status status;
    private String v1;

    public String getV1() {
        return v1;
    }

    public void setV1(String v1) {
        this.v1 = v1;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getCurSize() {
        return curSize;
    }

    public void setCurSize(long curSize) {
        this.curSize = curSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    private boolean selected=false;

    public String getTaskId(){
        if(taskId==null||taskId.length()<=0) {
            return Md5Utils.MD5Encode(getUrl());
        }
        return taskId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConver() {
        return conver;
    }

    public void setConver(String conver) {
        this.conver = conver;
    }

    public int getPrent() {
        return prent;
    }

    public void setPrent(int prent) {
        this.prent = prent;
    }
}
