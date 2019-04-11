package com.xcore.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.cache.CacheManager;
import com.xcore.cache.DBHandler;
import com.xcore.cache.beans.CacheBean;
import com.xcore.cache.beans.CacheType;
import com.xcore.data.bean.TypeListDataBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.utils.ViewUtils;

public class TypeItemAdapter extends BaseRecyclerAdapter<TypeListDataBean,TypeItemAdapter.ViewHolder> {


    public TypeItemAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.type_item_adapter,parent,false);
        return new ViewHolder(view);
    }
    private float dpToPx(float dp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
    }
    private TextView getText(String txt,int res){
        TextView textView=new TextView(mContext);
        textView.setText(txt);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
        textView.setPadding((int)dpToPx(6), (int)dpToPx(1), (int)dpToPx(6), (int)dpToPx(1));
        textView.setBackgroundResource(res);
        textView.setTextColor(mContext.getResources().getColor(R.color.color_white));
        return textView;
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        try {
            if (holder != null && holder.icon != null) {
//                Glide.clear(holder.icon);
//                GlideUtils.getInstance().clear(holder.icon);
            }
        }catch (Exception ex){}
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        try {
            final TypeListDataBean typeListDataBean = dataList.get(position);

            TextView txtInfo = holder.itemView.findViewById(R.id.txt_title);
            txtInfo.setText(typeListDataBean.getTitle());

            final ImageViewExt icon = holder.itemView.findViewById(R.id.img_icon);
            icon.loadUrl(typeListDataBean.getConverUrl());
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toPlayer(icon, typeListDataBean);
                }
            });

            TextView txtPlayCount = holder.itemView.findViewById(R.id.txt_playCount);
            txtPlayCount.setText(typeListDataBean.getPlayCountData());

            String dateStr = this.getDate(typeListDataBean.getReleasetime());
            TextView txtDate = holder.itemView.findViewById(R.id.txt_date);
            txtDate.setText(dateStr);

            //流布局
            FlowLayout flowLayout = holder.itemView.findViewById(R.id.bqFlowLayout);
            flowLayout.removeAllViews();

//        if(typeListDataBean.isEnable()==true) {//是否可播放
//            TextView playTxt=getText("可播放",R.drawable.type_tag_play);
//            flowLayout.addView(playTxt);
//        }
            if (!TextUtils.isEmpty(typeListDataBean.getCategoriesName())) {
                TextView wTxt = getText(typeListDataBean.getCategoriesName(), R.drawable.type_tag_wuma);
                wTxt.setTextColor(mContext.getResources().getColor(R.color.color_black));
                flowLayout.addView(wTxt);
            }

            DBHandler dbHandler = CacheManager.getInstance().getDbHandler();
            if (dbHandler != null) {
                //得到是否收藏
                CacheBean cacheBean = new CacheBean();
                cacheBean.setShortId(typeListDataBean.getShortId());
                cacheBean.settType(CacheType.CACHE_COLLECT);
                CacheBean cacheBean1 = dbHandler.query(cacheBean);
                if (cacheBean1 != null) {
                    if (cacheBean1.gettDelete().equals("1")) {
                        TextView cTxt = getText("已收藏", R.drawable.type_tag_collect);
                        flowLayout.addView(cTxt);
                    }
                }
                //得到是否观看
                cacheBean.settType(CacheType.CACHE_RECODE);
                CacheBean cacheBean2 = dbHandler.query(cacheBean);
                if (cacheBean2 != null) {
                    TextView cTxt = getText("已观看", R.drawable.type_tag_look);
                    cTxt.setTextColor(mContext.getResources().getColor(R.color.color_black));
                    flowLayout.addView(cTxt);
                }
            }
        }catch (Exception ex){}
//        TextView txtLook=holder.itemView.findViewById(R.id.txt_look);

    }

    private void toPlayer(View view,TypeListDataBean dataBean){
        ViewUtils.toPlayer((Activity) mContext,view,dataBean.getShortId(),null,null,null);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageViewExt icon;
        public ViewHolder(View itemView) {
            super(itemView);
            icon=itemView.findViewById(R.id.img_icon);
        }
    }
}
