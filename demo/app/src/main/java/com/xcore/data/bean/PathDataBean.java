package com.xcore.data.bean;

import java.io.Serializable;
import java.util.List;

public class PathDataBean implements Serializable{
    String path;
    String md5;
    String line;
    Integer quality;
    String qualityName;
    long fileSize;

    int eggTime;

    public int getEggTime() {
        return eggTime;
    }

    public void setEggTime(int eggTime) {
        this.eggTime = eggTime;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getQualityName() {
        return qualityName;
    }

    public void setQualityName(String qualityName) {
        this.qualityName = qualityName;
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
