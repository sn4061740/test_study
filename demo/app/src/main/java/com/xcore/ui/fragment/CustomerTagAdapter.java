package com.xcore.ui.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.Tag;

public class CustomerTagAdapter extends BaseRecyclerAdapter<TagSelectModel, CustomerTagAdapter.ViewHolder> {

    public CustomerTagAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.layout_customer_tag,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder,position);

        TagSelectModel tagSelectModel=dataList.get(position);
        holder.tagName.setText(tagSelectModel.getName());
        holder.selectLayout.setVisibility(tagSelectModel.isSelected()?View.VISIBLE:View.GONE);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tagName;
        LinearLayout selectLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            tagName=itemView.findViewById(R.id.tag_name);
            selectLayout=itemView.findViewById(R.id.selectLayout);
        }
    }
}
