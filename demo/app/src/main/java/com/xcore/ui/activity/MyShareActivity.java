package com.xcore.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.MyShareBean;
import com.xcore.presenter.MySharePresenter;
import com.xcore.presenter.view.MyShareView;
import com.xcore.ui.adapter.MyShareAdapter;
import com.xcore.utils.RefreshUtil;

import java.util.ArrayList;

public class MyShareActivity extends MvpActivity<MyShareView,MySharePresenter> implements MyShareView {

    private RecyclerView recyclerView;
    private MyShareAdapter shareAdapter;
    private SmartRefreshLayout refreshLayout;
    private int pageIndex=1;
    private boolean isMore=true;

    LinearLayout conLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_share;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("我的推广");
        setEdit("");

        hideNullLayout();
        conLayout=findViewById(R.id.conLayout);
        conLayout.setVisibility(View.GONE);

        recyclerView=findViewById(R.id.recyclerView);
        refreshLayout=findViewById(R.id.refreshLayout);

        shareAdapter=new MyShareAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));;
        recyclerView.setAdapter(shareAdapter);

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
    }

    @Override
    protected void initData() {
        presenter.getShareList(pageIndex);
        pageIndex++;
    }

    @Override
    public MySharePresenter initPresenter() {
        return new MySharePresenter();
    }

    @Override
    public void onShareResult(MyShareBean shareBean) {
        if(refreshLayout!=null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
            RefreshUtil.refreshHeader();
        }
        if(shareBean.getList()==null){
            shareBean.setList(new ArrayList<MyShareBean.MyShareBeanItem>());
        }
        if(shareBean.getPageIndex()==1&&shareBean.getList().size()<=0){
            showNullLayout();
            conLayout.setVisibility(View.GONE);
        }else{
            hideNullLayout();
            conLayout.setVisibility(View.VISIBLE);
        }
        if(shareBean.getList().size()<=0){
            isMore=false;
            return;
        }
        if(shareBean.getPageIndex()==1){
            shareAdapter.setData(shareBean.getList());
        }else{
            shareAdapter.dataList.addAll(shareBean.getList());
            shareAdapter.notifyDataSetChanged();
        }
    }
}
