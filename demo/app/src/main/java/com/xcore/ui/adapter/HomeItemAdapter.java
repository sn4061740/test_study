package com.xcore.ui.adapter;

import android.app.Activity;
import android.com.glide37.GlideUtils;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.utils.CacheFactory;
import com.xcore.utils.ViewUtils;

public class HomeItemAdapter extends BaseRecyclerAdapter<TypeListDataBean,HomeItemAdapter.ViewHoler>{

    public HomeItemAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_home_item, parent, false);
            return new ViewHoler(view);
        }catch (Exception ex){}
        return null;
    }

    @Override
    public void onViewRecycled(@NonNull ViewHoler holder) {
        super.onViewRecycled(holder);
        if(holder.conver==null){
            GlideUtils.getInstance().clear(holder.conver);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHoler holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder==null){
            return;
        }
        final TypeListDataBean dataBean= dataList.get(position);
        TextView cTitle= holder.itemView.findViewById(R.id.cTitle);
        cTitle.setText(dataBean.getTitle());

        final ImageViewExt conver=holder.conver;
//        conver.setTag(conver);
        conver.loadUrl(dataBean.getConverUrl());

        conver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.toPlayer((Activity) mContext,conver,dataBean.getShortId(),null,null,null);
            }
        });
    }

    class ViewHoler extends RecyclerView.ViewHolder{
        ImageViewExt conver;
        public ViewHoler(View itemView) {
            super(itemView);
            conver=itemView.findViewById(R.id.imageConver);
        }
    }
}
