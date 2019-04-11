package com.xcore.ui.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.shuyu.common.model.RecyclerBaseModel;
import com.xcore.R;
import com.xcore.ext.ImageViewExt;
import com.xcore.ui.holder.model.BaseHolder;
import com.xcore.ui.holder.model.GifFindModel;

public class GifFindHolder extends BaseHolder {
    public static int ID=R.layout.holder_gif_find;

    public GifFindHolder(Context context, View v) {
        super(context, v);
    }

    ImageViewExt icon;
    TextView title;
    TextView playTxt;
    TextView timeTxt;
    TextView dateTxt;

    @Override
    public void createView(View v) {
        icon=v.findViewById(R.id.icon);
//        title=v.findViewById(R.id.title);
//        playTxt=v.findViewById(R.id.txt_playCount);
//        timeTxt=v.findViewById(R.id.txt_time);
//        dateTxt=v.findViewById(R.id.txt_date);
    }

    @Override
    public void onBind(RecyclerBaseModel model, int position) {
        if(model==null){
            return;
        }
        GifFindModel gifFindModel= (GifFindModel) model;
        String url= (String) gifFindModel.getValue();
        icon.loadGif(url);
    }
}
