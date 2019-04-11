package com.xcore.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.RelatedBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.ui.activity.TagActivity;
import com.xcore.ui.touch.IPlayClickListenner;
import com.xcore.utils.ViewUtils;

public class RelatedAdapter1 extends BaseRecyclerAdapter<RelatedBean,BaseRecyclerAdapter.ViewHolder> {
    private IPlayClickListenner touchListener;
    public RelatedAdapter1(Context mContext,IPlayClickListenner iTouchListener) {
        super(mContext);
        touchListener=iTouchListener;
    }

    @NonNull
    @Override
    public BaseRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.adapter_related,parent,false);
        return new BaseRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        final RelatedBean relatedBean= dataList.get(position);

        final ImageViewExt icon=holder.itemView.findViewById(R.id.image_icon);
        icon.loadUrl(relatedBean.getConverUrl());

        TextView txtTitle=holder.itemView.findViewById(R.id.txt_title);
        txtTitle.setText(relatedBean.getName());

        TextView txtTime=holder.itemView.findViewById(R.id.txt_time);
        txtTime.setText(relatedBean.getDate());

        TextView pTxt=holder.itemView.findViewById(R.id.txt_playCount);
        pTxt.setText(relatedBean.getPlayCount());

        FlowLayout flowLayout= holder.itemView.findViewById(R.id.flowLayout);
        flowLayout.removeAllViews();
        int i=0;
        for(CategoriesBean item:relatedBean.getTagslist()){
            TextView textView=getText(item);
            textView.setMaxLines(1);
            textView.setTextColor(mContext.getResources().getColor(R.color.title_color));
            flowLayout.addView(textView);
            i++;
            if(i>1){
                break;
            }
        }
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//这里要给一个ID
                try {
                    ViewUtils.toPlayer((Activity) mContext, icon, relatedBean.getShortId(), null, null, null);
                }catch (Exception ex){}
                if(touchListener!=null){
                    touchListener.onFinish();
                }
            }
        });
    }
    private TextView getText(final CategoriesBean item){
        TextView textView=ViewUtils.getText(mContext,item.getName(),R.drawable.tag_feedback_tiwen);
        textView.setMaxEms(7);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, TagActivity.class);
                intent.putExtra("tag",item.getName());
                mContext.startActivity(intent);
            }
        });
        return textView;
    }

}
