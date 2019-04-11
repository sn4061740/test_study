package com.xcore.ui.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.BaseFragment;
import com.xcore.data.bean.RCBean;
import com.xcore.ui.activity.RecodeActivity;
import com.xcore.ui.adapter.Recode1Adapter;
import com.xcore.ui.other.XRecyclerView;
import com.xcore.utils.RefreshUtil;

import java.util.ArrayList;
import java.util.List;

public class RecodeFragment extends BaseFragment {

    private RecodeActivity.IRecodeListener iRecodeListener;

    public void setiRecodeListener(RecodeActivity.IRecodeListener iRecodeListener) {
        this.iRecodeListener = iRecodeListener;
    }

    private SmartRefreshLayout refreshLayout;
    private XRecyclerView recyclerView;
    private Recode1Adapter recodeAdapter;
    private LinearLayout bottomLayout;

    private boolean selected=false;
    private Button btnAll;
    private Button btnDel;
    private boolean isMore=true;

    int type=1;

    public void setType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }


    private List<RCBean.RCData> cacheBeans=new ArrayList<>();
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recode;
    }

    @Override
    protected void initView(View view) {
        hideNullLayout();

        recyclerView=view.findViewById(R.id.recyclerView);
        refreshLayout=view.findViewById(R.id.refreshLayout);
        bottomLayout=view.findViewById(R.id.bottomLayout);
        btnAll=view.findViewById(R.id.btn_all);
        btnDel=view.findViewById(R.id.btn_del);

        bottomLayout.setVisibility(View.GONE);

        recodeAdapter=new Recode1Adapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(recodeAdapter);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                isMore=true;
                isLoad=false;
                if(iRecodeListener!=null){
                    cacheBeans.clear();
                    iRecodeListener.onRefresh(getType(),true);
                }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if(isMore) {
                    if(iRecodeListener!=null){
                        iRecodeListener.onRefresh(getType(),false);
                    }
                }else{
                    refreshLayout.finishLoadMore(1000);
                }
            }
        });
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allSelect();
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        if(cacheBeans.size()>0||isMore==false){
            updateView();
        }else{
            if(iRecodeListener!=null){
                iRecodeListener.onRefresh(getType(),true);
            }
        }
    }

    public boolean isLoad() {
        return cacheBeans.size()>0;
    }

    //初始化数据
    @Override
    public void initData(){
    }
    //点击编辑
    public void onEdit(boolean boo){
        try {
            if(cacheBeans.size()<=0){
                if(iRecodeListener!=null){
                    iRecodeListener.onItemClick();
                }
                return;
            }
            if (boo) {
                bottomLayout.setVisibility(View.VISIBLE);
            } else {
                bottomLayout.setVisibility(View.GONE);
            }
            recodeAdapter.allShow(boo);
        }catch (Exception ex){}
    }
    //全选
    public void allSelect(){
        selected=!selected;
        recodeAdapter.allSelect(selected);
    }
    //删除选中
    public void delete(){
        try {
            List<RCBean.RCData> cacheBeans = recodeAdapter.dataList;
            List<RCBean.RCData> results = new ArrayList<>();
            List<String> deletes=new ArrayList<>();
            for (RCBean.RCData item : cacheBeans) {
                if (item.selected) {
                    deletes.add(item.getShortId());
                } else {
                    results.add(item);
                }
            }
            onEdit(false);
            this.cacheBeans=results;
            recodeAdapter.setData(results);
            recodeAdapter.notifyDataSetChanged();
            if(iRecodeListener!=null){
                iRecodeListener.onDelete(deletes);
            }

            if(results.size()<=0){
                if(iRecodeListener!=null){
                    iRecodeListener.onRefresh(getType(),true);
                }
            }
        }catch (Exception ex){}
    }

    public void update(RCBean rcBean){
        try {
            onEdit(false);
            if (refreshLayout != null) {
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                RefreshUtil.refreshHeader();
            }
            List<RCBean.RCData> rcDataList= rcBean.getList();
            //是第一页且没有数据
            if(rcBean.getPageIndex()==1&&rcDataList!=null&&rcDataList.size()<=0){
                showNullLayout();
                refreshLayout.setVisibility(View.GONE);
                cacheBeans.clear();
                isMore = false;
            }else{
                hideNullLayout();
                refreshLayout.setVisibility(View.VISIBLE);
            }
            cacheBeans.addAll(rcDataList);
            bottomLayout.setVisibility(View.GONE);
            updateView();
        }catch (Exception ex){}
    }

    public void updateView(){
        if(cacheBeans.size()<=0){
            showNullLayout();
            refreshLayout.setVisibility(View.GONE);
            cacheBeans.clear();
            return;
        }
        hideNullLayout();
        refreshLayout.setVisibility(View.VISIBLE);
        recodeAdapter.setData(cacheBeans);
    }


}
