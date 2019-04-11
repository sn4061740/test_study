package com.xcore.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.ThemeRecommendBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.ui.activity.ThemeActivity;
import com.xcore.utils.JumpUtils;

public class RecommendThemeAdapter extends BaseRecyclerAdapter<ThemeRecommendBean.ThemeData,RecommendThemeAdapter.ViewHolder> {

    public RecommendThemeAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public RecommendThemeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.adapter_recommend_theme,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendThemeAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        try {
            final ThemeRecommendBean.ThemeData data = dataList.get(position);
            if (data == null) {
                return;
            }
            ImageViewExt img = holder.itemView.findViewById(R.id.theme_conver);
            img.loadUrl(data.getConverUrl());

            String titleStr = data.getName();
            if (TextUtils.isEmpty(titleStr)) {
                titleStr = data.getTitle();
            }
            TextView titleTxt = holder.itemView.findViewById(R.id.theme_title);
            titleTxt.setText(titleStr);

            RelativeLayout advLayout=holder.itemView.findViewById(R.id.advLayout);
            advLayout.setVisibility(View.GONE);
            if(data.getAdvBean()!=null&&data.getAdvBean().getImagePath()!=null&&data.getAdvBean().getImagePath().length()>0){
                ImageViewExt advImage= holder.itemView.findViewById(R.id.adv_conver);
                TextView advTitle = holder.itemView.findViewById(R.id.adv_title);
                advTitle.setVisibility(View.GONE);
                if(!TextUtils.isEmpty(data.getAdvBean().getContent())) {
                    advTitle.setVisibility(View.VISIBLE);
                    advTitle.setText(data.getAdvBean().getContent());
                }
                advImage.loadUrl(data.getAdvBean().getImagePath());
                advImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      JumpUtils.to(mContext,data.getAdvBean());
                    }
                });
                advLayout.setVisibility(View.VISIBLE);
            }

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThemeRecommendBean.ThemeData item=data;
                    Intent intent=new Intent(mContext,ThemeActivity.class);
                    intent.putExtra("shortId",item.getShortId());
                    intent.putExtra("name",item.getName());
                    intent.putExtra("conver",item.getConverUrl());
                    intent.putExtra("maxConver",item.getMaxConverUrl());
                    intent.putExtra("desc",item.getRemarks());
                    mContext.startActivity(intent);
                }
            });

        }catch (Exception ex){}
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView=itemView;
        }
    }
}
