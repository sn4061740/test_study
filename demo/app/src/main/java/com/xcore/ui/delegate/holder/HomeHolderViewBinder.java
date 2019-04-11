package com.xcore.ui.delegate.holder;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.commonAdapter.ItemViewBinder;
import com.xcore.data.bean.HomeBean;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.ui.adapter.HomeItemAdapter;
import com.xcore.ui.delegate.HomeItem;
import com.xcore.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeHolderViewBinder extends ItemViewBinder<HomeItem,HomeHolderViewBinder.HomeHolder> {
    Context mContext;

    public HomeHolderViewBinder(Context ctx){
        this.mContext=ctx;
    }

    @Override
    protected void onViewRecycled(@NonNull HomeHolder holder) {
        super.onViewRecycled(holder);

    }

    @NonNull
    @Override
    protected HomeHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.holder_home_item, parent, false);
        return new HomeHolder(root);
    }
    HomeItemAdapter homeItemAdapter;

    @Override
    protected void onBindViewHolder(@NonNull final HomeHolder holder, @NonNull final HomeItem item) {
        try {
            final HomeBean.HomeDataItem dataItem= item.getHomeDataItem();
            holder.title.setText(dataItem.getName());
            List<TypeListDataBean> listDataBeans = dataItem.getList();
            List<TypeListDataBean> homeDataItems = new ArrayList<>();
            if (listDataBeans == null || listDataBeans.size() <= 0) {
                return;
            }
            //不是最近更新
            if (listDataBeans != null && dataItem.getType() != 4 && listDataBeans.size() > 0) {
                final TypeListDataBean typeListDataBean = listDataBeans.get(0);
                holder.conver.loadUrl(typeListDataBean.getConverUrl());
                holder.txtTitle.setText(typeListDataBean.getTitle());
                holder.xLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < listDataBeans.size(); i++) {
                    if (i == 0) {
                        continue;
                    }
                    homeDataItems.add(listDataBeans.get(i));
                }
                holder.conver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtils.toPlayer((Activity) mContext, holder.conver, typeListDataBean.getShortId(), null, null, null);
                    }
                });
            } else {
                holder.xLayout.setVisibility(View.GONE);
                homeDataItems = listDataBeans;
            }

            homeItemAdapter = new HomeItemAdapter(mContext);
            holder.recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
            holder.recyclerView.setAdapter(homeItemAdapter);
            homeItemAdapter.setData(homeDataItems);
            holder.btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if(item!=null&&item.getHomeOnClick()!=null){
                    item.getHomeOnClick().onClickMore(dataItem);
                }
                }
            });
            holder.btnChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if(item!=null&&item.getHomeOnClick()!=null){
                    item.getHomeOnClick().onClickChange(dataItem);
                }
                }
            });
        }catch (Exception ex){}
    }

    class HomeHolder extends RecyclerView.ViewHolder{

        ImageViewExt conver;
        TextView title;
        RecyclerView recyclerView;
        TextView txtTitle;
        LinearLayout btnMore;
        LinearLayout btnChange;
        LinearLayout xLayout;


        public HomeHolder(View v) {
            super(v);
            conver=v.findViewById(R.id.conver);
            title=v.findViewById(R.id.title);
            recyclerView=v.findViewById(R.id.recyclerView);
            txtTitle=v.findViewById(R.id.txtTitle);
            btnMore=v.findViewById(R.id.btn_more);
            btnChange=v.findViewById(R.id.btn_change);
            xLayout=v.findViewById(R.id.xLayout);
        }
    }

}
