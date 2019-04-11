package com.xcore.ui.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.cache.CacheManager;
import com.xcore.cache.beans.XCommentBean;
import com.xcore.data.bean.CommentBean;
import com.xcore.ui.touch.DialogTouchListenner;
import com.xcore.utils.CacheFactory;

import cn.carbs.android.avatarimageview.library.AvatarImageView;

public class CommentAdapter extends BaseRecyclerAdapter<CommentBean.CommentDataBean,CommentAdapter.CommentHolder> {

    DialogTouchListenner listenner;
    public CommentAdapter(Context mContext,DialogTouchListenner listenner1) {
        super(mContext);
        this.listenner=listenner1;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.adapter_comment,parent,false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, final int position) {
        final CommentBean.CommentDataBean dataBean= dataList.get(position);
        try {
            if(dataBean==null){
                return;
            }
            AvatarImageView headImg = holder.itemView.findViewById(R.id.item_avatar);
            if (!TextUtils.isEmpty(dataBean.getImageUrl())) {
                CacheFactory.getInstance().getImage(mContext, headImg, dataBean.getImageUrl());
            }

            TextView tName = holder.itemView.findViewById(R.id.txt_name);
            tName.setText(dataBean.getUserName());

            TextView tCon = holder.itemView.findViewById(R.id.txt_content);
            tCon.setText(dataBean.getText());

            TextView tTime = holder.itemView.findViewById(R.id.txt_time);
            tTime.setText(dataBean.getDateX());

            final TextView txtLike = holder.itemView.findViewById(R.id.txt_like);
            final String vLikeStr = dataBean.getPraise() + "";
            txtLike.setText(vLikeStr);

            final XCommentBean xCommentBean = CacheManager.getInstance().getDbHandler().getCommentByShortId(dataBean.getShortId());
            final ImageView likeButton = holder.itemView.findViewById(R.id.img_like);

            if (xCommentBean != null && "1".equals(xCommentBean.cDelete)) {
                likeButton.setImageResource(R.drawable.common_yesz);
            } else {
                likeButton.setImageResource(R.drawable.common_noz);
            }
            holder.itemView.findViewById(R.id.xLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if (xCommentBean!=null&&
                        xCommentBean.cDelete!=null&&
                        xCommentBean.cDelete.equals("1")) {
                    return;
                }
                xCommentBean.cDelete="1";
                likeButton.setEnabled(false);
                likeButton.setFocusableInTouchMode(false);
                likeButton.setOnClickListener(null);
                int v1 = dataBean.getPraise();
                v1++;
                dataBean.setPraise(v1);
                final String vLikeStr = dataBean.getPraise() + "";
                txtLike.setText(vLikeStr);
                likeButton.setEnabled(false);

                likeButton.setFocusableInTouchMode(false);
                likeButton.setImageResource(R.drawable.common_yesz);
                if (listenner != null) {
                    listenner.onTouch(dataBean, false, position);
                }
                }
            });
        }catch (Exception ex){}

//        final LikeButton likeButton=holder.itemView.findViewById(R.id.img_like);
//        if (xCommentBean!=null&&"1".equals(xCommentBean.cDelete)) {
//            likeButton.setLiked(true);
//            likeButton.setClickable(false);
//            likeButton.setEnabled(false);
//            likeButton.setOnLikeListener(null);
//        }else {
//            likeButton.setLiked(false);
//            likeButton.setOnLikeListener(new OnLikeListener() {
//                @Override
//                public void liked(LikeButton likeButton) {
//                    if(listenner!=null){
//                        listenner.onTouch(dataBean,false,position);
//                    }
//                    int v=dataBean.getPraise();
//                    v++;
//                    dataBean.setPraise(v);
//                    final String vLikeStr=dataBean.getPraise()+"";
//                    txtLike.setText(vLikeStr);
//                    likeButton.setEnabled(false);
//                    likeButton.setOnLikeListener(null);
//                }
//                @Override
//                public void unLiked(LikeButton likeButton) {
////                if(touchListenner!=null){
////                    touchListenner.onTouch(commentDataBean,true,position);
////                }
////                int v=commentDataBean.getPraise();
////                v--;
////                commentDataBean.setPraise(v);
////                final String vLikeStr=commentDataBean.getPraise()+"";
////                txtLike.setText(vLikeStr);
//                }
//            });
//        }

    }

    class CommentHolder extends RecyclerView.ViewHolder{

        public CommentHolder(View itemView) {
            super(itemView);
        }
    }
}
