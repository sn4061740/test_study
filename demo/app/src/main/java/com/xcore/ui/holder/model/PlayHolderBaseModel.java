package com.xcore.ui.holder.model;

import com.shuyu.common.model.RecyclerBaseModel;
import com.xcore.R;
import com.xcore.cache.CacheManager;
import com.xcore.cache.beans.CacheBean;
import com.xcore.cache.beans.CacheType;
import com.xcore.ui.touch.IPlayClickListenner;

public class PlayHolderBaseModel extends RecyclerBaseModel {
    private int id;
    private Object value;
    private IPlayClickListenner iPlayClickListenner;
    public boolean isShare=false;

    public IPlayClickListenner getiPlayClickListenner() {
        return iPlayClickListenner;
    }

    public void setiPlayClickListenner(IPlayClickListenner iPlayClickListenner) {
        this.iPlayClickListenner = iPlayClickListenner;
    }

    /**
     * 是否有收藏
     * @param shortId
     * @return
     */
    public boolean getCollected(String shortId) {
        boolean collected=false;
        try {
            CacheBean collect1 = new CacheBean();
            collect1.settType(CacheType.CACHE_COLLECT);
            collect1.setShortId(shortId);

            //查找是否有收藏
            CacheBean cacheBean = CacheManager.getInstance().getDbHandler().query(collect1);
            if (cacheBean != null) {
                if (cacheBean.gettDelete().equals("1")) {
                    collected = true;
                }
            }
            return collected;
        }catch (Exception ex){}
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
