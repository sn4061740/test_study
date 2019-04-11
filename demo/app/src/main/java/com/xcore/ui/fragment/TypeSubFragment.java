package com.xcore.ui.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.BaseFragment;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.ui.adapter.TypeItemAdapter;
import com.xcore.ui.other.XRecyclerView;
import com.xcore.ui.touch.OnFragmentListener;
import com.xcore.utils.RefreshUtil;

import java.util.ArrayList;
import java.util.List;

public class TypeSubFragment extends BaseFragment {
    private TypeItemAdapter typeItemAdapter;
    private LinearLayout emptyLayout;
    private SmartRefreshLayout refreshLayout;
    private LinearLayout rLayout;
    private boolean isMore=true;

    List<TypeListDataBean> listDataBeanList=new ArrayList<>();

    private OnFragmentListener mListener;
    public void setmListener(OnFragmentListener listener){
        this.mListener=listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_type_item;
    }

    @Override
    protected void initView(View view) {
        refreshLayout=view.findViewById(R.id.refreshLayout);
        XRecyclerView recyclerView= view.findViewById(R.id.recyclerView);
        emptyLayout=view.findViewById(R.id.empt_layout);
        rLayout=view.findViewById(R.id.rLayout);

        emptyLayout.setVisibility(View.GONE);

        typeItemAdapter=new TypeItemAdapter(getActivity());
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(typeItemAdapter);
        typeItemAdapter.setData(listDataBeanList);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            if(mListener!=null){
//                if(refreshLayout!=null) {
//                    refreshLayout.finishLoadMore();
//                    refreshLayout.finishRefresh();
//                }
                mListener.onRefresh(false);
                updateRefreshStatus();
            }else{
                refreshLayout.finishRefresh(1000);
            }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            if(mListener!=null){
//                if(refreshLayout!=null) {
//                    refreshLayout.finishLoadMore();
//                    refreshLayout.finishRefresh();
//                }
                mListener.onRefresh(true);
                updateRefreshStatus();
            }else{
                refreshLayout.finishLoadMore(1000);
            }
            }
        });
        typeItemAdapter.setOnItemClickListener(new TypeItemAdapter.OnItemClickListener<TypeListDataBean>() {
            @Override
            public void onItemClick(TypeListDataBean item, int position) {
            }
        });
    }

    private void updateRefreshStatus(){
        try {
            if (refreshLayout != null) {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh();
                            refreshLayout.finishLoadMore();
                        }
                    }
                }, 5000);
            }
        }catch (Exception ex){}
    }

    @Override
    protected void initData() {
    }

    public void onError(String msg){
        if(refreshLayout!=null){
            refreshLayout.finishLoadMore();
            refreshLayout.finishRefresh();
        }
    }

    //刷新 fragment 数据
    public void setData(TypeListBean typeListBean){
        try {
            Log.e("TAG","这里进来了");
            if (refreshLayout != null) {
                refreshLayout.finishLoadMore();
                refreshLayout.finishRefresh();
                RefreshUtil.refreshHeader();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        try {
            Log.e("TAG","赋值数据..");
            //是第一页且没有数据
            if (typeListBean.getPageIndex() == 1 && typeListBean.getList().size() <= 0) {
                emptyLayout.setVisibility(View.VISIBLE);
                rLayout.setVisibility(View.GONE);
                isMore = false;
                listDataBeanList.clear();
            } else {
                emptyLayout.setVisibility(View.GONE);
                rLayout.setVisibility(View.VISIBLE);
                if (typeListBean.getPageIndex() == 1) {
                    listDataBeanList = typeListBean.getList();
                } else {
                    listDataBeanList.addAll(typeListBean.getList());
                }
            }
            typeItemAdapter.setData(listDataBeanList);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            RefreshUtil.refreshHeader();
        }
    }

}
