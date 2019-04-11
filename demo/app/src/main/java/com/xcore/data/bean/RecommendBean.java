package com.xcore.data.bean;

import com.xcore.data.BaseBean;

import java.io.Serializable;
import java.util.List;

public class RecommendBean extends BaseBean{

    private RecommendItem data;

    @Override
    public String toString() {
        return "RecommendBean{" +
                "data=" + data +
                '}';
    }

    public RecommendItem getData() {
        return data;
    }

    public void setData(RecommendItem data) {
        this.data = data;
    }

    public static class RecommendItem implements Serializable{

        private List<NvStar> actorList;
        private List<ThemeRecommendBean.ThemeData> themeList;
        private List<HomeBean.HomeTypeItem> titleModels;

        private List<AdvBean> advList;

        public List<AdvBean> getAdvList() {
            return advList;
        }

        public void setAdvList(List<AdvBean> advList) {
            this.advList = advList;
        }

        public List<NvStar> getActorList() {
            return actorList;
        }

        public void setActorList(List<NvStar> actorList) {
            this.actorList = actorList;
        }

        public List<ThemeRecommendBean.ThemeData> getThemeList() {
            return themeList;
        }

        public void setThemeList(List<ThemeRecommendBean.ThemeData> themeList) {
            this.themeList = themeList;
        }

        public List<HomeBean.HomeTypeItem> getTitleModels() {
            return titleModels;
        }

        public void setTitleModels(List<HomeBean.HomeTypeItem> titleModels) {
            this.titleModels = titleModels;
        }
    }
}
