package com.xcore.ui;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.common.BaseCommon;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.GuestBean;
import com.xcore.data.bean.TokenBean;
import com.xcore.data.bean.TypeTabBean;
import com.xcore.data.utils.DataUtils;
import com.xcore.data.utils.TCallback;
import com.xcore.services.ApiFactory;
import com.xcore.services.ApiSystemFactory;
import com.xcore.ui.activity.LoginActivity;
import com.xcore.ui.activity.XMainActivity;
import com.xcore.utils.AppUpdateUtils;
import com.xcore.utils.NetWorkUtils;
import com.xcore.utils.SystemUtils;

//登录控制  不管是哪里登录,统一的从这里发送出去
public class LoginController {
    private Activity mContext;
    Config config;
    protected LoadingDailog dialog;
    private boolean isClose=false;

    public LoginController(Activity context){
        this.mContext=context;
        config=MainApplicationContext.getConfig();
        LoadingDailog.Builder loadBuilder;
        loadBuilder=new LoadingDailog.Builder(mContext)
            .setMessage("登录中...")
            .setCancelable(true)
            .setCancelOutside(true);
        dialog=loadBuilder.create();
    }
    //调用之个的话一般 close 设置为true
    public LoginController(Activity context,boolean close){
        this.mContext=context;
        this.isClose=close;

        config=MainApplicationContext.getConfig();
        LoadingDailog.Builder loadBuilder;
        loadBuilder=new LoadingDailog.Builder(mContext)
                .setMessage("登录中...")
                .setCancelable(true)
                .setCancelOutside(true);
        dialog=loadBuilder.create();
    }

    //登录
    public void toLogin(){
        if(config==null){
            config=MainApplicationContext.getConfig();
        }
        String uname=config.get("uname");
        config.remove(config.TOKEN);
        config.remove(config.REFRESH_TOKEN);
        config.remove(config.TOKEN_EXPIRES_IN);
        config.remove(config.CURRENT_EXPIRES_IN);
        //没有信息,请求游客信息
        if(uname==null||uname.length()<=0){
            getGuest();
        }else {
            String upass = config.get("upass");
            String fCode =config.getKey();// SystemUtils.getFingerprint();
            //游客登录
            if (uname.equals(fCode)) {
                toLogin(uname, upass);
            } else {
                if(upass.length()<=0){
                    //到登录去
                    LoginActivity.toActivity(mContext, LoginType.FORGET_PASS);
                    mContext.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    return;
                }
                toLogin(uname, upass);
            }
        }
    }

    //去登录
    public void toLogin(String name, String pwd){
        if(!checkNetwork()){
            return;
        }
        if(name==null||name.length()<=0||pwd==null||pwd.length()<=0){
            Toast.makeText(mContext,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(dialog!=null&&!dialog.isShowing()){
            dialog.show();
        }
        final String username=name;
        final String password=pwd;

        ApiSystemFactory.getInstance().<TokenBean>getToken(new TCallback<TokenBean>() {
            @Override
            public void onNext(TokenBean tokenBean) {
                final TokenBean tokenBean1=tokenBean;
                //有新版本
                if(!TextUtils.isEmpty(tokenBean.getError_description())&&"405".equals(tokenBean.getError_description())){
                    String errorStr=tokenBean.getError();
                    new AppUpdateUtils(mContext,errorStr);
                    if(dialog!=null){
                        dialog.cancel();
                    }
                    return;
                }
                if(!TextUtils.isEmpty(tokenBean.getError())&&tokenBean.getError().contains("invalid_client")){
                    show("登录失效,请重新登录!!!");
                    goToLogin();
                    return;
                }
                if(!TextUtils.isEmpty(tokenBean.getError())|| !TextUtils.isEmpty(tokenBean.getError_description())){
                    show(tokenBean.getError_description());
                    goToLogin();
                    return;
                }

                if(config==null) {
                    config = MainApplicationContext.getConfig();
                }
                config.put("uname",username);
                config.put("upass",password);
                config.put(config.TOKEN,tokenBean1.getAccess_token());
                config.put(config.REFRESH_TOKEN,tokenBean1.getRefresh_token());
                config.put(config.TOKEN_EXPIRES_IN,tokenBean1.getExpires_in()+"");
                config.put(config.CURRENT_EXPIRES_IN,System.currentTimeMillis()+"");

                String fCode=config.getKey();//SystemUtils.getFingerprint();
                if(username.equals(fCode)){
                    config.put("USER_GUEST","1");
                }else{
                    config.put("USER_GUEST","0");
                }
                config.save();//保存信息
                if(!isClose) {
                    toMainView();
                    getTags();
                }else{//为true 只登录
                    try {
                        mContext.finish();
                        mContext.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    }catch (Exception ex){}
                    mContext=null;
                    dialog=null;
                }
            }
            @Override
            public void onError(Response<TokenBean> response) {
                super.onError(response);
                goToLogin();
            }
        },username,password);
    }

    /**
     * 获取游客账号信息
     */
    private void getGuest(){
        if(dialog!=null&&!dialog.isShowing()){
            dialog.show();
        }

        String ShareCode="";

        String Language= SystemUtils.getSystemLanguage();
        String SystemVersion=SystemUtils.getSystemVersion();
        String DeviceBrand=SystemUtils.getDeviceBrand();
//        Config config=MainApplicationContext.getConfig();
        String AppDeviceCode=config.getKey();// SystemUtils.getFingerprint();
        String SystemModel=SystemUtils.getSystemModel();
        String Imei=SystemUtils.getM(mContext);
        String SystemInfo=SystemUtils.getDevice();

        String ClientVersion=BaseCommon.VERSION_NAME;

        HttpParams params=new HttpParams();
        params.put("ShareCode",ShareCode);
        params.put("Language",Language);
        params.put("SystemVersion",SystemVersion);
        params.put("DeviceBrand",DeviceBrand);
        params.put("Imei",Imei);
        params.put("SystemModel",SystemModel);
        params.put("ClientVersion",ClientVersion);
        params.put("AppDeviceCode",AppDeviceCode);
        params.put("SystemInfo",SystemInfo);

        ApiSystemFactory.getInstance().<GuestBean>getGuest(params, new TCallback<GuestBean>() {
            @Override
            public void onNext(GuestBean guestBean) {
                if(config==null) {
                    config = MainApplicationContext.getConfig();
                }
                GuestBean.GuestItemBean itemBean= guestBean.getData();
                config.put("uanme",itemBean.getName());
                config.put("upass",itemBean.getPassword());
                toLogin(itemBean.getName(),itemBean.getPassword());
            }
            @Override
            public void onError(Response<GuestBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
                goToLogin();
            }
        });
    }

    //得到所有的类型标签
    private void getTags(){
        if(!checkNetwork()){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ApiFactory.getInstance().<TypeTabBean>getTags(new TCallback<TypeTabBean>() {
                    @Override
                    public void onNext(TypeTabBean typeTabBean) {
                        typeTabBean.getData().getCategories().add(0,new CategoriesBean("","全部"));
                        typeTabBean.getData().getSpecies().add(0,new CategoriesBean("","全部"));
//                        typeTabBean.getData().getSorttype().add(0,new CategoriesBean("","全部"));
                        DataUtils.typeTabBean=typeTabBean;
                    }
                    @Override
                    public void onError(Response<TypeTabBean> response) {
                        super.onError(response);
                    }
                });
            }
        }).start();
    }
    //到登录去
    private void goToLogin(){
        if(dialog!=null){
            dialog.cancel();
        }
        if(!(mContext instanceof LoginActivity)) {//不是登录的才到登录去
            //到登录去
            LoginActivity.toActivity(mContext, LoginType.EXIT_LOGIN);
            if(mContext!=null) {
                mContext.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    }
    //去主页
    private void toMainView(){
        //到登录去
        Intent intent=new Intent(mContext, XMainActivity.class);
        mContext.startActivity(intent);
        mContext.finish();
        mContext.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        if(dialog!=null){
            dialog.cancel();
        }
        mContext=null;
        dialog=null;
    }

    //显示提示
    private void show(String msg){
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }
    protected boolean checkNetwork(){
        boolean boo= NetWorkUtils.isNetWorkAvailable();
        if(!boo){
            Toast.makeText(mContext,"请检查网络连接",Toast.LENGTH_SHORT).show();
        }
        return boo;
    }
}
