package com.xcore.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xcore.R;
import com.xcore.base.BaseFragment;
import com.xcore.cache.CacheManager;
import com.xcore.cache.CacheModel;
import com.xcore.ui.adapter.XOverAdapter;
import com.xcore.ui.other.XRecyclerView;
import com.xcore.ui.touch.OnFragmentInteractionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OverCacheFragment extends BaseFragment {
    private OnFragmentInteractionListener mListener;
    private LinearLayout bottomLayout;

    private XRecyclerView overRecyclerView;
    private XOverAdapter overAdapter;
    private RelativeLayout xLayout;
    private TextView txtCache;
    private boolean isSelect=false;

    private static String shortId="";

    public OverCacheFragment() {
    }

    public static OverCacheFragment newInstance(String param1, String param2) {
        OverCacheFragment fragment = new OverCacheFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        shortId=param1;
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_over_cache;
    }

    @Override
    protected void initView(View view) {
        hideNullLayout();
        xLayout=view.findViewById(R.id.xLayout);
        overAdapter=new XOverAdapter(getActivity());

        overRecyclerView=view.findViewById(R.id.overRecyclerView);
        overRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        overRecyclerView.setAdapter(overAdapter);

        txtCache= view.findViewById(R.id.txt_cacheCount);
        refreshData();

        bottomLayout=view.findViewById(R.id.bottomLayout);
        onEdit(false);

        view.findViewById(R.id.btn_selectAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectAll();
            }
        });
        view.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        if(shortId!=null&&shortId.length()>0) {
            int scIndex=0;
            List<CacheModel> oList = overAdapter.dataList;
            for (int i=0;i<oList.size();i++) {
                if(shortId.equals(oList.get(i).getShortId())){
                    scIndex=i;
                    break;
                }
            }
            if(scIndex>0){
                overRecyclerView.scrollToPosition(scIndex);
                LinearLayoutManager mLayoutManager =
                        (LinearLayoutManager) overRecyclerView.getLayoutManager();
                mLayoutManager.scrollToPositionWithOffset(scIndex, 0);
            }
        }

    }

    @Override
    protected void initData() {
    }

    private void delete(){
        try {
            List<CacheModel> cacheModels = overAdapter.dataList;
            List<CacheModel> deleteList = new ArrayList<>();
            for (CacheModel cacheModel : cacheModels) {
                if (cacheModel.isChecked()) {
                    deleteList.add(cacheModel);
                }
            }
            CacheManager.getInstance().getLocalDownLoader().deleteCache(deleteList);//.getDownHandler().deleteRunTask(deleteList, 1);// .batchDelete(deleteList);
            if (mListener != null) {
                mListener.onItemClick(null);
            }
        }catch (Exception e){}
        chageCount=0;
        refreshData();

        onEdit(false);
    }
    private int chageCount=0;
    private int tLen=0;

    //刷新数据
    public void refreshData(){
        try {
            List<CacheModel> cacheModels = new ArrayList<>();
            Map<String, CacheModel> cacheModelMap = CacheManager.getInstance().getLocalDownLoader().overMaps;//.getDownHandler().overMaps;
            if (cacheModelMap != null && cacheModelMap.size() > 0) {
                for (String key : cacheModelMap.keySet()) {
                    CacheModel cacheModel = cacheModelMap.get(key);
                    if (cacheModel == null) {
                        continue;
                    }
                    cacheModel.setChecked(false);
                    cacheModel.setShowCheck(false);
                    cacheModel.setBgSelected(false);
                    cacheModels.add(cacheModel);

                    if(shortId!=null&&shortId.equals(key)){
                        cacheModel.setBgSelected(true);
                    }
                }
            }
            if (tLen != cacheModels.size()) {
                chageCount = 0;
                tLen = cacheModels.size();
            }
            if (overAdapter != null) {
                overAdapter.dataList.clear();
                if (chageCount == 0) {
                    chageCount++;
                    overAdapter.setData(cacheModels);
                } else {
                    overAdapter.setData(cacheModels, 100);
                }
            }
            if (txtCache != null && cacheModels != null) {
                txtCache.setText("已缓存个数" + cacheModels.size());
            }
            if(cacheModels.size()<=0){//没有东西
                showNullLayout();
                xLayout.setVisibility(View.GONE);
            }else{
                hideNullLayout();
                xLayout.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){}
    }
    //全选
    private void onSelectAll(){
        isSelect=!isSelect;
        if(overAdapter!=null) {
            overAdapter.selectAll(isSelect);
        }
    }
    //点击编辑
    public void onEdit(boolean boo){//判断是否有内容,没有的话就不用显示底部信息了
        try {
            if (overAdapter == null || overAdapter.dataList == null || overAdapter.dataList.size() <= 0) {
                if (mListener != null) {
                    mListener.onItemClick(null);
                }
                if (bottomLayout != null) {
                    bottomLayout.setVisibility(View.GONE);
                }
                return;
            }
            if (overAdapter != null) {
                overAdapter.showAll(boo);
            }
            if (bottomLayout == null) {
                return;
            }
            if (boo) {
                bottomLayout.setVisibility(View.VISIBLE);
            } else {
                bottomLayout.setVisibility(View.GONE);
            }
        }catch (Exception ex){}
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
            }
        }catch (Exception ex){}
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
