package com.xcore.ui.holder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.shuyu.common.model.RecyclerBaseModel;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.ThemeRecommendBean;
import com.xcore.ui.activity.ThemeActivity;
import com.xcore.ui.adapter.RecommendThemeAdapter;
import com.xcore.ui.holder.model.BaseHolder;
import com.xcore.ui.holder.model.RecommendHolderBaseModel;
import com.xcore.ui.other.XRecyclerView;

import java.util.List;

public class RecommendThemeHolder extends BaseHolder {
    public static int ID= R.layout.holder_commend_theme;

    public RecommendThemeHolder(Context context, View v) {
        super(context, v);
    }

    private XRecyclerView xRecyclerView;
    private RecommendThemeAdapter themeAdapter;

    @Override
    public void createView(View v) {
        xRecyclerView=v.findViewById(R.id.xRecyclerView);
    }

    @Override
    public void onBind(RecyclerBaseModel model, int position) {
        if(model==null){
            return;
        }
        try {
            RecommendHolderBaseModel mod = (RecommendHolderBaseModel) model;
            if (mod == null) {
                return;
            }
            List<ThemeRecommendBean.ThemeData> dataList = (List<ThemeRecommendBean.ThemeData>) mod.getValue();
            if (dataList == null || dataList.size() <= 0) {
                return;
            }
            themeAdapter = new RecommendThemeAdapter(context);
            xRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            xRecyclerView.setAdapter(themeAdapter);

//            themeAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<ThemeRecommendBean.ThemeData>() {
//                @Override
//                public void onItemClick(ThemeRecommendBean.ThemeData item, int position) {//
//
//                }
//            });
            themeAdapter.setData(dataList);
        }catch (Exception ex){}
    }
}
