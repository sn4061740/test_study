package com.xcore.data.bean;

import com.xcore.cache.beans.CacheType;
import com.xcore.data.BasePageBean;
import com.xcore.utils.ImageUtils;

import java.io.Serializable;
import java.util.List;

//记录+收藏
public class RCBean extends BasePageBean {

    private List<RCData> list;

    @Override
    public String toString() {
        return "RCBean{" +
                "list=" + list +
                '}';
    }

    public List<RCData> getList() {
        return list;
    }

    public void setList(List<RCData> list) {
        this.list = list;
    }

    public static class RCData implements Serializable {
        String shortId;
        String title;
        String cover;
        List<Tag> tagsList;
        long position=0;

        String tType;

        public boolean selected;
        public boolean showed;


        @Override
        public String toString() {
            return "RCData{" +
                    "shortId='" + shortId + '\'' +
                    ", title='" + title + '\'' +
                    ", cover='" + cover + '\'' +
                    ", tagsList=" + tagsList +
                    '}';
        }
        //得到封面路径
        public String getConverUrl(){
            //return BaseCommon.RES_URL+this.cover;
            return ImageUtils.getRes(cover);
        }

        public String gettType() {
            return tType;
        }

        public void settType(String tType) {
            this.tType = tType;
        }

        public long getPosition() {
            return position;
        }

        public void setPosition(long position) {
            this.position = position;
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

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public List<Tag> getTagsList() {
            return tagsList;
        }

        public void setTagsList(List<Tag> tagsList) {
            this.tagsList = tagsList;
        }
    }
}
