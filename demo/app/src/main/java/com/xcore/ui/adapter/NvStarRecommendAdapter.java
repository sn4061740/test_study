package com.xcore.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.NvStar;
import com.xcore.utils.CacheFactory;

import cn.carbs.android.avatarimageview.library.AvatarImageView;

public class NvStarRecommendAdapter extends BaseRecyclerAdapter<NvStar,NvStarRecommendAdapter.ViewHolder> {

    public NvStarRecommendAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.adapter_nvstar_recommend,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        NvStar nvStar= dataList.get(position);

        AvatarImageView headImage=holder.itemView.findViewById(R.id.item_avatar);
        if(!TextUtils.isEmpty(nvStar.getHeadUrl())) {
            CacheFactory.getInstance().getImage(mContext, headImage, nvStar.getHeadUrl());
        }

        TextView txtName=holder.itemView.findViewById(R.id.txt_name);
        txtName.setText(nvStar.getActorName());
    }

    class ViewHolder extends RecyclerView.ViewHolder{

    public ViewHolder(View itemView) {
        super(itemView);
    }
}
}