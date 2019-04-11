package com.xcore.presenter.view;

import com.xcore.base.BaseView;
import com.xcore.cache.beans.CacheBean;
import com.xcore.data.bean.DetailBean;
import com.xcore.data.bean.DownloadBean;
import com.xcore.data.bean.LikeBean;
import com.xcore.data.bean.PathBean;
import com.xcore.data.bean.QualtyBean;

public interface DetailView extends BaseView {
    void onDetailResult(DetailBean detailBean);
    void onGetPathResult(PathBean pathBean);
    //更新收藏
    void onUpdateCollect();
    void onCacheResult(LikeBean likeBean);
    void onLikeResult(LikeBean likeBean,int type);
//    void onDownResult();
    void onError(String msg);
    void onDetailError();//获取电影信息错误

    void onGetQualityResult(QualtyBean qualtyBean);
    void onCacheResultV1(DownloadBean downloadBean);
}
