package com.xcore.data.bean;

import com.xcore.data.BaseBean;

import java.io.Serializable;

public class EggBean extends BaseBean {

    private EggDataBean data;

    public EggDataBean getData() {
        return data;
    }

    public void setData(EggDataBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "EggBean{" +
                "data=" + data +
                '}';
    }

    public static class EggDataBean implements Serializable{
        String code;
        String path;

        @Override
        public String toString() {
            return "EggDataBean{" +
                    "code='" + code + '\'' +
                    ", path='" + path + '\'' +
                    '}';
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

}
