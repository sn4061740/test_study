package com.xcore.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDailog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.MvpFragment1;
import com.xcore.commonAdapter.MultiTypeAdapter;
import com.xcore.data.bean.AdvBean;
import com.xcore.data.bean.HomeBean;
import com.xcore.data.bean.Tag;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.HomePresenter;
import com.xcore.presenter.view.HomeView;
import com.xcore.ui.activity.BoxActivity;
import com.xcore.ui.activity.CuestomerCaptureActivity;
import com.xcore.ui.activity.LastUpdatedActivity;
import com.xcore.ui.activity.SearchActivity;
import com.xcore.ui.activity.TagActivity;
import com.xcore.ui.delegate.AdvItem;
import com.xcore.ui.delegate.BannerItem;
import com.xcore.ui.delegate.ButtonItem;
import com.xcore.ui.delegate.HomeItem;
import com.xcore.ui.delegate.TagItem;
import com.xcore.ui.delegate.holder.ADVHolderViewBinder;
import com.xcore.ui.delegate.holder.BannerHolderViewBinder;
import com.xcore.ui.delegate.holder.ButtonHolderViewBinder;
import com.xcore.ui.delegate.holder.HomeHolderViewBinder;
import com.xcore.ui.delegate.holder.TagHolderViewBinder;
import com.xcore.ui.other.XRecyclerView;
import com.xcore.ui.touch.IHomeOnClick;
import com.xcore.utils.GuideUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment100 extends MvpFragment1<HomeView,HomePresenter> implements HomeView {

    public HomeFragment100() {
    }

    public static HomeFragment100 newInstance(String param1, String param2) {
        HomeFragment100 fragment = new HomeFragment100();
        return fragment;
    }

    @Override
    public HomePresenter initPresenter() {
        return new HomePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home100;
    }

    private boolean isRefresh=true;
    private HomeBean homeBean;
    private SmartRefreshLayout refreshLayout;
    private XRecyclerView recyclerView;
    private View view;

    @Override
    protected void initView(View view) {
        homeBean= DataUtils.homeBean;
        this.view=view;
        if(dialog==null){
            LoadingDailog.Builder loadBuilder;
            loadBuilder=new LoadingDailog.Builder(getActivity())
                    .setMessage("加载中...")
                    .setCancelable(true)
                    .setCancelOutside(true);
            dialog=loadBuilder.create();
        }

        //设置搜索框
        TextView editText= view.findViewById(R.id.edit_search);
        Drawable drawable=getResources().getDrawable(R.drawable.search_bg);
        drawable.setBounds(5,0,65,60);//searchEdit.getCompoundDrawables()[2]
        editText.setCompoundDrawables(drawable,null,editText.getCompoundDrawables()[2],null);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), SearchActivity.class);
                getContext().startActivity(intent);
            }
        });
        view.findViewById(R.id.img_box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean pwdBoo1=MainApplicationContext.toGuestLogin(getActivity());
                if(pwdBoo1){
                    return;
                }
                Intent intent=new Intent(getContext(), BoxActivity.class);
                getContext().startActivity(intent);
            }
        });
        view.findViewById(R.id.img_saomiao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CuestomerCaptureActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra("textType", 0);
                getActivity().startActivityForResult(intent, 410);
            }
        });

        refreshLayout=view.findViewById(R.id.refreshLayout);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            isRefresh=true;
            refreshData();
            }
        });
        refreshLayout.setEnableLoadMore(false);

        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new MultiTypeAdapter();
        recyclerView.setAdapter(adapter);

        updateView();
        initData();
    }
    private MultiTypeAdapter adapter;
    private List<Object> items=new ArrayList<>();
    Map<String,Integer> viewPageIndexs=new HashMap<>();
    BannerHolderViewBinder bannerHolderViewBinder;

    //更新
    private void updateView(){
        if(homeBean==null){
            return;
        }
        HomeBean.HomeData homeData= homeBean.getData();
        if(homeData==null){
            return;
        }
        items.clear();
        adapter.getItems().clear();

        //MZBanner
        BannerItem bannerItem=new BannerItem();
        bannerItem.bannerDatas=homeData.getBanner();
        bannerHolderViewBinder=new BannerHolderViewBinder(getContext());
        adapter.register(BannerItem.class,bannerHolderViewBinder);
        items.add(bannerItem);

        //BUTTON
        ButtonItem buttonItem=new ButtonItem();
        buttonItem.homeTypeItems=homeData.getTitleModels();
        adapter.register(ButtonItem.class,new ButtonHolderViewBinder(getContext()));
        items.add(buttonItem);

        //TAGS
        List<Tag> tagList=homeData.getTags();
//        List<HomeBean.HomeTypeItem> typeItems=new ArrayList<>();
//        for(int i=0;i<4;i++){
//            HomeBean.HomeTypeItem t=new HomeBean.HomeTypeItem();
//            t.setShortId(tagList.get(i).getShortId());
//            t.setName(tagList.get(i).getName());
//            typeItems.add(t);
//        }
//
//        ButtonItem1 buttonItem1=new ButtonItem1();
//        buttonItem1.homeTypeItems=typeItems;//homeData.getTitleModels();
//        adapter.register(ButtonItem1.class,new ButtonHolderViewBinder1(getContext()));
//        items.add(buttonItem1);

//        for(int i=0;i<12;i++){
//            Tag tag=new Tag();
//            tag.setShortId("i");
//            tag.setName("TAG_"+i);
//            tagList.add(tag);
//        }
        TagItem tagItem=new TagItem();
        tagItem.setTags(tagList);
        adapter.register(TagItem.class,new TagHolderViewBinder(getContext()));
        items.add(tagItem);

        //NEW
        HomeItem newHomeItem=new HomeItem();
        newHomeItem.setHomeDataItem(homeData.getNewList());
        newHomeItem.setHomeOnClick(homeOnClick);
        adapter.register(HomeItem.class,new HomeHolderViewBinder(getContext()));
        items.add(newHomeItem);
        viewPageIndexs.put(homeData.getNewList().getShortId()+"",1);

        //NEW ADV
        Map<String,AdvBean> advMaps= homeData.getAdvDatas();
        if(advMaps==null){
            advMaps=new HashMap<>();
        }
        AdvBean newAdvBean= advMaps.get(homeData.getNewList().getShortId());
        if(newAdvBean!=null) {
            adapter.register(AdvItem.class, new ADVHolderViewBinder(getContext()));
            items.add(new AdvItem(newAdvBean));
        }

        //HOT
        HomeItem hotHomeItem=new HomeItem();
        hotHomeItem.setHomeDataItem(homeData.getHotList());
        hotHomeItem.setHomeOnClick(homeOnClick);
        adapter.register(HomeItem.class,new HomeHolderViewBinder(getContext()));
        items.add(hotHomeItem);
        viewPageIndexs.put(homeData.getHotList().getShortId()+"",1);

        //HOT ADV
        AdvBean hotAdvBean= advMaps.get(homeData.getHotList().getShortId());
        if(hotAdvBean!=null){
            adapter.register(AdvItem.class, new ADVHolderViewBinder(getContext()));
            items.add(new AdvItem(hotAdvBean));
        }

        //TAG
        List<HomeBean.HomeDataItem> tags= homeData.getTagsList();
        for(HomeBean.HomeDataItem item:tags){
            HomeItem tagHomeItem=new HomeItem();
            tagHomeItem.setHomeDataItem(item);

            items.add(tagHomeItem);
            adapter.register(HomeItem.class,new HomeHolderViewBinder(getContext()));
            tagHomeItem.setHomeOnClick(homeOnClick);
            viewPageIndexs.put(item.getShortId()+"",1);

            AdvBean tagAdvBean= advMaps.get(item.getShortId());
            if(tagAdvBean!=null) {
                adapter.register(AdvItem.class, new ADVHolderViewBinder(getContext()));
                items.add(new AdvItem(tagAdvBean));
            }
        }
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }

    IHomeOnClick homeOnClick=new IHomeOnClick() {
        @Override
        public void onClickChange(HomeBean.HomeDataItem dataItem1) {
            if(dialog!=null){
                dialog.show();
            }
            HomeBean.HomeDataItem dataItem =dataItem1;
            int pageIndex=viewPageIndexs.get(dataItem.getShortId()+"");
            pageIndex++;
            //查询//判断是标签查询 1 、还是类型查询 2
            if(dataItem.getType()==1){
                presenter.getTypeData(pageIndex,dataItem);
            }else if(dataItem.getType()==2){
                presenter.getTagData(pageIndex,dataItem);
            }else if(dataItem.getType()==3){//热播&&dataItem.getShortId().equals("u")
                presenter.getTypeHot(pageIndex,dataItem);
            }else if(dataItem.getType()==4){//最近更新
                presenter.getTypeTagData(pageIndex,dataItem);
            }
            viewPageIndexs.put(dataItem.getShortId()+"",pageIndex);
        }
        @Override
        public void onClickMore(HomeBean.HomeDataItem dataItem1) {
            HomeBean.HomeDataItem dataItem =dataItem1;
            //Log.e("TAG","更多"+dataItem.getName());
            Intent intent=null;
            if(dataItem.getType()==1||dataItem.getType()==3||dataItem.getType()==4){//类型
                intent=new Intent(getContext(), LastUpdatedActivity.class);
            }else if(dataItem.getType()==2){//标签
                intent=new Intent(getContext(), TagActivity.class);
            }
            if(intent!=null) {
                intent.putExtra("shortId",dataItem.getShortId());
                intent.putExtra("tag",dataItem.getName());
                intent.putExtra("type",dataItem.getType());
                getContext().startActivity(intent);
            }
        }
    };
    @Override
    public void doGetInfo() {
    }

//    @Override
    public void initData() {
        if(homeBean==null){
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshData();
                }
            },1000);
        }
    }
    protected LoadingDailog dialog;
    //刷新
    private void refreshData(){
        if(isRefresh||homeBean==null&&!getActivity().isFinishing()) {
            try {
                if(dialog!=null) {
                    dialog.show();
                }
                presenter.getHomeData();
                if(refreshLayout!=null) {
                    refreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        if(refreshLayout!=null) {
                            refreshLayout.finishRefresh();
                        }
                        }
                    }, 5000);
                }
            }catch (Exception ex){}
        }
        isRefresh=false;
    }

    @Override
    public void onHomeTypeResult(HomeBean.HomeDataItem homeDataItem) {
        if(dialog!=null) {
            dialog.cancel();
        }
        //判断没有了就从第一页开始请求
        if(homeDataItem.getList().size()<=0){
            return;
        }
        String sId=homeDataItem.getShortId();
        int posIndex=-1;
        for(int i=0;i<items.size();i++){
            if(items.get(i) instanceof HomeItem){
                HomeItem item= (HomeItem) items.get(i);
                if(item!=null&&item.getHomeDataItem()!=null&&sId.equals(item.getHomeDataItem().getShortId())){
                    posIndex=i;
                    item.setHomeDataItem(homeDataItem);
                    break;
                }
            }
        }
        if(posIndex!=-1){//更新
            adapter.notifyItemChanged(posIndex);
        }
    }
    @Override
    public void onHomeResult(HomeBean homeBean) {
        if(refreshLayout!=null){
            refreshLayout.finishRefresh();
        }
        if(dialog!=null){
            dialog.cancel();
        }
        this.homeBean=homeBean;
        DataUtils.homeBean=homeBean;
        updateView();

        //新手引导
        GuideUtil.show(getActivity(),getFragmentManager());
    }
    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if(bannerHolderViewBinder!=null){
            bannerHolderViewBinder.startMz();
        }
    }
    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        if(bannerHolderViewBinder!=null){
            bannerHolderViewBinder.stopMz();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
