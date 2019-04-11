package com.xcore.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.com.baselibrary.BaseCommonActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.jay.HttpServerManager;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.tinker.CustomerTinkerLike;
import com.xcore.utils.StatusBarUtil;
import com.xcore.utils.ToastUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseActivity extends BaseCommonActivity implements BaseView {
    protected Activity activity;
    protected ProgressDialog progress;
    private boolean hasBus = false;
    protected View noDataStubView;
    protected View sysErrStubView;

    protected String className=getClass().getName();

    public Toolbar toolbar;
    private Timer _autoTimer;

    //不处理自动下载的类
    List<String> start_stopList= Arrays.asList(
                                    "PVideoViewActivity88",
                                    "LoginActivity",
                                    "RegisterActivity",
                                    "SplashActivity",
                                    "VideoActivity",
                                    "PlayActivity",
                                    "CuestomerCaptureActivity",
                                    "OpenScreenAdvActivity",
                                    //"M3u8CacheActivity",
                                    "XMainActivity",
                                    "RunActivity"
                                    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.
                FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏

        activity = this;
        MainApplicationContext.addActivity(activity);
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        toolbar = findViewById(R.id.toolbar1);
        if (toolbar != null) {
            toolbar.setFitsSystemWindows(true);
        }
        StatusBarUtil.darkMode(this);

        initBind();
        initViews(savedInstanceState);
        onBack();

        //先判断空闲自动缓存是否开启
        boolean idleBoo= MainApplicationContext.IS_IDLE_CACHE;
        String sName=getClass().getSimpleName();
        if(idleBoo==true&&!start_stopList.contains(sName)) {
            //先把所有任务停止了
//            final LocalDownLoader localDownLoader=CacheManager.getInstance().getLocalDownLoader();
            if(_autoTimer==null){
                _autoTimer = new Timer();
                _autoTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //localDownLoader.startAll();
//                        M3u8DownTaskManager.getInstance().startTaskAll();
                        HttpServerManager.getInstance().startAll();
                    }
                }, MainApplicationContext.IDLE_TIMER);
            }
        }
    }

    protected void initBind() {
//        ButterKnife.bind(activity);
    }

    protected abstract int getLayoutId();

    protected abstract void initViews(Bundle savedInstanceState);

    protected abstract void initData();

    /**
     * 打开一个Activity 默认 不关闭当前activity
     */
    public void gotoActivity(Class<?> clz) {
        gotoActivity(clz, false, null);
    }

    public void gotoActivity(Class<?> clz, boolean isCloseCurrentActivity) {
        gotoActivity(clz, isCloseCurrentActivity, null);
    }

    public void gotoActivity(Class<?> clz, boolean isCloseCurrentActivity, Bundle ex) {
        Intent intent = new Intent(this, clz);
        if (ex != null) intent.putExtras(ex);
        startActivity(intent);
        if (isCloseCurrentActivity) {
            finish();
        }
    }
    Toast toast;
    public void toast(String msg){
        if(toast!=null){
            toast.setText(msg);
        }else {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            toast.setText(msg);
        }
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
    public void toast(String msg,int color){
        TextView view=null;
        if(toast!=null){
            view=  (TextView) toast.getView().findViewById(android.R.id.message);
            toast.setText(msg);
        }else {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            toast.setText(msg);
            view= (TextView) toast.getView().findViewById(android.R.id.message);
        }
        toast.setGravity(Gravity.CENTER,0,0);
        if(view!=null) {
            view.setTextColor(this.getResources().getColor(color));
        }
        toast.show();
    }
    public void toastLong(String msg,int color){
        TextView view=null;
        if(toast!=null){
            view=  (TextView) toast.getView().findViewById(android.R.id.message);
            toast.setText(msg);
        }else {
            toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            toast.setText(msg);
            view= (TextView) toast.getView().findViewById(android.R.id.message);
        }
        toast.setGravity(Gravity.CENTER,0,0);
        if(view!=null) {
            view.setTextColor(this.getResources().getColor(color));
        }
        toast.show();
    }

    //返回 可重写
    public void onBack(){
        View view=findViewById(R.id.btn_back);
        if(view!=null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //finish();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                }else{
                    finish();
                }
                }
            });
        }
    }
    //设置标题
    public void setTitle(String title){
        TextView textView=findViewById(R.id.title_txt);
        if(textView!=null) {
            textView.setText(title);
        }
    }
    //设置右边编辑
    public void setEdit(String editStr, View.OnClickListener l){
        TextView editView=findViewById(R.id.edit_txt);
        if(editStr.equals("")){
            editView.setVisibility(View.GONE);
            return;
        }
        editView.setText(editStr);
        if(l!=null){
            editView.setOnClickListener(l);
        }
    }
    //设置右边编辑
    public void setEdit(String editStr){
        TextView editView=findViewById(R.id.edit_txt);
        if(editView==null){
            return;
        }
        editView.setText(editStr);
        if(editStr.equals("")){
            editView.setVisibility(View.GONE);
            return;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        MainApplicationContext.onWindowFocusChanged(hasFocus,this);
    }

    @Override
    public void showProgress() {
        if (progress == null) {
            progress = new ProgressDialog(activity);
            progress.setMessage("加载中...");
        }
        progress.show();
    }

    @Override
    public void hideProgress() {
        if (progress != null && progress.isShowing()) {
            progress.hide();
        }
    }

    @Override
    public void toast(CharSequence s) {
        ToastUtils.showShortToast(s);
    }

    @Override
    public void toast(int id) {
        ToastUtils.showShortToast(id);
    }

    @Override
    public void toastLong(CharSequence s) {
        ToastUtils.showLongToast(s);
    }

    @Override
    public void toastLong(int id) {
        ToastUtils.showLongToast(id);
    }


    @Override
    public void showNullLayout() {
        try {
            View v = findViewById(R.id.emptyLayout);
            if (v != null) {
                v.setVisibility(View.VISIBLE);
            }
        }catch (Exception ex){}
    }
    public void showNullLayout(String msg) {
        try {
            View v = findViewById(R.id.emptyLayout);
            if (v != null) {
                TextView txtInfo= v.findViewById(R.id.txt_empty);
                if(txtInfo!=null&&!TextUtils.isEmpty(msg)){
                    txtInfo.setText(msg);
                }
                v.setVisibility(View.VISIBLE);
            }
        }catch (Exception ex){}
    }
    @Override
    public void hideNullLayout() {
        try {
            View v = findViewById(R.id.emptyLayout);
            if (v != null) {
                v.setVisibility(View.GONE);
            }
        }catch (Exception ex){}
    }

    @Override
    public void showErrorLayout(View.OnClickListener listener) {

    }

    @Override
    public void hideErrorLayout() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        if(!className.contains("XMainActivity")){
            CustomerTinkerLike.isMain=false;
        }
    }

    @Override
    public void onDestroy() {
        try {
            if (_autoTimer != null) {
                _autoTimer.cancel();
            }
            _autoTimer = null;
            MainApplicationContext.removeActivity(this);
        }catch (Exception ex){}
        try {
            boolean idleBoo = MainApplicationContext.IS_IDLE_CACHE;
            if (idleBoo == true && !start_stopList.contains(getClass().getSimpleName())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            CacheManager.getInstance().getLocalDownLoader().stopRunAll();// .getDownHandler().stopRunAll();// stopAll();
//                            M3u8DownTaskManager.getInstance().stopTaskAll();
                            HttpServerManager.getInstance().stopAll();
                        } catch (Exception ex) {
                        }
                    }
                }).start();
            }
        }catch (Exception ex){}
        super.onDestroy();
    }
}
