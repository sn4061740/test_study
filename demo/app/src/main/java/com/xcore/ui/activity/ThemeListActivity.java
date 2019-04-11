package com.xcore.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.ThemeCoverBean;
import com.xcore.data.bean.ThemeRecommendBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.presenter.ThemePresenter;
import com.xcore.presenter.view.ThemeView;
import com.xcore.ui.adapter.RecommendThemeAdapter;
import com.xcore.utils.RefreshUtil;

import java.util.ArrayList;

public class ThemeListActivity extends MvpActivity<ThemeView,ThemePresenter> implements ThemeView {

    private int pageIndex=1;
    private boolean isMore=true;
    private RefreshLayout refreshLayout;
    private LinearLayout themeLayout;
    private RecommendThemeAdapter themeAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_theme_list;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("专题列表");
        setEdit("");
        hideNullLayout();
        themeLayout=findViewById(R.id.themeLayout);
        refreshLayout=findViewById(R.id.refreshLayout);

        RecyclerView recyclerView= findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        themeAdapter=new RecommendThemeAdapter(this);
        recyclerView.setAdapter(themeAdapter);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            pageIndex=1;
            isMore=true;
            initData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            if(!isMore){
                refreshLayout.finishLoadMore(1000);
                return;
            }
            initData();
            }
        });
        themeAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<ThemeRecommendBean.ThemeData>() {
            @Override
            public void onItemClick(ThemeRecommendBean.ThemeData item, int position) {
                Intent intent=new Intent(ThemeListActivity.this,ThemeActivity.class);
                intent.putExtra("shortId",item.getShortId());
                intent.putExtra("name",item.getName());
                intent.putExtra("conver",item.getConverUrl());
                intent.putExtra("desc",item.getRemarks());
                intent.putExtra("maxConver",item.getMaxConverUrl());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {
        try {
            presenter.getThemes(pageIndex);
            pageIndex++;
        }catch (Exception ex){}
    }

    @Override
    public ThemePresenter initPresenter() {
        return new ThemePresenter();
    }

    @Override
    public void onResoult(TypeListBean typeListBean) {
    }

    @Override
    public void onRecommendTheme(ThemeRecommendBean recommendBean) {
        if(refreshLayout!=null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
            RefreshUtil.refreshHeader();
        }
        if(recommendBean==null){
            showNullLayout();
            themeLayout.setVisibility(View.GONE);
            return;
        }
        if(recommendBean!=null&&recommendBean.getList()==null){
            recommendBean.setList(new ArrayList<ThemeRecommendBean.ThemeData>());
        }
        if(recommendBean.getList().size()<=0&&recommendBean.getPageIndex()==1){
            showNullLayout();
            themeLayout.setVisibility(View.GONE);
        }
        if(recommendBean.getList().size()<=0){
            isMore=false;
            return;
        }
        hideNullLayout();
        themeLayout.setVisibility(View.VISIBLE);
        if(recommendBean.getPageIndex()==1){
            themeAdapter.setData(recommendBean.getList());
        }else{
            themeAdapter.dataList.addAll(recommendBean.getList());
            themeAdapter.notifyDataSetChanged();
        }
        RefreshUtil.refreshHeader();
    }

    @Override
    public void onRecommendCoverResult(ThemeCoverBean themeCoverBean) {

    }
}
