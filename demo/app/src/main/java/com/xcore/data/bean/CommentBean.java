package com.xcore.data.bean;

import android.util.Log;

import com.xcore.data.BasePageBean;
import com.xcore.utils.DateUtils;
import com.xcore.utils.ImageUtils;
import com.xcore.utils.ViewUtils;

import java.io.Serializable;
import java.util.List;

public class CommentBean extends BasePageBean {

    private List<CommentDataBean> list;
    private CommentDataBean data;

    public CommentDataBean getData() {
        return data;
    }

    public void setData(CommentDataBean data) {
        this.data = data;
    }

    public List<CommentDataBean> getList() {
        return list;
    }

    public void setList(List<CommentDataBean> list) {
        this.list = list;
    }

    public static class CommentDataBean implements Serializable{
        private String userName;
        private String text;
        private String image;
        private int praise;
        private int dislike;
        private String shortId;
        private long createTime;

        public int isSelect=0;

        public String getDateTimer(){
            return DateUtils.getDate(DateUtils.Y_M_D_H_M_S,createTime);
        }
        public String getDateX(){
            return DateUtils.getShortTime(createTime);
        }

        //头像
        public String getImageUrl(){
            return ImageUtils.getRes(this.getImage());
        }

        public String getUserName() {
            return ViewUtils.replaceStr(userName);
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getPraise() {
            return praise;
        }

        public void setPraise(int praise) {
            this.praise = praise;
        }

        public int getDislike() {
            return dislike;
        }

        public void setDislike(int dislike) {
            this.dislike = dislike;
        }

        public String getShortId() {
            return shortId;
        }

        public void setShortId(String shortId) {
            this.shortId = shortId;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }
}
