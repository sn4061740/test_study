package com.xcore.ui.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.data.bean.BannerBean;
import com.xcore.ext.ImageViewExt;

public class AdvItemView {
    private Context mContext;
    private ViewGroup mRoot;

    private ImageViewExt advImage;
    private TextView advTxt;

    public AdvItemView(Context context,ViewGroup root){
        this.mContext=context;
        this.mRoot=root;
    }
    public View getView(){
        View view=LayoutInflater.from(mContext).inflate(R.layout.layout_adv_item,mRoot,false);
        advImage=view.findViewById(R.id.adv_image);
        advTxt=view.findViewById(R.id.adv_txt);
        return view;
    }
    public void setView(BannerBean.BannerData bannerData){
        String url=bannerData.getPathUrl();
        advImage.loadUrl("http://wx2.sinaimg.cn/mw690/76c23283gy1fniyf81ns4g20c80967wk.gif");
//        String content=bannerData.getContent();
//        advTxt.setVisibility(View.GONE);
//        if(content!=null&&content.length()>0){
//            advTxt.setText(content);
//        }
    }

}
