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
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.base.MvpActivity;
import com.xcore.cache.CacheManager;
import com.xcore.cache.DownModel;
import com.xcore.data.bean.LikeBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.presenter.FindPresenter;
import com.xcore.presenter.view.FindView;
import com.xcore.ui.adapter.FindAdapter;
import com.xcore.ui.other.XRecyclerView;
import com.xcore.ui.touch.IFindTouchListener;
import com.xcore.utils.RefreshUtil;
import com.xcore.utils.SystemUtils;

import java.util.ArrayList;

public class FindActivity extends MvpActivity<FindView,FindPresenter> implements FindView {
    String shortId="";
    int pageIndex=1;
    boolean isMore=true;

    SmartRefreshLayout refreshLayout;

    LinearLayout emptyLayout;
    LinearLayout contentLayout;

    FindAdapter findAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("发现");
        setEdit("");
        hideNullLayout();

//        emptyLayout=findViewById(R.id.empty_layout);
        contentLayout=findViewById(R.id.rlayout);
//        emptyLayout.setVisibility(View.GONE);

        refreshLayout=findViewById(R.id.refreshLayout);

        findAdapter=new FindAdapter(this,null);
        RecyclerView recyclerView=findViewById(R.id.content_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        recyclerView.setAdapter(findAdapter);

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
            if(isMore==false){
                refreshLayout.finishLoadMore(1000);
                return;
            }
            initData();
            }
        });

    }

    @Override
    protected void initData() {
        presenter.getFind(shortId,pageIndex);
        pageIndex++;
    }

    @Override
    public FindPresenter initPresenter() {
        return new FindPresenter();
    }

    @Override
    public void onResult(TypeListBean typeListBean) {
        if(refreshLayout!=null){
            refreshLayout.finishLoadMore();
            refreshLayout.finishRefresh();
            RefreshUtil.refreshHeader();
        }
        try {
            if (typeListBean.getList() == null) {
                typeListBean.setList(new ArrayList<TypeListDataBean>());
            }
            shortId = typeListBean.getShortId();
            if (typeListBean.getList().size() <= 0) {
                isMore = false;
            }
            if (typeListBean.getList().size() <= 0 && typeListBean.getPageIndex() == 1) {
                showNullLayout();
                contentLayout.setVisibility(View.GONE);
                return;
            }
            hideNullLayout();
            contentLayout.setVisibility(View.VISIBLE);
            if (typeListBean.getPageIndex() == 1) {
                findAdapter.setData(typeListBean.getList());
            } else {
                findAdapter.dataList.addAll(typeListBean.getList());
                findAdapter.notifyDataSetChanged();
            }
        }catch (Exception ex){}
    }
    TypeListDataBean typeListDataBean;
    @Override
    public void onPathResult(LikeBean likeBean) {
        if(typeListDataBean==null||
                likeBean.getData().equals("")||
                likeBean.getData().length()<=0){
            toast("下载出错,请重试!!!");
            return;
        }
        DownModel downModel=new DownModel();
        downModel.setUrl(likeBean.getData());
        downModel.setConver(typeListDataBean.getConverUrl());
        downModel.setName(typeListDataBean.getTitle());
        downModel.setShortId(typeListDataBean.getShortId());

        CacheManager.getInstance().downByUrl(downModel);
    }

//    @Override
//    public void onDownClick(TypeListDataBean dataBean) {
//        this.typeListDataBean=dataBean;
//        presenter.getPath(dataBean.getShortId());
//    }
//
//    @Override
//    public void onShareClick(TypeListDataBean dataBean) {
//        SystemUtils.copy("",this);
//    }
}
