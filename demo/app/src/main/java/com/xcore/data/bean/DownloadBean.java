package com.xcore.data.bean;

import com.xcore.data.BaseBean;

import java.io.Serializable;

public class DownloadBean extends BaseBean {
    private DownloadData data;

    public DownloadData getData() {
        return data;
    }

    public void setData(DownloadData data) {
        this.data = data;
    }

    public class DownloadData implements Serializable{
        String path;
        String md5;
        long fileSize;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }
    }
}
