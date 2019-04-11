package com.xcore.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.cache.beans.CacheBean;
import com.xcore.cache.beans.CacheType;
import com.xcore.data.bean.RCBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.utils.ViewUtils;

import java.math.BigDecimal;

public class SelfAdapter extends BaseRecyclerAdapter<RCBean.RCData,SelfAdapter.ViewHolder>{

    public SelfAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.adapter_self,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        final RCBean.RCData cacheBean=dataList.get(position);

        //ProgressBar progressBar= holder.itemView.findViewById(R.id.pro_progress);
        TextView txtTitle=holder.itemView.findViewById(R.id.title);
        TextView proTxt=holder.itemView.findViewById(R.id.proTxt);
        //ImageView playImage=holder.itemView.findViewById(R.id.play);
        ImageViewExt conver=holder.itemView.findViewById(R.id.conver);
        conver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(cacheBean.gettType().equals(CacheType.CACHE_RECODE)) {
                ViewUtils.toPlayer((Activity) mContext, v, cacheBean.getShortId(), null, cacheBean.getPosition()+"",null);
            }else if(cacheBean.gettType().equals(CacheType.CACHE_COLLECT)){
                ViewUtils.toPlayer((Activity) mContext, v, cacheBean.getShortId(), null, null,null);
            }
            }
        });

        //LinearLayout linearLayout= holder.itemView.findViewById(R.id.content);
        txtTitle.setText(cacheBean.getTitle());
        conver.loadUrl(cacheBean.getConverUrl());

//        CacheFactory.getInstance().getImage(mContext,conver,cacheBean.getConverUrl());

        //linearLayout.setVisibility(View.VISIBLE);

        proTxt.setVisibility(View.GONE);
        if(cacheBean.gettType().equals(CacheType.CACHE_RECODE)) {//观看
//            double v=Double.valueOf(cacheBean.getPlayPosition());
            long pos=cacheBean.getPosition();
            if(pos>0) {
                double x = pos * 1.0 / 1000 / 60;
                BigDecimal bigDecimal = new BigDecimal(x);
                double doubleValue = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                String value = doubleValue + "分钟";
                proTxt.setText(value);
            }else{
                proTxt.setText("0分钟");
            }
//            if (x == 0) {
//                value = "0分钟";
//            }
//            proTxt.setText(value);
            proTxt.setVisibility(View.VISIBLE);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
