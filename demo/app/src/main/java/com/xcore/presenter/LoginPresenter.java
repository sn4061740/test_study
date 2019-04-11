package com.xcore.presenter;

import android.app.Activity;
import android.text.TextUtils;

import com.android.tu.loadingdialog.LoadingDailog;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.base.BasePresent;
import com.xcore.cache.CacheManager;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.HomeBean;
import com.xcore.data.bean.RegisterBean;
import com.xcore.data.bean.TokenBean;
import com.xcore.data.bean.TypeTabBean;
import com.xcore.data.utils.DataUtils;
import com.xcore.data.utils.TCallback;
import com.xcore.presenter.view.LoginView;
import com.xcore.services.ApiFactory;
import com.xcore.services.ApiSystemFactory;
import com.xcore.ui.Config;
import com.xcore.ui.other.TipsEnum;
import com.xcore.utils.SystemUtils;


public class LoginPresenter extends BasePresent<LoginView> {

    @Override
    public void attach(LoginView view) {
        this.view = view;
        dbHandler=CacheManager.getInstance().getDbHandler();
        Activity activity=MainApplicationContext.getLastActivity();
        if(activity==null){
            activity= (Activity) view;
        }
        LoadingDailog.Builder loadBuilder;loadBuilder=new LoadingDailog.Builder(activity)
                .setMessage("登录中...")
                .setCancelable(true)
                .setCancelOutside(true);

        dialog=loadBuilder.create();
    }

    //注册
    public void toRegister(final String name, String password, String versionCode, String shareCode){
        if(!checkNetwork()){
            return;
        }
        final String Name=name;
        final String Password=password;
        String ShareCode=shareCode;

        String Language= SystemUtils.getSystemLanguage();
        String SystemVersion=SystemUtils.getSystemVersion();
        String DeviceBrand=SystemUtils.getDeviceBrand();
        Config config=MainApplicationContext.getConfig();
        String AppDeviceCode=config.getKey();//SystemUtils.getFingerprint();
        String Imei=SystemUtils.getM(MainApplicationContext.context);
        String SystemModel=SystemUtils.getSystemModel();
        String SystemInfo=SystemUtils.getDevice();

        String ClientVersion=versionCode;

        HttpParams params=new HttpParams();
        params.put("Name",Name);
        params.put("Password",Password);
        params.put("ShareCode",ShareCode);
        params.put("Language",Language);
        params.put("SystemVersion",SystemVersion);
        params.put("DeviceBrand",DeviceBrand);
        params.put("Imei",Imei);
        params.put("SystemModel",SystemModel);
        params.put("ClientVersion",ClientVersion);
        params.put("AppDeviceCode",AppDeviceCode);
        params.put("SystemInfo",SystemInfo);

        if(dialog!=null){
            dialog.show();
        }

        ApiSystemFactory.getInstance().<RegisterBean>toRegister(new TCallback<RegisterBean>() {
            @Override
            public void onNext(RegisterBean registerBean) {
                registerBean.setUsername(Name);
                registerBean.setPassword(Password);
                if(view!=null) {
                    view.getRegisterResult(registerBean);
                }
                if(dialog!=null){
                    dialog.cancel();
                }
            }
            @Override
            public void onError(Response<RegisterBean> response) {
                String errorStr="注册失败,请确认信息后重试!!";
                try {
                    RegisterBean registerBean = response.body();
                    if(registerBean==null){
                        errorStr="注册失败,请稍后重试!!";
                    }else {
                        errorStr = registerBean.getMessage();//得到服务器返回的错误消息
                    }
                }catch (Exception e){}
                if(dialog!=null){
                    dialog.cancel();
                }
                if(view!=null){
                    view.onError(errorStr);
                }else {
                    MainApplicationContext.showips(errorStr,null,TipsEnum.TO_TIPS);
                }
            }
        },params);

    }


}
