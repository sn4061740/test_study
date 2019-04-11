package com.xcore.data.bean;

import com.xcore.data.BaseBean;

import java.io.Serializable;
import java.util.List;

public class QualtyBean extends BaseBean {

    private List<QualtyData> data;

    public List<QualtyData> getData() {
        return data;
    }

    public void setData(List<QualtyData> data) {
        this.data = data;
    }

    public class QualtyData implements Serializable {
        private int quality;
        private String qualityName;

        public int getQuality() {
            return quality;
        }

        public void setQuality(int quality) {
            this.quality = quality;
        }

        public String getQualityName() {
            return qualityName;
        }

        public void setQualityName(String qualityName) {
            this.qualityName = qualityName;
        }
    }
}
