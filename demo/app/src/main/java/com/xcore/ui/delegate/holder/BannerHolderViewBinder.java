package com.xcore.ui.delegate.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;

import com.stx.xhb.xbanner.XBanner;
import com.stx.xhb.xbanner.entity.SimpleBannerInfo;
import com.xcore.R;
import com.xcore.commonAdapter.ItemViewBinder;
import com.xcore.data.bean.BannerBean;
import com.xcore.data.bean.HomeBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.ui.delegate.BannerItem;
import com.xcore.utils.JumpUtils;

import java.util.ArrayList;
import java.util.List;

public class BannerHolderViewBinder extends ItemViewBinder<BannerItem,BannerHolderViewBinder.BannerHolder> {
    Context mContext;
    public BannerHolderViewBinder(Context ctx){
        this.mContext=ctx;
    }

    View root;
    boolean isShow=false;
    BannerHolder holder;

    @NonNull
    @Override
    protected BannerHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        root = inflater.inflate(R.layout.holder_banner, parent, false);
        isShow=false;
        holder=new BannerHolder(root);
        return holder;
    }
    //暂停
    public void stopMz(){
        if(holder!=null&&holder.banner!=null) {
            holder.banner.stopAutoPlay();
        }
    }
    //开始
    public void startMz(){
        if(holder!=null&&holder.banner!=null) {
            holder.banner.startAutoPlay();
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull final BannerHolder holder, @NonNull BannerItem item) {
        final List<BannerBean.BannerData> bannerDataList = item.bannerDatas;
        isShow=true;

        //刷新数据之后，需要重新设置是否支持自动轮播
        holder.banner.setAutoPlayAble(bannerDataList.size() > 1);
        holder.banner.setIsClipChildrenMode(true);

        List<SimpleBannerInfo> bannerInfos=new ArrayList<>();
        for(int i=0;i<bannerDataList.size();i++){
            SimpleBannerInfo info=new NetBannerImage(bannerDataList.get(i).getPathUrl());
            bannerInfos.add(info);
        }

        //设置广告图片点击事件
        holder.banner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, Object model, View view, int position) {
                BannerBean.BannerData bannerData = bannerDataList.get(position);
                HomeBean.HomeTypeItem homeTypeItem = new HomeBean.HomeTypeItem();
                homeTypeItem.setJump(bannerData.getJump());
                homeTypeItem.setShortId(bannerData.getShortId());
                JumpUtils.to(mContext, bannerDataList.get(position));
            }
        });

        //加载广告图片
        holder.banner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                ImageViewExt img= (ImageViewExt) view;
                img.loadRadius(((NetBannerImage)model).getXBannerUrl().toString());
            }
        });

        holder.banner.setAutoPlayAble(bannerInfos.size() > 1);
        holder.banner.setIsClipChildrenMode(true);
        holder.banner.setBannerData(R.layout.banner_layout, bannerInfos);

    }

    class NetBannerImage extends SimpleBannerInfo{
        private String url;
        public NetBannerImage(String url){
            this.url=url;
        }

        @Override
        public Object getXBannerUrl() {
            return url;
        }
    }

    class BannerHolder extends RecyclerView.ViewHolder{
        XBanner banner;

        public BannerHolder(View itemView) {
            super(itemView);
            banner=itemView.findViewById(R.id.banner);
        }

    }

}
