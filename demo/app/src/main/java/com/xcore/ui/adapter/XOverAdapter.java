package com.xcore.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.cache.CacheModel;
import com.xcore.ext.ImageViewExt;
import com.xcore.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class XOverAdapter extends BaseRecyclerAdapter<CacheModel,BaseRecyclerAdapter.ViewHolder>{

    public XOverAdapter(Context mContext) {
        super(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.adapter_run,parent,false);
        return new BaseRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        final CacheModel cacheModel=dataList.get(position);
        if(cacheModel==null){
            return;
        }
        try {
            LinearLayout bgLayout = holder.itemView.findViewById(R.id.bgLayout);
            bgLayout.setBackgroundColor(mContext.getResources().getColor(R.color.black_1A));
            if (cacheModel.isBgSelected()) {
                bgLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color_black));
            }
            final RadioButton radioButton = holder.itemView.findViewById(R.id.radioBtn);
            if (payloads.isEmpty()) {//payloads为空 即不是调用notifyItemChanged(position,payloads)方法执行的
                //在这里进行初始化item全部控件
                try {
                    ImageViewExt imgConver = holder.itemView.findViewById(R.id.img_conver);
                    imgConver.loadUrl(cacheModel.getConverUrl());
                    radioButton.setChecked(cacheModel.isChecked());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ViewUtils.toPlayer((Activity) mContext, null, cacheModel.getShortId(), "", "0", "");
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
                    TextView txtTitle = holder.itemView.findViewById(R.id.txt_title);
                    TextView txtState = holder.itemView.findViewById(R.id.txt_state);
                    TextView txtTotal = holder.itemView.findViewById(R.id.txt_total);
                    ProgressBar progressBar = holder.itemView.findViewById(R.id.pro_progressbar);
                    progressBar.setVisibility(View.GONE);
                    txtTotal.setVisibility(View.GONE);
                    txtState.setText("已完成");
                    txtTitle.setText(cacheModel.getName());
                } catch (Exception e) {
                }
            } else {//payloads不为空 即调用notifyItemChanged(position,payloads)方法后执行的
                //在这里可以获取payloads中的数据  进行局部刷新
                //假设是int类型
                if (payloads == null) {
                    payloads = new ArrayList<>();
                    payloads.add(100);
                }
                int type = (int) payloads.get(0);// 刷新哪个部分 标志位
                switch (type) {
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
                        } catch (Exception e) {
                        }
                        break;
                }
            }
        }catch (Exception ex){}
    }

    //全选或全不选
    public void selectAll(boolean boo){
        try {
            for (int i = 0; i < dataList.size(); i++) {
                CacheModel cacheModel = dataList.get(i);
                if (cacheModel == null) {
                    continue;
                }
                cacheModel.setChecked(boo);
                cacheModel.setShowCheck(true);
                notifyItemChanged(i, 100);
            }
        }catch (Exception e){}
    }
    //显示或不显示
    public void showAll(boolean boo){
        try {
            for (int i = 0; i < dataList.size(); i++) {
                CacheModel cacheModel = dataList.get(i);
                if (cacheModel == null) {
                    continue;
                }
                cacheModel.setShowCheck(boo);
                if (boo == false) {
                    cacheModel.setChecked(false);
                }
                notifyItemChanged(i, 100);
            }
        }catch (Exception e){}
    }
}
