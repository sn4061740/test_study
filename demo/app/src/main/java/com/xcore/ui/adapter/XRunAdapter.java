package com.xcore.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.cache.CacheModel;
import com.xcore.ext.ImageViewExt;
import com.xcore.ui.touch.ChangeTotalLister;

import java.util.ArrayList;
import java.util.List;

import cn.dolit.utils.common.Utils;

public class XRunAdapter extends BaseRecyclerAdapter<CacheModel,BaseRecyclerAdapter.ViewHolder> {

    private ChangeTotalLister changeTotalLister;

    public XRunAdapter(Context mContext,ChangeTotalLister changeTotalLister) {
        super(mContext);
        this.changeTotalLister=changeTotalLister;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.adapter_run,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull List<Object> payloads) {
//        final ContactHolder contact= (ContactHolder) holder;
        if(dataList==null||dataList.size()<=0){
            return;
        }
        final CacheModel cacheModel=dataList.get(position);
        if(cacheModel==null){
            return;
        }

        LinearLayout bgLayout= holder.itemView.findViewById(R.id.bgLayout);
        bgLayout.setBackgroundColor(mContext.getResources().getColor(R.color.black_1A));
        if(cacheModel.isBgSelected()){
            bgLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color_black));
        }
        final RadioButton radioButton= holder.itemView.findViewById(R.id.radioBtn);
        if(payloads.isEmpty()){//payloads为空 即不是调用notifyItemChanged(position,payloads)方法执行的
            //在这里进行初始化item全部控件
            try {
                ImageViewExt imgConver = holder.itemView.findViewById(R.id.img_conver);
                imgConver.loadUrl(cacheModel.getConverUrl());

                TextView txtTitle = holder.itemView.findViewById(R.id.txt_title);
                txtTitle.setText(cacheModel.getName());
                radioButton.setChecked(cacheModel.isChecked());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (changeTotalLister != null) {
                            changeTotalLister.onChangeTotal(position);
                        }
                    }
                });

                if (cacheModel.isShowCheck() == true) {
                    radioButton.setVisibility(View.VISIBLE);
                } else {
                    radioButton.setVisibility(View.GONE);
                }
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean boo = !cacheModel.isChecked();
                        cacheModel.setChecked(boo);
                        radioButton.setChecked(boo);
                    }
                });
            }catch (Exception e){}
        }else{//payloads不为空 即调用notifyItemChanged(position,payloads)方法后执行的
            //在这里可以获取payloads中的数据  进行局部刷新
            //假设是int类型
            if(payloads==null){
                payloads=new ArrayList<>();
                payloads.add(100);
            }
            int type= (int) payloads.get(0);// 刷新哪个部分 标志位
            switch(type){
                case 100:
                    try {
                        radioButton.setChecked(cacheModel.isChecked());
                        if (cacheModel.isShowCheck() == true) {
                            radioButton.setVisibility(View.VISIBLE);
                        } else {
                            radioButton.setVisibility(View.GONE);
                        }
                        radioButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean boo = !cacheModel.isChecked();
                                cacheModel.setChecked(boo);
                                radioButton.setChecked(boo);
                            }
                        });
                    }catch (Exception e){}
                    break;
                case 1:
//                    contact.userId.setText(mList.get(position).getId());//只刷新userId
                    break;
                case 2:
//                    contact.userImg.setImageResources(mList.get(position).getImg());//只刷新userImg
                    break;
            }
        }
    try {
        TextView txtState = holder.itemView.findViewById(R.id.txt_state);
        TextView txtTotal = holder.itemView.findViewById(R.id.txt_total);
        ProgressBar progressBar = holder.itemView.findViewById(R.id.pro_progressbar);

        int total = Integer.valueOf(cacheModel.getTotalSize());
        if (total <= 0) {
            txtTotal.setText("未知");
        } else {
            String tStr = Utils.getDisplayFileSize(total);
            txtTotal.setText(tStr);
        }
        progressBar.setMax(100);
        int proValue = Integer.valueOf(cacheModel.getPercent());
        progressBar.setProgress(proValue);

        if (cacheModel.getComplete() == 1) {
            txtState.setText("已完成");
            progressBar.setVisibility(View.GONE);
        } else {
            txtState.setText("未开始");
            if (cacheModel.getStatus() == 2) {
                txtState.setText("暂停");
            }else if (cacheModel.getStatus() == 1) {//下载中..
                if (cacheModel.getStreamInfo1() != null) {
                    String speedStr = Utils.getDisplayFileSize(cacheModel.getStreamInfo1().getDownloadSpeed());
                    txtState.setText("" + cacheModel.getPercent() + "% [" + speedStr + "/S]");
                } else {
                    txtState.setText("" + cacheModel.getPercent() + "% [0B/S]");
                }
            }
        }
    }catch (Exception e){}

    }

    //全选或全不选
    public void selectAll(boolean boo){
        for(int i=0;i<dataList.size();i++){
            CacheModel cacheModel=dataList.get(i);
            if(cacheModel!=null) {
                cacheModel.setChecked(boo);
                cacheModel.setShowCheck(true);
                notifyItemChanged(i,100);
            }
        }
//        notifyDataSetChanged();
    }
    //显示或不显示
    public void showAll(boolean boo){
        for(int i=0;i<dataList.size();i++){
            CacheModel cacheModel=dataList.get(i);
            if(cacheModel!=null) {
                cacheModel.setShowCheck(boo);
                if (boo == false) {
                    cacheModel.setChecked(false);
                }
                notifyItemChanged(i,100);
            }
        }
//        notifyDataSetChanged();
    }
}
