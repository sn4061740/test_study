package com.xcore.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.model.SelectModel;

public class SelectAdapter extends BaseRecyclerAdapter<SelectModel,BaseRecyclerAdapter.ViewHolder> {

    public SelectAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.adapter_list,parent,false);
        return new BaseRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        SelectModel selectModel= dataList.get(position);
        if(selectModel==null){
            return;
        }
        ImageView icon= holder.itemView.findViewById(R.id.icon);
        LinearLayout bgLayout= holder.itemView.findViewById(R.id.bg_layout);
        TextView txtName= holder.itemView.findViewById(R.id.txt_name);
//        if(position==0){
//            icon.setImageResource(R.drawable.common_noz);
//            txtName.setVisibility(View.GONE);
//            bgLayout.setBackgroundResource(0);
//            return;
//        }
        txtName.setVisibility(View.VISIBLE);
        if(selectModel.getPosition()>0){
            bgLayout.setBackgroundColor(0x802d4fbb);
            txtName.setTextColor(mContext.getResources().getColor(R.color.title_color));
        }else{
            bgLayout.setBackgroundResource(0);
            txtName.setTextColor(mContext.getResources().getColor(R.color.no_select));
        }

        if(selectModel.getIcon()==null||selectModel.getIcon()==0){
            icon.setVisibility(View.GONE);
        }else{
            icon.setImageResource(selectModel.getIcon());
            icon.setVisibility(View.VISIBLE);
        }
        txtName.setText(selectModel.getName());
    }
}
