package com.xcore.down;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jay.HttpServerManager;
import com.jay.config.NetSpeedUtils;
import com.jay.config.Status;
import com.jay.down.listener.IDownListener;
import com.xcore.R;
import com.xcore.sliderRecyclerView.BaseRecyclerViewAdapter;
import com.xcore.sliderRecyclerView.RecyclerViewHolder;
import com.xcore.utils.CacheFactory;
import com.xcore.utils.ImageUtils;
import com.xcore.utils.ViewUtils;

import java.io.File;
import java.util.List;

public class M3u8Adapter extends BaseRecyclerViewAdapter<CacheModel> {
    public M3u8Adapter(Context context, List<CacheModel> data, IM3u8Listener m3u8Listener) {
        super(context, data,R.layout.layout_adapter_cache);
        im3u8Listener=m3u8Listener;
    }
    private OnDeleteClickLister mDeleteClickListener;
    private IM3u8Listener im3u8Listener;

    @Override
    protected void onBindData(RecyclerViewHolder holder, final CacheModel bean, final int position) {
        try {
            View view = holder.getView(R.id.tv_delete);
            view.setTag(position);
            if (!view.hasOnClickListeners()) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDeleteClickListener != null) {
                            mDeleteClickListener.onDeleteClick(v, (Integer) v.getTag());
                        }
                    }
                });
            }
            LinearLayout downLayout= (LinearLayout) holder.getView(R.id.downLayout);
            if(bean.isSelected()){
                downLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color_black));
            }else{
                downLayout.setBackgroundColor(mContext.getResources().getColor(R.color.black_1A));
            }
            final ProgressBar progressBar = (ProgressBar) holder.getView(R.id.progress);
            final TextView statusTxt = (TextView) holder.getView(R.id.txt_status);
            final TextView speedTxt = (TextView) holder.getView(R.id.txt_speed);
            final TextView txtTitle = (TextView) holder.getView(R.id.txt_title);
            final TextView txtInfo = (TextView) holder.getView(R.id.txt_info);
            speedTxt.setVisibility(View.VISIBLE);
            final ImageView conver = (ImageView) holder.getView(R.id.conver);
            CacheFactory.getInstance().getImage(conver, ImageUtils.getRes(bean.getConver()));

            conver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bean.getPrent()>=100) {
                        ViewUtils.toPlayer((Activity) mContext, null, bean.getShortId(), "", "0");
                    }
                }
            });
            progressBar.setProgress(bean.getPrent());
            txtTitle.setText(bean.getTitle());
            String curStr=NetSpeedUtils.getInstance().displayFileSize(bean.getCurSize());
            String tStr=NetSpeedUtils.getInstance().displayFileSize(bean.getTotalSize());
            txtInfo.setText(curStr+"/"+tStr);
            switch (bean.getStatus()){
                case RUN:
                    statusTxt.setText("下载中...");
                    break;
                case STOP:
                    statusTxt.setText("停止下载");
                    break;
                case COMPLETE:
                    statusTxt.setText("下载完成");
                    break;
                case NET_ERR:
                    statusTxt.setText("网络错误");
                    break;
                case WAIT:
                    statusTxt.setText("等待下载");
                    break;
            }
            if(bean.getStatus()==Status.COMPLETE){
                return;
            }
            HttpServerManager.getInstance().addListener(bean.getTaskId(),new IDownListener() {
                @Override
                public void onStart() {
                    bean.setStatus(Status.RUN);
                    statusTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            statusTxt.setText("获取资源...");
                        }
                    });
                }
                @Override
                public void onDownloadSize(long l) {
                    final long v=l;
                    txtInfo.post(new Runnable() {
                        @Override
                        public void run() {
                            String curStr=NetSpeedUtils.getInstance().displayFileSize(v);
                            String tStr=NetSpeedUtils.getInstance().displayFileSize(bean.getTotalSize());
                            txtInfo.setText(curStr+"/"+tStr);
                        }
                    });
                }
                @Override
                public void onProgress(int i) {
                    final int v=i;
                    bean.setPrent(i);
                    progressBar.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(v);
                        }
                    });
                }
                @Override
                public void onSpeed(long l) {
                    if(bean.getStatus()!=Status.RUN){
                        return;
                    }
                    bean.setStatus(Status.RUN);
                    final String speedStr=NetSpeedUtils.getInstance().displayFileSize(l);
                    statusTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            statusTxt.setText("下载中..."+speedStr+"/S");
                        }
                    });
                }
                @Override
                public void onSuccess(long fLength) {
                }
                @Override
                public void onDownError(int i, Throwable throwable) {
                    final String vStr=bean.getStatus()==Status.STOP?"停止下载":"网络出错";
                    bean.setStatus(Status.NET_ERR);
                    statusTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            statusTxt.setText(vStr);
                        }
                    });
                }
                @Override
                public void onStop() {
                    bean.setStatus(Status.STOP);
                    statusTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            statusTxt.setText("停止下载");
                        }
                    });
                }
                @Override
                public void onComplete() {
//                    bean.setPrent(100);
//                    bean.setStatus(Status.COMPLETE);
//                    statusTxt.post(new Runnable() {
//                        @Override
//                        public void run() {
//                        statusTxt.setText("下载完成");
//                        progressBar.setProgress(100);
//                        String tStr=NetSpeedUtils.getInstance().displayFileSize(bean.getTotalSize());
//                        txtInfo.setText(tStr+"/"+tStr);
//                        }
//                    });
                    //HttpServerManager.getInstance().removeListener(bean.getTaskId());
                    statusTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            if(im3u8Listener!=null){
                                im3u8Listener.onComplete(position);
                            }
                        }
                    });
                    //notifyDataSetChanged();
                }
                @Override
                public void onWait() {
                    bean.setStatus(Status.WAIT);
                    statusTxt.post(new Runnable() {
                        @Override
                        public void run() {
                            statusTxt.setText("等待下载");
                        }
                    });
                }
            });
        }catch (Exception ex){}
    }
    public void setOnDeleteClickListener(OnDeleteClickLister listener) {
        this.mDeleteClickListener = listener;
    }

    public interface OnDeleteClickLister {
        void onDeleteClick(View view, int position);
    }
}
