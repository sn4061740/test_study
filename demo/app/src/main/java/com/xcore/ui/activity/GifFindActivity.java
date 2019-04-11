package com.xcore.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuyu.common.CommonRecyclerAdapter;
import com.shuyu.common.CommonRecyclerManager;
import com.shuyu.common.model.RecyclerBaseModel;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.presenter.GifFindPresenter;
import com.xcore.presenter.view.GifFindView;
import com.xcore.ui.holder.GifFindHolder;
import com.xcore.ui.holder.model.GifFindModel;
import com.xcore.ui.other.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GifFindActivity extends MvpActivity<GifFindView,GifFindPresenter> implements GifFindView {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gif_find;
    }

    List<RecyclerBaseModel> baseModels=new ArrayList<>();
    private SmartRefreshLayout refreshLayout;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setEdit("");
        setTitle("GIF出处");

        XRecyclerView xRecyclerView=findViewById(R.id.recyclerView);
        CommonRecyclerManager manager=new CommonRecyclerManager();
        manager.addType(GifFindHolder.ID,GifFindHolder.class.getName());

        CommonRecyclerAdapter adapter=new CommonRecyclerAdapter(this,manager,baseModels);
        xRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        xRecyclerView.setAdapter(adapter);

        refreshLayout=findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);
            }
        });


        GifFindModel model=new GifFindModel();
        model.setValue("http://wx1.sinaimg.cn/mw690/0067uDIDly1flkv8x4wqig30fk06ehdu.gif");
        model.setResLayoutId(GifFindHolder.ID);
        baseModels.add(model);

        GifFindModel model1=new GifFindModel();
        model1.setResLayoutId(GifFindHolder.ID);
        model1.setValue("http://wx2.sinaimg.cn/mw690/0067uDIDly1fked4cddrsg30fk06e4qr.gif");
        baseModels.add(model1);

        GifFindModel model2=new GifFindModel();
        model2.setResLayoutId(GifFindHolder.ID);
        model2.setValue("http://wx1.sinaimg.cn/mw690/0067uDIDly1flqvuyu7kbg30fk06e4qr.gif");
        baseModels.add(model2);


        GifFindModel model3=new GifFindModel();
        model3.setResLayoutId(GifFindHolder.ID);
        model3.setValue("http://wx1.sinaimg.cn/mw690/0067uDIDly1fp6mmntw0wg30fk06e4qr.gif");
        baseModels.add(model3);


        GifFindModel model4=new GifFindModel();
        model4.setResLayoutId(GifFindHolder.ID);
        model4.setValue("http://wx4.sinaimg.cn/mw690/a561b538ly1fjnxhqtkukg20fk06eu0y.gif");
        baseModels.add(model4);


        GifFindModel model5=new GifFindModel();
        model5.setResLayoutId(GifFindHolder.ID);
        model5.setValue("http://wx1.sinaimg.cn/mw690/0067uDIDly1fmt16vjrt6g30fk06ex6q.gif");
        baseModels.add(model5);

        GifFindModel model6=new GifFindModel();
        model6.setResLayoutId(GifFindHolder.ID);
        model6.setValue("http://wx3.sinaimg.cn/mw690/0067uDIDly1fmt17pq702g30fk06eu10.gif");
        baseModels.add(model6);

        adapter.notifychange();
    }

    @Override
    protected void initData() {

    }

    @Override
    public GifFindPresenter initPresenter() {
        return new GifFindPresenter();
    }
}
