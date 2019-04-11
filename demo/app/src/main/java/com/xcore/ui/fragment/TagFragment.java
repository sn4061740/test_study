package com.xcore.ui.fragment;


import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.base.MvpFragment1;
import com.xcore.data.bean.Tag;
import com.xcore.data.bean.TagBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.utils.TCallback;
import com.xcore.presenter.TagSelectPreseneter;
import com.xcore.presenter.view.TagSelectView;
import com.xcore.services.ApiFactory;
import com.xcore.ui.adapter.TypeItemAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagFragment extends MvpFragment1<TagSelectView, TagSelectPreseneter> implements TagSelectView {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tag;
    }

//    NestedScrollView scrollView;

    SmartRefreshLayout refreshLayout;
    RecyclerView contentRecy;

    LinearLayout tagResultLayout;
    LinearLayout topLayout;

    RecyclerView recyclerView;
    CustomerTagAdapter tagAdapter;

    RecyclerView resultRecy;
    CustomerSelectTagAdapter selectTagAdapter;

    Button btnOk;

    TagSelectModel currentTag;//当前选中

    //结果
    Map<String,TagSelectModel> tagResultMaps= new HashMap<>();
    //选择
    Map<String,TagBean> selectMaps=new HashMap<>();


    private TypeItemAdapter typeItemAdapter;

    @Override
    protected void initView(View rootView) {
//        scrollView=rootView.findViewById(R.id.scrollView);
//        scrollView.setSmoothScrollingEnabled(false);

        refreshLayout=rootView.findViewById(R.id.refreshLayout);
        contentRecy=rootView.findViewById(R.id.contentRecy);

        refreshLayout.setVisibility(View.GONE);

        tagResultLayout=rootView.findViewById(R.id.tagResultLayout);
        topLayout=rootView.findViewById(R.id.topLayout);
        topLayout.setVisibility(View.GONE);

        selectTagAdapter=new CustomerSelectTagAdapter(getActivity());
        resultRecy=rootView.findViewById(R.id.resultRecy);
        resultRecy.setLayoutManager(new GridLayoutManager(getActivity(),2));
        resultRecy.setAdapter(selectTagAdapter);

        tagAdapter=new CustomerTagAdapter(getActivity());

        recyclerView=rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),5));
        recyclerView.setAdapter(tagAdapter);

        tagAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<TagSelectModel>() {
            @Override
            public void onItemClick(TagSelectModel item, int position) {
                Log.e("TAG","点击了："+item.getName());
                currentTag=item;
                selectTagAdapter.setData(currentTag.getList());
            }
        });

        selectTagAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<Tag>() {
            @Override
            public void onItemClick(Tag item, int position) {
                boolean checked=item.isChecked();
                item.setChecked(!checked);

                selectTagAdapter.notifyItemChanged(position);
            }
        });


        btnOk=rootView.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagResultLayout.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
                refreshLayout.autoRefresh();
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                onRefreshData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                onLoadMoreData();
            }
        });

//        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if(scrollY>50){
//                    topLayout.setVisibility(View.VISIBLE);
//                }
//            }
//        });

        typeItemAdapter=new TypeItemAdapter(getActivity());
        contentRecy.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        contentRecy.setAdapter(typeItemAdapter);
//        typeItemAdapter.setData(listDataBeanList);
    }

    private void onRefreshData(){
//        refreshLayout.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                refreshLayout.finishRefresh();
//            }
//        },3000);
//        presenter.getTagList(1,"SM");
        getTag("SM",1);
    }
    private void onLoadMoreData(){
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.finishLoadMore();
            }
        },3000);
    }

    public void getTag(final String key,final int pageIndex){

        HttpParams params=new HttpParams();
        params.put("key",key);
        params.put("PageIndex",pageIndex);
        params.put("sorttype","");
        ApiFactory.getInstance().<TypeListBean>getMovieBytagsList(params, new TCallback<TypeListBean>() {
            @Override
            public void onNext(TypeListBean typeListBean) {
                refreshLayout.finishRefresh();
                typeItemAdapter.setData(typeListBean.getList());
            }

            @Override
            public void onError(Response<TypeListBean> response) {
                super.onError(response);
            }
        });
    }


    private void refreshData(){
//        if(tagBean==null) {
//            presenter.getTags();
//        }else{
//            //得到标签
//            List<Tag> tags=tagBean.getList();
//            tagAdapter.setData(tags);
//        }
        presenter.getTags();

//        refreshLayout.autoRefresh();
    }

    @Override
    public TagSelectPreseneter initPresenter() {
        return new TagSelectPreseneter();
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        refreshData();
    }

    @Override
    public void onTagResult(TagBean tagBean) {
//        //得到标签
//        List<Tag> tags=tagBean.getData();
//        Tag tag=new Tag();
//        tag.setName("全部");
//        tag.setShortId("-1");
//        tag.setChecked(false);
//        tags.add(0,tag);
//        tagAdapter.setData(tags);
//        currentTag=tags.get(0);
//        getTagList();
        List<TagSelectModel> tagSelectModels=new ArrayList<>();
        for(int j=0;j<10;j++) {
            TagSelectModel model = new TagSelectModel();
            model.setName("一本道"+j);
            model.setShortId("v");
            model.setSelected(false);

            List<Tag> tags = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                Tag tag = new Tag();
                tag.setName("浴衣・着物"+j+"_"+i);
                tag.setShortId("tc");
                tags.add(tag);
            }
            model.setList(tags);
            tagSelectModels.add(model);
        }
        tagAdapter.setData(tagSelectModels);
        currentTag=tagSelectModels.get(0);
        //初始化

        selectTagAdapter.setData(currentTag.getList());
    }
    //设置页面
    private void resetView(){

    }

    @Override
    public void onResult(TagBean tagBean) {

    }
}
