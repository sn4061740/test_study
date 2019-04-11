package com.xcore.down;

import com.jay.config.Md5Utils;
import java.io.Serializable;

//保存数据库的模型
public class TaskModel implements Serializable {
    public String id;
    public String taskId="";//任务Id
    public String url;//下载url
    public String percent="0";//当前进度
    public String conver;//封面
    public String title;//标题
    public String vKey;//文件验证码  暂时无用。。。。//
    public String shortId;//id
    public String fileSize;//文件总大小

    public String getvKey() {
        return vKey;
    }

    public void setvKey(String vKey) {
        this.vKey = vKey;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTaskIdByUrl(){
        return Md5Utils.MD5Encode(this.getUrl());
    }
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getConver() {
        return conver;
    }

    public void setConver(String conver) {
        this.conver = conver;
    }

    public String getFileSize() {
        return fileSize;
    }
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}
