package com.xcore.data.bean;

import com.common.BaseCommon;
import com.xcore.data.utils.DataUtils;
import com.xcore.utils.DateUtils;
import com.xcore.utils.ImageUtils;
import com.xcore.utils.NumberUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MovieBean implements Serializable{
    String title;
    String actor;
    int duration;
    String releasetime;
    String cover;
    String shortId;
    String desc;
    String year;
    String sourceID;

    public int praise;//喜欢
    public int dislike;//不喜欢
    public double avgRating;//评分
    public int playCount;//播放次数

    public List<NvStar> actorList;
    public String seriesName;
    public String seriesShortId;
    public String categoriesName;
    public String categoriesShortId;

    public List<CategoriesBean> tagsList;
    public List<RelatedBean> related;//相关

    private List<CategoriesBean> quality;//调高清选项
    private Integer filmReviewCount;

    private String movieShareTextV1;
    private String shareUrl;

    //新版本参数
    List<LineModel> lineList;

    AdvBean detailAdv;
    AdvBean playerAdv;

    public AdvBean getPlayerAdv() {
        return playerAdv;
    }

    public void setPlayerAdv(AdvBean playerAdv) {
        this.playerAdv = playerAdv;
    }

    public AdvBean getDetailAdv() {
        return detailAdv;
    }

    public void setDetailAdv(AdvBean detailAdv) {
        this.detailAdv = detailAdv;
    }

    public String getMovieShareTextV1() {
        return movieShareTextV1;
    }

    public void setMovieShareTextV1(String movieShareTextV1) {
        this.movieShareTextV1 = movieShareTextV1;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public List<LineModel> getLineList() {
        return lineList;
    }

    public void setLineList(List<LineModel> lineList) {
        this.lineList = lineList;
    }

    //得到评论次数
    public String getFilmReviewCountStr(){
        if(filmReviewCount==null){
            return 0+"";
        }
        if(filmReviewCount>=10000){
            double v1=filmReviewCount*1.0/10000;
            return String.format("%.1f", v1)+"";
        }
        return filmReviewCount+"";
    }

    public Integer getFilmReviewCount() {
        return filmReviewCount==null?0:filmReviewCount;
    }

    public void setFilmReviewCount(Integer filmReviewCount) {
        this.filmReviewCount = filmReviewCount;
    }

    public List<CategoriesBean> getQuality() {
        return quality;
    }
    public void setQuality(List<CategoriesBean> quality) {
        this.quality = quality;
    }

    boolean enable;//是否可播放
    public boolean isCollect=false;

    public int praiseState;//
    public String movieShareText;

    public String getSourceID() {
        return sourceID;
    }

    public void setSourceID(String sourceID) {
        this.sourceID = sourceID;
    }

    //得到封面信息
    public String getConverUrl(){
        return ImageUtils.getRes(this.getCover());
    }

    public String getTime(){
        return DateUtils.getM_S(this.getDuration());
    }
    /**
     * 根据时间戳得到日期
     * @return
     */
    public String getDate(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String sd = sdf.format(new Date(Long.parseLong(getReleasetime())));      // 时间戳转换成时间
        return sd;
    }
    public String getUrl(String url){
//        return BaseCommon.RES_URL+url;
        return url;
    }

    /**
     * 得到播放次数
     * @param
     * @return
     */
    public String getPlayCountData(){
        if(playCount>=10000){
            if(playCount%10000>0) {
                double v= (playCount *1.0/ 10000);
                String value=String.format("%.2f", v);
                return value+"万次播放";
            }else{
                return (playCount / 10000) + "万次播放";
            }
        }
        return playCount+"次播放";
    }

    @Override
    public String toString() {
        return "MovieBean{" +
                "title='" + title + '\'' +
                ", actor='" + actor + '\'' +
                ", duration=" + duration +
                ", releasetime='" + releasetime + '\'' +
                ", cover='" + cover + '\'' +
                ", quality='" + quality + '\'' +
                ", desc='" + desc + '\'' +
                ", year='" + year + '\'' +
                ", praise=" + praise +
                ", dislike=" + dislike +
                ", avgRating=" + avgRating +
                ", playCount=" + playCount +
                ", actorList=" + actorList +
                ", seriesName='" + seriesName + '\'' +
                ", seriesShortId='" + seriesShortId + '\'' +
                ", categoriesName='" + categoriesName + '\'' +
                ", categoriesShortId='" + categoriesShortId + '\'' +
                ", tagsList=" + tagsList +
                ", related=" + related +
                ", enable=" + enable +
                ", isCollect=" + isCollect +
                '}';
    }

    public String getMovieShareText() {
        return movieShareText;
    }

    public void setMovieShareText(String movieShareText) {
        this.movieShareText = movieShareText;
    }

    public int getPraiseState() {
        return praiseState;
    }

    public void setPraiseState(int praiseState) {
        this.praiseState = praiseState;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getReleasetime() {
        return releasetime;
    }

    public void setReleasetime(String releasetime) {
        this.releasetime = releasetime;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public String getDesc() {
        if(sourceID==null||sourceID.length()<=0){
            sourceID="";
        }
        return desc+"_"+sourceID;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public List<NvStar> getActorList() {
        return actorList==null? new ArrayList<NvStar>():actorList;
    }

    public void setActorList(List<NvStar> actorList) {
        this.actorList = actorList;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getSeriesShortId() {
        return seriesShortId;
    }

    public void setSeriesShortId(String seriesShortId) {
        this.seriesShortId = seriesShortId;
    }

    public String getCategoriesName() {
        return categoriesName;
    }

    public void setCategoriesName(String categoriesName) {
        this.categoriesName = categoriesName;
    }

    public String getCategoriesShortId() {
        return categoriesShortId;
    }

    public void setCategoriesShortId(String categoriesShortId) {
        this.categoriesShortId = categoriesShortId;
    }

    public List<CategoriesBean> getTagsList() {
        return tagsList;
    }

    public void setTagsList(List<CategoriesBean> tagsList) {
        this.tagsList = tagsList;
    }

    public List<RelatedBean> getRelated() {
        return related;
    }

    public void setRelated(List<RelatedBean> related) {
        this.related = related;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }
}

