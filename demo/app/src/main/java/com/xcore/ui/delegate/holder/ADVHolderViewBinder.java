package com.xcore.ui.delegate.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.xcore.R;
import com.xcore.commonAdapter.ItemViewBinder;
import com.xcore.data.bean.AdvBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.ui.delegate.AdvItem;
import com.xcore.utils.JumpUtils;

public class ADVHolderViewBinder extends ItemViewBinder<AdvItem,ADVHolderViewBinder.ADVHolder> {
    Context mContext;
    public ADVHolderViewBinder(Context ctx){
        this.mContext=ctx;
    }

    @NonNull
    @Override
    protected ADVHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        mContext=parent.getContext();
        View root = inflater.inflate(R.layout.layout_adv_item, parent, false);
        return new ADVHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ADVHolder holder, @NonNull AdvItem item) {
        final AdvBean advBean=item.getAdvBean();

        holder.advImage.loadUrl(advBean.getImagePath());
        holder.contentTxt.setVisibility(View.GONE);
        if(advBean.getContent()!=null&&advBean.getContent().length()>0) {
            holder.contentTxt.setText(advBean.getContent());
            holder.contentTxt.setVisibility(View.VISIBLE);
        }
        holder.advImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              JumpUtils.to(mContext,advBean);
            }
        });
    }

    class ADVHolder extends RecyclerView.ViewHolder{

        ImageViewExt advImage;
        TextView contentTxt;

        public ADVHolder(View itemView) {
            super(itemView);
            advImage=itemView.findViewById(R.id.adv_image);
            contentTxt=itemView.findViewById(R.id.adv_txt);
            contentTxt.setVisibility(View.GONE);
        }
    }
}
