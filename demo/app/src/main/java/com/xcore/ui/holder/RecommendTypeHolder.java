package com.xcore.ui.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.shuyu.common.model.RecyclerBaseModel;
import com.xcore.R;
import com.xcore.data.bean.HomeBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.ui.holder.model.BaseHolder;
import com.xcore.ui.holder.model.RecommendHolderBaseModel;
import com.xcore.utils.JumpUtils;

import java.util.List;

public class RecommendTypeHolder extends BaseHolder {
    public static int ID= R.layout.holder_commend_type;

    private View view;
    int[] txtList=new int[]{R.id.txt_0,R.id.txt_1,R.id.txt_2,R.id.txt_3};
    int[] resList=new int[]{R.id.image_0,R.id.image_1,R.id.image_2,R.id.image_3};
    int[] layoutList=new int[]{R.id.btn_0,R.id.btn_1,R.id.btn_2,R.id.btn_3};

    public RecommendTypeHolder(Context context, View v) {
        super(context, v);
    }

    @Override
    public void createView(View v) {
        this.view=v;
    }

    @Override
    public void onBind(RecyclerBaseModel model, int position) {
        if(model==null){
            return;
        }
        try {
            RecommendHolderBaseModel mod = (RecommendHolderBaseModel) model;
            List<HomeBean.HomeTypeItem> typeItemList = (List<HomeBean.HomeTypeItem>) mod.getValue();
            if (typeItemList == null || typeItemList.size() <= 0) {
                return;
            }
            for (int i = 0; i < resList.length; i++) {
                HomeBean.HomeTypeItem item = typeItemList.get(i);
                if (item == null) {
                    continue;
                }
                ImageViewExt img = view.findViewById(resList[i]);
                img.loadUrl(item.getResUrl());

                TextView txt = view.findViewById(txtList[i]);
                txt.setText(item.getName());

                setClickView(view.findViewById(layoutList[i]), mod, item);
            }
        }catch (Exception ex){}
    }
    //点击
    private void setClickView(View view, final RecommendHolderBaseModel mod, final Object o){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpUtils.to(context,(HomeBean.HomeTypeItem) o);
            }
        });
    }
}
