package com.xcore.ui.holder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nex3z.flowlayout.FlowLayout;
import com.shuyu.common.model.RecyclerBaseModel;
import com.wx.goodview.GoodView;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.MovieBean;
import com.xcore.data.bean.NvStar;
import com.xcore.ext.ImageViewExt;
import com.xcore.ui.activity.StarDetailActivity;
import com.xcore.ui.activity.TagActivity;
import com.xcore.ui.adapter.NvStarAdapter;
import com.xcore.ui.holder.model.BaseHolder;
import com.xcore.ui.holder.model.PlayHolderBaseModel;
import com.xcore.ui.other.XRecyclerView;
import com.xcore.utils.JumpUtils;
import com.xcore.utils.ViewUtils;

import java.util.List;

public class PlayDetailHolder extends BaseHolder{
    public static int ID= R.layout.holder_play_detail;

    public PlayDetailHolder(Context context, View v) {
        super(context, v);
    }

    private LinearLayout advLayout;
    private ImageViewExt advImage;
    private TextView titleTxt;
    private LinearLayout xqLinearLayout;
    private ImageView pointImg;
    private boolean isClickXQ=false;

    private TextView playTxt;
    private TextView dateTxt;
    private TextView timeTxt;

    private TextView errorTxt;
    private LinearLayout commentLayout;
    private TextView commentTxt;

    private LinearLayout detailLinearLayot;
    private XRecyclerView nvRecyclerView;
    private NvStarAdapter nvStarAdapter;

    private LinearLayout starLinearLayout;
    private LinearLayout tagLinearLayout;

    FlowLayout flowLayout;
    TextView descTxt;

    Button likeButton;
    Button noLikeButton;
    Button shareButton;
    Button downButton;
    Button collectButton;

    private boolean isCollect;

    @Override
    public void createView(View v) {
        advLayout=v.findViewById(R.id.advLayout);
        advImage=v.findViewById(R.id.advImage);
        titleTxt=v.findViewById(R.id.txt_title);
        xqLinearLayout=v.findViewById(R.id.layout_xiangqing);
        pointImg=v.findViewById(R.id.img_point);
        playTxt=v.findViewById(R.id.txt_play);
        dateTxt=v.findViewById(R.id.txt_date);
        timeTxt=v.findViewById(R.id.txt_time);
        errorTxt=v.findViewById(R.id.to_error);
        commentLayout=v.findViewById(R.id.comment_layout);
        commentTxt=v.findViewById(R.id.txt_comment);
        detailLinearLayot=v.findViewById(R.id.detailLayout);
        detailLinearLayot.setVisibility(View.GONE);
        nvRecyclerView=v.findViewById(R.id.nv_recyclerView);
        flowLayout=v.findViewById(R.id.bqFlowLayout);
        descTxt=v.findViewById(R.id.txt_desc);

        starLinearLayout=v.findViewById(R.id.starLinearLayout);
        tagLinearLayout=v.findViewById(R.id.tagLinearLayout);

        likeButton=v.findViewById(R.id.btn_like);
        noLikeButton=v.findViewById(R.id.btn_noLike);
        shareButton=v.findViewById(R.id.btn_share);
        downButton=v.findViewById(R.id.btn_down);
        collectButton=v.findViewById(R.id.btn_collect);
    }

    @Override
    public void onBind(RecyclerBaseModel model, int position) {
        if(model==null){
            return;
        }
        final PlayHolderBaseModel mod= (PlayHolderBaseModel) model;
        if(mod==null){
            return;
        }
        final MovieBean movieBean= (MovieBean) mod.getValue();
        if(movieBean==null){
            return;
        }
        try {
            advLayout.setVisibility(View.GONE);
            if(movieBean.getDetailAdv()!=null){
                advImage.loadUrl(movieBean.getDetailAdv().getImagePath());
                advImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JumpUtils.to(context,movieBean.getDetailAdv());
                    }
                });
                advLayout.setVisibility(View.VISIBLE);
            }

            titleTxt.setText(movieBean.getTitle());

            playTxt.setText(movieBean.getPlayCountData());
            dateTxt.setText(movieBean.getDate());
            timeTxt.setText(movieBean.getTime());
            commentTxt.setText(movieBean.getFilmReviewCountStr());
            descTxt.setText(movieBean.getDesc());

            List<NvStar> nvStars = movieBean.getActorList();
            if (nvStars != null && nvStars.size() > 0) {
                starLinearLayout.setVisibility(View.VISIBLE);
                nvStarAdapter = new NvStarAdapter(context);
                nvRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                nvRecyclerView.setAdapter(nvStarAdapter);
                nvStarAdapter.setData(nvStars);

                nvStarAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<NvStar>() {
                    @Override
                    public void onItemClick(NvStar item, int position) {
                    Intent intent = new Intent(context, StarDetailActivity.class);
                    String nvStr=new Gson().toJson(item);
                    intent.putExtra("nvItem",nvStr);
                    context.startActivity(intent);

//                    if(mod.getiPlayClickListenner()!=null){
//                        mod.getiPlayClickListenner().onFinish();
//                    }
                    }
                });
            }else{
                starLinearLayout.setVisibility(View.GONE);
            }

            flowLayout.removeAllViews();
            List<CategoriesBean> tags = movieBean.getTagsList();
            if(tags!=null&&tags.size()>0) {
                tagLinearLayout.setVisibility(View.VISIBLE);
                if (tags != null && tags.size() > 0) {
                    for (CategoriesBean tagBean : tags) {
                        TextView textView = getTagItem(tagBean, mod);
                        flowLayout.addView(textView);
                    }
                }
            }else{
                tagLinearLayout.setVisibility(View.GONE);
            }
        }catch (Exception ex){}
        initButton();
        isCollect=mod.getCollected(movieBean.getShortId());

        changeCollect();
        changeLikeOrNoLike(movieBean);
        if(mod.isShare){
            changeShareButton();
        }
        setClick(mod);
    }
    //初始数据
    void changeCollect(){
        try {
            Drawable drawable = null;
            if (isCollect) {//收藏了
                drawable = context.getResources().getDrawable(R.drawable.type_collected);
                collectButton.setTextColor(context.getResources().getColor(R.color.color_ff3158));
            } else {
                drawable = context.getResources().getDrawable(R.drawable.collet_item);
                collectButton.setTextColor(context.getResources().getColor(R.color.color_9c9c9c));
            }
            collectButton.setCompoundDrawables(null, drawable, null, null);
            Drawable[] collectDs = collectButton.getCompoundDrawables();
            collectDs[1].setBounds(0, 0, collectButton.getResources().getDimensionPixelSize(R.dimen._30),
                    collectButton.getResources().getDimensionPixelSize(R.dimen._30));
            collectButton.setCompoundDrawables(collectDs[0], collectDs[1], collectDs[2], collectDs[3]);
        }catch (Exception ex){}
    }
    void changeShareButton(){
        try {
            shareButton.setTextColor(context.getResources().getColor(R.color.color_4eb034));
            Drawable drawable = context.getResources().getDrawable(R.drawable.share_select);
            //分享
            shareButton.setCompoundDrawables(null, drawable, null, null);
            Drawable[] shareDs = shareButton.getCompoundDrawables();
            shareDs[1].setBounds(0, 0, shareButton.getResources().getDimensionPixelSize(R.dimen._30),
                    shareButton.getResources().getDimensionPixelSize(R.dimen._30));
            shareButton.setCompoundDrawables(shareDs[0], shareDs[1], shareDs[2], shareDs[3]);
        }catch (Exception ex){}
    }
    void changeDownButton(){
        try {
            downButton.setTextColor(context.getResources().getColor(R.color.color_1296db));
            Drawable drawable = context.getResources().getDrawable(R.drawable.dianjhuancun);
            downButton.setCompoundDrawables(null, drawable, null, null);
            Drawable[] collectDs = downButton.getCompoundDrawables();
            collectDs[1].setBounds(0, 0, downButton.getResources().getDimensionPixelSize(R.dimen._30),
                    downButton.getResources().getDimensionPixelSize(R.dimen._30));
            downButton.setCompoundDrawables(collectDs[0], collectDs[1], collectDs[2], collectDs[3]);
        }catch (Exception ex){}
    }
    void changeLikeOrNoLike(MovieBean movieBean){
        try {
            Drawable drawable = null;
            if (movieBean.getPraiseState() == 2) {
                drawable = context.getResources().getDrawable(R.drawable.like_select);
                likeButton.setCompoundDrawables(null, drawable, null, null);
                Drawable[] likeDs = likeButton.getCompoundDrawables();
                likeDs[1].setBounds(0, 0, likeButton.getResources()
                                .getDimensionPixelSize(R.dimen._30),
                        likeButton.getResources().getDimensionPixelSize(R.dimen._30));
                likeButton.setCompoundDrawables(likeDs[0], likeDs[1], likeDs[2], likeDs[3]);
//                int v = movieBean.getPraise();
//                likeButton.setText(v + "");
                likeButton.setTextColor(context.getResources().getColor(R.color.title_color));
            } else if (movieBean.getPraiseState() == 3) {
                drawable = context.getResources().getDrawable(R.drawable.no_like_select);
                noLikeButton.setCompoundDrawables(null, drawable, null, null);
                Drawable[] noLikeDs = noLikeButton.getCompoundDrawables();
                noLikeDs[1].setBounds(0, 0, noLikeButton.getResources()
                                .getDimensionPixelSize(R.dimen._30),
                        noLikeButton.getResources().getDimensionPixelSize(R.dimen._30));
                noLikeButton.setCompoundDrawables(noLikeDs[0], noLikeDs[1], noLikeDs[2], noLikeDs[3]);
//                int v = movieBean.getDislike();
//                noLikeButton.setText(v + "");
                noLikeButton.setTextColor(context.getResources().getColor(R.color.title_color));
            }
            int v = movieBean.getPraise();
            likeButton.setText(v + "");

            int v1 = movieBean.getDislike();
            noLikeButton.setText(v1 + "");
        }catch (Exception ex){}
    }
    //获取TextView
    TextView getTagItem(final CategoriesBean item, final PlayHolderBaseModel mod){
        TextView textView = ViewUtils.getText(context, item.getName(), R.drawable.tag_feedback_tiwen);
        textView.setTextColor(context.getResources().getColor(R.color.title_color));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //到标签
            Intent intent = new Intent(context, TagActivity.class);
            intent.putExtra("tag", item.getName());
            context.startActivity(intent);
//                if(mod.getiPlayClickListenner()!=null){
//                    mod.getiPlayClickListenner().onFinish();
//                }
            }
        });
        return textView;
    }
    //设置点击事件
    void setClick(final PlayHolderBaseModel mod){
        xqLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isClickXQ=!isClickXQ;
                    if (isClickXQ) {
                        detailLinearLayot.setVisibility(View.VISIBLE);
                        pointImg.setImageResource(R.drawable.point_up);
                    } else {
                        detailLinearLayot.setVisibility(View.GONE);
                        pointImg.setImageResource(R.drawable.point_down);
                    }
                }catch (Exception ex){}
            }
        });
        errorTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mod!=null&&mod.getiPlayClickListenner()!=null){
                    mod.getiPlayClickListenner().onClickError();
                }
            }
        });
        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mod!=null&&mod.getiPlayClickListenner()!=null){
                    mod.getiPlayClickListenner().onClickComment();
                }
            }
        });
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                if (mod != null && mod.getiPlayClickListenner() != null) {
                    boolean boo = mod.getiPlayClickListenner().onLike();
                    if (boo) {
                        final GoodView goodView = new GoodView(context);

                        goodView.setDuration(2000);
                        goodView.setAlpha(1, 0);
                        goodView.setDistance(80);
                        MovieBean movieBean = (MovieBean) mod.getValue();
                        goodView.setTextInfo("+1", context.getResources().getColor(R.color.title_color), 12);
                        goodView.show(likeButton);

                        int dislike = movieBean.getPraise();
                        dislike++;
                        movieBean.setPraise(dislike);

                        movieBean.setPraiseState(2);
                        changeLikeOrNoLike(movieBean);
                    }
                }
            }catch (Exception ex){}
            }
        });
        noLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                if (mod != null && mod.getiPlayClickListenner() != null) {
                    boolean boo = mod.getiPlayClickListenner().onNolike();
                    if (boo) {
                        final GoodView goodView = new GoodView(context);
                        goodView.setDuration(2000);
                        goodView.setAlpha(1, 0);
                        goodView.setDistance(80);
                        MovieBean movieBean = (MovieBean) mod.getValue();
                        int dislike = movieBean.getDislike();
                        dislike++;
                        movieBean.setDislike(dislike);
                        goodView.setTextInfo("-1", context.getResources().getColor(R.color.title_color), 12);
                        goodView.show(noLikeButton);

                        movieBean.setPraiseState(3);
                        changeLikeOrNoLike(movieBean);
                    }
                }
            }catch (Exception ex){}
            }
        });
        collectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                if (mod != null && mod.getiPlayClickListenner() != null) {
                    boolean boo = mod.getiPlayClickListenner().onCollect(isCollect);
                    if (boo) {
                        isCollect = !isCollect;
                        changeCollect();
                    }
                }
            }catch (Exception ex){}
            }
        });
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                boolean boo=false;
                if (mod != null && mod.getiPlayClickListenner() != null) {
                    boo=mod.getiPlayClickListenner().onDown();
                }
                if(boo) {
                    changeDownButton();
                }
            }catch (Exception ex){}
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                if (mod != null && mod.getiPlayClickListenner() != null) {
                    boolean b = mod.getiPlayClickListenner().onShare();
                    if (b) {
                        mod.isShare=true;
                        changeShareButton();
                    }
                }
            }catch (Exception ex){}
            }
        });
    }
    //初始设置按钮
    void initButton(){
        try {
            //喜欢
            Drawable[] likeDs = likeButton.getCompoundDrawables();
            likeDs[1].setBounds(0, 0, likeButton.getResources().getDimensionPixelSize(R.dimen._30),
                    likeButton.getResources().getDimensionPixelSize(R.dimen._30));
            likeButton.setCompoundDrawables(likeDs[0], likeDs[1], likeDs[2], likeDs[3]);

            //不喜欢
            Drawable[] noLikeDs = noLikeButton.getCompoundDrawables();
            noLikeDs[1].setBounds(0, 0, noLikeButton.getResources().getDimensionPixelSize(R.dimen._30),
                    noLikeButton.getResources().getDimensionPixelSize(R.dimen._30));
            noLikeButton.setCompoundDrawables(noLikeDs[0], noLikeDs[1], noLikeDs[2], noLikeDs[3]);

            //分享
            Drawable[] shareDs = shareButton.getCompoundDrawables();
            shareDs[1].setBounds(0, 0, shareButton.getResources().getDimensionPixelSize(R.dimen._30),
                    shareButton.getResources().getDimensionPixelSize(R.dimen._30));
            shareButton.setCompoundDrawables(shareDs[0], shareDs[1], shareDs[2], shareDs[3]);

            //下载
            Drawable[] downDs = downButton.getCompoundDrawables();
            downDs[1].setBounds(0, 0, downButton.getResources().getDimensionPixelSize(R.dimen._30),
                    downButton.getResources().getDimensionPixelSize(R.dimen._30));
            downButton.setCompoundDrawables(downDs[0], downDs[1], downDs[2], downDs[3]);

            //下载
            Drawable[] collectDs = collectButton.getCompoundDrawables();
            collectDs[1].setBounds(0, 0, collectButton.getResources().getDimensionPixelSize(R.dimen._30),
                    collectButton.getResources().getDimensionPixelSize(R.dimen._30));
            collectButton.setCompoundDrawables(collectDs[0], collectDs[1], collectDs[2], collectDs[3]);
        }catch (Exception ex){}
    }

}
