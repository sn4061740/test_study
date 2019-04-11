package com.xcore.ui.holder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shuyu.common.model.RecyclerBaseModel;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.NvStar;
import com.xcore.ui.activity.NvStarListActivity;
import com.xcore.ui.activity.StarDetailActivity;
import com.xcore.ui.adapter.NvStarRecommendAdapter;
import com.xcore.ui.holder.model.BaseHolder;
import com.xcore.ui.holder.model.RecommendHolderBaseModel;
import com.xcore.ui.other.XRecyclerView;

import java.util.List;

public class RecommendNvHolder extends BaseHolder {
    public static int ID=R.layout.holder_recommend_nv;

    public RecommendNvHolder(Context context, View v) {
        super(context, v);
    }

    private XRecyclerView xRecyclerView;
    private TextView moreNv;
    private NvStarRecommendAdapter nvStarAdapter;

    @Override
    public void createView(View v) {
        xRecyclerView=v.findViewById(R.id.nv_recyclerView);
        moreNv=v.findViewById(R.id.nv_more);
    }

    @Override
    public void onBind(RecyclerBaseModel model, int position) {
        if(model==null){
            return;
        }
        RecommendHolderBaseModel mod= (RecommendHolderBaseModel) model;
        List<NvStar> nvStarList= (List<NvStar>) mod.getValue();
        if(nvStarList==null||nvStarList.size()<=0){
            return;
        }
        nvStarAdapter=new NvStarRecommendAdapter(getContext());
        xRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));
        xRecyclerView.setAdapter(nvStarAdapter);

        nvStarAdapter.setData(nvStarList);

        nvStarAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<NvStar>() {
            @Override
            public void onItemClick(NvStar item, int position) {
//                Intent intent = new Intent(context, ActressActivity.class);
                String jsonStr=new Gson().toJson(item);
                Intent intent=new Intent(context,StarDetailActivity.class);
                intent.putExtra("nvItem",jsonStr);

                context.startActivity(intent);
            }
        });

        moreNv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, NvStarListActivity.class);
                context.startActivity(intent);
            }
        });
    }
}
