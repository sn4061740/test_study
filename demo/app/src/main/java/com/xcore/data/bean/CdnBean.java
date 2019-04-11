package com.xcore.data.bean;

import android.support.v7.widget.LinearLayoutManager;

import com.common.BaseCommon;
import com.xcore.data.BaseBean;

import java.io.Serializable;
import java.util.List;

public class CdnBean extends BaseBean {

    public CdnItem data;

    @Override
    public String toString() {
        return "CdnBean{" +
                "data=" + data +
                '}';
    }

    public CdnItem getData() {
        return data;
    }

    public void setData(CdnItem data) {
        this.data = data;
    }

    public static class CdnItem implements Serializable {

        List<CdnDataItem> apiList;
        List<CdnDataItem> imageList;
        List<CdnDataItem> recordList;
        List<CdnDataItem> torrentList;
        List<CdnDataItem> httpAccelerateList;
        List<CdnDataItem> apkDownList;
        List<CdnDataItem> testUrlList;

        List<CdnDataItem> downSpeedTestList;
        List<CdnDataItem> ccList;
        List<CdnDataItem> trackerList;
        List<CdnDataItem> m3U8List;//m3u8
        List<AdvBean> advList;

        int enableHTTP;
        int defaultLine;
        boolean socketTestEnable;
        boolean playSpeedLogEnable;
        boolean cacheLogEnable;
        boolean apiErrorLogEnable;
        boolean imageErrorLogEnable;

        boolean testClientRunEnable;//测试版本是否可用
        boolean torrentMD5CheckEnable;//是否验证md5
        boolean umengErrorEnable;//是否上传自定义错误到umeng

        boolean connectionCloseBoo;//是否设置 connection =  close 请求头
        boolean imageWebPEnable;

        public boolean isImageWebPEnable() {
            return imageWebPEnable;
        }
        public void setImageWebPEnable(boolean imageWebPEnable) {
            this.imageWebPEnable = imageWebPEnable;
        }

        public boolean isConnectionCloseBoo() {
            return connectionCloseBoo;
        }

        public void setConnectionCloseBoo(boolean connectionCloseBoo) {
            this.connectionCloseBoo = connectionCloseBoo;
        }

        public boolean isUmengErrorEnable() {
            return umengErrorEnable;
        }

        public void setUmengErrorEnable(boolean umengErrorEnable) {
            this.umengErrorEnable = umengErrorEnable;
        }

        public boolean isTestClientRunEnable() {
            return testClientRunEnable;
        }

        public boolean isTorrentMD5CheckEnable() {
            return torrentMD5CheckEnable;
        }

        public void setTorrentMD5CheckEnable(boolean torrentMD5CheckEnable) {
            this.torrentMD5CheckEnable = torrentMD5CheckEnable;
        }

        public List<AdvBean> getAdvList() {
            return advList;
        }

        public void setAdvList(List<AdvBean> advList) {
            this.advList = advList;
        }

        public List<CdnDataItem> getM3U8List() {
            return m3U8List;
        }

        public void setM3U8List(List<CdnDataItem> m3U8List) {
            this.m3U8List = m3U8List;
        }

        public List<CdnDataItem> getTrackerList() {
            return trackerList;
        }

        public void setTrackerList(List<CdnDataItem> trackerList) {
            this.trackerList = trackerList;
        }

        public List<CdnDataItem> getCcList() {
            return ccList;
        }

        public void setCcList(List<CdnDataItem> ccList) {
            this.ccList = ccList;
        }

        public List<CdnDataItem> getDownSpeedTestList() {
            return downSpeedTestList;
        }

        public void setDownSpeedTestList(List<CdnDataItem> downSpeedTestList) {
            this.downSpeedTestList = downSpeedTestList;
        }

        public void setTestClientRunEnable(boolean testClientRunEnable) {
            this.testClientRunEnable = testClientRunEnable;
        }

        public boolean isSocketTestEnable() {
            return socketTestEnable;
        }

        public void setSocketTestEnable(boolean socketTestEnable) {
            this.socketTestEnable = socketTestEnable;
        }

        public boolean isPlaySpeedLogEnable() {
            return playSpeedLogEnable;
        }

        public void setPlaySpeedLogEnable(boolean playSpeedLogEnable) {
            this.playSpeedLogEnable = playSpeedLogEnable;
        }

        public boolean isCacheLogEnable() {
            return cacheLogEnable;
        }

        public void setCacheLogEnable(boolean cacheLogEnable) {
            this.cacheLogEnable = cacheLogEnable;
        }

        public boolean isApiErrorLogEnable() {
            return apiErrorLogEnable;
        }

        public void setApiErrorLogEnable(boolean apiErrorLogEnable) {
            this.apiErrorLogEnable = apiErrorLogEnable;
        }

        public boolean isImageErrorLogEnable() {
            return imageErrorLogEnable;
        }

        public void setImageErrorLogEnable(boolean imageErrorLogEnable) {
            this.imageErrorLogEnable = imageErrorLogEnable;
        }

        public int getEnableHTTP() {
            return enableHTTP;
        }

        public void setEnableHTTP(int enableHTTP) {
            this.enableHTTP = enableHTTP;
        }

        public int getDefaultLine() {
            return defaultLine;
        }

        public void setDefaultLine(int defaultLine) {
            this.defaultLine = defaultLine;
        }

        @Override
        public String toString() {
            return "CdnItem{" +
                    "apiList=" + apiList +
                    ", imageList=" + imageList +
                    ", recordList=" + recordList +
                    ", torrentList=" + torrentList +
                    ", httpAccelerateList=" + httpAccelerateList +
                    ", apkDownList=" + apkDownList +
                    '}';
        }

        public List<CdnDataItem> getTestUrlList() {
            return testUrlList;
        }

        public void setTestUrlList(List<CdnDataItem> testUrlList) {
            this.testUrlList = testUrlList;
        }

        public List<CdnDataItem> getApkDownList() {
            return apkDownList;
        }

        public void setApkDownList(List<CdnDataItem> apkDownList) {
            this.apkDownList = apkDownList;
        }

        public List<CdnDataItem> getApiList() {
            return apiList;
        }

        public void setApiList(List<CdnDataItem> apiList) {
            this.apiList = apiList;
        }

        public List<CdnDataItem> getImageList() {
            return imageList;
        }

        public void setImageList(List<CdnDataItem> imageList) {
            this.imageList = imageList;
        }

        public List<CdnDataItem> getRecordList() {
            return recordList;
        }

        public void setRecordList(List<CdnDataItem> recordList) {
            this.recordList = recordList;
        }

        public List<CdnDataItem> getTorrentList() {
            return torrentList;
        }

        public void setTorrentList(List<CdnDataItem> torrentList) {
            this.torrentList = torrentList;
        }

        public List<CdnDataItem> getHttpAccelerateList() {
            return httpAccelerateList;
        }

        public void setHttpAccelerateList(List<CdnDataItem> httpAccelerateList) {
            this.httpAccelerateList = httpAccelerateList;
        }
    }
    public static class CdnDataItem implements Serializable{
        String url;
        int cdnConfigType;
        Integer port;
        String md5;

        @Override
        public String toString() {
            return "CdnDataItem{" +
                    "url='" + url + '\'' +
                    ", cdnConfigType=" + cdnConfigType +
                    '}';
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getCdnConfigType() {
            return cdnConfigType;
        }

        public void setCdnConfigType(int cdnConfigType) {
            this.cdnConfigType = cdnConfigType;
        }
    }
}
