package com.xcore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.data.bean.FeedbackBean;
import com.xcore.data.bean.FeedbackRecodeBean;
import com.xcore.data.bean.LikeBean;
import com.xcore.presenter.FeedbackPresenter;
import com.xcore.presenter.view.FeedbackView;
import com.xcore.ui.adapter.FeedbackAdapter;
import com.xcore.utils.RefreshUtil;

import java.util.ArrayList;

public class FeedbackRecodeActivity extends MvpActivity<FeedbackView,FeedbackPresenter> implements FeedbackView {
    private SmartRefreshLayout refreshLayout;
    private FeedbackAdapter feedbackAdapter;
    private int pageIndex=1;
    private boolean isMore=true;

    public static void toActivity(Context ctx){
        Intent intent2=new Intent(ctx,FeedbackRecodeActivity.class);
        ctx.startActivity(intent2);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback_recode;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("反馈记录");
        setEdit("");

        hideNullLayout();

        RecyclerView recyclerView= findViewById(R.id.recyclerView);
        refreshLayout=findViewById(R.id.refreshLayout);

        feedbackAdapter=new FeedbackAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(feedbackAdapter);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            if(isMore==false){
                refreshLayout.finishLoadMore(1000);
                return;
            }
            initData();
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            pageIndex=1;
            initData();
            }
        });
    }

    @Override
    protected void initData() {
        presenter.getFeedList(pageIndex);
        pageIndex++;
    }

    @Override
    public FeedbackPresenter initPresenter() {
        return new FeedbackPresenter();
    }

    @Override
    public void onTypeResult(FeedbackBean feedbackBean) {
    }

    @Override
    public void onAddResult(LikeBean likeBean) {
    }

    @Override
    public void onRecodeResult(FeedbackRecodeBean recodeBean) {
        if(refreshLayout!=null){
            refreshLayout.finishLoadMore();
            refreshLayout.finishRefresh();
            RefreshUtil.refreshHeader();
        }
        if(recodeBean.getList()==null){
            recodeBean.setList(new ArrayList<FeedbackRecodeBean.RecodeItem>());
        }
        if(recodeBean.getPageIndex()==1&&recodeBean.getList().size()<=0){//是第一页且没有数据
            showNullLayout();
            refreshLayout.setVisibility(View.GONE);
        }
        if(recodeBean.getList().size()<=0){
            isMore=false;
            return;
        }
        hideNullLayout();
        refreshLayout.setVisibility(View.VISIBLE);
        if(recodeBean.getPageIndex()==1){
            feedbackAdapter.setData(recodeBean.getList());
        }else{
            feedbackAdapter.dataList.addAll(recodeBean.getList());
            feedbackAdapter.notifyDataSetChanged();;
        }
    }
}
