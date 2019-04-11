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
import com.xcore.data.bean.HomeBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.ui.delegate.ButtonItem;
import com.xcore.utils.JumpUtils;

import java.util.List;

public class ButtonHolderViewBinder extends ItemViewBinder<ButtonItem,ButtonHolderViewBinder.ButtonHolder> {
    Context mContext;

    public ButtonHolderViewBinder(Context ctx){
        this.mContext=ctx;
    }

    int[] imgRes=new int[]{R.id.image_0,R.id.image_1,R.id.image_2,R.id.image_3};
    int[] btnRes=new int[]{R.id.btn_0,R.id.btn_1,R.id.btn_2,R.id.btn_3};
    int[] txtRes=new int[]{R.id.txt_0,R.id.txt_1,R.id.txt_2,R.id.txt_3};
    private boolean isShow=false;
    @NonNull
    @Override
    protected ButtonHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.holder_home_button, parent, false);
        return new ButtonHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ButtonHolder holder, @NonNull ButtonItem item) {
        try {
            if(isShow){
                return;
            }
            isShow=true;
            List<HomeBean.HomeTypeItem> typeItemList =item.homeTypeItems;
            View view=holder.itemView;
            for (int i = 0; i < btnRes.length; i++) {
                ImageViewExt img = view.findViewById(imgRes[i]);
                if(typeItemList.get(i).getResId().isEmpty()){
                    img.setVisibility(View.GONE);
                }else{
                    img.loadUrl(typeItemList.get(i).getResUrl());
                }
                TextView txt = view.findViewById(txtRes[i]);
                txt.setText(typeItemList.get(i).getName());

                setViewClick(view.findViewById(btnRes[i]),typeItemList.get(i));
            }
        }catch (Exception ex){}
    }

    private void setViewClick(View view, final HomeBean.HomeTypeItem item){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpUtils.to(mContext,item);
            }
        });
    }

    class ButtonHolder extends RecyclerView.ViewHolder{
        public ButtonHolder(View itemView) {
            super(itemView);
        }
    }
}
