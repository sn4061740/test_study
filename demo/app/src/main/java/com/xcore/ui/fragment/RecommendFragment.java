package com.xcore.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuyu.common.CommonRecyclerAdapter;
import com.shuyu.common.CommonRecyclerManager;
import com.shuyu.common.model.RecyclerBaseModel;
import com.xcore.R;
import com.xcore.base.MvpFragment1;
import com.xcore.data.bean.HomeBean;
import com.xcore.data.bean.NvStarBean;
import com.xcore.data.bean.RecommendBean;
import com.xcore.data.bean.ThemeRecommendBean;
import com.xcore.presenter.RecommendPresenter;
import com.xcore.presenter.view.RecommendView;
import com.xcore.ui.activity.FindActivity;
import com.xcore.ui.activity.MakeLTDActivity;
import com.xcore.ui.activity.TagActivity;
import com.xcore.ui.activity.TagSelectActivity;
import com.xcore.ui.activity.ThemeListActivity;
import com.xcore.ui.activity.TypeActivity;
import com.xcore.ui.adapter.RThemeAdapter;
import com.xcore.ui.holder.RecommendNvHolder;
import com.xcore.ui.holder.RecommendThemeHolder;
import com.xcore.ui.holder.RecommendTypeHolder;
import com.xcore.ui.holder.model.RecommendHolderBaseModel;
import com.xcore.ui.other.XRecyclerView;
import com.xcore.ui.touch.IHomeClickLinstenner;
import com.xcore.utils.RefreshUtil;
import com.xcore.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class RecommendFragment extends MvpFragment1<RecommendView,RecommendPresenter> implements RecommendView,IHomeClickLinstenner {

    private RThemeAdapter rThemeAdapter;

    SmartRefreshLayout refreshLayout;
    boolean isRefresh=true;
    private RecommendBean rBean;

    private List<RecyclerBaseModel> baseModels=new ArrayList<>();
    private CommonRecyclerAdapter adapter;

    public RecommendFragment() {
    }
    public static RecommendFragment newInstance(String param1, String param2) {
        RecommendFragment fragment = new RecommendFragment();
        return fragment;
    }


    @Override
    public RecommendPresenter initPresenter() {
        return new RecommendPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_recommend_new;
    }

    //点击跳转
    private void toJump(final HomeBean.HomeTypeItem typeItem){
        Intent intent=null;
        switch (typeItem.getJump()){
            case 1://电影详情 banner 跳转？
//                intent=new Intent(getContext(), VideoActivity.class);
//                intent.putExtra("shortId",typeItem.getShortId());
                ViewUtils.toPlayer((Activity) getContext(), null, typeItem.getShortId(), null, null);
                return;
            case 2://类型
                intent=new Intent(getContext(), TypeActivity.class);
                intent.putExtra("shortId",typeItem.getShortId());
                intent.putExtra("type",2);
                intent.putExtra("tag",typeItem.getName());
//                XMainActivity.stupToFrament(2,typeItem.getShortId());
                break;
            case 3://标签
                intent=new Intent(getContext(), TagActivity.class);
                intent.putExtra("tag",typeItem.getName());
                break;
            case 4://专题
                intent=new Intent(getContext(), ThemeListActivity.class);
//                if(typeItem.getShortId().equals("")){//没有专题ID就切换到推荐页
//                    XMainActivity.stupToFrament(1);
//                }else{//跳到专题页面
//                    intent=new Intent(getContext(), ThemeActivity.class);
//                    intent.putExtra("shortId",typeItem.getShortId());//传进专题ID
//                }
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                intent=new Intent(getContext(), TypeActivity.class);
                intent.putExtra("shortId",typeItem.getShortId());
                intent.putExtra("type",7);
                intent.putExtra("tag",typeItem.getName());
//                XMainActivity.stupToFrament(7,typeItem.getShortId());
                break;
            case 8://浏览地址
                if(!TextUtils.isEmpty(typeItem.getShortId())){
                    intent = new Intent( Intent.ACTION_VIEW );//https://www.potato.im/1avco1
                    intent.setData( Uri.parse(typeItem.getShortId()) ); //这里面是需要调转的rul
                    intent = Intent.createChooser( intent, null );
                }
                break;
            case 9://下载地址 直接下载..  到浏览器打开下载
                if(!TextUtils.isEmpty(typeItem.getShortId())){
                    intent = new Intent( Intent.ACTION_VIEW );//https://www.potato.im/1avco1
                    intent.setData( Uri.parse(typeItem.getShortId()) ); //这里面是需要调转的rul
                    intent = Intent.createChooser( intent, null );
                }
                break;
            case 10://发现
                intent=new Intent(getContext(), FindActivity.class);
                intent.putExtra("tag","");
                break;
            case 11://跳一本道、东京热....
                intent=new Intent(getContext(), MakeLTDActivity.class);
                intent.putExtra("shortId",typeItem.getShortId());
                break;
        }
        if(intent!=null){
            getContext().startActivity(intent);
        }
    }

    @Override
    protected void initView(View view) {
        TextView textView= view.findViewById(R.id.tagSelect);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent=new Intent(getContext(), TagSelectActivity.class);
            intent.putExtra("toTag","1");
            startActivity(intent);
            }
        });

        XRecyclerView xRecyclerView =view.findViewById(R.id.xRecyclerView);
        refreshLayout=view.findViewById(R.id.refreshLayout);

        CommonRecyclerManager manager=new CommonRecyclerManager();
        manager.addType(RecommendTypeHolder.ID,RecommendTypeHolder.class.getName());
        manager.addType(RecommendNvHolder.ID,RecommendNvHolder.class.getName());
        manager.addType(RecommendThemeHolder.ID,RecommendThemeHolder.class.getName());

        adapter=new CommonRecyclerAdapter(getActivity(),manager,baseModels);
        xRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
        xRecyclerView.setAdapter(adapter);

        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            isRefresh=true;
            refreshData();
            }
        });

        updateView();
    }
    //更新显示
    private void updateView(){
        if(rBean==null){
            return;
        }
        try {
            baseModels.clear();

            RecommendHolderBaseModel typeBtnModel = new RecommendHolderBaseModel();
            typeBtnModel.setValue(rBean.getData().getTitleModels());
            typeBtnModel.setResLayoutId(RecommendTypeHolder.ID);
            typeBtnModel.setiHomeOnClick(this);

            RecommendHolderBaseModel nvBtnModel = new RecommendHolderBaseModel();
            nvBtnModel.setValue(rBean.getData().getActorList());
            nvBtnModel.setResLayoutId(RecommendNvHolder.ID);
            nvBtnModel.setiHomeOnClick(this);

            List<ThemeRecommendBean.ThemeData> themeDataList=rBean.getData().getThemeList();
//            List<AdvBean> advBeans= rBean.getData().getAdvList();
//            for(AdvBean advBean:advBeans){
//                try {
//                    int ix = Integer.valueOf(advBean.getIndex());
//                    ThemeRecommendBean.ThemeData data= themeDataList.get(ix);
//                    if(data!=null){
//                        data.setAdvBean(advBean);
//                    }
//                }catch (Exception ex){}
//            }
            RecommendHolderBaseModel themeBtnModel = new RecommendHolderBaseModel();
            themeBtnModel.setValue(themeDataList);
            themeBtnModel.setResLayoutId(RecommendThemeHolder.ID);
            themeBtnModel.setiHomeOnClick(this);

            baseModels.add(typeBtnModel);
            baseModels.add(nvBtnModel);
            baseModels.add(themeBtnModel);
            adapter.notifychange();

            RefreshUtil.refreshHeader();
        }catch (Exception ex){}
    }

    private void refreshData(){
        if(rBean==null||isRefresh==true){
            presenter.getRecommendData();
            try {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (refreshLayout != null) {
                            refreshLayout.finishRefresh();
                        }
                    }
                }, 5000);
            }catch (Exception ex){}
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(HomeBean.HomeTypeItem typeItem) {
        toJump(typeItem);
    }

    @Override
    public void onRecommendNvStar(NvStarBean nvStarBean) {
    }

    @Override
    public void onRecommendTheme(ThemeRecommendBean recommendBean) {
    }

    @Override
    public void onRecommendResult(RecommendBean recommendBean) {
        if(refreshLayout!=null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }
        if(recommendBean==null){
            return;
        }
        isRefresh=false;
        rBean=recommendBean;
        updateView();
    }

    //界面可见的时候调用
    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        refreshData();
    }
}
