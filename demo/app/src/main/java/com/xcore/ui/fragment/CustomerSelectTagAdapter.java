package com.xcore.ui.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.Tag;
import com.xcore.R;

public class CustomerSelectTagAdapter extends BaseRecyclerAdapter<Tag, CustomerSelectTagAdapter.ViewHolder> {


    public CustomerSelectTagAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Tag tag=dataList.get(position);
        holder.tag_name.setText(tag.getName());
        holder.selectImg.setVisibility(tag.isChecked()?View.VISIBLE:View.GONE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.layout_customer_select_tag,parent,false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView selectImg;
        TextView tag_name;

        public ViewHolder(View itemView) {
            super(itemView);
            selectImg=itemView.findViewById(R.id.selectImg);
            tag_name=itemView.findViewById(R.id.tag_name);
        }
    }
}
