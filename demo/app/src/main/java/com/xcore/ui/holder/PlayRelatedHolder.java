package com.xcore.ui.holder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.shuyu.common.model.RecyclerBaseModel;
import com.xcore.R;
import com.xcore.data.bean.RelatedBean;
import com.xcore.ui.adapter.RelatedAdapter1;
import com.xcore.ui.holder.model.BaseHolder;
import com.xcore.ui.holder.model.PlayHolderBaseModel;
import com.xcore.ui.other.XRecyclerView;

import java.util.List;

public class PlayRelatedHolder extends BaseHolder {
    public static int ID=R.layout.holder_play_related;

    public PlayRelatedHolder(Context context, View v) {
        super(context, v);
    }

    XRecyclerView xRecyclerView;
    RelatedAdapter1 relatedAdapter;

    @Override
    public void createView(View v) {
        xRecyclerView=v.findViewById(R.id.related_recyclerView);
    }

    @Override
    public void onBind(RecyclerBaseModel model, int position) {
        if(model==null){
            return;
        }
        PlayHolderBaseModel mod= (PlayHolderBaseModel) model;
        if(mod==null){
            return;
        }
        try {
            List<RelatedBean> relatedBeans = (List<RelatedBean>) mod.getValue();
            if (relatedBeans == null || relatedBeans.size() <= 0) {
                return;
            }
            relatedAdapter = new RelatedAdapter1(context, mod.getiPlayClickListenner());
            xRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            xRecyclerView.setAdapter(relatedAdapter);

            relatedAdapter.setData(relatedBeans);
        }catch (Exception ex){}
    }
}
