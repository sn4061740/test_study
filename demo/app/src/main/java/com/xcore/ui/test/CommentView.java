package com.xcore.ui.test;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.model.HttpParams;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xcore.R;
import com.xcore.cache.CacheManager;
import com.xcore.cache.beans.XCommentBean;
import com.xcore.data.bean.CommentBean;
import com.xcore.data.bean.LikeBean;
import com.xcore.data.utils.TCallback;
import com.xcore.services.ApiFactory;
import com.xcore.ui.touch.DialogTouchListenner;

import java.util.ArrayList;
import java.util.List;

public class CommentView {
    Activity activity;
    View parent;

    SmartRefreshLayout refreshLayout;
    RecyclerView commentRecyclerView;
    private CommentAdapter adapter;
    private int commentPageIndex = 1;
    private String shortId = "";
    private boolean isMore = true;
    private boolean isNull = false;
    private View view;
    private TextView commentTxt;

    List<CommentBean.CommentDataBean> dataBeans = new ArrayList<>();

    View emptyView;

    public CommentView(final Activity activity1, String sId) {
        commentPageIndex = 1;
        this.activity = activity1;
        this.shortId = sId;
        try {
            parent = activity.findViewById(R.id.playLayout);
            view = activity.findViewById(R.id.commentUIView);

            emptyView = activity.findViewById(R.id.emptyLayout111);
            emptyView.setVisibility(View.GONE);

            commentTxt = activity1.findViewById(R.id.comment_txt11);

            adapter = new CommentAdapter(activity, listenner);
            commentRecyclerView = view.findViewById(R.id.recyclerView);
            commentRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

            commentRecyclerView.setAdapter(adapter);
            activity.findViewById(R.id.closeLayout1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(popupWindow!=null&&popupWindow.isShowing()){
                            popupWindow.dismiss();
                        }
                        if (view != null) {
                            view.setVisibility(View.GONE);
                        }
                        hideKey();
                    }catch (Exception ex){}
                }
            });
            activity.findViewById(R.id.edit_comment).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInput();
                }
            });
            activity.findViewById(R.id.btn_comment).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(activity, "请输入内容", Toast.LENGTH_SHORT).show();
                }
            });

            refreshLayout = activity.findViewById(R.id.refreshLayout111);
            refreshLayout.setRefreshHeader(new ClassicsHeader(activity));
            refreshLayout.setRefreshFooter(new ClassicsFooter(activity));

            refreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    commentPageIndex = 1;
                    dataBeans.clear();
                    adapter.dataList.clear();
                    if (!isNull) {
                        onCommentRefresh();
                    } else {
                        refreshLayout.finishRefresh(1000);
                    }
                }
            });
            refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (isMore) {
                        onCommentRefresh();
                    } else {
                        refreshLayout.finishLoadMore(1000);
                    }
                }
            });
        } catch (Exception ex) {
        }
    }

    public void show() {
        try {
            if(activity==null||activity.isFinishing()||activity.isDestroyed()){
                return;
            }
            if (view != null) {
                view.setVisibility(View.VISIBLE);
                Animation myAnim = AnimationUtils.loadAnimation(activity, R.anim.actionsheet_dialog_in);
                view.setAnimation(myAnim);
            }
            if (dataBeans == null || dataBeans.size() <= 0) {
                refreshLayout.autoRefresh();
            }
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
            popupWindow = null;
        }catch (Exception ex){}
    }

    //加载评论
    private void onCommentRefresh() {
        HttpParams params = new HttpParams();
        params.put("PageIndex", commentPageIndex);
        params.put("shortId", shortId);
        ApiFactory.getInstance().<CommentBean>getCommentByShortId(params, new TCallback<CommentBean>() {
            @Override
            public void onNext(CommentBean s) {
                onResult(s);
            }
        });
        commentPageIndex++;
    }

    private void onResult(CommentBean commentBean) {
        try {
            if (refreshLayout != null) {
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
            }
            if (commentBean.getPageIndex() == 1 && commentBean.getList().size() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
                refreshLayout.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
            }
            if (commentBean == null || commentBean.getList() == null) {
                isNull = true;
                isMore = false;
                return;
            }
            dataBeans.addAll(commentBean.getList());
            adapter.setData(dataBeans);
            commentTxt.setText("全部" + commentBean.getTotalCount() + "条评论");
        }catch (Exception ex){}
    }

    EditText editText;
    PopupWindow popupWindow;
    //弹出发送评论
    private void showInput() {
        try {
            View pop = View.inflate(activity, R.layout.layout_comment_input, null);
            popupWindow = new PopupWindow(pop, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            //popupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
            //防止PopupWindow被软件盘挡住
//        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);//stateAlwaysVisible
            final Button btnSend = pop.findViewById(R.id.btn_send11);
            editText = pop.findViewById(R.id.comment_edit11);
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            //点击发送
            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                try {
                    String txtInfo = editText.getText().toString();
                    if (txtInfo.length() <= 0) {
                        //Toast.makeText(activity, "请输入内容", Toast.LENGTH_SHORT).show();
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                        }
                        return;
                    }
                    sendMessage(txtInfo);
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                    }
                    hideKey();
                } catch (Exception ex) {
                }
                }
            });
            //强制弹出软键盘
            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            //hideKey();
        } catch (Exception ex) {
        }
    }

    //点击喜欢
    DialogTouchListenner listenner = new DialogTouchListenner() {
        @Override
        public void onTouch(final CommentBean.CommentDataBean commentDataBean, final boolean isLike, int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                XCommentBean xCommentBean = new XCommentBean();
                xCommentBean.shortId = commentDataBean.getShortId();
                xCommentBean.cTime = System.currentTimeMillis() + "";
                int status = 1;//点赞和取消点赞都是 1   踩: 2
                int type = 0;
                if (isLike) {//赞过了
                    type = 2;
                    xCommentBean.cDelete = "0";
                    //CacheManager.getInstance().getDbHandler().updateComment(xCommentBean);
                } else {
                    xCommentBean.cDelete = "1";
                }
                CacheManager.getInstance().getDbHandler().updateComment(xCommentBean);
                ApiFactory.getInstance().<LikeBean>getDianZ(commentDataBean.getShortId(), status, type, new TCallback<LikeBean>() {
                    @Override
                    public void onNext(LikeBean s) {
                        //commentAdapter.notifyDataSetChanged(position,listView);
                    }
                });
            } catch (Exception e) {
            }
            }
        }).start();
        }
    };

    private void sendMessage(String str) {
        String content = str;

        HttpParams params = new HttpParams();
        params.put("avgRating", 1.0);
        params.put("text", content);
        params.put("shortId", shortId);
        ApiFactory.getInstance().<CommentBean>addComment(params, new TCallback<CommentBean>() {
            @Override
            public void onNext(CommentBean commentBean) {
                try {
                    if (commentBean != null && commentBean.getData() != null) {
                        refreshLayout.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);

                        CommentBean.CommentDataBean dataBean = commentBean.getData();
                        dataBeans.add(dataBean);
                        adapter.setData(dataBeans);
                        commentRecyclerView.scrollToPosition(dataBeans.size() - 1);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void hideKey(){
        try {
            //隐藏虚拟按键，并且全屏
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                View v = activity.getWindow().getDecorView();
                v.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                //for new api versions.
                View decorView = activity.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }catch (Exception ex){}
    }

    public void hide(){
        if(popupWindow!=null&&popupWindow.isShowing()){
            popupWindow.dismiss();
        }else if(!popupWindow.isShowing()&&view!=null&&view.getVisibility()==View.VISIBLE){
            view.setVisibility(View.GONE);
        }
    }

    public void onDestroy() {
        activity = null;
        view = null;

    }

}
