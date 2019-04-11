package com.xcore.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.base.MvpActivity;
import com.xcore.cache.CacheManager;
import com.xcore.cache.beans.CacheBean;
import com.xcore.cache.beans.CacheType;
import com.xcore.data.bean.RCBean;
import com.xcore.presenter.CollectPresenter;
import com.xcore.presenter.view.CollectView;
import com.xcore.ui.adapter.Recode1Adapter;
import com.xcore.ui.decorations.LinearDividerItemDecoration;
import com.xcore.ui.other.XRecyclerView;
import com.xcore.utils.RefreshUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CollectActivity extends MvpActivity<CollectView,CollectPresenter> implements CollectView{
    private RefreshLayout refreshLayout;
    private XRecyclerView recyclerView;
    private Recode1Adapter recodeAdapter;
    private LinearLayout bottomLayout;

    private LinearLayout cLayout;
    private boolean selected=false;
    private Button btnAll;
    private Button btnDel;

    private boolean isEdit=false;
    private int pageIndex=1;
    private boolean isMore=true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_collect;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("我的收藏");
        setEdit("编辑",new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEdit();
            }
        });
        hideNullLayout();

        cLayout=findViewById(R.id.cLayout);
        bottomLayout=findViewById(R.id.bottomLayout);
        btnAll=findViewById(R.id.btn_all);
        btnDel=findViewById(R.id.btn_del);

        refreshLayout=findViewById(R.id.refreshLayout);
        recyclerView=findViewById(R.id.recyclerView);

        bottomLayout.setVisibility(View.GONE);

        recodeAdapter=new Recode1Adapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(recodeAdapter);
        recyclerView.addItemDecoration(new LinearDividerItemDecoration(this,1,1,getResources().getColor(R.color.color_black)));

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAll();
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageIndex=1;
                isMore=true;
                recodeAdapter.dataList.clear();
                initData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if(isMore){
                    List<RCBean.RCData> cacheBeans=recodeAdapter.dataList;
                    for(RCBean.RCData item:cacheBeans){
                        item.selected=false;
                        item.showed=false;
                    }
                    initData();
                }else{
                    refreshLayout.finishLoadMore(1000);
                }
            }
        });
    }

    @Override
    public void onBack() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public CollectPresenter initPresenter() {
        return new CollectPresenter();
    }

    //全选
    private void selectAll(){
        selected=!selected;
        recodeAdapter.allSelect(selected);
    }
    //删除
    private void delete(){
        List<RCBean.RCData> cacheBeans=recodeAdapter.dataList;
        List<String> deletes=new ArrayList<>();
        List<RCBean.RCData> rcDataList=new ArrayList<>();
        for(RCBean.RCData item:cacheBeans){
            if(item.selected){
                deletes.add(item.getShortId());
            }else{
                rcDataList.add(item);
            }
        }
        setEdit();
        //重新刷新
        recodeAdapter.setData(rcDataList);
        recodeAdapter.notifyDataSetChanged();
        //删除
        presenter.deleteCollect(deletes,0);

        if(rcDataList.size()<=0){
            pageIndex=1;
            initData();
        }
    }
    private void setEdit(){
        if(recodeAdapter.dataList==null||recodeAdapter.dataList.size()<=0){
            return;
        }
        isEdit=!isEdit;
        if(isEdit){
            bottomLayout.setVisibility(View.VISIBLE);
//            editBt.setText("取消");
            setEdit("取消");
        }else{
            bottomLayout.setVisibility(View.GONE);
            setEdit("编辑");
        }
        recodeAdapter.allShow(isEdit);
    }

    //初始化
    @Override
    public void initData(){
        presenter.getCollect(pageIndex);
        pageIndex++;
    }

    @Override
    public void onCollect(RCBean rcBean) {
        if(refreshLayout!=null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
            RefreshUtil.refreshHeader();
        }
        if(isEdit){//还原为初始的状态显示
            setEdit();
        }else{
            bottomLayout.setVisibility(View.GONE);
        }
        if(rcBean==null){
            return;
        }
        List<RCBean.RCData> rcDataList= rcBean.getList();
        if(rcDataList==null){
            return;
        }
        if(rcBean.getPageIndex()==1&&rcDataList.size()<=0){
            showNullLayout();
            isMore=false;
            setEdit("编辑");
            return;
        }
        hideNullLayout();
        cLayout.setVisibility(View.VISIBLE);
        setEdit("编辑");
        recodeAdapter.dataList.addAll(rcDataList);
        recodeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRecode(RCBean rcBean,int t) {
    }
}
