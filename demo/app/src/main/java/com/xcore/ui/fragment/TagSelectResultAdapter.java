package com.xcore.ui.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.ext.ImageViewExt;

public class TagSelectResultAdapter extends BaseRecyclerAdapter<TypeListDataBean, TagSelectResultAdapter.ViewHolder> {

    public TagSelectResultAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.adapter_tag_select_result,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);


    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageViewExt conver;
        TextView titleTxt;
        FlowLayout flowLayout;
        TextView playCountTxt;

        public ViewHolder(View itemView) {
            super(itemView);
            conver=itemView.findViewById(R.id.conver);
            titleTxt=itemView.findViewById(R.id.titleTxt);
            flowLayout=itemView.findViewById(R.id.flowLayout);
            playCountTxt=itemView.findViewById(R.id.playCountTxt);
        }
    }
}
