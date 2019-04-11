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
import com.xcore.base.MvpFragment;
import com.xcore.base.MvpFragment1;
import com.xcore.data.bean.RCBean;
import com.xcore.presenter.CollectPresenter;
import com.xcore.presenter.view.CollectView;
import com.xcore.ui.adapter.Recode1Adapter;
import com.xcore.ui.other.XRecyclerView;

public class RecodeV1Fragment extends BaseFragment {

    public static RecodeV1Fragment newInstance(String param1, String param2) {
        RecodeV1Fragment fragment = new RecodeV1Fragment();
        return fragment;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recode_v1;
    }

    private SmartRefreshLayout refreshLayout;
    private XRecyclerView recyclerView;
    private Recode1Adapter recodeAdapter;
    private LinearLayout bottomLayout;

    private boolean selected=false;
    private Button btnAll;
    private Button btnDel;

    private int pageIndex=1;
    private boolean isMore=true;

    private int type=1;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    protected void initView(View rootView) {
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
                pageIndex=1;
                isMore=true;
                initData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if(isMore) {
                    initData();
                }else{
                    refreshLayout.finishLoadMore(1000);
                }
            }
        });
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //allSelect();
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete();
            }
        });
        //initData();
    }

    @Override
    protected void initData() {
        //presenter.getRecode(getType());
    }

//    @Override
//    public void onRecode(RCBean rcBean) {
//
//    }
}
